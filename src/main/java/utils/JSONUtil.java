package utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import objects.Commission;
import objects.JSONParsingObject;
import objects.Jury;
import objects.TFE;

public class JSONUtil {

	public static JSONParsingObject parseJSON(String path) throws FileNotFoundException{
		Reader reader = new FileReader(path);
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(JSONParsingObject.class, new InputJSONDeserializer());
		Gson gson = gsonBuilder.create();
		JSONParsingObject jsonParsingObject = gson.fromJson(reader, JSONParsingObject.class);
		return jsonParsingObject;
	}

	public static void writeJSON(String path, List<TFE> tfes){
		try {
			JsonWriter writer = new JsonWriter(new FileWriter(path));
			writer.setIndent("\t");
			writer.beginArray();
			Collections.sort(tfes, new SessionComparator());
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
	
	public static void writeCommissionJSON(String path, List<Commission> commissions){
		try {
			JsonWriter writer = new JsonWriter(new FileWriter(path));
			writer.setIndent("\t");
			writer.beginObject();
			for(Commission commission : commissions){
				writer.name(commission.getFaculty());
				writer.beginArray();
				for(TFE tfe : commission.getTfes()){
					writer.value(tfe.getCode());
				}
				writer.endArray();
			}
			writer.endObject();
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
				throw new JsonSyntaxException(field+" must be an Integer");
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
	
	public static List<String> getStringList(String field, JsonObject obj){
		Type stringType = new TypeToken<List<String>>(){}.getType();
		Gson gson = new Gson();
		try{
			List<String> stringList = gson.fromJson(obj.get(field), stringType);
	        if (stringList == null)
	        	throw new JsonSyntaxException("Missing field in JSON: "+field);
	        else
	        	return stringList;
		}catch(Exception e){ // If json is list of jury instead of list of string
			Type juryType = new TypeToken<List<Jury>>(){}.getType();
			List<Jury> juryList = gson.fromJson(obj.get(field), juryType);
			if (juryList == null)
	        	throw new JsonSyntaxException("Missing field in JSON: "+field);
	        else{
	        	List<String> stringList = new ArrayList<String>();
	        	for(Jury jury : juryList){
	        		stringList.add(jury.getEmail());
	        	}
	        	return stringList;
	        }
		}
	}
	
	public static class SessionComparator implements Comparator<TFE> {
	    @Override
	    public int compare(TFE t1, TFE t2) {
	    	int a = t1.getFixedSession();
	    	int b = t2.getFixedSession();
	        return a > b ? +1 : a < b ? -1 : 0;
	    }
	}
}
