import java.util.ArrayList;
import java.util.HashMap;


public class RightParenthesisNode extends UnaryOperator {
	public RightParenthesisNode() { super(")"); }
	
	public Object evaluate() {
		if(operand != null)
			return operand.evaluate();
		else
			return new HashMap<String, Object>();
	}
	
	public Object evaluateArgs(ArrayList<VariableNode> array) {
		if(operand instanceof CommaNode) {
			((CommaNode)operand).evaluateArgs(array);
		} else if(operand != null) {
			if(operand instanceof VariableNode)
				array.add((VariableNode)operand);
			else
				throw new RuntimeException("Variable name expected in function list");
		}
		
		return array;
	}
	
	public Object evaluate(ArrayList<Object> array) {
		if(operand instanceof CommaNode) {
			((CommaNode)operand).evaluate(array, false);
		} else if(operand != null) {
			Object value = operand.evaluate();
			
			if(value instanceof Variable)
				value = ((Variable)value).getValue();
			
			if(!(value instanceof Variable))
				value = new Variable("<Array>[" + array.size() + "]", value);
			
			array.add(value);
		}
		return array;
	}
	
	public int precedence() {
		if(this.parent != null)
			return 950;
		else
			return 101;
	}
	
	public String toString() {
		if(operand != null)
			return operand.toString();
		else
			return "";
	}
	public boolean expectBinaryOperator() {
		if(this.parent != null && this.parent.parent != null && (this.parent.parent instanceof KeywordNode || this.parent.parent instanceof FuncDefNode))
			return false;
		else
			return true; 
	}
	
	public boolean rightAssociative() { return false; }
}
