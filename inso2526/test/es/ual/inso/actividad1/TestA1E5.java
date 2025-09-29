package es.ual.inso.actividad1;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.DirectedRelationship;
import org.eclipse.uml2.uml.Extend;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Include;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Relationship;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.UseCase;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestA1E5 {

  private static ResourceSet resSet;
  private static List<UseCase> useCaseList;
  private static List<Actor> actorList;
  private static List<Generalization> generalizationUCList;
  private static List<Generalization> generalizationActorList;
  private static List<Include> includeList;
  private static List<Extend> extendList;
  private static List<Association> associationList;

  private static int nactor = 5;
  private static int nusecase = 21;
  private static int ngenusecase = 9;
  private static int ngenactor = 3;
  private static int ninclude = 6;
  private static int nextend = 4;
  private static int nassociation = 10;
  private static boolean shared = false;


  private static String modelName = "A1E5.uml";
  private static String path = "../../main/A1E5/";

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

    loadModel();

  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  private static void loadModel() throws IOException {

    System.out.println("Cargando modelo DCU...");

    useCaseList = new ArrayList<UseCase>();
    actorList = new ArrayList<Actor>();
    generalizationUCList = new ArrayList<Generalization>();
    generalizationActorList = new ArrayList<Generalization>();
    includeList = new ArrayList<Include>();
    extendList = new ArrayList<Extend>();
    associationList = new ArrayList<Association>();

    Resource resource = null;

    try {
      resource = resSet.getResource(URI.createURI(path + modelName), true);
      // System.out.println("Resource: " + resource.getContents().get(0));
    } catch (Exception e) {
      System.out.println("Ha ocurrido un error al cargar el modelo en " + path + modelName);
      throw e;
    }

    Model model = (Model) resource.getContents().get(0);
    System.out.println("Nombre del diagrama: " + model.getName());
    System.out.println("Número de elementos: " + model.getPackagedElements().size());

    System.out.println("Contenido del modelo: ");
    for (PackageableElement pe : model.getPackagedElements()) {
      if (pe instanceof UseCase) {
        System.out.println("-- UseCase: " + pe.getName());
        useCaseList.add((UseCase) pe);
        for (Relationship r : pe.getRelationships()) {
          if (r instanceof Association) {
            String relatedActor = null;
            if (r.getRelatedElements().size() == 2) {
              if (r.getRelatedElements().get(0) instanceof Actor) {
                relatedActor = ((Actor) r.getRelatedElements().get(0)).getName();
              } else if (r.getRelatedElements().get(1) instanceof Actor) {
                relatedActor = ((Actor) r.getRelatedElements().get(1)).getName();
              }
            }
            System.out.println("---- Association: " + relatedActor);
          }
        }
        for (DirectedRelationship r : pe.getSourceDirectedRelationships()) {
          // System.out.println("Size: " + r.getTargets().size());
          // System.out.println("(0): " + r.getTargets().get(0));
          String targetName = ((PackageableElement) r.getTargets().get(0)).getName();
          if (r instanceof Include) {
            System.out.println("---- Include: " + targetName);
            includeList.add((Include) r);
          } else if (r instanceof Extend) {
            System.out.println("---- Extend: " + targetName);
            extendList.add((Extend) r);
          } else if (r instanceof Generalization) {
            System.out.println("---- Generalization: " + targetName);
            generalizationUCList.add((Generalization) r);
          }
        }

      } else if (pe instanceof Actor) {
        System.out.println("-- Actor: " + pe.getName());
        actorList.add((Actor) pe);
        for (Relationship r : pe.getRelationships()) {
          if (r instanceof Association) {
            String relatedUseCase = null;
            if (r.getRelatedElements().size() == 2) {
              if (r.getRelatedElements().get(0) instanceof UseCase) {
                relatedUseCase = ((UseCase) r.getRelatedElements().get(0)).getName();
              } else if (r.getRelatedElements().get(1) instanceof UseCase) {
                relatedUseCase = ((UseCase) r.getRelatedElements().get(1)).getName();
              }
            }
            System.out.println("---- Association: " + relatedUseCase);
            associationList.add((Association) r);
          }
        }
        for (DirectedRelationship r : pe.getSourceDirectedRelationships()) {
          // System.out.println("Size: " + r.getTargets().size());
          // System.out.println("(0): " + r.getTargets().get(0));
          String targetName = ((PackageableElement) r.getTargets().get(0)).getName();
          if (r instanceof Include) {
            System.out.println("---- Include: " + targetName);
          } else if (r instanceof Extend) {
            System.out.println("---- Extend: " + targetName);
          } else if (r instanceof Generalization) {
            System.out.println("---- Generalization: " + targetName);
            generalizationActorList.add((Generalization) r);
          }
        }
      }
    }
  }

  @Test
  public void testNumeroDeActores() {

    assertFalse("Revisar el número de actores. Es posible que sobren actores.", actorList.size() > nactor);

    assertFalse("Revisar el número de actores. Es posible que falten actores.", actorList.size() < nactor);
  }

  @Test
  public void testNumeroDeCasosDeUso() {

    assertFalse("Revisar los casos de uso. Es posible sobren casos de uso.", useCaseList.size() > nusecase);

    assertFalse("Revisar los casos de uso. Es posible falten casos de uso.", useCaseList.size() < nusecase);
  }

  @Test
  public void testNumeroDeGeneralizacionesCasosDeUso() throws IOException {

    assertFalse("Revisar la generalización de casos de uso. Es posible que sobren generalizaciones.",
        generalizationUCList.size() > ngenusecase);

    assertFalse("Revisar la generalización de casos de uso. Es posible que falten generalizaciones.",
        generalizationUCList.size() < ngenusecase);
  }

  @Test
  public void testNumeroDeGeneralizacioneActores() throws IOException {

    assertFalse("Revisar la generalización de actores. Es posible que sobren generalizaciones.",
        generalizationActorList.size() > ngenactor);

    assertFalse("Revisar la generalización de actores. Es posible que falten generalizaciones.",
        generalizationActorList.size() < ngenactor);
  }

  @Test
  public void testNumeroDeInclude() throws IOException {

    assertFalse("Revisar las relaciones de tipo <<Include>>. Es posible que sobren relaciones de este tipo.",
        includeList.size() > ninclude);

    assertFalse("Revisar las relaciones de tipo <<Include>>. Es posible que falten relaciones de este tipo.",
        includeList.size() < ninclude);
  }

  @Test
  public void testNumeroDeExtend() throws IOException {

    assertFalse("Revisar las relaciones de tipo <<Extend>>. Es posible que sobren relaciones de este tipo.",
        extendList.size() > nextend);

    assertFalse("Revisar las relaciones de tipo <<Extend>>. Es posible que falten relaciones de este tipo.",
        extendList.size() < nextend);
  }

  @Test
  public void testNumeroDeAssociation() throws IOException {

    assertFalse("Revisar las asociaciones entre actores y casos de uso. Es posible que sobren asociaciones.",
        associationList.size() > nassociation);

    assertFalse("Revisar las asociaciones entre actores y casos de uso. Es posible que falten asociaciones.",
        associationList.size() < nassociation);

  }

  @Test
  public void testCasosDeUsoConectados() throws IOException {

    boolean conectados = true;

    for (UseCase uc : useCaseList) {
      System.out.println(
          "[testCasosDeUsoConectados] Nombre CU: " + uc.getName() + " -- Relaciones: " + uc.getRelationships().size());
      if (uc.getRelationships().size() < 1) {
        conectados = false;
        break;
      }
    }

    assertTrue("Todos los casos de uso deben estar conectados con otros elementos del diagrama.", conectados);
  }

  @Test
  public void testAsociacionesEntreCasosDeUso() throws IOException {

    boolean existenAsociaciones = false;

    for (UseCase uc : useCaseList) {
      System.out.println("[testAsociacionesEntreCasosDeUso] Nombre CU: " + uc.getName());
      for (Relationship r : uc.getRelationships()) {
        if (r instanceof Association) {
          if (r.getRelatedElements().size() == 2) {
            if (r.getRelatedElements().get(0) instanceof UseCase && r.getRelatedElements().get(1) instanceof UseCase) {
              existenAsociaciones = true;
              break;
            }
          }
        }
      }
    }

    assertFalse("Dos casos de uso no deben estar conectados con una relación de tipo 'Association'.",
        existenAsociaciones);
  }

  @Test
  public void testCasoDeUsoCompartidoConAsociaciones() throws IOException {

    boolean cuCompartido = false;

    for (int i = 0; i < useCaseList.size() && cuCompartido == false; i++) {
      UseCase uc = useCaseList.get(i);
      System.out.println("[testCasoDeUsoCompartidoConAsociaciones] Nombre CU: " + uc.getName());
      int numeroAsociaciones = 0;
      for (int j = 0; j < uc.getRelationships().size() && cuCompartido == false; j++) {
        Relationship r = uc.getRelationships().get(j);
        if (r instanceof Association) {
          numeroAsociaciones++;
          if (numeroAsociaciones >= 2) {
            cuCompartido = true;
          }
        }
      }
    }
    
    if(shared == false) {

    assertFalse(
        "Para este ejercicio no debe haber casos de uso que estén compartidos con relaciones de tipo 'Association'.",
        cuCompartido);
    
    }
    
  }

}