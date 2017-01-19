import java.util.ArrayList;


public class LeftArrayNode extends UnaryOperator {

	public LeftArrayNode() { super("["); }

	public Object evaluate() { return evaluate(false); }

	public Object evaluate(boolean arrayInit) {
		return ((RightIndexNode)operand).evaluate(new ArrayList<Object>(), arrayInit);
	}
	
	public int precedence() {
		if(this.parent == null || (this.operand != null && this.operand instanceof RightIndexNode))
			return 900;
		else
			return 100;
	}
	
	public String toString() {
		return "[" + operand.toString() + "]";
	}

	public boolean expectBinaryOperator() { return false; }
	
	public boolean rightAssociative() { return false; }
}
