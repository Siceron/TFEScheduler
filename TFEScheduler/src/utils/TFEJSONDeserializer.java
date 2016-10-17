package utils;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import objects.Jury;
import objects.Person;
import objects.TFE;

public class TFEJSONDeserializer implements JsonDeserializer<TFE> {
	
	public TFE deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException, JsonSyntaxException {
		
		JsonObject obj = jsonElement.getAsJsonObject();
		
		String code = JSONUtil.getString("code", obj);
		
		List<Person> studentList = getPersonList(obj);
		
		List<Jury> advisorList = getJuryList("advisors", obj);
		
		List<Jury> readerList = getJuryList("readers", obj);
		
		TFE tfe = new TFE(code, studentList, advisorList, readerList);
		
		return tfe;
	}

	private List<Person> getPersonList(JsonObject obj){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Person.class, new PersonJSONDeserializer());
		Gson gson = gsonBuilder.create();
		Type personType = new TypeToken<List<Person>>(){}.getType();
		List<Person> personList = gson.fromJson(obj.get("students"), personType);
        if (personList == null)
        	throw new JsonSyntaxException("Missing field in JSON: students");
        else
        	return personList;
	}
	
	private List<Jury> getJuryList(String field, JsonObject obj){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Jury.class, new JuryJSONDeserializer());
		Gson gson = gsonBuilder.create();
		Type juryType = new TypeToken<List<Jury>>(){}.getType();
		List<Jury> juryList = gson.fromJson(obj.get(field), juryType);
        if (juryList == null)
        	throw new JsonSyntaxException("Missing field in JSON: "+field);
        else
        	return juryList;
	}
}
