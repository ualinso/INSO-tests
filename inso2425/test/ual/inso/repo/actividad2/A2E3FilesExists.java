package ual.inso.repo.actividad2;


import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;

public class A2E3FilesExists extends BaseTC {
	private String vppSourcePathStudent;
	private String umlSourcePathStudent;
	private String vppSourcePath;
	private String umlSourcePath;

	private String fileName;

	@Before
	public void setUp() throws Exception {

		vppSourcePathStudent = "/A2E3/vpproject/";
		umlSourcePathStudent = "/A2E3/";

		String userDirectory = System.getProperty("user.dir");
		System.out.println("User directory: " + userDirectory);

		vppSourcePath = userDirectory + "/../../main" + vppSourcePathStudent;
		umlSourcePath = userDirectory + "/../../main" + umlSourcePathStudent;

		System.out.println("Path vpp: " + vppSourcePath);
		System.out.println("Path uml: " + umlSourcePath);

	}

	@Test
	public void FileA2E3vppDoesNotExists() {
		fileName = "A2E3.vpp";
		Path p = Paths.get(vppSourcePath + fileName);
		assertTrue("El archivo " + vppSourcePathStudent + fileName + " no existe en el respositorio del estudiante.",
				p.toFile().exists());
	}

	@Test
	public void FileA2E3umlDoesNotExists() {
		fileName = "A2E3.uml";
		Path p = Paths.get(umlSourcePath + fileName);
		assertTrue("El archivo " + umlSourcePathStudent + fileName + " no existe en el respositorio del estudiante.",
				p.toFile().exists());
	}

	//@Test
	public void FileA2E3umlProfileDoesNotExists() {
		fileName = "A2E3.profile.uml";
		Path p = Paths.get(umlSourcePath + fileName);
		assertTrue("El archivo " + umlSourcePathStudent + fileName + " no existe en el respositorio del estudiante.",
				p.toFile().exists());
	}

}
