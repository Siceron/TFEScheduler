package utils;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import objects.Jury;

public class JuryJSONDeserializer implements JsonDeserializer<Jury> {

	public Jury deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException, JsonSyntaxException {

		JsonObject obj = jsonElement.getAsJsonObject();
		
		String email = JSONUtil.getString("email", obj);

		String faculty = JSONUtil.getString("faculty", obj);
		
		List<Boolean> disponibilities = getDisponibilitiesList(obj);
		
		Jury jury = new Jury(email, faculty, disponibilities);
		
		return jury;
	}
	
	private List<Boolean> getDisponibilitiesList(JsonObject obj){
		Type booleanType = new TypeToken<List<Boolean>>(){}.getType();
		Gson gson = new Gson();
		List<Boolean> disponibilitiesList = gson.fromJson(obj.get("disponibilities"), booleanType);
        if (disponibilitiesList == null)
        	throw new JsonSyntaxException("Missing field in JSON: disponibilities");
        else
        	return disponibilitiesList;
	}
}
