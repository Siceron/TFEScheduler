package solver;

import java.io.FileNotFoundException;

import objects.JSONParsingObject;
import utils.JSONUtil;
import utils.OutputChecker;

public class Main {

	public static void main (String args[]) throws FileNotFoundException{

		JSONParsingObject jsonParsingObject = JSONUtil.parseJSON("assets/json/InputTemplate.JSON");
		JSONUtil.writeJSON("assets/outputs/output.JSON", jsonParsingObject.getTfes());
		OutputChecker.writeReport("assets/outputs/report.txt", "assets/outputs/output.JSON");
	}
}
