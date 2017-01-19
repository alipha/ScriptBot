
public abstract class BinaryOperator extends Operator {

	public TokenNode left;
	public TokenNode right;
	
	
	public BinaryOperator(String op) { super(op); }
	
	
	public abstract Object perform(Object leftValue, Object rightValue);
	
	
	public TokenNode replaceChild(TokenNode child) {
		if(left == null) {
			left = child;
			return null;
		}
		
		TokenNode old = right;
		right = child;
		return old;
	}
	
	public Object evaluate() {
		Object leftValue = left.evaluate();
		return perform(leftValue, right.evaluate());
	}
	
	public String leftPadding() {
		return " ";
	}
	
	public String rightPadding() {
		return " ";
	}

	public String toString() {
		String output = "";

		//if(left.precedence() < this.precedence()) {
		//	output = "(" + left.toString() + ")"; 
		//} else{
			output = left.toString();
		//}
		
		output += leftPadding() + operator + rightPadding();
		
		//if(right.precedence() < this.precedence()) {
		//	output += "(" + right.toString() + ")"; 
		//} else {
			output += right.toString();
		//}
		
		return output;
	}
	
	public boolean expectBinaryOperator() { return false; }
}
