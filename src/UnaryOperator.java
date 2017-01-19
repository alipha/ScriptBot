
public abstract class UnaryOperator extends Operator {

	public TokenNode operand;
	
	
	public UnaryOperator(String op) { super(op); }
	
	public boolean rightAssociative() { return true; }
	
	public boolean expectBinaryOperator() { return false; }
	
	
	public TokenNode replaceChild(TokenNode child) {
		TokenNode old = operand;
		operand = child;
		return old;
	}
	
		
	public String toString() {
		//if(operand.precedence() < this.precedence()) {
		//	return operator + "(" + operand.toString() + ")";
		//}
		return operator + operand.toString();
	} 
}
