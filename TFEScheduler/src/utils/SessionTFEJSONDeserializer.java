package utils;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import objects.TFE;

public class SessionTFEJSONDeserializer implements JsonDeserializer<TFE> {
	
	private boolean isFixed;
	
	public SessionTFEJSONDeserializer(boolean isFixed){
		this.isFixed = isFixed;
	}

	public TFE deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException, JsonSyntaxException {
		
		JsonObject obj = jsonElement.getAsJsonObject();
		
		String code = JSONUtil.getString("code", obj);
		
		int session = JSONUtil.getInt("session", obj);
		
		TFE tfe = new TFE(code, session, isFixed);
		
		return tfe;
	}
}
