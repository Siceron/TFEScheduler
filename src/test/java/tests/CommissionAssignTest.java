package tests;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import objects.Commission;
import objects.JSONParsingObject;
import objects.TFE;
import solver.CommissionAssign;
import utils.JSONUtil;

public class CommissionAssignTest {

	private JSONParsingObject jsonParsingObject;
	
	@Before
	public void setUp() throws FileNotFoundException {
		jsonParsingObject = JSONUtil.parseJSON("assets/json/InputTemplate.JSON");
	}
	
	@Test
	public void withCommissions(){
		CommissionAssign solver = new CommissionAssign(jsonParsingObject);
		List<Commission> commissions = jsonParsingObject.getCommissions();
		solver.solve(commissions);
		List<TFE> tfes = commissions.get(8).getTfes();
		assertEquals(tfes.get(0).getCode(), "EPL1617-999");
		assertEquals(tfes.get(1).getCode(), "EPL1617-111");
	}
	
	@Test
	public void withoutSecretaries(){
		CommissionAssign solver = new CommissionAssign(jsonParsingObject);
		Map<String, List<TFE>> map = solver.solve();
		List<TFE> tfes = map.get("SINF");
		assertEquals(tfes.get(0).getCode(), "EPL1617-999");
		assertEquals(tfes.get(1).getCode(), "EPL1617-111");
	}
}
