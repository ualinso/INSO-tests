package es.ual.inso.actividad3;

import static es.ual.inso.actividad3.Comprobaciones.*;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.ual.inso.RequiredFiles;
import es.ual.inso.actividad3.ModeloSecuencia.Diagrama;

public class TestA3E1 {

  private String testpackage = "actividad3ejercicio1";

  // Diagrama 1: depositarEnLocker
  private static String[] d1Nombres = { "depositarEnLocker" };
  private String[] d1Clases = { "Repartidor", "Locker", "Taquilla", "Envio", "CodigoRecogida" };
  private String[] d1Operaciones = { "depositarEnLocker", "obtenerTaquillaDisponible", "depositarEnvio", "setEstado",
      "registrarIncidencia" };
  private String d1Crea = "CodigoRecogida";
  private int d1nloop = 0;
  private int d1nalt = 1;
  private int d1nopt = 0;
  private String[] d1ClasesAlt = { "Repartidor", "Taquilla", "Envio" };

  // Diagrama 2: rotarPaquetes
  private static String[] d2Nombres = { "rotarPaquetes" };
  private String[] d2Clases = { "RobotDistribucion", "Oficina", "Estanteria", "Envio" };
  private String[] d2Operaciones = { "rotarPaquetes", "getTotalEstanterias", "getTotalEnvios", "setEstado",
      "retirarEnvio", "agregarEnvio" };
  private int d2nloop = 2;
  private int d2nalt = 1;
  private int d2nopt = 0;
  private String[] d2ClasesLoop = { "Oficina", "Estanteria", "Envio" };

  private static String modelName = "A3E1.uml";
  private static String path = "../../main/A3E1/";

  private static String problem;

  private static ModeloSecuencia modelo;
  private static Diagrama d1;
  private static Diagrama d2;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    problem = RequiredFiles.check("A3E1", TestA3E1.class, path + modelName, path + "vpproject/A3E1.vpp");
    if (problem != null) {
      return;
    }

    try {
      modelo = ModeloSecuencia.cargar(path, modelName);
      d1 = modelo.getDiagrama(d1Nombres);
      d2 = modelo.getDiagrama(d2Nombres);
    } catch (Exception e) {
      problem = RequiredFiles.loadError("A3E1", TestA3E1.class, path + modelName, e);
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
    existe(d2, d2Nombres[0]);
  }

  // Diagrama 1

  @Test
  public void testLineasDeVidaDiagrama1() {
    lineasDeVida(d1, d1Nombres[0], d1Clases);
  }

  @Test
  public void testMensajesDiagrama1() {
    mensajes(d1, d1Nombres[0], d1Operaciones);
    creacion(d1, d1Nombres[0], d1Crea);
  }

  @Test
  public void testFragmentosDiagrama1() {
    fragmentos(d1, d1Nombres[0], d1nloop, d1nalt, d1nopt);
  }

  @Test
  public void testCoberturaAltDiagrama1() {
    cobertura(d1, d1Nombres[0], "alt", d1ClasesAlt);
  }

  @Test
  public void testGuardasDiagrama1() {
    guardas(d1, d1Nombres[0]);
  }

  @Test
  public void testFragmentosBienAnidadosDiagrama1() {
    fragmentosBienAnidados(d1, d1Nombres[0]);
  }

  // Diagrama 2

  @Test
  public void testLineasDeVidaDiagrama2() {
    lineasDeVida(d2, d2Nombres[0], d2Clases);
  }

  @Test
  public void testMensajesDiagrama2() {
    mensajes(d2, d2Nombres[0], d2Operaciones);
  }

  @Test
  public void testFragmentosDiagrama2() {
    fragmentos(d2, d2Nombres[0], d2nloop, d2nalt, d2nopt);
  }

  @Test
  public void testAnidamientoDiagrama2() {
    anidamiento(d2, d2Nombres[0], "loop", "loop");
    anidamiento(d2, d2Nombres[0], "loop", "alt");
  }

  @Test
  public void testCoberturaLoopDiagrama2() {
    cobertura(d2, d2Nombres[0], "loop", d2ClasesLoop);
  }

  @Test
  public void testGuardasDiagrama2() {
    guardas(d2, d2Nombres[0]);
  }

  @Test
  public void testFragmentosBienAnidadosDiagrama2() {
    fragmentosBienAnidados(d2, d2Nombres[0]);
  }

}
