package utils;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import objects.Secretary;

public class SecretaryJSONDeserializer implements JsonDeserializer<Secretary> {

	public Secretary deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException, JsonSyntaxException {
		
		JsonObject obj = jsonElement.getAsJsonObject();
		
		String email = JSONUtil.getString("email", obj);
		
		List<String> facultiesList = JSONUtil.getStringList("faculties", obj);
		
		Secretary secretary = new Secretary(email, facultiesList);
		
		return secretary;
	}
}
