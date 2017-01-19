
public class RightBracketNode extends UnaryOperator {
	
	public RightBracketNode() { super("}"); }
	
	public Object evaluate() { return operand.evaluate(); }

	public int precedence() {
		if(this.parent != null)
			return 950;
		else
			return 101;
	}
	
	public String toString() { return operand.toString(); }
	
	public boolean expectBinaryOperator() {
		if(this.parent != null && this.parent.parent != null && this.parent.parent instanceof KeywordNode)
			return false;
		else
			return true; 
	}
	
	public boolean rightAssociative() { return false; }
}
