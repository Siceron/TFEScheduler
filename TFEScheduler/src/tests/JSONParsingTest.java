package tests;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import utils.JSONUtil;

public class JSONParsingTest {
	
	@Test
	public void objectFromJSON(){
		assertNotNull(JSONUtil.parseJSON("assets/json/InputTemplate.JSON"));
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingField(){
		JSONUtil.parseJSON("assets/json/MissingField.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingNestedFieldCode(){
		JSONUtil.parseJSON("assets/json/MissingNestedFieldCode.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingNestedFieldSession(){
		JSONUtil.parseJSON("assets/json/MissingNestedFieldSession.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingNestedFieldStudents(){
		JSONUtil.parseJSON("assets/json/MissingNestedFieldStudents.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingNestedFieldEmail(){
		JSONUtil.parseJSON("assets/json/MissingNestedFieldEmail.JSON");
	}
	
	@Test (expected = JsonSyntaxException.class)
	public void missingNestedFieldDisponibilities(){
		JSONUtil.parseJSON("assets/json/MissingNestedFieldDisponibilities.JSON");
	}

	@Test (expected = JsonParseException.class)
	public void jsonParseExceptions(){
		JSONUtil.parseJSON("assets/json/MissingBracket.JSON");
	}
}
