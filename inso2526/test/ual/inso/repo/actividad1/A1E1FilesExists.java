package ual.inso.repo.actividad1;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;

public class A1E1FilesExists extends BaseTC {
	private String vppSourcePathStudent;
	private String umlSourcePathStudent;
	private String vppSourcePath;
	private String umlSourcePath;

	private String fileName;

	@Before
	public void setUp() throws Exception {

		vppSourcePathStudent = "/A1E1/vpproject/";
		umlSourcePathStudent = "/A1E1/";

		String userDirectory = System.getProperty("user.dir");
		System.out.println("User directory: " + userDirectory);

		vppSourcePath = userDirectory + "/../../main" + vppSourcePathStudent;
		umlSourcePath = userDirectory + "/../../main" + umlSourcePathStudent;

		System.out.println("Path vpp: " + vppSourcePath);
		System.out.println("Path uml: " + umlSourcePath);
	}

	@Test
	public void FileA1E1vppDoesNotExists() {
		fileName = "A1E1.vpp";
		Path p = Paths.get(vppSourcePath + fileName);
		assertTrue("El archivo " + vppSourcePathStudent + fileName + " no existe en el respositorio del estudiante.",
				p.toFile().exists());
	}

	@Test
	public void FileA1E1umlDoesNotExists() {
		fileName = "A1E1.uml";
		Path p = Paths.get(umlSourcePath + fileName);
		assertTrue("El archivo " + umlSourcePathStudent + fileName + " no existe en el respositorio del estudiante.",
				p.toFile().exists());
	}

	//@Test
	public void FileA1E1umlProfileDoesNotExists() {
		fileName = "A1E1.profile.uml";
		Path p = Paths.get(umlSourcePath + fileName);
		assertTrue("El archivo " + umlSourcePathStudent + fileName + " no existe en el respositorio del estudiante.",
				p.toFile().exists());
	}

}
