package objects;

import java.util.List;

public class TFE {
	
	private String code;
	private List<Person> students;
	private List<Jury> advisors;
	private List<Jury> readers;
	private int fixedSession;
	private int bannedSession;
	
	public TFE(String code, List<Person> students, List<Jury> advisors, List<Jury> readers) {
		super();
		this.code = code;
		this.students = students;
		this.advisors = advisors;
		this.readers = readers;
		this.fixedSession = -1;
	}
	
	public TFE(String code, int session, boolean isFixed) {
		super();
		this.code = code;
		if(isFixed)
			this.fixedSession = session;
		else
			this.bannedSession = session;
	}

	public String getCode() {
		return code;
	}

	public List<Person> getStudents() {
		return students;
	}

	public List<Jury> getAdvisors() {
		return advisors;
	}

	public List<Jury> getReaders() {
		return readers;
	}

	public int getFixedSession() {
		return fixedSession;
	}

	public void setFixedSession(int fixedSession) {
		this.fixedSession = fixedSession;
	}

	public int getBannedSession() {
		return bannedSession;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		TFE other = (TFE) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	
	@Override
	public String toString(){
		return code;
	}
}
