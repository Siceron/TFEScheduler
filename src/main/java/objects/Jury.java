package objects;

import java.util.List;

public class Jury extends Person {
	
	private List<Boolean> disponibilities;

	public Jury(String email, String faculty, List<Boolean> disponibilities) {
		super(email, faculty);
		this.disponibilities = disponibilities;
	}

	public List<Boolean> getDisponibilities() {
		return disponibilities;
	}
}
