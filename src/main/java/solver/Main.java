package solver;

import java.io.IOException;
import java.util.List;

import objects.JSONParsingObject;
import objects.Secretary;
import utils.JSONUtil;

public class Main {

	public static void main (String args[]) throws IOException{

		JSONParsingObject jsonParsingObject = JSONUtil.parseJSON(args[0]);
		SecretaryAssign solver = new SecretaryAssign(jsonParsingObject);
		List<Secretary> secretaries = jsonParsingObject.getSecretaries();
		solver.solve(secretaries);
		for(Secretary s : secretaries){
			System.out.println(s.getEmail()+" "+s.getFaculties()+" : "+s.getTfes().size());
		}
		Scheduler scheduler = new Scheduler(jsonParsingObject);
		scheduler.solve(4);
		JSONUtil.writeJSON("assets/outputs/output.JSON", jsonParsingObject.getTfes());
	}
}
