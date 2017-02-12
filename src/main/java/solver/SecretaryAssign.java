package solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import objects.JSONParsingObject;
import objects.Jury;
import objects.Person;
import objects.Secretary;
import objects.TFE;

/**
 * @author ludovic fastré
 *
 * This class assign each TFE to a secretary
 * 
 * The solver algorithm is a greedy algorithm based on the
 * faculty of the professor and the students
 */
public class SecretaryAssign {

	private Map<String, List<TFE>> secretaryMap;

	public SecretaryAssign(JSONParsingObject infos) {
		this.secretaryMap = new HashMap<String, List<TFE>>();
		fillSecretaryMap(infos);
	}

	/**
	 * Enter the informations into the secretary map
	 * @param infos : the informations to put in the map
	 */
	private void fillSecretaryMap(JSONParsingObject infos){
		secretaryMap.put("ELEC", new ArrayList<TFE>());
		secretaryMap.put("ELME", new ArrayList<TFE>());
		secretaryMap.put("GBIO", new ArrayList<TFE>());
		secretaryMap.put("FYAP", new ArrayList<TFE>());
		secretaryMap.put("KIMA", new ArrayList<TFE>());
		secretaryMap.put("GCE", new ArrayList<TFE>());
		secretaryMap.put("INFO", new ArrayList<TFE>());
		secretaryMap.put("SINF", new ArrayList<TFE>());
		secretaryMap.put("MAP", new ArrayList<TFE>());
		secretaryMap.put("MECA", new ArrayList<TFE>());
		secretaryMap.put("UNK", new ArrayList<TFE>());
		List<TFE> tfes = infos.getTfes();
		for(TFE tfe : tfes){
			String faculty = getFaculty(tfe).toUpperCase();
			if(faculty.equals("INGI"))
				faculty = "INFO";
			else if(faculty.equals("ELEN"))
				faculty = "ELEC";
			else if(faculty.equals("INMA"))
				faculty = "MAP";
			if(secretaryMap.containsKey(faculty))
				secretaryMap.get(faculty).add(tfe);
			else{
				secretaryMap.get("UNK").add(tfe);
				System.out.println("Faculty : "+faculty+" does not exist");
			}

		}
	}

	/**
	 * Return a map with each faculty and its TFE associated
	 * @return A Map key<String>:faculty, value<List<TFE>>:tfes
	 */
	public Map<String, List<TFE>> solve(){
		return secretaryMap;
	}

	/**
	 * Assign the tfes for all the secretaries
	 * @param secretaries : The list of secretaries
	 */
	public void solve(List<Secretary> secretaries){
		for(Secretary secretary : secretaries){
			for(String faculty : secretary.getFaculties()){
				if(secretaryMap.containsKey(faculty))
					secretary.addTfeList(secretaryMap.get(faculty));
				else
					System.out.println("Faculty : "+faculty+" does not exist");
			}
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
		for(Jury advisor : tfe.getAdvisors()){
			String faculty = getFacultyPrefix(advisor.getFaculty());
			if(!faculty.equals("UNK"))
				facultyOccurrenceMap.put(faculty,
						facultyOccurrenceMap.getOrDefault(faculty, 0)+1);
		}
		for(Jury reader : tfe.getAdvisors()){
			String faculty = getFacultyPrefix(reader.getFaculty());
			if(!faculty.equals("UNK"))
				facultyOccurrenceMap.put(faculty,
						facultyOccurrenceMap.getOrDefault(faculty, 0)+1);
		}
		if(facultyOccurrenceMap.isEmpty())
			return "UNK";
		else
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
		Comparator<? super Entry<String, Integer>> maxValueComparator = (entry1, entry2) ->
		entry1.getValue().compareTo(entry2.getValue());

		Optional<Entry<String, Integer>> maxValue = map.entrySet()
				.stream().max(maxValueComparator);

		return maxValue.get().getKey();
	}
}
