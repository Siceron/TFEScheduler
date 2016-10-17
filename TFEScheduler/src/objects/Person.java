package objects;

public class Person {

	private String email;
	private String faculty;
	
	public Person(String email, String faculty) {
		super();
		this.email = email;
		this.faculty = faculty;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getFaculty() {
		return faculty;
	}
	
	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
}
