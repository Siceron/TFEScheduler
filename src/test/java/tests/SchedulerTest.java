package tests;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import objects.JSONParsingObject;
import solver.Scheduler;
import utils.JSONUtil;

public class SchedulerTest {

private JSONParsingObject jsonParsingObject;
	
	@Before
	public void setUp() throws FileNotFoundException {
		jsonParsingObject = JSONUtil.parseJSON("assets/json/InputTemplate.JSON");
	}
	
	@Test
	public void solverTest() throws IOException{
		Scheduler scheduler = new Scheduler(jsonParsingObject);
		assertEquals(scheduler.solve(1), true);
	}
}
