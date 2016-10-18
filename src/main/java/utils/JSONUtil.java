package utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import objects.JSONParsingObject;

public class JSONUtil {

	public static JSONParsingObject parseJSON(String path){
		try {
			Reader reader = new FileReader(path);
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(JSONParsingObject.class, new InputJSONDeserializer());
			Gson gson = gsonBuilder.create();
			JSONParsingObject jsonParsingObject = gson.fromJson(reader, JSONParsingObject.class);
			return jsonParsingObject;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}

	public static String getString(String field, JsonObject obj){
		JsonElement element = obj.get(field);
		if(element == null)
			throw new JsonSyntaxException("Missing field in JSON: "+field);
		else
			return element.getAsString();
	}
	
	public static int getInt(String field, JsonObject obj){
		JsonElement element = obj.get(field);
		if(element == null)
			throw new JsonSyntaxException("Missing field in JSON: "+field);
		else
			return element.getAsInt();
	}
}
