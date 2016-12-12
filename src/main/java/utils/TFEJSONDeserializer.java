package utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	
	private Map<String, Jury> advisorsMap;
	private Map<String, Jury> readersMap;
	
	public TFEJSONDeserializer(Map<String, Jury> advisorsMap, Map<String, Jury> readersMap) {
		super();
		this.advisorsMap = advisorsMap;
		this.readersMap = readersMap;
	}

	public TFE deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException, JsonSyntaxException {
		
		JsonObject obj = jsonElement.getAsJsonObject();
		
		String code = JSONUtil.getString("code", obj);
		
		List<Person> studentList = getPersonList(obj);
		List<Jury> advisorsList = getJuryList("advisors", obj, advisorsMap);
		List<Jury> readersList = getJuryList("readers", obj, readersMap);
		
		TFE tfe = new TFE(code, studentList, advisorsList, readersList);
		
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
	
	private List<Jury> getJuryList(String field, JsonObject obj, Map<String, Jury> juryMap){
		List<Jury> juryList = new ArrayList<Jury>();
		List<String> emailList = JSONUtil.getStringList(field, obj);
		for(String email : emailList){
			if(juryMap.containsKey(email))
				juryList.add(juryMap.get(email));
		}
		return juryList;
	}
}
