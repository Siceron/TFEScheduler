package tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

import objects.JSONParsingObject;
import utils.JSONUtil;

public class WriteJSONTest {

	@Test
	public void writeJSON() throws FileNotFoundException{
		String outputJSONPath = "assets/outputs/output.JSON";
		JSONParsingObject jsonParsingObject = JSONUtil.parseJSON("assets/json/InputTemplate.JSON");
		JSONUtil.writeJSON(outputJSONPath, jsonParsingObject.getTfes());
		File file = new File(outputJSONPath);
		assertTrue(file.exists());
	}
}
