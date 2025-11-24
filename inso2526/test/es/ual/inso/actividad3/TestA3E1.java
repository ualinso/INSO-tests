package es.ual.inso.actividad3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Collaboration;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestA3E1 {

  private static String modelName = "A3E1.uml";
  private static String path = "../../main/A3E1/";
  //private static String path = "C:\\git\\INSO-tests\\inso2526\\uml\\";

  private String testpackage = "actividad3ejercicio1";

  private static ResourceSet resSet;
  private static String packageName;

  private static Behavior obtenerPartidosDestacados;
  private static Vector<String> lld1;
  private static Vector<String> coveredLoopD1;

  private static Behavior generarEventos;
  private static Vector<String> lld2;
  private static Vector<String> coveredLoopD2;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    // System.out.println("Registering factory ...");
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
        new XMIResourceFactoryImpl());

    // System.out.println("Registering packages ...");
    resSet = new ResourceSetImpl();
    UMLResourcesUtil.init(resSet);
    resSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
    resSet.getPackageRegistry().put("http://schema.omg.org/spec/XMI/2.1", UMLPackage.eINSTANCE);
    resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION,
        UMLResource.Factory.INSTANCE);

    cleanModel();
    loadModel();

  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  private static void cleanModel() throws IOException {
    String fileContent = "";
    File f = new File(path + modelName);
    BufferedReader br = new BufferedReader(new FileReader(f));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = br.readLine()) != null) {
      sb.append(line);
      sb.append(System.lineSeparator());
    }
    fileContent = sb.toString();
    br.close();

    fileContent = fileContent.replaceAll("isOrdered=\"false\"", "");
    fileContent = fileContent.replaceAll("isUnique=\"true\"", "");

    // System.out.println(fileContent);

    BufferedWriter out = new BufferedWriter(new FileWriter(path + "_" + modelName));
    out.write(fileContent);
    out.close();

  }

  private static void loadModel() throws IOException {

//    File f = new File(path + "_" + modelName);
//    URI fileURI = URI.createFileURI(f.getAbsolutePath());
//    System.out.println(fileURI);
//    Resource resource = resSet.getResource(fileURI, true);

    Resource resource = resSet.getResource(URI.createURI(path + "_" + modelName), true);

    // System.out.println("Resource: " + resource.getContents().get(0));

    Model model = (Model) resource.getContents().get(0);
    System.out.println("Nombre del diagrama: " + model.getName());

    for (PackageableElement pe : model.getPackagedElements()) {
      // System.out.println(pe.getName() + " - " + pe.getClass());

      if (pe instanceof org.eclipse.uml2.uml.Package) {
        org.eclipse.uml2.uml.Package pack = (org.eclipse.uml2.uml.Package) pe;
        loadDCL(pack);
      }

      if (pe instanceof org.eclipse.uml2.uml.Collaboration) {
        Collaboration colab = (Collaboration) pe;
        for (Behavior ob : colab.getOwnedBehaviors()) {
          System.out.println("Nombre del diagrama de secuencia: " + ob.getName());
          String dseName = ob.getName();
          if (dseName.equalsIgnoreCase("obtenerPartidosDestacados")) {
            obtenerPartidosDestacados = ob;
            loadObtenerPartidosDestacados(ob);

          }

          if (dseName.equalsIgnoreCase("generarEventos")) {
            generarEventos = ob;
            loadGenerarEventos(ob);

          }
        }
      }
    }

  }

  private static void loadDCL(org.eclipse.uml2.uml.Package pack) {

    System.out.println("Cargando modelo DCL...");

    // org.eclipse.uml2.uml.Package pack = (org.eclipse.uml2.uml.Package)
    // model.getPackagedElements().get(0);
    System.out.println("Nombre del package: " + pack.getName());
    packageName = pack.getName();

  }

  private static void loadObtenerPartidosDestacados(org.eclipse.uml2.uml.Behavior dse) {

    lld1 = new Vector<String>();
    coveredLoopD1 = new Vector<String>();

    EList<Element> ownElements = dse.getOwnedElements();
    // EList<Property> allAtt = dse.getAllAttributes();

    /*
     * System.out.println("All attributes: " + allAtt.size()); for(Property prop :
     * allAtt) { System.out.println("Name: " + prop.getName());
     * System.out.println("Prop: " + prop); }
     */

    for (Element element : ownElements) {

      if (element instanceof Lifeline) {
        Lifeline ll = (Lifeline) element;
        // ll.getCoveredBys()
        String llName = ll.getName();
        // String llRepresents = ll.getRepresents().getName();
        // System.out.println("llRepresents: " + ll.getRepresents());
        // System.out.println(ll.getRepresents().getOwnedElements().size());
        // System.out.println("represents' att size: " +
        // ll.getInteraction().getAllAttributes().size());

        String llClass = "";

        if (ll.getRepresents() instanceof Property) {
          Property repProperty = (Property) ll.getRepresents();

          /*
           * System.out.println("Rep prop name: " + repProperty.getName());
           * System.out.println("Rep prop rel: " + repProperty.getRelationships());
           * System.out.println("Rep prop rel size: " +
           * repProperty.getRelationships().size()); System.out.println("Rep prop ow: " +
           * repProperty.getOwnedElements()); System.out.println("Rep prop ow size: " +
           * repProperty.getOwnedElements().size()); System.out.println("rep datatpye: " +
           * repProperty.getDatatype()); System.out.println("rep otherend: "
           * +repProperty.getOtherEnd()); System.out.println("rep opposite: "
           * +repProperty.getOpposite());
           * 
           * System.out.println("rep ends: " + repProperty.getEnds());
           * System.out.println("rep ends size: " + repProperty.getEnds().size());
           * System.out.println("rep qualifiers: " + repProperty.getQualifiers());
           * System.out.println("rep qualifiers size: " +
           * repProperty.getQualifiers().size());
           * 
           * System.out.println("rep type: " + repProperty.getType());
           */

          if (repProperty.getType() instanceof Class) {
            Class repClass = (Class) repProperty.getType();
            llClass = repClass.getName();
          }

        }
        System.out.println("------ Lifeline: [" + llName + " : " + llClass + "]");
        lld1.add(llClass);
      } else if (element instanceof Property) {
        // Property prop = (Property)element;
        // System.out.println("------ Property: " + prop.getName());
      } else if (element instanceof Message) {
        // Message msg = (Message)element;
        // System.out.println("Message name: " + msg.getName() + " -- Message signature:
        // " +msg.getSignature());

      } else if (element instanceof CombinedFragment) {

        CombinedFragment cb = (CombinedFragment) element;
        System.out.println("-Name: " + cb.getName());
        System.out.println("-Label: " + cb.getLabel());
        System.out.println("-Operator: " + cb.getInteractionOperator());
        for (Lifeline ll : cb.getCovereds()) {
          Property repProperty = (Property) ll.getRepresents();
          if (repProperty.getType() instanceof Class) {
            Class repClass = (Class) repProperty.getType();
            String llClass = repClass.getName();
            System.out.println("-Covered: " + llClass);
            coveredLoopD1.add(llClass);
          }
        }
//        for(Element e1 : cb.getOwnedElements()) {
//          //System.out.println("e1: " + e1);         
//          
//          for(Element e2 : e1.getOwnedElements()) {
//            System.out.println("e2: " + e2);
//          }
//          
//          for(PackageableElement pe : e1.getModel().getPackagedElements()) {
//            System.out.println("pe: " + pe);
//          }
//        }
//        for(org.eclipse.uml2.uml.Package p1 : cb.allOwningPackages()) {
//          System.out.println("ppp: " + p1);
//        }

      } else {
        // System.out.println("****** Element: " + element);
      }
    }
  }

  private static void loadGenerarEventos(org.eclipse.uml2.uml.Behavior dse) {

    lld2 = new Vector<String>();
    coveredLoopD2 = new Vector<String>();

    EList<Element> ownElements = dse.getOwnedElements();
    // EList<Property> allAtt = dse.getAllAttributes();

    /*
     * System.out.println("All attributes: " + allAtt.size()); for(Property prop :
     * allAtt) { System.out.println("Name: " + prop.getName());
     * System.out.println("Prop: " + prop); }
     */

    for (Element element : ownElements) {

      if (element instanceof Lifeline) {
        Lifeline ll = (Lifeline) element;
        // ll.getCoveredBys()
        String llName = ll.getName();
        // String llRepresents = ll.getRepresents().getName();
        // System.out.println("llRepresents: " + ll.getRepresents());
        // System.out.println(ll.getRepresents().getOwnedElements().size());
        // System.out.println("represents' att size: " +
        // ll.getInteraction().getAllAttributes().size());

        String llClass = "";

        if (ll.getRepresents() instanceof Property) {
          Property repProperty = (Property) ll.getRepresents();

          /*
           * System.out.println("Rep prop name: " + repProperty.getName());
           * System.out.println("Rep prop rel: " + repProperty.getRelationships());
           * System.out.println("Rep prop rel size: " +
           * repProperty.getRelationships().size()); System.out.println("Rep prop ow: " +
           * repProperty.getOwnedElements()); System.out.println("Rep prop ow size: " +
           * repProperty.getOwnedElements().size()); System.out.println("rep datatpye: " +
           * repProperty.getDatatype()); System.out.println("rep otherend: "
           * +repProperty.getOtherEnd()); System.out.println("rep opposite: "
           * +repProperty.getOpposite());
           * 
           * System.out.println("rep ends: " + repProperty.getEnds());
           * System.out.println("rep ends size: " + repProperty.getEnds().size());
           * System.out.println("rep qualifiers: " + repProperty.getQualifiers());
           * System.out.println("rep qualifiers size: " +
           * repProperty.getQualifiers().size());
           * 
           * System.out.println("rep type: " + repProperty.getType());
           */

          if (repProperty.getType() instanceof Class) {
            Class repClass = (Class) repProperty.getType();
            llClass = repClass.getName();
          }

        }
        System.out.println("------ Lifeline: [" + llName + " : " + llClass + "]");
        lld2.add(llClass);
      } else if (element instanceof Property) {
        // Property prop = (Property)element;
        // System.out.println("------ Property: " + prop.getName());
      } else if (element instanceof Message) {
        // Message msg = (Message)element;
        // System.out.println("Message name: " + msg.getName() + " -- Message signature:
        // " +msg.getSignature());

      } else if (element instanceof CombinedFragment) {

        CombinedFragment cb = (CombinedFragment) element;
        System.out.println("-Name: " + cb.getName());
        System.out.println("-Label: " + cb.getLabel());
        System.out.println("-Operator: " + cb.getInteractionOperator());
        for (Lifeline ll : cb.getCovereds()) {
          Property repProperty = (Property) ll.getRepresents();
          if (repProperty.getType() instanceof Class) {
            Class repClass = (Class) repProperty.getType();
            String llClass = repClass.getName();
            System.out.println("-Covered: " + llClass);
            coveredLoopD2.add(llClass);
          }
        }
//        for(Element e1 : cb.getOwnedElements()) {
//          //System.out.println("e1: " + e1);         
//          
//          for(Element e2 : e1.getOwnedElements()) {
//            System.out.println("e2: " + e2);
//          }
//          
//          for(PackageableElement pe : e1.getModel().getPackagedElements()) {
//            System.out.println("pe: " + pe);
//          }
//        }
//        for(org.eclipse.uml2.uml.Package p1 : cb.allOwningPackages()) {
//          System.out.println("ppp: " + p1);
//        }

      } else {
        // System.out.println("****** Element: " + element);
      }
    }
  }

  @Test
  public void testNombreDePackage() {
    System.out.println("El nombre del package es: " + packageName);
    assertEquals("El package debe llamarse " + testpackage, testpackage, packageName);
  }

  @Test
  public void testDiagramasDeSecuencia() {

    assertNotEquals("No existe el diagrama 'obtenerPartidosDestacados'.", obtenerPartidosDestacados, null);
    assertNotEquals("No existe el diagrama 'generarEventos'.", generarEventos, null);

  }

  @Test
  public void testLifeLinesObtenerPartidosDestacados() {
    if (obtenerPartidosDestacados != null) {

      String dseName = "obtenerPartidosDestacados";
      String ll1Name = "Filtro";
      String ll2Name = "Estadisticas";
      String ll3Name = "Partido";

      boolean ll1 = lld1.contains(ll1Name);
      boolean ll2 = lld1.contains(ll2Name);
      boolean ll3 = lld1.contains(ll3Name);

      assertTrue("La clase " + ll1Name + " no está bien representada en '" + dseName + "'.", ll1);
      assertTrue("La clase " + ll2Name + " no está bien representada en '" + dseName + "'.", ll2);
      assertTrue("La clase " + ll3Name + " no está bien representada en '" + dseName + "'.", ll3);
    }
  }

  @Test
  public void testLifeLinesGenerarEventos() {
    if (generarEventos != null) {

      String dseName = "generarEventos";
      String ll1Name = "Partido";
      String ll2Name = "Estadisticas";

      boolean ll1 = lld2.contains(ll1Name);
      boolean ll2 = lld2.contains(ll2Name);

      assertTrue("La clase " + ll1Name + " no está bien representada en '" + dseName + "'.", ll1);
      assertTrue("La clase " + ll2Name + " no está bien representada en '" + dseName + "'.", ll2);
    }
  }

  @Test
  public void testLoop1ObtenerPartidosDestacados() {
    if (obtenerPartidosDestacados != null) {
      
      String dseName = "obtenerPartidosDestacados";
      String ll1Name = "Filtro";
      String ll2Name = "Estadisticas";
      String ll3Name = "Partido";
      
      boolean ll1 = coveredLoopD1.contains(ll1Name);
      boolean ll2 = coveredLoopD1.contains(ll2Name);
      boolean ll3 = coveredLoopD1.contains(ll3Name);
      
      assertTrue("El bucle principal no abarca correctamente la clase " + ll1Name + " en '" + dseName + "'.", ll1);
      assertTrue("El bucle principal no abarca correctamente la clase " + ll2Name + " en '" + dseName + "'.", ll2);
      assertTrue("El bucle principal no abarca correctamente la clase " + ll3Name + " en '" + dseName + "'.", ll3);
    }
  }

}