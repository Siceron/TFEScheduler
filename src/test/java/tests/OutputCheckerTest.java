package tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import objects.JSONParsingObject;
import utils.JSONUtil;
import utils.OutputChecker;

public class OutputCheckerTest {

	@Test
	public void writeJSON() throws FileNotFoundException{
		String reportPath = "assets/outputs/report.txt";
		String outputJSONPath = "assets/outputs/output.JSON";
		JSONParsingObject jsonParsingObject = JSONUtil.parseJSON("assets/json/InputTemplate.JSON");
		JSONUtil.writeJSON(outputJSONPath, jsonParsingObject.getTfes());
		File file = new File(reportPath);
		try {
			Files.deleteIfExists(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputChecker.writeReport(reportPath, outputJSONPath);
		assertTrue(file.exists());
	}
}
