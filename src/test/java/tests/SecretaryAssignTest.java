package tests;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import objects.JSONParsingObject;
import objects.Secretary;
import objects.TFE;
import solver.SecretaryAssign;
import utils.JSONUtil;

public class SecretaryAssignTest {

	private JSONParsingObject jsonParsingObject;
	
	@Before
	public void setUp() throws FileNotFoundException {
		jsonParsingObject = JSONUtil.parseJSON("assets/json/InputTemplate.JSON");
	}
	
	@Test
	public void withSecretaries(){
		SecretaryAssign solver = new SecretaryAssign(jsonParsingObject);
		List<Secretary> secretaries = getSecretaries();
		solver.solve(secretaries);
		List<TFE> tfes = secretaries.get(3).getTfes();
		assertEquals(tfes.get(0).getCode(), "EPL1617-999");
		assertEquals(tfes.get(1).getCode(), "EPL1617-111");
	}
	
	@Test
	public void withoutSecretaries(){
		SecretaryAssign solver = new SecretaryAssign(jsonParsingObject);
		Map<String, List<TFE>> map = solver.solve();
		List<TFE> tfes = map.get("INFO");
		assertEquals(tfes.get(0).getCode(), "EPL1617-999");
		assertEquals(tfes.get(1).getCode(), "EPL1617-111");
	}
	
	public List<Secretary> getSecretaries(){
		List<Secretary> secretaries = new ArrayList<Secretary>();
		List<String> faculties = new ArrayList<String>();
		faculties.add("ELEC");
		faculties.add("ELME");
		faculties.add("GBIO");
		secretaries.add(new Secretary("secretary1@uclouvain.be", faculties));
		faculties = new ArrayList<String>();
		faculties.add("FYAP");
		faculties.add("KIMA");
		secretaries.add(new Secretary("secretary2@uclouvain.be", faculties));
		faculties = new ArrayList<String>();
		faculties.add("GCE");
		secretaries.add(new Secretary("secretary3@uclouvain.be", faculties));
		faculties = new ArrayList<String>();
		faculties.add("INFO");
		faculties.add("SINF");
		secretaries.add(new Secretary("secretary4@uclouvain.be", faculties));
		faculties = new ArrayList<String>();
		faculties.add("MAP");
		secretaries.add(new Secretary("secretary5@uclouvain.be", faculties));
		faculties = new ArrayList<String>();
		faculties.add("MECA");
		secretaries.add(new Secretary("secretary6@uclouvain.be", faculties));
		return secretaries;
	}
}
