package support;

/**
 * Enum of possible commands
 * 
 * @author Tom Stappaerts, Benjamin Stienlet
 */
public enum Command {

	GET(false), PUT(true), HEAD(false), POST(true);
	
	private Command(boolean needsInput) {
		this.needsInput = needsInput;
	}
	
	public boolean needsInput() {
		return needsInput;
	}
	
	private final boolean needsInput;
	
}
