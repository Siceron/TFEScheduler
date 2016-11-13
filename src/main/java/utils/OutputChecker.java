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

import objects.JSONParsingObject;
import objects.Jury;
import objects.Session;
import objects.TFE;

public class OutputChecker {

	private BufferedWriter bw;
	private Map<Integer,List<String>> map;
	private JSONParsingObject jsonParsingObject;

	public OutputChecker(String jsonPath, JSONParsingObject jsonParsingObject) throws IOException{
		this.map = getSessionMap(jsonPath);
		this.jsonParsingObject = jsonParsingObject;
	}

	public void writeReport(String path) throws IOException{
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		this.bw = new BufferedWriter(fw);
		writeConflicts();
		writeAssigned();
		//writeJuryMissing();
		bw.close();
	}

	private void writeJuryMissing() throws IOException{
		bw.write("Jury Missing :\n");
		boolean isJuryMissing = false;
		for(TFE t : jsonParsingObject.getTfes()){
			for(Jury j : t.getJuryList()){
				if(t.getFixedSession() != -1 && !j.getSessions().contains(t.getFixedSession())){
					isJuryMissing = true;
					bw.write("\t- TFE "+t.getCode()+" : "+j.getEmail()+" \n");
				}
			}
		}
		if(!isJuryMissing)
			bw.write("\t No jury missing\n");
	}

	private void writeConflicts() throws IOException{
		bw.write("Conflicts :\n");
		boolean isConflicts = false;
		for (Entry<Integer, List<String>> entry : map.entrySet()) {
			List<String> value = entry.getValue();
			if(value.size() > 3 && entry.getKey() != -1){
				isConflicts = true;
				bw.write("\t- Session "+entry.getKey()+" :\n");
				for(String code : value) bw.write("\t\t - "+code+"\n");
			}
		}
		if(!isConflicts)
			bw.write("\t No conflicts\n");
	}

	private void writeAssigned() throws IOException{
		bw.write("Not assigned :\n");
		boolean notAssigned = false;
		for (Entry<Integer, List<String>> entry : map.entrySet()) {
			List<String> value = entry.getValue();
			if(entry.getKey() == -1){
				notAssigned = true;
				for(String code : value) bw.write("\t - "+code+"\n");
			}
		}
		if(!notAssigned)
			bw.write("\t Every TFE is assigned\n");
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
