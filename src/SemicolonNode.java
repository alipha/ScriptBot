
public class SemicolonNode extends BinaryOperator {

	public SemicolonNode() { super(";"); }
	
	public Object perform(Object leftValue, Object rightValue) { throw new RuntimeException("perform on ;"); }
	
	public Object evaluate() {		
		if(right != null) {
			left.evaluate();
			return right.evaluate();
		} else {
			return left.evaluate();
		}
	}

	//public String leftPadding() { return ""; }
	
	public String toString() {
		String output = left.toString() + "; ";
		
		if(right != null)
			output += right.toString();
		
		return output;
	}
	
	public int precedence() { return 125; }
}