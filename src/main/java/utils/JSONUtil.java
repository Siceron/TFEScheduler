package utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;

import objects.JSONParsingObject;
import objects.TFE;

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
		}
		return null;
	}

	public static void writeJSON(String path, List<TFE> tfes){
		try {
			JsonWriter writer = new JsonWriter(new FileWriter(path));
			writer.setIndent("\t");
			writer.beginArray();
			for(TFE tfe : tfes){
				writer.beginObject();
				writer.name("code").value(tfe.getCode());
				writer.name("session").value(tfe.getFixedSession());
				writer.endObject();
			}
			writer.endArray();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		else{
			if(!isInteger(element))
				throw new JsonSyntaxException("Session must be an Integer");
			return element.getAsInt();
		}
	}
	
	public static boolean isInteger(JsonElement element) {
		try{
			element.getAsInt();
		} catch(NumberFormatException e){
			return false;
		}
		return true;
	}
}
