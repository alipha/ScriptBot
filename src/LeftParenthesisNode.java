
public class LeftParenthesisNode extends UnaryOperator {

	public LeftParenthesisNode() { super("("); }

	public Object evaluate() { return operand.evaluate(); }

	public int precedence() {
		if(this.parent == null || (this.operand != null && this.operand instanceof RightParenthesisNode))
			return 950;
		else
			return 100;
	}
	
	public String toString() {
		return "(" + operand.toString() + ")";
	}

	public boolean expectBinaryOperator() { return false; }
	
	public boolean rightAssociative() { return false; }
}
