package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import objects.Commission;
import objects.JSONParsingObject;
import objects.Person;
import objects.TFE;

/**
 * @author ludovic fastré
 *
 * This class assign each TFE to a commission
 * 
 * The solver algorithm is a greedy algorithm based on the
 * faculty of the professor and the students
 */
public class CommissionAssign {

	private Map<String, List<TFE>> CommissionMap;

	public CommissionAssign(JSONParsingObject infos) {
		this.CommissionMap = new HashMap<String, List<TFE>>();
		fillCommissionMap(infos);
	}

	/**
	 * Enter the informations into the commission map
	 * @param infos : the informations to put in the map
	 */
	private void fillCommissionMap(JSONParsingObject infos){
		CommissionMap.put("ELEC", new ArrayList<TFE>());
		CommissionMap.put("ELME", new ArrayList<TFE>());
		CommissionMap.put("GBIO", new ArrayList<TFE>());
		CommissionMap.put("FYAP", new ArrayList<TFE>());
		CommissionMap.put("KIMA", new ArrayList<TFE>());
		CommissionMap.put("NANO", new ArrayList<TFE>());
		CommissionMap.put("GCE", new ArrayList<TFE>());
		CommissionMap.put("INFO", new ArrayList<TFE>());
		CommissionMap.put("SINF", new ArrayList<TFE>());
		CommissionMap.put("MAP", new ArrayList<TFE>());
		CommissionMap.put("MECA", new ArrayList<TFE>());
		CommissionMap.put("UNK", new ArrayList<TFE>());
		List<TFE> tfes = infos.getTfes();
		for(TFE tfe : tfes){
			String faculty = getFaculty(tfe).toUpperCase();
			if(faculty.equals("INGI"))
				faculty = "INFO";
			else if(faculty.equals("ELEN"))
				faculty = "ELEC";
			else if(faculty.equals("INMA"))
				faculty = "MAP";
			if(CommissionMap.containsKey(faculty))
				CommissionMap.get(faculty).add(tfe);
			else{
				CommissionMap.get("UNK").add(tfe);
				System.out.println("Faculty : "+faculty+" does not exist");
			}

		}
	}

	/**
	 * Return a map with each faculty and its TFE associated
	 * @return A Map key<String>:faculty, value<List<TFE>>:tfes
	 */
	public Map<String, List<TFE>> solve(){
		return CommissionMap;
	}

	/**
	 * Assign the tfes for all the commissions
	 * @param commissions : The list of commissions
	 */
	public void solve(List<Commission> commissions){
		for(Commission commission : commissions){
			if(CommissionMap.containsKey(commission.getFaculty()))
				commission.addTfeList(CommissionMap.get(commission.getFaculty()));
			else
				System.out.println("Faculty : "+commission.getFaculty()+" does not exist");
		}
	}

	/**
	 * Get the most representative faculty of the TFE
	 * @return A String representing the faculty
	 */
	private String getFaculty(TFE tfe){
		Map<String, Integer> facultyOccurrenceMap = new HashMap<String, Integer>();
		for(Person student : tfe.getStudents()){
			String faculty = getFacultyPrefix(student.getFaculty());
			if(!faculty.equals("UNK"))
				facultyOccurrenceMap.put(faculty,
						facultyOccurrenceMap.getOrDefault(faculty, 0)+1);
		}
		return keyOfMaxValue(facultyOccurrenceMap);
	}
	
	/**
	 * Get the prefix of the faculty (ex : 'MECA' for 'MECA21MS/G')
	 * @param faculty : the faculty to get the prefix
	 * @return the prefix of the faculty
	 */
	private String getFacultyPrefix(String faculty){
		if(faculty.contains("ELEC"))
			return "ELEC";
		else if(faculty.contains("ELME"))
			return "ELME";
		else if(faculty.contains("GBIO"))
			return "GBIO";
		else if(faculty.contains("FYAP"))
			return "FYAP";
		else if(faculty.contains("KIMA"))
			return "KIMA";
		else if(faculty.contains("NANO"))
			return "NANO";
		else if(faculty.contains("GCE"))
			return "GCE";
		else if(faculty.contains("INFO"))
			return "INFO";
		else if(faculty.contains("SINF"))
			return "SINF";
		else if(faculty.contains("MAP"))
			return "MAP";
		else if(faculty.contains("MECA"))
			return "MECA";
		else if(faculty.contains("INGI"))
			return "INGI";
		else if(faculty.contains("ELEN"))
			return "ELEN";
		else if(faculty.contains("INMA"))
			return "INMA";
		else
			return faculty;
	}

	/**
	 * Get the key with the largest value
	 * @param map : the map containing the values
	 * @return A String representing the key with the largest value
	 */
	private String keyOfMaxValue(Map<String, Integer> map) {
		int max = 0;
		String result = "UNK";
		for(Entry<String, Integer> e : map.entrySet()){
			if(e.getValue() > max){
				max = e.getValue();
				result = e.getKey();
			}
			else if(e.getValue() == max){
				result = "UNK";
			}
		}
		return result;
	}
}
