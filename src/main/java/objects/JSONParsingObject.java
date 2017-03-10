package objects;

import java.util.List;

public class JSONParsingObject {
	
	private int reserveDay;
	private int sessionDays;
	private int sessionRooms;
	private List<Commission> commissions;
	private List<Jury> advisors;
	private List<Jury> readers;
	private List<TFE> tfes;
	private List<TFE> fixed;
	private List<TFE> banned;
	
	public JSONParsingObject(int reserveDay, int sessionDays, int sessionRooms, List<Commission> commissions,
			List<Jury> advisors, List<Jury> readers, List<TFE> tfes, List<TFE> fixed, List<TFE> banned) {
		this.reserveDay = reserveDay;
		this.sessionDays = sessionDays;
		this.sessionRooms = sessionRooms;
		this.commissions = commissions;
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

	public int getReserveDay() {
		return reserveDay;
	}

	public int getSessionDays() {
		return sessionDays;
	}

	public int getSessionRooms() {
		return sessionRooms;
	}

	public List<Commission> getCommissions() {
		return commissions;
	}

	public List<Jury> getAdvisors() {
		return advisors;
	}

	public List<Jury> getReaders() {
		return readers;
	}
}
