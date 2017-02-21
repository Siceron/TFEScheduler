package objects;

import java.util.ArrayList;
import java.util.List;

public class Commission{

	private String faculty;
	private List<TFE> tfes;
	
	public Commission(String faculty) {
		this.faculty = faculty;
		this.tfes = new ArrayList<TFE>();
	}

	public List<TFE> getTfes() {
		return tfes;
	}
	
	public void addTfeList(List<TFE> tfes){
		if(this.tfes == null)
			this.tfes = new ArrayList<TFE>();
		this.tfes.addAll(tfes);
	}

	public String getFaculty() {
		return faculty;
	}
	
	@Override
	public String toString(){
		return faculty;
	}
}
