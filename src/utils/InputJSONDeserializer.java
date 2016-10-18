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

import objects.JSONParsingObject;
import objects.TFE;

public class InputJSONDeserializer implements JsonDeserializer<JSONParsingObject> {

	public JSONParsingObject deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException, JsonSyntaxException {
		
		JsonObject obj = jsonElement.getAsJsonObject();
		
        List<TFE> tfeList = getTFEList("TFE", obj);
        
        List<TFE> fixedList = getSessionTFEList("fixed", obj, true);
        
        List<TFE> bannedList = getSessionTFEList("banned", obj, false);
        
        JSONParsingObject jsonParsingObject = new JSONParsingObject(tfeList, fixedList, bannedList);
		
		return jsonParsingObject;
	}

	private List<TFE> getTFEList(String field, JsonObject obj){
		GsonBuilder tfeGsonBuilder = new GsonBuilder();
		tfeGsonBuilder.registerTypeAdapter(TFE.class, new TFEJSONDeserializer());
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
}
