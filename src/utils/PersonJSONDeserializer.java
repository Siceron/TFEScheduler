package utils;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import objects.Person;

public class PersonJSONDeserializer implements JsonDeserializer<Person> {

	public Person deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException, JsonSyntaxException {
	
		JsonObject obj = jsonElement.getAsJsonObject();
		
		String email = JSONUtil.getString("email", obj);
		
		String faculty = JSONUtil.getString("faculty", obj);
		
		Person person = new Person(email, faculty);
		
		return person;
	}
}
