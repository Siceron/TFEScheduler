package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import objects.JSONParsingObject;
import objects.Jury;
import objects.TFE;

public class SolverReport {

	private int time;
	private int nbrSessions;
	private int nbrJury;
	private int nbrTFE;
	private int nbrTFEAssigned;
	private int nbrTFEImpossible;
	private List<String> impossibleTFE;
	private Map<Jury, List<Integer>> juryParallel;
	private Map<Integer,List<TFE>> map;
	private JSONParsingObject jsonParsingObject;
	
	public SolverReport(JSONParsingObject jsonParsingObject) {
		this.jsonParsingObject = jsonParsingObject;
		this.map = getSessionMap();
	}
	
	public void write(String path) throws IOException{
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		writeStats(bw);
		writeConflicts(bw);
		writeAssigned(bw);
		writeNotDisponible(bw);
		writeParallel(bw);
		bw.close();
	}
	
	private void writeStats(BufferedWriter bw) throws IOException{
		bw.write("Statistics :\n");
		bw.write("\t- Solution found in : "+time+"min\n");
		bw.write("\t- Sessions allocated : "+map.size()+"\n");
		bw.write("\t- Jury : "+nbrJury+"\n");
		bw.write("\t- TFE : "+nbrTFE+"\n");
		bw.write("\t- TFE impossible : "+nbrTFEImpossible+"\n");
		bw.write("\t- TFE assigned : "+nbrTFEAssigned+"\n");
		bw.write("\t  ratio : "+(((float)nbrTFEAssigned)/nbrTFE)*100+"%\n");
	}
	
	private void writeNotDisponible(BufferedWriter bw) throws IOException{
		bw.write("Not disponible :\n");
		boolean isNotDisponible = false;
		for(Entry<Integer, List<TFE>> entry : map.entrySet()) {
			if(entry.getKey() != -1){
				for(TFE t : entry.getValue()){
					for(Jury j : t.getJuryList()){
						if(!j.getDisponibilities().get(entry.getKey()%12)){
							isNotDisponible = true;
							bw.write("\t-"+j.getEmail()+" for "+t.getCode()+" session "+entry.getKey()+"\n");
						}
					}
				}
			}
		}
		if(!isNotDisponible)
			bw.write("\t Everyone can attend\n");
	}
	
	private void writeParallel(BufferedWriter bw) throws IOException{
		bw.write("Parallel sessions :\n");
		boolean isParallel = false;
		for(Entry<Jury, List<Integer>> entry : getJuryMap().entrySet()){
			List<Integer> impactedSessions = getParallelSessions(entry.getValue());
			if(!impactedSessions.isEmpty()){
				isParallel = true;
				bw.write("\t-"+entry.getKey().getEmail()+" for "+impactedSessions.toString()+"\n");
			}
		}
		if(!isParallel)
			bw.write("\t No parallel sessions\n");
	}
	
	private List<Integer> getParallelSessions(List<Integer> sessions){
		List<Integer> impactedSessions = new ArrayList<Integer>();
		for(int i = 0 ; i < sessions.size() ; i++){
			for(int j = 0 ; j < sessions.size() ; j++){
				if(i!=j && (sessions.get(i)%12 == sessions.get(j)%12)){
					impactedSessions.add(sessions.get(i));
				}
			}
		}
		return impactedSessions;
	}
	
	private void writeConflicts(BufferedWriter bw) throws IOException{
		bw.write("Conflicts :\n");
		boolean isConflicts = false;
		for (Entry<Integer, List<TFE>> entry : map.entrySet()) {
			List<TFE> value = entry.getValue();
			if(value.size() > 3 && entry.getKey() != -1){
				isConflicts = true;
				bw.write("\t- Session "+entry.getKey()+" :\n");
				for(TFE t : value) bw.write("\t\t - "+t.getCode()+"\n");
			}
		}
		if(!isConflicts)
			bw.write("\t No conflicts\n");
	}

	private void writeAssigned(BufferedWriter bw) throws IOException{
		bw.write("Not assigned :\n");
		boolean notAssigned = false;
		for (Entry<Integer, List<TFE>> entry : map.entrySet()) {
			List<TFE> value = entry.getValue();
			if(entry.getKey() == -1){
				notAssigned = true;
				for(TFE t : value){
					if(impossibleTFE.contains(t.getCode()))
						bw.write("\t - "+t.getCode()+" (impossible)\n");
					else
						bw.write("\t - "+t.getCode()+"\n");
				}
			}
		}
		if(!notAssigned)
			bw.write("\t Every TFE is assigned\n");
	}
	
	private Map<Integer,List<TFE>> getSessionMap(){
		Map<Integer,List<TFE>> map = new HashMap<Integer,List<TFE>>();
		for(TFE tfe : jsonParsingObject.getTfes()){
			if(map.containsKey(tfe.getFixedSession())){
				map.get(tfe.getFixedSession()).add(tfe);
			}
			else{
				List<TFE> newList = new ArrayList<TFE>();
				newList.add(tfe);
				map.put(tfe.getFixedSession(), newList);
			}
		}
		return map;
	}
	
	private Map<Jury, List<Integer>> getJuryMap(){
		Map<Jury, List<Integer>> map = new HashMap<Jury, List<Integer>>();
		for(TFE tfe : jsonParsingObject.getTfes()){
			for(Jury j : tfe.getJuryList()){
				if(map.containsKey(j)){
					if(!map.get(j).contains(tfe.getFixedSession()))
						map.get(j).add(tfe.getFixedSession());
				}
				else{
					List<Integer> newList = new ArrayList<Integer>();
					newList.add(tfe.getFixedSession());
					map.put(j, newList);
				}
			}
		}
		return map;
	}
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getNbrSessions() {
		return nbrSessions;
	}
	public void setNbrSessions(int nbrSessions) {
		this.nbrSessions = nbrSessions;
	}
	public int getNbrJury() {
		return nbrJury;
	}
	public void setNbrJury(int nbrJury) {
		this.nbrJury = nbrJury;
	}
	public int getNbrTFE() {
		return nbrTFE;
	}
	public void setNbrTFE(int nbrTFE) {
		this.nbrTFE = nbrTFE;
	}
	public int getNbrTFEAssigned() {
		return nbrTFEAssigned;
	}
	public void setNbrTFEAssigned(int nbrTFEAssigned) {
		this.nbrTFEAssigned = nbrTFEAssigned;
	}
	public int getNbrTFEImpossible() {
		return nbrTFEImpossible;
	}
	public void setNbrTFEImpossible(int nbrTFEImpossible) {
		this.nbrTFEImpossible = nbrTFEImpossible;
	}
	public List<String> getImpossibleTFE() {
		return impossibleTFE;
	}
	public void setImpossibleTFE(List<String> impossibleTFE) {
		this.impossibleTFE = impossibleTFE;
	}
	public Map<Jury, List<Integer>> getJuryParallel() {
		return juryParallel;
	}
	public void setJuryParallel(Map<Jury, List<Integer>> juryParallel) {
		this.juryParallel = juryParallel;
	}
}
