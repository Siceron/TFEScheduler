package objects;

public class Session {

	private String code;
	private int session;
	
	public Session(String code, int session) {
		super();
		this.code = code;
		this.session = session;
	}
	
	public String getCode() {
		return code;
	}
	
	public int getSession() {
		return session;
	}
}
