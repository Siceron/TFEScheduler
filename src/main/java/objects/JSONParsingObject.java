package objects;

import java.util.List;

public class JSONParsingObject {
	
	private List<TFE> tfes;
	private List<TFE> fixed;
	private List<TFE> banned;
	
	public JSONParsingObject(List<TFE> tfes, List<TFE> fixed, List<TFE> banned) {
		super();
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
}
