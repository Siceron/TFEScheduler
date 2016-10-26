package tests;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import org.junit.Test;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import utils.JSONUtil;

public class JSONParsingTest {
	
	@Test
	public void objectFromJSON() throws FileNotFoundException{
		assertNotNull(JSONUtil.parseJSON("assets/json/InputTemplate.JSON"));
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingField() throws FileNotFoundException{
		JSONUtil.parseJSON("assets/json/MissingField.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingNestedFieldCode() throws FileNotFoundException{
		JSONUtil.parseJSON("assets/json/MissingNestedFieldCode.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingNestedFieldSession() throws FileNotFoundException{
		JSONUtil.parseJSON("assets/json/MissingNestedFieldSession.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingNestedFieldStudents() throws FileNotFoundException{
		JSONUtil.parseJSON("assets/json/MissingNestedFieldStudents.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingNestedFieldEmail() throws FileNotFoundException{
		JSONUtil.parseJSON("assets/json/MissingNestedFieldEmail.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingNestedFieldDisponibilities() throws FileNotFoundException{
		JSONUtil.parseJSON("assets/json/MissingNestedFieldDisponibilities.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingFieldTFE() throws FileNotFoundException{
		JSONUtil.parseJSON("assets/json/MissingFieldTFE.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingTFEForSession() throws FileNotFoundException{
		JSONUtil.parseJSON("assets/json/MissingTFEForSession.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void sessionNotInteger() throws FileNotFoundException{
		JSONUtil.parseJSON("assets/json/SessionNotInteger.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void MissingNestedFieldAdvisors() throws FileNotFoundException{
		JSONUtil.parseJSON("assets/json/MissingNestedFieldAdvisors.JSON");
	}

	@Test (expected = JsonParseException.class)
	public void jsonParseExceptions() throws FileNotFoundException{
		JSONUtil.parseJSON("assets/json/MissingBracket.JSON");
	}
}
