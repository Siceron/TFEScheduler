package tests;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
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
		List<Secretary> secretaries = jsonParsingObject.getSecretaries();
		solver.solve(secretaries);
		List<TFE> tfes = secretaries.get(2).getTfes();
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
}
