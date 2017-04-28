package solver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import objects.JSONParsingObject;
import objects.Jury;
import objects.TFE;
import utils.SolverReport;

public class Scheduler {

	private JSONParsingObject jsonParsingObject;
	private int nbrSessions;
	private List<Jury> juryList;
	private List<TFE> tfeList;
	private int nbrJury;
	private int nbrTFE;
	private int nbrRooms;
	private List<Integer> juryTFENbr;
	private List<String> impossibleTFE;
	private GRBVar tfes[][];
	private GRBVar profs[][];
	private GRBVar sessions[];

	public Scheduler(JSONParsingObject jsonParsingObject){
		this.jsonParsingObject = jsonParsingObject;
	}

	/**
	 * Solve the problem within the time limit
	 * @param timeLimit : time limit in minutes
	 * @return true if the solver found a solution
	 * @throws IOException 
	 */
	public boolean solve(int timeLimit) throws IOException{
		try {
			GRBEnv env = new GRBEnv("scheduler.log");

			nbrRooms = jsonParsingObject.getSessionRooms();
			nbrSessions = nbrRooms*4*jsonParsingObject.getSessionDays();
			juryList = getJuryList();
			tfeList = preprocessing(jsonParsingObject.getTfes());
			tfeList = preprocessingJury(tfeList);
			nbrJury = juryList.size();
			nbrTFE = tfeList.size();
			juryTFENbr = getJuryTFENbr(juryList);

			GRBModel model = initialize(env);

			// Set objective: maximize 
			// sum^nbrJury (sum^nbrSessions(profs[i][t] - juryTFENbr[i] / 3) * juryTFENbr[i])
			GRBLinExpr expr = new GRBLinExpr();
			for(int i = 0 ; i < nbrJury ; i++){
				GRBLinExpr insidelinExpr = new GRBLinExpr();
				for(int t = 0 ; t < nbrSessions ; t++){
					insidelinExpr.addTerm(1.0, profs[i][t]);
				}
				insidelinExpr.addConstant(Math.ceil(juryTFENbr.get(i)/3.0));
				expr.multAdd(juryTFENbr.get(i), insidelinExpr);
			}
			// Add to the original objective : 
			// sum^nbrTFE (sum^nbrSessions (5 * tfes[i][t] if reserve))
			for(int i = 0 ; i < tfes.length ; i++){
				GRBLinExpr memExpr = new GRBLinExpr();
				for(int t = 0 ; t < tfes[0].length ; t++){
					if(isReserve(t, jsonParsingObject.getReserveDay())){
						memExpr.addTerm(5, tfes[i][t]);
					}
				}
				expr.add(memExpr);
			}
			// Add to the original objective : 
			// sum^nbrSessions (4 * sessions[i])
			for(int t = 0 ; t < nbrSessions ; t++){
				GRBLinExpr memExpr = new GRBLinExpr();
				memExpr.addTerm(4, sessions[t]);
				expr.add(memExpr);
			}
			
			model.setObjective(expr, GRB.MINIMIZE);
			
			addBannedConstraint(model);
			
			addFixedConstraint(model);

			addMoreProfsThanTFEConstraint(model);

			addTFEMustBeAssignedConstraint(model);

			addMax3TFESessionConstraint(model);

			addDisponibleProfConstraint(model);

			addParallelProfConstraint(model);

			addProfLinkedConstraints(model);
			
			addSetSessionConstraint(model);

			//model.getEnv().set(GRB.IntParam.SolutionLimit, 1);
			model.getEnv().set(GRB.DoubleParam.TimeLimit, 60*timeLimit);
			//model.getEnv().set(GRB.IntParam.MIPFocus, 3);
			//model.getEnv().set(GRB.IntParam.Cuts, 0);

			// Optimize model
			model.optimize();

			System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

			int assigned = 0;
			for(int i = 0 ; i < tfes.length ; i++){
				String result = jsonParsingObject.getTfes().get(i)+" : ";
				for(int t = 0 ; t < tfes[0].length ; t++){
					if(tfes[i][t].get(GRB.DoubleAttr.X) == 1.0){
						tfeList.get(i).setFixedSession(t);
						assigned++;
						result = result+t;
						System.out.println(result);
					}
				}
			}
			
			makeReport(assigned, jsonParsingObject.getTfes().size()-tfeList.size(), timeLimit);

			model.write("scheduler.sol");

			model.dispose();
			env.dispose();

			return true;
		} catch (GRBException e) {
			
			System.out.println("!!! model infeasible !!!");
			
			// If the model is infeasible
			try {
				GRBEnv env = new GRBEnv("scheduler.log");
				GRBModel model = initialize(env);
				
				// Set objective: maximize 
				// sum^nbrJury (sum^nbrSessions(profs[i][t] - juryTFENbr[i] / 3) * juryTFENbr[i])
				GRBLinExpr expr = new GRBLinExpr();
				for(int i = 0 ; i < nbrJury ; i++){
					GRBLinExpr insidelinExpr = new GRBLinExpr();
					for(int t = 0 ; t < nbrSessions ; t++){
						insidelinExpr.addTerm(1.0, profs[i][t]);
					}
					insidelinExpr.addConstant(Math.ceil(juryTFENbr.get(i)/3.0));
					expr.multAdd(juryTFENbr.get(i), insidelinExpr);
				}
				// Add to the original objective : 
				// sum^nbrTFE (sum^nbrSessions (-10 * tfes[i][t]))
				for(int i = 0 ; i < tfes.length ; i++){
					GRBLinExpr memExpr = new GRBLinExpr();
					for(int t = 0 ; t < tfes[0].length ; t++){
						memExpr.addTerm(-20, tfes[i][t]);
					}
					expr.add(memExpr);
				}
				// Add to the original objective : 
				// sum^nbrTFE (sum^nbrSessions (5 * tfes[i][t] if reserve))
				for(int i = 0 ; i < tfes.length ; i++){
					GRBLinExpr memExpr = new GRBLinExpr();
					for(int t = 0 ; t < tfes[0].length ; t++){
						if(isReserve(t, jsonParsingObject.getReserveDay())){
							memExpr.addTerm(5, tfes[i][t]);
						}
					}
					expr.add(memExpr);
				}
				// Add to the original objective : 
				// sum^nbrSessions (4 * sessions[i])
				for(int t = 0 ; t < nbrSessions ; t++){
					GRBLinExpr memExpr = new GRBLinExpr();
					memExpr.addTerm(4, sessions[t]);
					expr.add(memExpr);
				}

				model.setObjective(expr, GRB.MINIMIZE);
				
				addBannedConstraint(model);
				
				addFixedConstraint(model);

				addMoreProfsThanTFEConstraint(model);

				addOneSessionPerTFEConstraint(model);

				addMax3TFESessionConstraint(model);

				addDisponibleProfConstraint(model);

				addParallelProfConstraint(model);

				addProfLinkedConstraints(model);
				
				addSetSessionConstraint(model);

				//model.getEnv().set(GRB.IntParam.SolutionLimit, 1);
				model.getEnv().set(GRB.DoubleParam.TimeLimit, 60*timeLimit);

				// Optimize model
				model.optimize();

				System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

				int assigned = 0;
				for(int i = 0 ; i < tfes.length ; i++){
					String result = jsonParsingObject.getTfes().get(i)+" : ";
					for(int t = 0 ; t < tfes[0].length ; t++){
						if(tfes[i][t].get(GRB.DoubleAttr.X) == 1.0){
							tfeList.get(i).setFixedSession(t);
							result = result+t;
							assigned++;
							System.out.println(result);
						}
					}
				}
				
				makeReport(assigned, jsonParsingObject.getTfes().size()-tfeList.size(), timeLimit);

				model.write("scheduler.sol");
				
				model.dispose();
				env.dispose();
				
				return true;
			} catch (GRBException e1) {
				System.out.println("Error code: " + e1.getErrorCode() + ". " +
						e1.getMessage());
			}
		}
		return false;
	}

	/**
	 * Initialize the model with the variables
	 * @param env : the environment of the model
	 * @return The model
	 * @throws GRBException
	 */
	private GRBModel initialize(GRBEnv env) throws GRBException{
		GRBModel model = new GRBModel(env);

		// Create variables
		tfes = new GRBVar[nbrTFE][nbrSessions];
		for(int i = 0 ; i < tfes.length ; i++){
			for(int t = 0 ; t < tfes[0].length ; t++){
				tfes[i][t] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "tfe"+i+","+t);
			}
		}
		profs = new GRBVar[nbrJury][nbrSessions];
		for(int i = 0 ; i < profs.length ; i++){
			for(int t = 0 ; t < profs[0].length ; t++){
				profs[i][t] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "prof"+i+","+t);
			}
		}
		
		sessions = new GRBVar[nbrSessions];
		for(int i = 0 ; i < nbrSessions ; i++){
			sessions[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "session"+i);
		}

		// Integrate new variables
		model.update();

		return model;
	}
	
	/**
	 * Remove tfes with too constrained juries
	 * ex: if one jury is available for only one session and
	 * 		he has 4 or more tfes to attend.
	 */
	private List<TFE> preprocessingJury(List<TFE> tfes){
		List<TFE> tfesToBeRemoved = new ArrayList<TFE>();
		List<TFE> tfesResult = new ArrayList<TFE>(tfes);
		
		Map<String, Integer> juryTfeNbrMap = getJuryTFENbrMap(juryList);
		
		for(TFE t : tfesResult){
			for(Jury j : t.getJuryList()){
				if(juryTfeNbrMap.get(j.getEmail()) > j.getNumberSessionDisp()*3){
					tfesToBeRemoved.add(t);
					break;
				}
			}
		}
		
		for(TFE t : tfesToBeRemoved){
			impossibleTFE.add(t.getCode());
			if(!jsonParsingObject.getFixed().contains(t)){
				tfesResult.remove(t);
				System.out.println(t.getCode()+" removed");
			}
		}
		return tfesResult;
	}

	/**
	 * Remove all the unsolvable tfes from the solver
	 */
	private List<TFE> preprocessing(List<TFE> tfes){
		List<TFE> tfesToBeRemoved = new ArrayList<TFE>();
		List<TFE> tfesResult = new ArrayList<TFE>(tfes);
		impossibleTFE = new ArrayList<String>();
		for(TFE t : tfesResult){
			boolean mustRemove = true;
			for(int d = 0 ; d < 12 ; d++){
				boolean allSet = true;
				for(Jury j : t.getJuryList()){
					if(!j.getDisponibilities().get(d))
						allSet = false;
				}
				if(allSet){
					mustRemove = false;
					break;
				}
			}
			if(mustRemove){
				tfesToBeRemoved.add(t);
			}
		}
		for(TFE t : tfesToBeRemoved){
			impossibleTFE.add(t.getCode());
			if(!jsonParsingObject.getFixed().contains(t)){
				tfesResult.remove(t);
				System.out.println(t.getCode()+" removed");
			}
		}
		return tfesResult;
	}
	
	private void makeReport(int nbrTFEAssigned, int nbrTFEImpossible, int time) throws IOException{
		SolverReport report = new SolverReport(jsonParsingObject);
		report.setNbrTFE(jsonParsingObject.getTfes().size());
		report.setNbrJury(nbrJury);
		report.setNbrSessions(nbrSessions);
		report.setNbrTFEAssigned(nbrTFEAssigned);
		report.setNbrTFEImpossible(nbrTFEImpossible);
		report.setTime(time);
		report.setImpossibleTFE(impossibleTFE);
		File theDir = new File("static");
		if (!theDir.exists()) {
			theDir.mkdir();
		}
		report.write("static/report.txt");
	}
	
	/**
	 * Add constraint : tfes[bannedTfe][bannedSession] == 0
	 * Banned the session for the tfe
	 * @param model : the GRBModel
	 */
	private void addBannedConstraint(GRBModel model){
		try{
			for(TFE t : jsonParsingObject.getBanned()){
				GRBLinExpr expr = new GRBLinExpr();
				expr.addTerm(1, tfes[tfeList.indexOf(t)][t.getBannedSession()]);
				model.addConstr(expr, GRB.EQUAL, 0, "cBanned"+tfeList.indexOf(t));
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}
	
	/**
	 * Add constraint : tfes[fixedTfe][fixedSession] == 1
	 * Fixed the session for the tfe
	 * @param model : the GRBModel
	 */
	private void addFixedConstraint(GRBModel model){
		try{
			for(TFE t : getFixedTfes(jsonParsingObject.getFixed())){
				GRBLinExpr expr = new GRBLinExpr();
				expr.addTerm(1, tfes[tfeList.indexOf(t)][t.getFixedSession()]);
				model.addConstr(expr, GRB.EQUAL, 1, "cFixed"+tfeList.indexOf(t));
				forbidJury(t, model);
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}
	
	/**
	 * Get the list of the fixed tfes with all infos
	 * (Must change the jsonParsingObject.getFixed() in order to
	 * get the tfes with all infos)
	 * @param tfes : the fixed tfes
	 * @return fixed tfes with all infos
	 */
	private List<TFE> getFixedTfes(List<TFE> tfes){
		List<TFE> result = new ArrayList<TFE>();
		for(TFE t : tfeList){
			if(jsonParsingObject.getFixed().contains(t))
				result.add(t);
		}
		return result;
	}
	
	/**
	 * Forbid all the parallel sessions and the session concerned
	 * for all the jury of the tfe fixed
	 * @param t : The tfe fixed
	 * @param model : the GRBModel
	 */
	private void forbidJury(TFE t, GRBModel model){
		try{
			int s = t.getFixedSession();
			int sPerDays = (nbrSessions/nbrRooms);
			for(Jury j : t.getJuryList()){
				for(int l = 0 ; l < nbrRooms ; l++){
					GRBLinExpr expr = new GRBLinExpr();
					expr.addTerm(1, profs[juryList.indexOf(j)][(s%sPerDays)+(l*sPerDays)]);
					model.addConstr(expr, GRB.EQUAL, 0, "cForbid"+juryList.indexOf(j)+","+t.getFixedSession());
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	/**
	 * Add constraint : tfes[i][t] <= profs[j][t]
	 * More profs than tfes for a session t
	 * @param model : the GRBModel
	 */
	private void addMoreProfsThanTFEConstraint(GRBModel model){
		try{
			for(int t = 0 ; t < nbrSessions ; t++){
				GRBLinExpr expr = new GRBLinExpr();
				GRBLinExpr rightExpr = new GRBLinExpr();
				for(int i = 0 ; i < tfes.length ; i++){
					expr.addTerm(1, tfes[i][t]);
				}
				for(int i = 0 ; i < profs.length ; i++){
					rightExpr.addTerm(1, profs[i][t]);
				}
				model.addConstr(expr, GRB.LESS_EQUAL, rightExpr, "cMP"+t);
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	/**
	 * Add constraint : tfes[i][t] <= 1
	 * One tfe i can be assigned to only one session
	 * @param model : the GRBModel
	 */
	private void addOneSessionPerTFEConstraint(GRBModel model){
		try{
			for(int i = 0 ; i < tfes.length ; i++){
				GRBLinExpr expr = new GRBLinExpr();
				for(int t = 0 ; t < nbrSessions ; t++){
					expr.addTerm(1, tfes[i][t]);
				}
				model.addConstr(expr, GRB.LESS_EQUAL, 1, "cM"+i);
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	/**
	 * Add constraint : tfes[i][t] == 1
	 * One tfe i must be assigned to one session
	 * @param model : the GRBModel
	 */
	private void addTFEMustBeAssignedConstraint(GRBModel model){
		try{
			for(int i = 0 ; i < tfes.length ; i++){
				GRBLinExpr expr = new GRBLinExpr();
				for(int t = 0 ; t < nbrSessions ; t++){
					expr.addTerm(1, tfes[i][t]);
				}
				model.addConstr(expr, GRB.EQUAL, 1, "cM"+i);
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	/**
	 * Add constraint : tfes[i][t] <= 3
	 * Max 3 tfes per session
	 * @param model : the GRBModel
	 */
	private void addMax3TFESessionConstraint(GRBModel model){
		try{
			for(int t = 0 ; t < nbrSessions ; t++){
				GRBLinExpr expr = new GRBLinExpr();
				for(int i = 0 ; i < tfes.length ; i++){
					expr.addTerm(1, tfes[i][t]);
				}
				model.addConstr(expr, GRB.LESS_EQUAL, 3, "cS"+t);
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	/**
	 * Add constraint : profs[i][t] <= disp(profs[i][t])
	 * One prof i must be disponible at the session t
	 * @param model : the GRBModel
	 */
	private void addDisponibleProfConstraint(GRBModel model){
		try{
			for(int i = 0 ; i < profs.length ; i++){
				for(int t = 0 ; t < nbrSessions ; t++){
					model.addConstr(profs[i][t], GRB.LESS_EQUAL, juryList.get(i).getDisponibility(t%(nbrSessions/nbrRooms)), "cPdisp"+i+","+t);
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	/**
	 * Add constraint : profs[i][k+l*(nbrSessions/nbrRooms)] <= 1
	 * One prof i must have no parallel sessions
	 * @param model : the GRBModel
	 */
	private void addParallelProfConstraint(GRBModel model){
		try{
			for(int i = 0 ; i < profs.length ; i++){
				for(int k = 0 ; k < nbrSessions/nbrRooms ; k++){
					GRBLinExpr expr = new GRBLinExpr();
					for(int l = 0 ; l < nbrRooms ; l++){
						expr.addTerm(1, profs[i][k+(l*(nbrSessions/nbrRooms))]);
					}
					model.addConstr(expr, GRB.LESS_EQUAL, 1, "cPparallel"+i+","+k);
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	/**
	 * Add constraint : tfes[i][t] <= profs[j][t]
	 * profs linked to tfes have the same session
	 * @param model : the GRBModel
	 */
	private void addProfLinkedConstraints(GRBModel model){
		try{
			for(int i = 0 ; i < tfes.length ; i++){
				if(!jsonParsingObject.getFixed().contains(tfeList.get(i))){
					for(int t = 0 ; t < nbrSessions ; t++){
						GRBLinExpr leftExpr = new GRBLinExpr();
						leftExpr.addTerm(1, tfes[i][t]);
						for(Jury j : tfeList.get(i).getJuryList()){
							GRBLinExpr expr = new GRBLinExpr();
							expr.addTerm(1, profs[juryList.indexOf(j)][t]);
							model.addConstr(leftExpr, GRB.LESS_EQUAL, expr, "cMJury"+i+","+j+","+t);
						}
					}
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}
	
	/**
	 * Add contraint : session[t] >= tfe[i][t]
	 * If the tfe is set to session t then session t is set
	 * @param model : the GRBModel
	 */
	private void addSetSessionConstraint(GRBModel model){
		try{
			for(int i = 0 ; i < tfes.length ; i++){
				for(int t = 0 ; t < nbrSessions ; t++){
					GRBLinExpr leftExpr = new GRBLinExpr();
					leftExpr.addTerm(1, sessions[t]);
					GRBLinExpr expr = new GRBLinExpr();
					expr.addTerm(1, tfes[i][t]);
					model.addConstr(leftExpr, GRB.GREATER_EQUAL, expr, "cSessionTFE"+t+","+i);
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	private boolean isReserve(int session, int day){
		return session%12 >= 4*day && session%12 <= (4*(day+1))-1;
	}
	
	private List<Jury> getJuryList(){
		List<Jury> juryList = new ArrayList<Jury>();
		Map<String, Jury> juryMap = new HashMap<String, Jury>();
		for(Jury j : jsonParsingObject.getAdvisors()){
			juryMap.put(j.getEmail(), j);
		}
		for(Jury j : jsonParsingObject.getReaders()){
			juryMap.put(j.getEmail(), j);
		}
		juryList.addAll(juryMap.values());
		return juryList;
	}

	private List<Integer> getJuryTFENbr(List<Jury> juryList){
		List<Integer> tfeNbr = new ArrayList<Integer>();
		for(Jury j : juryList){
			int count = 0;
			for(TFE t : jsonParsingObject.getTfes()){
				if(t.containsJury(j))
					count++;
			}
			tfeNbr.add(count);
		}
		return tfeNbr;
	}
	
	private Map<String, Integer> getJuryTFENbrMap(List<Jury> juryList){
		Map<String, Integer> tfeNbrMap = new HashMap<String, Integer>();
		for(Jury j : juryList){
			int count = 0;
			for(TFE t : jsonParsingObject.getTfes()){
				if(t.containsJury(j))
					count++;
			}
			tfeNbrMap.put(j.getEmail(), count);
		}
		return tfeNbrMap;
	}
}
