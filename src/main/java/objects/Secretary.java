package objects;

import java.util.ArrayList;
import java.util.List;

public class Secretary{

	private String email;
	private List<String> faculties;
	private List<TFE> tfes;
	
	public Secretary(String email, List<String> faculties) {
		this.email = email;
		this.faculties = faculties;
		this.tfes = new ArrayList<TFE>();
	}

	public List<TFE> getTfes() {
		return tfes;
	}
	
	public void addTfeList(List<TFE> tfes){
		this.tfes.addAll(tfes);
	}

	public String getEmail() {
		return email;
	}

	public List<String> getFaculties() {
		return faculties;
	}
}
