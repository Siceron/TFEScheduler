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
	
	public String getFaculty() {
		return faculty;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}
}
