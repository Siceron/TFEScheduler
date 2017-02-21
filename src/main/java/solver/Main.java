package solver;

import java.io.IOException;
import java.util.List;

import objects.Commission;
import objects.JSONParsingObject;
import utils.JSONUtil;

public class Main {

	public static void main (String args[]) throws IOException{

		if(args.length != 2){
			System.out.println("Enter correct arguments (<json> <time(min)>)");
			System.exit(1);
		}
		if(!isNumeric(args[1])){
			System.out.println("Time must be a number");
			System.exit(1);
		}
		JSONParsingObject jsonParsingObject = JSONUtil.parseJSON(args[0]);
		CommissionAssign solver = new CommissionAssign(jsonParsingObject);
		List<Commission> commissions = jsonParsingObject.getCommissions();
		solver.solve(commissions);
		for(Commission commission : commissions){
			System.out.println(commission.getFaculty()+" : "+commission.getTfes().size());
		}
		JSONUtil.writeCommissionJSON("commissions.JSON", commissions);
		Scheduler scheduler = new Scheduler(jsonParsingObject);
		scheduler.solve(Integer.parseInt(args[1]));
		JSONUtil.writeJSON("output.JSON", jsonParsingObject.getTfes());
	}

	private static boolean isNumeric(String str)  
	{  
		try  
		{  
			Double.parseDouble(str);  
		}  
		catch(NumberFormatException e)  
		{  
			return false;  
		}  
		return true;  
	}
}
