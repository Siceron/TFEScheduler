package objects;

import java.util.ArrayList;
import java.util.List;

public class Jury extends Person {
	
	private List<Boolean> disponibilities;
	private List<Integer> sessions;

	public Jury(String email, String faculty, List<Boolean> disponibilities) {
		super(email, faculty);
		this.disponibilities = disponibilities;
		this.sessions = new ArrayList<Integer>();
	}

	public List<Boolean> getDisponibilities() {
		return disponibilities;
	}
	
	public int getDisponibility(int index){
		if(disponibilities.get(index))
			return 1;
		else
			return 0;
	}
	
	public void addSession(int session){
		sessions.add(session);
	}
	
	public List<Integer> getSessions(){
		return sessions;
	}
	
	public int getNumberSessionDisp(){
		int count = 0;
		for(boolean b : disponibilities){
			if(b) count++;
		}
		return count;
	}
}
