package solver;

import java.io.FileNotFoundException;
import java.util.List;

import objects.JSONParsingObject;
import objects.Secretary;
import utils.JSONUtil;

public class Main {

	public static void main (String args[]) throws FileNotFoundException{

		JSONParsingObject jsonParsingObject = JSONUtil.parseJSON(args[0]);
		//JSONUtil.writeJSON("assets/outputs/output.JSON", jsonParsingObject.getTfes());
		//OutputChecker.writeReport("assets/outputs/report.txt", "assets/outputs/output.JSON");
		SecretaryAssign solver = new SecretaryAssign(jsonParsingObject);
		List<Secretary> secretaries = jsonParsingObject.getSecretaries();
		solver.solve(secretaries);
		for(Secretary s : secretaries){
			System.out.println(s.getEmail()+" "+s.getFaculties()+" : "+s.getTfes().size());
		}
	}
}
