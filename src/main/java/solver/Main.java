package solver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import objects.JSONParsingObject;
import objects.Jury;
import objects.Person;
import objects.TFE;
import utils.InputJSONDeserializer;

public class Main {

	public static void main (String args[]){

		try {
			Reader reader = new FileReader("assets/json/MissingNestedField.JSON");
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(JSONParsingObject.class, new InputJSONDeserializer());
			Gson gson = gsonBuilder.create();
			JSONParsingObject jsonParsingObject = gson.fromJson(reader, JSONParsingObject.class);
			
			for(TFE tfe : jsonParsingObject.getTfes()){
				System.out.println(tfe.getCode());
				for(Person students : tfe.getStudents()){
					System.out.println("\t"+students.getEmail());
				}
				for(Jury advisor : tfe.getAdvisors()){
					System.out.println("\t"+advisor.getEmail());
					System.out.print("\t");
					for(boolean session : advisor.getDisponibilities()){
						System.out.print(session+" ");
					}
					System.out.println("");
				}
				for(Jury readers : tfe.getReaders()){
					System.out.println("\t"+readers.getEmail());
				}
			}
			
			for(TFE banned : jsonParsingObject.getBanned()){
				System.out.println(banned.getCode());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
