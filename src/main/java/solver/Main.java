package solver;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import objects.JSONParsingObject;
import objects.Secretary;
import utils.JSONUtil;
import utils.OutputChecker;

public class Main {

	public static void main (String args[]) throws FileNotFoundException{

		JSONParsingObject jsonParsingObject = JSONUtil.parseJSON("assets/json/InputTemplate.JSON");
		JSONUtil.writeJSON("assets/outputs/output.JSON", jsonParsingObject.getTfes());
		OutputChecker.writeReport("assets/outputs/report.txt", "assets/outputs/output.JSON");
		
		SecretaryAssign solver = new SecretaryAssign(jsonParsingObject);
		List<Secretary> secretaries = getSecretaries();
		solver.solve(secretaries);
		for(Secretary s : secretaries){
			System.out.print(s.getEmail()+" : ");
			System.out.println(s.getTfes());
		}
	}
	
	public static List<Secretary> getSecretaries(){
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
		return secretaries;
	}
}
