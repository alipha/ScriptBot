
public class BreakStatementException extends RuntimeException {
	
	public boolean continueStatement;
	
	public BreakStatementException(boolean continueStatement) { 
		super((continueStatement ? "Continue" : "Break") + " statement not expected");
		
		this.continueStatement = continueStatement;
	}
	
	public boolean isContinue() { return continueStatement; }
}
