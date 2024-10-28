package es.ual.inso.actividad2;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestA2E1 {

	private String testpackage = "actividad2ejercicio1";
	private int testncl = 7;
	private int testncla = 1;
	private int testngener = 3;
	private int testnassoc = 5;
	private int testnattr = 6;
	private int testnop = 5;
	private int testnenum = 0;

	private static String modelName = "A2E1.uml";
	private static String path = "../../main/A2E1/";

	private static ResourceSet resSet;
	private static String packageName;
	private static List<Class> classList;
	private static List<Class> abstractClassList;
	private static List<Association> associationList;
	private static int numberOfMultiSuperTypes;
	private static int numberOfSuperTypes;
	private static int numberOfAttributtes;
	// private static int numberOfAttributtesFromAssociations;
	private static int numberOfOperations;
	private static int numberOfEnumerations;
	
	
	

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

    System.out.println("Cargando modelo DCL...");

    Resource resource = resSet.getResource(URI.createURI(path + "_" + modelName), true);
    // System.out.println("Resource: " + resource.getContents().get(0));

    Model model = (Model) resource.getContents().get(0);
    System.out.println("Nombre del diagrama: " + model.getName());

    org.eclipse.uml2.uml.Package pack = (org.eclipse.uml2.uml.Package) model.getPackagedElements().get(0);
    System.out.println("Nombre del package: " + pack.getName());
    
    if(pack.getName().equalsIgnoreCase("candidate items")) {
    	pack = (org.eclipse.uml2.uml.Package) model.getPackagedElements().get(1);
    	System.out.println("Nombre del package: " + pack.getName());
    }
    
    packageName = pack.getName();

    classList = new ArrayList<Class>();
    abstractClassList = new ArrayList<Class>();
    associationList = new ArrayList<Association>();
    numberOfMultiSuperTypes = 0;
    numberOfSuperTypes = 0;
    numberOfAttributtes = 0;
    // numberOfAttributtesFromAssociations = 0;
    numberOfOperations = 0;

    System.out.println("Contenido del modelo: ");
    for (PackageableElement pe : pack.getPackagedElements()) {
      if (pe instanceof Class) {
        Class c = (Class) pe;
        classList.add(c);
        System.out.println("---- Class: " + c.getName());

        // Abstract
        if (c.isAbstract()) {
          System.out.println("------ [abstract]");
          abstractClassList.add(c);
        }

        // Attributes (including associations)
        for (Property attr : c.getAttributes()) {
          if (attr.getAssociation() == null) {
            System.out.println("------ Attribute: " + attr.getName());
            numberOfAttributtes++;
          } else {
            String targetMult = "(" + attr.getLower() + "," + attr.getUpper() + ")";
            System.out.println(
                "------ Association role: " + attr.getName() + " -- Multiplicity: " + targetMult);
            // numberOfAttributtesFromAssociations++;
          }
        }

        // Operations
        for (Operation op : c.getOperations()) {
          System.out.println("------ Operation: " + op.getName());
          numberOfOperations++;
        }

        // Generalization
        EList<Class> superClasses = c.getSuperClasses();
        // System.out.println(superClasses.size());
        if (superClasses.size() > 0) {
          if (superClasses.size() > 1) {
            numberOfMultiSuperTypes++;
          } else {
            System.out.println("------ SuperClass: " + superClasses.get(0).getName());
            numberOfSuperTypes++;
          }
        }

      } else if (pe instanceof Association) {
        Association a = (Association) pe;

        if (a.getRelatedElements().size() == 2) {
          if (a.getRelatedElements().get(0) instanceof Class
              && a.getRelatedElements().get(1) instanceof Class) {

            Class sourceClass = (Class) a.getRelatedElements().get(0);
            Class targetClass = (Class) a.getRelatedElements().get(1);

            EList<Property> pList = a.getMemberEnds();
            String sourceMult = "(" + pList.get(0).getLower() + "," + pList.get(0).getUpper() + ")";
            String targetMult = "(" + pList.get(1).getLower() + "," + pList.get(1).getUpper() + ")";
            /*
             * System.out.println("pList size: " + pList.size());
             * for(Property property : pList) {
             * System.out.println("------" + property.getLower());
             * System.out.println("------" + property.getUpper()); }
             */
            associationList.add(a);
            System.out.println("---- Association: " + sourceClass.getName() + " " + sourceMult + " -- "
                + targetMult + " " + targetClass.getName());
          }
        }

      } else if (pe instanceof Enumeration) {
        Enumeration e = (Enumeration) pe;
        numberOfEnumerations++;
        System.out.println("---- Enumeration: " + e.getName());
      }
    }

  }

	@Test
	public void testNombreDePackage() {
		System.out.println("El nombre del package es: " + packageName);
		assertEquals("El package debe llamarse " + testpackage, testpackage, packageName);
	}

	@Test
	public void testNumeroDeClases() {

		System.out.println("El número de clases del modelo es: " + classList.size());
		
		assertFalse("Revisar el número de clases. Es posible que sobren clases.", classList.size() > testncl);
    assertFalse("Revisar el número de clases. Es posible que falten clases.", classList.size() < testncl);
		
	}

	@Test
	public void testNumeroDeClasesAbstractas() {

		System.out.println("El número de clases abstractas del modelo es: " + abstractClassList.size());
		assertEquals("El número recomendado de clases abstractas es " + testncla, testncla, abstractClassList.size());
	}

	@Test
	public void testNumeroDeGeneralizaciones() {

		System.out.println("El número de generalizaciones del modelo es: " + numberOfSuperTypes);
		assertEquals("El número recomendado de generalizaciones es " + testngener, testngener, numberOfSuperTypes);
	}

	@Test
	public void testHerenciaMultiple() {

		System.out.println("El número de herencias múltiples es: " + numberOfMultiSuperTypes);
		//assertEquals("El número recomendado de generalizaciones es " + testnmgener, testnmgener,
		//		numberOfMultiSuperTypes);
		
		assertFalse("No debe haber herencia múltiple.", numberOfMultiSuperTypes > 0);

	}

	@Test
	public void testNumeroDeAsociaciones() {

		System.out.println("El número de relaciones de asociación es: " + associationList.size());
		assertEquals(
				"El número recomendado de asociaciones (incluyendo asociaciones normales, composiciones y agregraciones) es "
						+ testnassoc,
				testnassoc, associationList.size());
	}

	@Test
	public void testNumeroDeAtributos() {

		System.out.println("El número de atributos del modelo es: " + numberOfAttributtes);
		assertEquals("El número recomendado de atributos (sin incluir los atributos derivados de los roles de las asociaciones) es " + testnattr, testnattr, numberOfAttributtes);
	}

	@Test
	public void testNumeroDeOperaciones() {

		System.out.println("El número de operaciones del modelo es: " + numberOfOperations);
		assertEquals("El número recomendado de operaciones es " + testnop, testnop, numberOfOperations);
	}
	
	 @Test
	  public void testNumeroDeTiposEnumerados() {

	    System.out.println("El número de tipos enumerados del modelo es: " + numberOfEnumerations);
	    assertEquals("El número recomendado de tipos enumerados es " + testnenum, testnenum, numberOfEnumerations);
	  }

}