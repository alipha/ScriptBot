
public class ReturnStatementException extends RuntimeException {
	public Object value;
	
	public ReturnStatementException(Object value) {
		super("Return statement not expected");
		this.value = value;
	}
	
	public Object getValue() { return this.value; }
}
