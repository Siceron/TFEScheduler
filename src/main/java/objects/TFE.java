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

	public void setCode(String code) {
		this.code = code;
	}

	public List<Person> getStudents() {
		return students;
	}

	public void setStudents(List<Person> students) {
		this.students = students;
	}

	public List<Jury> getAdvisors() {
		return advisors;
	}

	public void setAdvisors(List<Jury> advisors) {
		this.advisors = advisors;
	}

	public List<Jury> getReaders() {
		return readers;
	}

	public void setReaders(List<Jury> readers) {
		this.readers = readers;
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

	public void setBannedSession(int bannedSession) {
		this.bannedSession = bannedSession;
	}
}
