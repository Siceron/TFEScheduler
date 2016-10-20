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
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public int getSession() {
		return session;
	}
	
	public void setSession(int session) {
		this.session = session;
	}
}
