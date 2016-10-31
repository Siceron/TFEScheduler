package utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
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

import objects.JSONParsingObject;
import objects.Jury;
import objects.Secretary;
import objects.TFE;

public class InputJSONDeserializer implements JsonDeserializer<JSONParsingObject> {

	public JSONParsingObject deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException, JsonSyntaxException {
		
		JsonObject obj = jsonElement.getAsJsonObject();
		
		int sessionNumber = JSONUtil.getInt("sessionNumber", obj);
		int sessionDays = JSONUtil.getInt("sessionDays", obj);
		int sessionRooms = JSONUtil.getInt("sessionRooms", obj);
		
		List<Secretary> secretaries = getSecretaries("secretaries", obj);
		List<Jury> advisorList = getJuryList("advisors", obj);
		List<Jury> readerList = getJuryList("readers", obj);
		
        List<TFE> tfeList = getTFEList("TFE", obj, advisorList, readerList);
        List<TFE> fixedList = getSessionTFEList("fixed", obj, true);
        List<TFE> bannedList = getSessionTFEList("banned", obj, false);
        tfeList = updateTFEList(tfeList, fixedList);
        
        JSONParsingObject jsonParsingObject = new JSONParsingObject(sessionNumber, sessionDays, sessionRooms, 
        		secretaries, advisorList, readerList, tfeList, fixedList, bannedList);
		
		return jsonParsingObject;
	}

	private List<TFE> getTFEList(String field, JsonObject obj, List<Jury> advisorList, List<Jury> readerList){
		Map<String, Jury> advisorsMap = new HashMap<String, Jury>();
		for(Jury advisor : advisorList){
			advisorsMap.put(advisor.getEmail(), advisor);
		}
		Map<String, Jury> readersMap = new HashMap<String, Jury>();
		for(Jury reader : readerList){
			advisorsMap.put(reader.getEmail(), reader);
		}
		GsonBuilder tfeGsonBuilder = new GsonBuilder();
		tfeGsonBuilder.registerTypeAdapter(TFE.class, new TFEJSONDeserializer(advisorsMap, readersMap));
		Gson tfeGson = tfeGsonBuilder.create();
        Type tfesType = new TypeToken<List<TFE>>(){}.getType();
        List<TFE> tfeList = tfeGson.fromJson(obj.get(field), tfesType);
        if (tfeList == null)
        	throw new JsonSyntaxException("Missing field in JSON: TFE");
        else
        	return tfeList;
	}
	
	private List<TFE> getSessionTFEList(String field, JsonObject obj, boolean isFixed){
		GsonBuilder fixedGsonBuilder = new GsonBuilder();
		fixedGsonBuilder.registerTypeAdapter(TFE.class, new SessionTFEJSONDeserializer(isFixed));
		Gson tfeGson = fixedGsonBuilder.create();
        Type tfesType = new TypeToken<List<TFE>>(){}.getType();
        List<TFE> fixedList = tfeGson.fromJson(obj.get(field), tfesType);
        if (fixedList == null)
        	throw new JsonSyntaxException("Missing field in JSON: "+field);
        else
        	return fixedList;
	}
	
	private List<TFE> updateTFEList(List<TFE> tfes, List<TFE> fixedList){
		Map<String,TFE> map = new HashMap<String,TFE>();
		for(TFE tfe : tfes) map.put(tfe.getCode(),tfe);
		for(TFE fixed : fixedList){
			TFE toBeUpdated = map.get(fixed.getCode());
			if(toBeUpdated == null)
				throw new JsonSyntaxException("Can't fix a session for a TFE that doesn't exist");
			else
				toBeUpdated.setFixedSession(fixed.getFixedSession());
		}
		return new ArrayList<TFE>(map.values());
	}
	
	private List<Secretary> getSecretaries(String field, JsonObject obj){
		GsonBuilder secretariesGsonBuilder = new GsonBuilder();
		secretariesGsonBuilder.registerTypeAdapter(TFE.class, new SecretaryJSONDeserializer());
		Gson secretariesGson = secretariesGsonBuilder.create();
        Type secretariesType = new TypeToken<List<Secretary>>(){}.getType();
        List<Secretary> secretaries = secretariesGson.fromJson(obj.get(field), secretariesType);
        if (secretaries == null)
        	throw new JsonSyntaxException("Missing field in JSON: "+field);
        else
        	return secretaries;
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
