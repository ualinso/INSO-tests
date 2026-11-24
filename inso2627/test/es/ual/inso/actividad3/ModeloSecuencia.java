package es.ual.inso.actividad3;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionConstraint;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.ValueSpecification;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Carga un modelo de Visual Paradigm con un diagrama de clases y uno o varios
 * diagramas de secuencia, y ofrece la información necesaria para los tests de
 * la actividad 3.
 *
 * Antes de cargar el modelo con EMF se corrige el XMI que exporta Visual
 * Paradigm:
 * <ul>
 * <li>Los fragmentos combinados anidados se exportan como
 * {@code packagedElement} dentro de un {@code operand}, por lo que EMF los
 * descarta. Se renombran a {@code fragment}.</li>
 * <li>La operación invocada por cada mensaje se exporta a través del evento de
 * su ocurrencia de envío ({@code CallEvent}), que UML 5 ya no admite. Se añade
 * como {@code signature} del mensaje.</li>
 * <li>Los argumentos cuyo valor es un literal de un tipo enumerado se exportan
 * con un tipo que no es válido y hacen fallar la carga. Se elimina dicho
 * tipo.</li>
 * </ul>
 */
public class ModeloSecuencia {

  private String packageName;
  private List<Diagrama> diagramas = new ArrayList<Diagrama>();

  /**
   * Carga el modelo.
   *
   * @param path      directorio del modelo (terminado en /)
   * @param modelName nombre del archivo .uml
   */
  public static ModeloSecuencia cargar(String path, String modelName) throws Exception {

    String cleanFile = path + "_" + modelName;
    Map<String, int[]> geometrias = corregirXMI(new File(path + modelName), new File(cleanFile));

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
        new XMIResourceFactoryImpl());

    ResourceSet resSet = new ResourceSetImpl();
    UMLResourcesUtil.init(resSet);
    resSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
    resSet.getPackageRegistry().put("http://schema.omg.org/spec/XMI/2.1", UMLPackage.eINSTANCE);
    resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION,
        UMLResource.Factory.INSTANCE);

    URI uri = URI.createURI(cleanFile);
    Resource resource;
    try {
      resource = resSet.getResource(uri, true);
    } catch (RuntimeException e) {
      // EMF lanza una excepción si hay errores, aunque el modelo se haya podido
      // cargar. Solo se considera un fallo si no hay contenido.
      resource = resSet.getResource(uri, false);
      if (resource == null || resource.getContents().isEmpty() || !(resource.getContents().get(0) instanceof Model)) {
        throw e;
      }
      System.out.println("Avisos al cargar el modelo:");
      for (Resource.Diagnostic d : resource.getErrors()) {
        System.out.println("-- " + d.getMessage());
      }
    }

    ModeloSecuencia modelo = new ModeloSecuencia();
    modelo.geometrias = geometrias;
    modelo.leer((Model) resource.getContents().get(0));
    return modelo;
  }

  /** Posición en el diagrama (x, y, ancho, alto) de cada fragmento, por su id. */
  private Map<String, int[]> geometrias;

  private void leer(Model model) {

    System.out.println("Nombre del modelo: " + model.getName());

    for (PackageableElement pe : model.getPackagedElements()) {
      if (pe instanceof org.eclipse.uml2.uml.Package && packageName == null
          && !pe.getName().equalsIgnoreCase("candidate items")) {
        packageName = pe.getName();
        System.out.println("Nombre del package: " + packageName);
      }
    }

    TreeIterator<EObject> it = model.eAllContents();
    while (it.hasNext()) {
      EObject o = it.next();
      if (o instanceof Interaction) {
        Diagrama d = new Diagrama((Interaction) o, geometrias);
        diagramas.add(d);
        d.imprimir();
        it.prune();
      }
    }
  }

  public String getPackageName() {
    return packageName;
  }

  public List<Diagrama> getDiagramas() {
    return diagramas;
  }

  /**
   * Devuelve el diagrama de secuencia con alguno de los nombres indicados (sin
   * distinguir mayúsculas), o null si no existe.
   */
  public Diagrama getDiagrama(String... nombres) {
    for (String nombre : nombres) {
      for (Diagrama d : diagramas) {
        if (nombre.equalsIgnoreCase(d.getNombre().trim())) {
          return d;
        }
      }
    }
    return null;
  }

  // ---------------------------------------------------------------------------
  // Diagrama de secuencia
  // ---------------------------------------------------------------------------

  public static class Fragmento {
    String operador;
    Fragmento padre;
    List<String> guardas = new ArrayList<String>();
    Set<String> clasesCubiertas = new LinkedHashSet<String>();
    List<Fragmento> hijos = new ArrayList<Fragmento>();
    boolean primeraGuarda;
    int[] geometria;

    public String getOperador() {
      return operador;
    }

    /** Operador y primera guarda, p. ej. "opt [codigoActual == codigo]". */
    public String descripcion() {
      String guarda = guardas.isEmpty() ? "" : guardas.get(0);
      return guarda.isEmpty() ? operador : operador + " [" + guarda + "]";
    }

    boolean contieneVisualmente(Fragmento otro) {
      int margen = 2;
      int[] a = geometria;
      int[] b = otro.geometria;
      return a != null && b != null && (long) a[2] * a[3] > (long) b[2] * b[3] && b[0] >= a[0] - margen
          && b[1] >= a[1] - margen && b[0] + b[2] <= a[0] + a[2] + margen && b[1] + b[3] <= a[1] + a[3] + margen;
    }

    public Set<String> getClasesCubiertas() {
      return clasesCubiertas;
    }
  }

  public static class Mensaje {
    String tipo;
    String texto;
    String operacion;
    String origen;
    String destino;
  }

  public static class Diagrama {

    private String nombre;
    private List<String> lineasDeVida = new ArrayList<String>();
    private Set<String> clases = new LinkedHashSet<String>();
    private List<Mensaje> mensajes = new ArrayList<Mensaje>();
    private List<Fragmento> fragmentos = new ArrayList<Fragmento>();
    private List<Fragmento> raices = new ArrayList<Fragmento>();
    private Map<String, int[]> geometrias;

    Diagrama(Interaction interaction, Map<String, int[]> geometrias) {
      this.geometrias = geometrias;
      nombre = interaction.getName() == null ? "" : interaction.getName();

      for (Lifeline ll : interaction.getLifelines()) {
        String clase = clase(ll);
        lineasDeVida.add((ll.getName() == null ? "" : ll.getName()) + " : " + clase);
        if (!clase.isEmpty()) {
          clases.add(clase);
        }
      }

      for (Message m : interaction.getMessages()) {
        Mensaje msg = new Mensaje();
        msg.tipo = m.getMessageSort() == null ? "" : m.getMessageSort().getLiteral();
        msg.texto = m.getName() == null ? "" : m.getName();
        msg.operacion = m.getSignature() == null ? null : m.getSignature().getName();
        msg.origen = extremo(m.getSendEvent());
        msg.destino = extremo(m.getReceiveEvent());
        mensajes.add(msg);
      }

      leerFragmentos(interaction.getFragments(), null);
    }

    private void leerFragmentos(List<InteractionFragment> lista, Fragmento padre) {
      for (InteractionFragment f : lista) {
        if (f instanceof CombinedFragment) {
          CombinedFragment cf = (CombinedFragment) f;
          Fragmento fr = new Fragmento();
          fr.operador = cf.getInteractionOperator().getLiteral();
          fr.padre = padre;
          if (geometrias != null && cf.eResource() instanceof XMLResource) {
            fr.geometria = geometrias.get(((XMLResource) cf.eResource()).getID(cf));
          }
          for (Lifeline ll : cf.getCovereds()) {
            String clase = clase(ll);
            if (!clase.isEmpty()) {
              fr.clasesCubiertas.add(clase);
            }
          }
          fragmentos.add(fr);
          if (padre == null) {
            raices.add(fr);
          } else {
            padre.hijos.add(fr);
          }
          boolean primero = true;
          for (InteractionOperand op : cf.getOperands()) {
            String guarda = guarda(op.getGuard());
            fr.guardas.add(guarda);
            if (primero) {
              fr.primeraGuarda = !guarda.isEmpty();
              primero = false;
            }
            leerFragmentos(op.getFragments(), fr);
          }
        }
      }
    }

    private static String guarda(InteractionConstraint g) {
      if (g == null) {
        return "";
      }
      String spec = valor(g.getSpecification());
      String min = valor(g.getMinint());
      String max = valor(g.getMaxint());
      String rango = min.isEmpty() && max.isEmpty() ? "" : "(" + min + ", " + max + ")";
      return (rango + " " + spec).trim();
    }

    private static String valor(ValueSpecification v) {
      if (v == null || v.stringValue() == null) {
        return "";
      }
      return v.stringValue().trim();
    }

    private static String clase(Lifeline ll) {
      if (ll.getRepresents() instanceof Property) {
        Property p = (Property) ll.getRepresents();
        if (p.getType() instanceof Class) {
          return p.getType().getName();
        }
      }
      return "";
    }

    private static String extremo(Object event) {
      if (event instanceof MessageOccurrenceSpecification) {
        MessageOccurrenceSpecification mos = (MessageOccurrenceSpecification) event;
        if (!mos.getCovereds().isEmpty()) {
          Lifeline ll = mos.getCovereds().get(0);
          String clase = clase(ll);
          return clase.isEmpty() ? (ll.getName() == null ? "?" : ll.getName().trim()) : clase;
        }
      }
      return "?";
    }

    void imprimir() {
      System.out.println("Diagrama de secuencia: " + nombre);
      for (String ll : lineasDeVida) {
        System.out.println("---- Línea de vida: " + ll);
      }
      for (Mensaje m : mensajes) {
        String op = m.operacion == null ? "" : " [operación: " + m.operacion + "]";
        System.out.println("---- Mensaje (" + m.tipo + "): " + m.origen + " -> " + m.destino + " : " + m.texto + op);
      }
      for (Fragmento f : raices) {
        imprimir(f, "---- ");
      }
      for (String aviso : fragmentosMalAnidados()) {
        System.out.println("---- AVISO: " + aviso);
      }
    }

    /**
     * Fragmentos que en el diagrama se ven dentro de otro pero que en el modelo
     * no están anidados en él (normalmente porque se han dibujado encima en
     * lugar de soltarlos dentro). Devuelve un mensaje por cada uno.
     */
    public List<String> fragmentosMalAnidados() {
      List<String> avisos = new ArrayList<String>();
      for (Fragmento f : fragmentos) {
        // Contenedor visual: el fragmento más pequeño que lo contiene en el
        // diagrama
        Fragmento contenedor = null;
        for (Fragmento g : fragmentos) {
          if (g != f && g.contieneVisualmente(f) && (contenedor == null || contenedor.contieneVisualmente(g))) {
            contenedor = g;
          }
        }
        if (contenedor != null && f.padre != contenedor) {
          avisos.add("El fragmento " + f.descripcion() + " se ve dentro del fragmento " + contenedor.descripcion()
              + " en el diagrama, pero en el modelo no está anidado en él. Probablemente se ha dibujado encima en"
              + " lugar de dentro: en Visual Paradigm, arrastra el fragmento hasta el interior del otro (el"
              + " contenedor se resalta al soltarlo) y vuelve a exportar el modelo.");
        }
      }
      return avisos;
    }

    private void imprimir(Fragmento f, String prefijo) {
      System.out.println(prefijo + "Fragmento " + f.operador + " " + f.guardas + " cubre " + f.clasesCubiertas);
      for (Fragmento h : f.hijos) {
        imprimir(h, "--" + prefijo);
      }
    }

    public String getNombre() {
      return nombre;
    }

    /** Clases representadas por las líneas de vida. */
    public Set<String> getClases() {
      return clases;
    }

    /** Todos los fragmentos combinados (incluidos los anidados). */
    public List<Fragmento> getFragmentos() {
      return fragmentos;
    }

    /** Número de fragmentos combinados con el operador indicado. */
    public int numeroDeFragmentos(String operador) {
      int n = 0;
      for (Fragmento f : fragmentos) {
        if (f.operador.equals(operador)) {
          n++;
        }
      }
      return n;
    }

    /**
     * Indica si hay algún fragmento con el operador hijo anidado directamente en
     * uno con el operador padre.
     */
    public boolean hayAnidamiento(String padre, String hijo) {
      for (Fragmento f : fragmentos) {
        if (f.operador.equals(hijo) && f.padre != null && f.padre.operador.equals(padre)) {
          return true;
        }
      }
      return false;
    }

    /** Fragmentos sin condición de guarda en su primer operando. */
    public List<String> fragmentosSinGuarda() {
      List<String> sinGuarda = new ArrayList<String>();
      for (Fragmento f : fragmentos) {
        if (!f.primeraGuarda) {
          sinGuarda.add(f.operador);
        }
      }
      return sinGuarda;
    }

    /**
     * Indica si algún mensaje invoca la operación indicada, bien porque está
     * asociado a ella o bien porque su texto contiene una llamada a ella.
     */
    public boolean invoca(String operacion) {
      Pattern llamada = Pattern.compile("(^|[^A-Za-z0-9_])" + Pattern.quote(operacion) + "\\s*(\\(|$)");
      for (Mensaje m : mensajes) {
        if (operacion.equals(m.operacion) || llamada.matcher(m.texto.trim()).find()) {
          return true;
        }
      }
      return false;
    }

    /** Indica si hay algún mensaje de creación hacia la clase indicada. */
    public boolean crea(String clase) {
      for (Mensaje m : mensajes) {
        if (MessageSort.CREATE_MESSAGE_LITERAL.getLiteral().equals(m.tipo) && clase.equals(m.destino)) {
          return true;
        }
      }
      return false;
    }

    /** Clases de la lista que no están representadas por ninguna línea de vida. */
    public List<String> clasesQueFaltan(String... esperadas) {
      List<String> faltan = new ArrayList<String>();
      for (String c : esperadas) {
        if (!clases.contains(c)) {
          faltan.add(c);
        }
      }
      return faltan;
    }

    /** Operaciones de la lista que no invoca ningún mensaje. */
    public List<String> operacionesQueFaltan(String... esperadas) {
      List<String> faltan = new ArrayList<String>();
      for (String op : esperadas) {
        if (!invoca(op)) {
          faltan.add(op);
        }
      }
      return faltan;
    }

    /**
     * Primer fragmento (el más externo) con el operador indicado, o null si no
     * hay ninguno.
     */
    public Fragmento fragmentoPrincipal(String operador) {
      for (Fragmento f : fragmentos) {
        if (f.operador.equals(operador)) {
          return f;
        }
      }
      return null;
    }

    /**
     * Clases de la lista que no abarca el fragmento principal con el operador
     * indicado.
     */
    public List<String> clasesNoCubiertas(String operador, String... esperadas) {
      Fragmento f = fragmentoPrincipal(operador);
      List<String> faltan = new ArrayList<String>();
      for (String c : esperadas) {
        if (f == null || !f.clasesCubiertas.contains(c)) {
          faltan.add(c);
        }
      }
      return faltan;
    }
  }

  // ---------------------------------------------------------------------------
  // Corrección del XMI exportado por Visual Paradigm
  // ---------------------------------------------------------------------------

  /**
   * Corrige el XMI y devuelve la posición en el diagrama (x, y, ancho, alto) de
   * cada fragmento combinado, indexada por su id.
   */
  private static Map<String, int[]> corregirXMI(File origen, File destino) throws Exception {

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.parse(origen);

    Map<String, Element> porId = new HashMap<String, Element>();
    NodeList todos = doc.getElementsByTagName("*");
    for (int i = 0; i < todos.getLength(); i++) {
      Element e = (Element) todos.item(i);
      String id = e.getAttribute("xmi:id");
      if (!id.isEmpty()) {
        porId.put(id, e);
      }
    }

    List<Element> renombrar = new ArrayList<Element>();
    Map<String, int[]> geometrias = new HashMap<String, int[]>();

    for (int i = 0; i < todos.getLength(); i++) {
      Element e = (Element) todos.item(i);
      String tipo = e.getAttribute("xmi:type");

      // Posición de los fragmentos combinados en el diagrama
      if ("CombinedFragment".equals(e.getAttribute("preferredShapeType")) && e.hasAttribute("subject")
          && !geometrias.containsKey(e.getAttribute("subject"))) {
        String[] g = e.getAttribute("geometry").split(",");
        if (g.length == 4) {
          try {
            int[] rect = new int[4];
            for (int k = 0; k < 4; k++) {
              rect[k] = (int) Math.round(Double.parseDouble(g[k].trim()));
            }
            geometrias.put(e.getAttribute("subject"), rect);
          } catch (NumberFormatException ex) {
            // Se ignora la geometría si no tiene el formato esperado
          }
        }
      }

      // Atributos que impiden cargar el modelo
      if ("false".equals(e.getAttribute("isOrdered"))) {
        e.removeAttribute("isOrdered");
      }
      if ("true".equals(e.getAttribute("isUnique"))) {
        e.removeAttribute("isUnique");
      }

      // Tipos que apuntan a literales de enumeraciones
      if (e.hasAttribute("type")) {
        Element ref = porId.get(e.getAttribute("type"));
        if (ref != null && "uml:EnumerationLiteral".equals(ref.getAttribute("xmi:type"))) {
          e.removeAttribute("type");
        }
      }

      // Fragmentos combinados anidados
      if ("packagedElement".equals(e.getTagName()) && "uml:CombinedFragment".equals(tipo)) {
        Node padre = e.getParentNode();
        if (padre instanceof Element && "operand".equals(((Element) padre).getTagName())) {
          renombrar.add(e);
        }
      }

      // Operación invocada por cada mensaje
      if ("uml:Message".equals(tipo) && !e.hasAttribute("signature")) {
        Element envio = porId.get(e.getAttribute("sendEvent"));
        Element evento = envio == null ? null : porId.get(envio.getAttribute("event"));
        if (evento != null && evento.hasAttribute("operation")
            && porId.containsKey(evento.getAttribute("operation"))) {
          e.setAttribute("signature", evento.getAttribute("operation"));
        }
      }
    }

    for (Element e : renombrar) {
      doc.renameNode(e, e.getNamespaceURI(), "fragment");
    }

    Transformer t = TransformerFactory.newInstance().newTransformer();
    t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    t.transform(new DOMSource(doc), new StreamResult(destino));

    return geometrias;
  }

}
