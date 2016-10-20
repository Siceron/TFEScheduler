package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import objects.Session;

public class OutputChecker {

	public static void writeReport(String path, String jsonPath) throws FileNotFoundException{
		Map<Integer,List<String>> map = getSessionMap(jsonPath);
		
		try {
			File file = new File(path);

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Conflicts :\n");
			for (Entry<Integer, List<String>> entry : map.entrySet()) {
				List<String> value = entry.getValue();
				if(value.size() > 1){
					bw.write("\t- Session "+entry.getKey()+" :\n");
					for(String code : value) bw.write("\t\t - "+code+"\n");
				}
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<Session> parseJSON(String path) throws FileNotFoundException{
		Reader reader = new FileReader(path);
		Gson gson = new Gson();
		Type personType = new TypeToken<List<Session>>(){}.getType();
		List<Session> sessionList = gson.fromJson(reader, personType);
		return sessionList;
	}
	
	private static Map<Integer,List<String>> getSessionMap(String jsonPath) throws FileNotFoundException{
		Map<Integer,List<String>> map = new HashMap<Integer,List<String>>();
		for(Session session : parseJSON(jsonPath)){
			if(map.containsKey(session.getSession())){
				map.get(session.getSession()).add(session.getCode());
			}
			else{
				List<String> newList = new ArrayList<String>();
				newList.add(session.getCode());
				map.put(session.getSession(), newList);
			}
		}
		return map;
	}
}
