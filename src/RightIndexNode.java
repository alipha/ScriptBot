import java.util.ArrayList;


public class RightIndexNode extends UnaryOperator {
	public RightIndexNode() { super("]"); }
	
	public Object evaluate() { return operand.evaluate(); }
	
	public Object evaluate(ArrayList<Object> array, boolean arrayInit) {
		if(operand instanceof CommaNode) {
			((CommaNode)operand).evaluate(array, arrayInit);
		} else if(operand != null) {
			Object value = operand.evaluate();
			
			if(!arrayInit && value instanceof Variable)
				value = ((Variable)value).getValue();
			
			if(!(value instanceof Variable))
				value = new Variable("<Array>[" + array.size() + "]", value);
			array.add(value);
		}
		return array;
	}
	
	public int precedence() {
		if(this.parent != null)
			return 900;
		else
			return 101;
	}
	
	public String toString() {
		if(operand != null)
			return operand.toString();
		else
			return "";
	}
	
	public boolean expectBinaryOperator() { return true; }
	
	public boolean rightAssociative() { return false; }
}