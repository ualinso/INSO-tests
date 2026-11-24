package es.ual.inso.actividad3;

import static es.ual.inso.actividad3.Comprobaciones.*;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.ual.inso.RequiredFiles;
import es.ual.inso.actividad3.ModeloSecuencia.Diagrama;

public class TestA3E2 {

  private String testpackage = "actividad3ejercicio2";

  // Diagrama 1: detectarTendencias
  private static String[] d1Nombres = { "detectarTendencias" };
  private String[] d1Clases = { "AnalizadorMercado", "Accion", "Noticia" };
  private String[] d1Operaciones = { "detectarTendencias", "getTotalAcciones", "detectarTendencia", "getTotalNoticias",
      "getSentimiento", "setTendencia" };
  private int d1nloop = 2;
  private int d1nalt = 3;
  private int d1nopt = 0;
  private String[] d1ClasesLoop = { "AnalizadorMercado", "Accion", "Noticia" };

  private static String modelName = "A3E2.uml";
  private static String path = "../../main/A3E2/";

  private static String problem;

  private static ModeloSecuencia modelo;
  private static Diagrama d1;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    problem = RequiredFiles.check("A3E2", TestA3E2.class, path + modelName, path + "vpproject/A3E2.vpp");
    if (problem != null) {
      return;
    }

    try {
      modelo = ModeloSecuencia.cargar(path, modelName);
      d1 = modelo.getDiagrama(d1Nombres);
    } catch (Exception e) {
      problem = RequiredFiles.loadError("A3E2", TestA3E2.class, path + modelName, e);
    }

  }

  @Before
  public void setUp() {
    RequiredFiles.assertExecutable(problem);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Test
  public void testNombreDePackage() {
    System.out.println("El nombre del package es: " + modelo.getPackageName());
    assertEquals("El package debe llamarse " + testpackage, testpackage, modelo.getPackageName());
  }

  @Test
  public void testDiagramasDeSecuencia() {
    existe(d1, d1Nombres[0]);
  }

  @Test
  public void testLineasDeVidaDiagrama1() {
    lineasDeVida(d1, d1Nombres[0], d1Clases);
  }

  @Test
  public void testMensajesDiagrama1() {
    mensajes(d1, d1Nombres[0], d1Operaciones);
  }

  @Test
  public void testFragmentosDiagrama1() {
    fragmentos(d1, d1Nombres[0], d1nloop, d1nalt, d1nopt);
  }

  @Test
  public void testAnidamientoDiagrama1() {
    anidamiento(d1, d1Nombres[0], "loop", "loop");
    anidamiento(d1, d1Nombres[0], "loop", "alt");
    anidamiento(d1, d1Nombres[0], "alt", "alt");
  }

  @Test
  public void testCoberturaLoopDiagrama1() {
    cobertura(d1, d1Nombres[0], "loop", d1ClasesLoop);
  }

  @Test
  public void testGuardasDiagrama1() {
    guardas(d1, d1Nombres[0]);
  }

  @Test
  public void testFragmentosBienAnidadosDiagrama1() {
    fragmentosBienAnidados(d1, d1Nombres[0]);
  }

}
