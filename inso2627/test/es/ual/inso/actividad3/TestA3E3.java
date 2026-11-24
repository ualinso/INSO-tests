package es.ual.inso.actividad3;

import static es.ual.inso.actividad3.Comprobaciones.*;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.ual.inso.RequiredFiles;
import es.ual.inso.actividad3.ModeloSecuencia.Diagrama;

public class TestA3E3 {

  private String testpackage = "actividad3ejercicio3";

  // Diagrama 1: consumirReactivos
  private static String[] d1Nombres = { "consumirReactivos" };
  private String[] d1Clases = { "Artesano", "Reactivo" };
  private String[] d1Operaciones = { "consumirReactivos", "getTotalReactivos", "getCodigo", "retirar",
      "decrementarCantidad" };
  private int d1nloop = 1;
  private int d1nalt = 0;
  private int d1nopt = 1;
  private String[] d1ClasesLoop = { "Artesano", "Reactivo" };

  // Diagrama 2: fabricarObjeto
  private static String[] d2Nombres = { "fabricarObjeto" };
  private String[] d2Clases = { "Artesano", "Receta", "Objeto" };
  private String[] d2Operaciones = { "fabricarObjeto", "buscarReceta", "getTotalRecetas", "getCodigo",
      "getNivelHabilidad", "comprobarNivel", "getCodigoReactivo", "getUnidadesReactivo", "consumirReactivos",
      "getCalidad", "establecerDurabilidad", "calcularPuntos", "subirHabilidad", "getNivelRequerido",
      "establecerObjetivo" };
  private String d2Crea = "Objeto";
  private int d2nloop = 1;
  private int d2nalt = 3;
  private int d2nopt = 3;
  private String[] d2ClasesLoop = { "Artesano", "Receta" };

  private static String modelName = "A3E3.uml";
  private static String path = "../../main/A3E3/";

  private static String problem;

  private static ModeloSecuencia modelo;
  private static Diagrama d1;
  private static Diagrama d2;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    problem = RequiredFiles.check("A3E3", TestA3E3.class, path + modelName, path + "vpproject/A3E3.vpp");
    if (problem != null) {
      return;
    }

    try {
      modelo = ModeloSecuencia.cargar(path, modelName);
      d1 = modelo.getDiagrama(d1Nombres);
      d2 = modelo.getDiagrama(d2Nombres);
    } catch (Exception e) {
      problem = RequiredFiles.loadError("A3E3", TestA3E3.class, path + modelName, e);
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
  }

  @Test
  public void testFragmentosDiagrama1() {
    fragmentos(d1, d1Nombres[0], d1nloop, d1nalt, d1nopt);
  }

  @Test
  public void testAnidamientoDiagrama1() {
    anidamiento(d1, d1Nombres[0], "loop", "opt");
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

  // Diagrama 2

  @Test
  public void testLineasDeVidaDiagrama2() {
    lineasDeVida(d2, d2Nombres[0], d2Clases);
  }

  @Test
  public void testMensajesDiagrama2() {
    mensajes(d2, d2Nombres[0], d2Operaciones);
    creacion(d2, d2Nombres[0], d2Crea);
  }

  @Test
  public void testFragmentosDiagrama2() {
    fragmentos(d2, d2Nombres[0], d2nloop, d2nalt, d2nopt);
  }

  @Test
  public void testAnidamientoDiagrama2() {
    anidamiento(d2, d2Nombres[0], "loop", "opt");
    anidamiento(d2, d2Nombres[0], "opt", "alt");
    anidamiento(d2, d2Nombres[0], "alt", "opt");
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
