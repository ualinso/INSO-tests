package es.ual.inso;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Genera un resumen en Markdown de los resultados agrupado por ejercicio (una
 * clase de test por ejercicio). La salida por consola de los tests se captura
 * y se incluye en el resumen como sección desplegable.
 */
public class SummaryListener extends RunListener {

  private static final String SUMMARY_FILE = "target/resumen-tests.md";

  private static class Exercise {
    String name;
    String activity;
    int run = 0;
    int failed = 0;
    boolean notExecuted = false;
    boolean classFailure = false;
    Set<String> messages = new LinkedHashSet<String>();
    StringBuilder log = new StringBuilder();

    int passed() {
      return run - failed;
    }

    String status() {
      if (notExecuted) {
        return "⚠️ No ejecutado";
      }
      if (failed > 0 || classFailure) {
        return "❌ Con errores";
      }
      return "✅ Correcto";
    }
  }

  private Map<String, Exercise> exercises = new LinkedHashMap<String, Exercise>();
  private ByteArrayOutputStream captured = new ByteArrayOutputStream();
  private PrintStream originalOut;

  @Override
  public void testRunStarted(Description description) throws Exception {
    originalOut = System.out;
    System.setOut(new PrintStream(captured, true, "UTF-8"));
  }

  @Override
  public void testStarted(Description description) throws Exception {
    Exercise e = exercise(description);
    e.run++;
    moveCapturedOutputTo(e);
  }

  @Override
  public void testFailure(Failure failure) throws Exception {
    Exercise e = exercise(failure.getDescription());

    if (failure.getDescription().getMethodName() == null) {
      // Fallo en @BeforeClass o @AfterClass: no corresponde a ningún test
      e.classFailure = true;
      moveCapturedOutputTo(e);
    } else {
      e.failed++;
    }

    if (failure.getException() instanceof RequiredFiles.NotExecutedError) {
      e.notExecuted = true;
    }

    e.messages.add(failure.getMessage() != null ? failure.getMessage() : failure.getException().toString());
  }

  @Override
  public void testFinished(Description description) throws Exception {
    moveCapturedOutputTo(exercise(description));
  }

  @Override
  public void testRunFinished(Result result) throws Exception {
    System.setOut(originalOut);

    for (Exercise e : exercises.values()) {
      System.out.println(e.name + ": " + e.passed() + " de " + e.run + " tests superados");
    }

    Path summary = Paths.get(SUMMARY_FILE);
    Files.createDirectories(summary.getParent());
    Files.write(summary, buildSummary().getBytes(StandardCharsets.UTF_8));
  }

  private Exercise exercise(Description description) {
    String className = description.getClassName();
    Exercise e = exercises.get(className);

    if (e == null) {
      String[] parts = className.split("\\.");
      e = new Exercise();
      e.name = parts[parts.length - 1].replaceFirst("^Test", "");
      e.activity = parts.length > 1 ? parts[parts.length - 2].replaceFirst("^actividad(\\d+)$", "Actividad $1") : "";
      exercises.put(className, e);
    }

    return e;
  }

  private void moveCapturedOutputTo(Exercise e) throws Exception {
    e.log.append(captured.toString("UTF-8"));
    captured.reset();
  }

  private String buildSummary() {
    StringBuilder table = new StringBuilder();
    StringBuilder details = new StringBuilder();
    String activity = null;
    int run = 0;
    int passed = 0;

    for (Exercise e : exercises.values()) {
      run += e.run;
      passed += e.passed();

      if (!e.activity.equals(activity)) {
        activity = e.activity;
        table.append("\n### ").append(activity).append("\n\n");
        table.append("| Ejercicio | Resultado | Tests superados |\n|---|---|---|\n");
      }
      table.append("| ").append(e.name).append(" | ").append(e.status()).append(" | ").append(e.passed())
          .append(" de ").append(e.run).append(" |\n");

      details.append("\n### ").append(e.name).append(" — ").append(e.status()).append("\n\n");
      for (String message : e.messages) {
        details.append("- ").append(escape(message)).append("\n");
      }
      if (e.log.length() > 0) {
        details.append("\n<details>\n<summary>Salida de los tests de ").append(e.name)
            .append(" (contenido detectado en el modelo)</summary>\n\n```text\n").append(e.log)
            .append("```\n\n</details>\n");
      }
    }

    return "## Resultados de los tests\n\n**Total: " + passed + " de " + run + " tests superados**\n" + table
        + "\n## Detalle por ejercicio\n" + details;
  }

  private static String escape(String message) {
    return message.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }

}
