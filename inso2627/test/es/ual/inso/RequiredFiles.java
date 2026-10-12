package es.ual.inso;

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class RequiredFiles {

  /**
   * Fallo con el que se marcan los tests de un ejercicio que no se ha podido
   * ejecutar (faltan archivos o no se puede cargar el modelo).
   */
  public static class NotExecutedError extends AssertionError {

    private static final long serialVersionUID = 1L;

    public NotExecutedError(String message) {
      super(message);
    }
  }

  private static final String STUDENT_ROOT = "../../main";

  /**
   * Comprueba que existen los archivos necesarios para ejecutar los tests de un
   * ejercicio.
   *
   * @param exercise  nombre del ejercicio (p. ej. "A1E1")
   * @param testClass clase que contiene los tests del ejercicio
   * @param files     rutas de los archivos, relativas al directorio de ejecución
   * @return el mensaje con los archivos que faltan, o null si están todos
   */
  public static String check(String exercise, Class<?> testClass, String... files) {

    List<String> missing = new ArrayList<String>();
    String userDirectory = System.getProperty("user.dir");

    for (String file : files) {
      if (!Paths.get(userDirectory, file).toFile().exists()) {
        missing.add(studentPath(file));
      }
    }

    if (missing.isEmpty()) {
      return null;
    }

    return "No se han podido ejecutar los tests de " + exercise + " porque faltan los archivos " + missing
        + ". Una vez que se suban dichos archivos, se ejecutarán " + countTests(testClass) + " tests.";
  }

  /**
   * Devuelve el mensaje para un ejercicio cuyo modelo existe pero no se ha
   * podido cargar.
   */
  public static String loadError(String exercise, Class<?> testClass, String file, Exception e) {

    System.out.println("Error al cargar el modelo: " + e);

    return "No se han podido ejecutar los tests de " + exercise + " porque no se ha podido cargar el modelo "
        + studentPath(file) + ". Comprueba que el archivo es un modelo UML válido. Una vez corregido, se ejecutarán "
        + countTests(testClass) + " tests.";
  }

  /**
   * Hace fallar el test en curso si el ejercicio no se ha podido ejecutar.
   *
   * @param problem mensaje devuelto por check o loadError, o null si no hay
   *                problema
   */
  public static void assertExecutable(String problem) {

    if (problem != null) {
      throw new NotExecutedError(problem);
    }
  }

  private static String studentPath(String file) {
    return file.startsWith(STUDENT_ROOT) ? file.substring(STUDENT_ROOT.length()) : file;
  }

  private static int countTests(Class<?> testClass) {

    int ntests = 0;

    for (Method m : testClass.getMethods()) {
      if (m.isAnnotationPresent(Test.class) && !m.isAnnotationPresent(Ignore.class)) {
        ntests++;
      }
    }

    return ntests;
  }

}
