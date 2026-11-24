package es.ual.inso.actividad3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import es.ual.inso.actividad3.ModeloSecuencia.Diagrama;

/**
 * Comprobaciones comunes a los tests de los diagramas de secuencia de la
 * actividad 3.
 */
public class Comprobaciones {

  public static void existe(Diagrama d, String nombre) {
    assertNotNull("No existe el diagrama de secuencia '" + nombre + "'.", d);
  }

  public static void lineasDeVida(Diagrama d, String nombre, String... clases) {
    existe(d, nombre);
    System.out.println("Clases representadas en '" + nombre + "': " + d.getClases());
    List<String> faltan = d.clasesQueFaltan(clases);
    assertTrue("En '" + nombre + "' faltan líneas de vida que representen a las clases " + faltan + ".",
        faltan.isEmpty());
  }

  public static void mensajes(Diagrama d, String nombre, String... operaciones) {
    existe(d, nombre);
    List<String> faltan = d.operacionesQueFaltan(operaciones);
    assertTrue("En '" + nombre + "' no se invocan las operaciones " + faltan + ".", faltan.isEmpty());
  }

  public static void creacion(Diagrama d, String nombre, String clase) {
    existe(d, nombre);
    assertTrue("En '" + nombre + "' falta el mensaje de creación de la clase " + clase + ".", d.crea(clase));
  }

  public static void fragmentos(Diagrama d, String nombre, int nloop, int nalt, int nopt) {
    existe(d, nombre);
    System.out.println("Fragmentos en '" + nombre + "': loop=" + d.numeroDeFragmentos("loop") + ", alt="
        + d.numeroDeFragmentos("alt") + ", opt=" + d.numeroDeFragmentos("opt"));
    assertEquals("El número recomendado de fragmentos loop en '" + nombre + "' es " + nloop, nloop,
        d.numeroDeFragmentos("loop"));
    assertEquals("El número recomendado de fragmentos alt en '" + nombre + "' es " + nalt, nalt,
        d.numeroDeFragmentos("alt"));
    assertEquals("El número recomendado de fragmentos opt en '" + nombre + "' es " + nopt, nopt,
        d.numeroDeFragmentos("opt"));
  }

  public static void anidamiento(Diagrama d, String nombre, String padre, String hijo) {
    existe(d, nombre);
    String otro = padre.equals(hijo) ? "otro" : "un";
    assertTrue("En '" + nombre + "' debe haber un fragmento " + hijo + " dentro de " + otro + " " + padre + "."
        + pista(d), d.hayAnidamiento(padre, hijo));
  }

  public static void fragmentosBienAnidados(Diagrama d, String nombre) {
    existe(d, nombre);
    List<String> avisos = d.fragmentosMalAnidados();
    assertTrue("En '" + nombre + "': " + String.join(" ", avisos), avisos.isEmpty());
  }

  /**
   * Mensaje orientativo para añadir a un fallo cuando hay fragmentos que se ven
   * anidados en el diagrama pero no lo están en el modelo.
   */
  private static String pista(Diagrama d) {
    List<String> avisos = d.fragmentosMalAnidados();
    return avisos.isEmpty() ? "" : " " + String.join(" ", avisos);
  }

  public static void cobertura(Diagrama d, String nombre, String operador, String... clases) {
    existe(d, nombre);
    assertNotNull("En '" + nombre + "' no hay ningún fragmento " + operador + ".", d.fragmentoPrincipal(operador));
    List<String> faltan = d.clasesNoCubiertas(operador, clases);
    assertTrue("En '" + nombre + "' el fragmento " + operador + " principal no abarca correctamente las clases "
        + faltan + "." + pista(d), faltan.isEmpty());
  }

  public static void guardas(Diagrama d, String nombre) {
    existe(d, nombre);
    List<String> sinGuarda = d.fragmentosSinGuarda();
    assertTrue("En '" + nombre + "' hay fragmentos sin condición de guarda: " + sinGuarda + ".",
        sinGuarda.isEmpty());
  }

}
