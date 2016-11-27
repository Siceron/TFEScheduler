package solver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import objects.JSONParsingObject;
import objects.Jury;
import objects.Person;
import objects.Secretary;
import objects.TFE;
import utils.InputJSONDeserializer;

public class CSVToJSON {
	
	private static Map<String, Jury> advisorsMap;
	private static Map<String, Jury> readersMap;
	private static List<List<Boolean>> disponibilities;
	private static Map<String, String> advisorsFaculty;

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length > 2) {
			advisorsMap = new HashMap<String, Jury>();
			readersMap = new HashMap<String, Jury>();
			disponibilities = disponibilities(args[1]);
			advisorsFaculty = advisors(args[2]);
			List<TFE> tfes = tfes(args[0]);
			JSONParsingObject object = new JSONParsingObject(60, 3, 5, secretaries(), new ArrayList<Jury>(advisorsMap.values()),
					new ArrayList<Jury>(readersMap.values()), tfes, new ArrayList<TFE>(), new ArrayList<TFE>());
			try {
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(JSONParsingObject.class, new InputJSONDeserializer());
				Gson gson = gsonBuilder.setPrettyPrinting().create();
				JsonWriter writer = new JsonWriter(new FileWriter(args[3]));
				writer.jsonValue(gson.toJson(object));
				writer.close();
				//System.out.println(gson.toJson(object));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Please specify the path to the csv file.");
		}
	}

	private static List<Secretary> secretaries(){
		List<Secretary> secretaries = new ArrayList<Secretary>();
		List<String> faculties = new ArrayList<String>();
		faculties.add("ELEC");
		faculties.add("ELME");
		faculties.add("GBIO");
		secretaries.add(new Secretary("secretary1@uclouvain.be", faculties));
		faculties = new ArrayList<String>();
		faculties.add("FYAP");
		faculties.add("KIMA");
		secretaries.add(new Secretary("secretary2@uclouvain.be", faculties));
		faculties = new ArrayList<String>();
		faculties.add("GCE");
		secretaries.add(new Secretary("secretary3@uclouvain.be", faculties));
		faculties = new ArrayList<String>();
		faculties.add("INFO");
		faculties.add("SINF");
		secretaries.add(new Secretary("secretary4@uclouvain.be", faculties));
		faculties = new ArrayList<String>();
		faculties.add("MAP");
		secretaries.add(new Secretary("secretary5@uclouvain.be", faculties));
		faculties = new ArrayList<String>();
		faculties.add("MECA");
		secretaries.add(new Secretary("secretary6@uclouvain.be", faculties));
		faculties = new ArrayList<String>();
		faculties.add("UNK");
		secretaries.add(new Secretary("secretary-poubelle@uclouvain.be", faculties));
		return secretaries;
	}
	
	private static Map<String, String> advisors(String path){
		Map<String, String> advisors = new HashMap<String, String>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";

		try {

			br = new BufferedReader(new FileReader(path));
			boolean isFirst = true;
			while ((line = br.readLine()) != null) {
				if(isFirst)
					isFirst = false;
				else{
					String[] array = line.split(cvsSplitBy);
					advisors.put(array[4].trim(), array[3].trim());
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return advisors;
	}

	private static List<List<Boolean>> disponibilities(String path){
		List<List<Boolean>> disponibilities = new ArrayList<List<Boolean>>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";

		try {

			br = new BufferedReader(new FileReader(path));
			int j = 0;
			while ((line = br.readLine()) != null) {
				if(j < 2)
					j++;
				else{
					String[] array = line.split(cvsSplitBy);
					List<Boolean> element = new ArrayList<Boolean>();
					boolean isEmpty = array.length!=14;
					for(int i = 0 ; i<array.length ; i++){
						if(i==0 || i==13){
							// do nothing
						}
						else{
							if(array[i].equals("1"))
								element.add(true);
							else
								element.add(false);
						}
					}
					if(!isEmpty)
						disponibilities.add(element);
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return disponibilities;
	}

	private static List<TFE> tfes(String path){
		List<TFE> tfes = new ArrayList<TFE>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";

		try {

			br = new BufferedReader(new FileReader(path));
			boolean isFirst = true;
			while ((line = br.readLine()) != null) {
				if(isFirst)
					isFirst = false;
				else{
					String[] array = line.split(cvsSplitBy);
					List<String> element = new ArrayList<String>();
					boolean isEmpty = false;
					int nPersons = 0;
					int nEmails = array[20].split(",").length;
					for(int i = 0 ; i<array.length ; i++){
						if(array[i].equals("")){
							isEmpty = true;
							break;
						}
						if(i==0 || i==20)
							element.add(array[i]);
						else if (i==2 || i==3 || i==4){
							nPersons += array[i].split(" - ").length;
							element.add(array[i]);
						}
					}
					if(!isEmpty && nPersons == nEmails){
						List<Person> students = new ArrayList<Person>();
						List<Jury> advisors = new ArrayList<Jury>();
						List<Jury> readers = new ArrayList<Jury>();
						String[] studentArray = element.get(1).split(" - ");
						for(int i = 0 ; i < studentArray.length ; i++){
							String faculty = "UNK";
							if(studentArray[i].contains("ELEC"))
								faculty = "ELEC";
							else if(studentArray[i].contains("ELME"))
								faculty = "ELME";
							else if(studentArray[i].contains("GBIO"))
								faculty = "GBIO";
							else if(studentArray[i].contains("FYAP"))
								faculty = "FYAP";
							else if(studentArray[i].contains("KIMA"))
								faculty = "KIMA";
							else if(studentArray[i].contains("SINF"))
								faculty = "SINF";
							else if(studentArray[i].contains("INFO"))
								faculty = "INFO";
							else if(studentArray[i].contains("GCE"))
								faculty = "GCE";
							else if(studentArray[i].contains("MAP"))
								faculty = "MAP";
							else if(studentArray[i].contains("MECA"))
								faculty = "MECA";
							students.add(new Person(element.get(4).split(",")[i].trim(), faculty));
						}
						String[] advisorsArray = element.get(2).split(" - ");
						for(int i = 0 ; i < advisorsArray.length ; i++){
							String email = element.get(4).split(",")[studentArray.length+i].trim();
							Jury advisor = new Jury(email,advisorsFaculty.getOrDefault(email, "UNK"), getRandomDisponibilities());
							advisors.add(advisor);
							if(!advisorsMap.containsKey(advisor.getEmail()))
								advisorsMap.put(advisor.getEmail(), advisor);
						}
						String[] readersArray = element.get(3).split(" - ");
						for(int i = 0 ; i < readersArray.length ; i++){
							String email = element.get(4).split(",")[studentArray.length+advisorsArray.length+i].trim();
							Jury reader = new Jury(email,advisorsFaculty.getOrDefault(email, "UNK"), getRandomDisponibilities());
							advisors.add(reader);
							if(!readersMap.containsKey(reader.getEmail()))
								readersMap.put(reader.getEmail(), reader);
						}
						tfes.add(new TFE(element.get(0), students, advisors, readers));
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return tfes;
	}
	
	private static List<Boolean> getRandomDisponibilities(){
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(disponibilities.size());
        List<Boolean> item = disponibilities.get(index);
        return item;
	}
}
