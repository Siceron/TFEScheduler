package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;
import objects.JSONParsingObject;
import objects.Jury;
import objects.TFE;

public class Scheduler {

	private JSONParsingObject jsonParsingObject;
	private int nbrSessions;
	private List<Jury> juryList;
	private List<TFE> tfeList;
	private int nbrJury;
	private int nbrTFE;
	private int nbrRooms;
	private List<Integer> juryTFENbr;
	private GRBVar tfes[][];
	private GRBVar profs[][];

	public Scheduler(JSONParsingObject jsonParsingObject){
		this.jsonParsingObject = jsonParsingObject;
	}

	public void solve(int timeLimit){
		try {
			GRBEnv    env   = new GRBEnv("mip1.log");
			GRBModel  model = new GRBModel(env);

			nbrSessions = jsonParsingObject.getSessionNumber();
			juryList = getJuryList();
			tfeList = jsonParsingObject.getTfes();
			nbrJury = juryList.size();
			nbrTFE = tfeList.size();
			nbrRooms = jsonParsingObject.getSessionRooms();
			juryTFENbr = getJuryTFENbr(juryList);

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

			// Integrate new variables
			model.update();

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
			for(int i = 0 ; i < tfes.length ; i++){
				GRBLinExpr memExpr = new GRBLinExpr();
				for(int t = 0 ; t < tfes[0].length ; t++){
					memExpr.addTerm(-10, tfes[i][t]);
				}
				expr.add(memExpr);
			}
			model.setObjective(expr, GRB.MINIMIZE);

			addMoreProfsThanTFEConstraint(model);

			addOneSessionPerTFEConstraint(model);

			addMax3TFESessionConstraint(model);

			addDisponibleProfConstraint(model);

			addParallelProfConstraint(model);

			addProfLinkedConstraints(model);

			//model.getEnv().set(GRB.IntParam.SolutionLimit, 1);
			model.getEnv().set(GRB.DoubleParam.TimeLimit, 60*timeLimit);
			
			// Optimize model
			model.optimize();
			
			System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

			for(int i = 0 ; i < tfes.length ; i++){
				String result = jsonParsingObject.getTfes().get(i)+" : ";
				for(int t = 0 ; t < tfes[0].length ; t++){
					if(tfes[i][t].get(GRB.DoubleAttr.X) == 1.0){
						tfeList.get(i).setFixedSession(t);
						result = result+t+" ";
					}
				}
			}
			
			for(int i = 0 ; i < profs.length ; i++){
				for(int t = 0 ; t < profs[0].length ; t++){
					if(profs[i][t].get(GRB.DoubleAttr.X) == 1.0){
						juryList.get(i).addSession(t);
					}
				}
			}

			model.write("assets/outputs/scheduler.sol");

			model.dispose();
			env.dispose();

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
	 * Add constraint : tfes[i][t]*profs[i][j] = tfeList.get(i).getJuryList.size()
	 * profs linked to tfes have the same session
	 * @param model : the GRBModel
	 */
	private void addProfLinkedConstraints(GRBModel model){
		try{
			/*for(int i = 0 ; i < tfes.length ; i++){
				for(Jury j : tfeList.get(i).getJuryList()){
					GRBLinExpr expr = new GRBLinExpr();
					for(int t = 0 ; t < nbrSessions ; t++){
						//GRBQuadExpr quadExpr = new GRBQuadExpr();
						//quadExpr.addTerm(1, tfes[i][t], profs[juryList.indexOf(j)][t]);
						//expr.add(quadExpr.getLinExpr());
						//expr.addTerm(tfes[i][t].get(GRB.DoubleAttr.X), profs[juryList.indexOf(j)][t]);
					}
					System.out.println(expr.getValue());
					model.addConstr(expr, GRB.EQUAL, 1, "cMJury"+i+","+j);
				}
			}*/
			for(int i = 0 ; i < tfes.length ; i++){
				for(Jury j : tfeList.get(i).getJuryList()){
					GRBQuadExpr quadExpr = new GRBQuadExpr();
					GRBLinExpr expr = new GRBLinExpr();
					for(int t = 0 ; t < nbrSessions ; t++){
						quadExpr.addTerm(1, tfes[i][t], profs[juryList.indexOf(j)][t]);
						expr.addTerm(1, tfes[i][t]);
					}
					model.addQConstr(quadExpr, GRB.EQUAL, expr, "cMJury"+i+","+j);
				}
			}
			/*for(int i = 0 ; i < tfes.length ; i++){
				for(int t = 0 ; t < nbrSessions ; t++){
					GRBLinExpr expr = new GRBLinExpr();
					for(Jury j : tfeList.get(i).getJuryList()){
						GRBQuadExpr quadExpr = new GRBQuadExpr();
						quadExpr.addTerm(1, tfes[i][t], profs[juryList.indexOf(j)][t]);
						expr.add(quadExpr.getLinExpr());
					}
					model.addConstr(expr, GRB.EQUAL, tfeList.get(i).getJuryList().size(), "cMJury"+i+","+t);
				}
			}*/
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	private List<Jury> getJuryList(){
		List<Jury> juryList = new ArrayList<Jury>();
		Map<String, Jury> juryMap = new HashMap<String, Jury>();
		for(Jury j : jsonParsingObject.getReaders()){
			juryMap.put(j.getEmail(), j);
		}
		for(Jury j : jsonParsingObject.getAdvisors()){
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
}
