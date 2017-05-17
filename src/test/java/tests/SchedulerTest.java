package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
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
	@Ignore
	public void solverTest() throws IOException{
		Scheduler scheduler = new Scheduler(jsonParsingObject);
		assertEquals(scheduler.solve(1), true);
	}
	
	@Test
	@Ignore
	public void solverReportTest() throws IOException{
		String reportPath = "static/report.txt";
		Scheduler scheduler = new Scheduler(jsonParsingObject);
		scheduler.solve(1);
		File file = new File(reportPath);
		assertTrue(file.exists());
	}
}
