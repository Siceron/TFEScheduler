package objects;

import java.util.List;

public class JSONParsingObject {
	
	private int sessionNumber;
	private int sessionDays;
	private int sessionRooms;
	private List<Secretary> secretaries;
	private List<Jury> advisors;
	private List<Jury> readers;
	private List<TFE> tfes;
	private List<TFE> fixed;
	private List<TFE> banned;
	
	public JSONParsingObject(int sessionNumber, int sessionDays, int sessionRooms, List<Secretary> secretaries,
			List<Jury> advisors, List<Jury> readers, List<TFE> tfes, List<TFE> fixed, List<TFE> banned) {
		this.sessionNumber = sessionNumber;
		this.sessionDays = sessionDays;
		this.sessionRooms = sessionRooms;
		this.secretaries = secretaries;
		this.advisors = advisors;
		this.readers = readers;
		this.tfes = tfes;
		this.fixed = fixed;
		this.banned = banned;
	}
	
	public List<TFE> getTfes() {
		return tfes;
	}
	
	public List<TFE> getFixed() {
		return fixed;
	}
	
	public List<TFE> getBanned() {
		return banned;
	}

	public int getSessionNumber() {
		return sessionNumber;
	}

	public int getSessionDays() {
		return sessionDays;
	}

	public int getSessionRooms() {
		return sessionRooms;
	}

	public List<Secretary> getSecretaries() {
		return secretaries;
	}

	public List<Jury> getAdvisors() {
		return advisors;
	}

	public List<Jury> getReaders() {
		return readers;
	}
}
