import java.util.ArrayList;


public class CommaNode extends BinaryOperator {

	public CommaNode() { super(","); }
	
	public Object perform(Object leftValue, Object rightValue) { throw new RuntimeException("perform on ,"); }

	public Object evaluate() { throw new RuntimeException("evaluate on ,"); }
	
	public void perform(TokenNode node, ArrayList<Object> array, boolean arrayInit) {
		if(node instanceof CommaNode) {
			((CommaNode)node).evaluate(array, arrayInit);
		} else if(node != null) {
			Object value = node.evaluate();
			
			if(!arrayInit && value instanceof Variable)
				value = ((Variable)value).getValue();

			if(!(value instanceof Variable))
				value = new Variable("<Array>[" + array.size() + "]", value);
			
			array.add(value);
		}
	}
	
	public void performArgs(TokenNode node, ArrayList<VariableNode> array) {
		if(node instanceof CommaNode) {
			((CommaNode)node).evaluateArgs(array);
		} else if(node != null) {
			if(node instanceof VariableNode)
				array.add((VariableNode)node);
			else
				throw new RuntimeException("Variable name expected in function list");
		}
	}
	
	public Object evaluateArgs(ArrayList<VariableNode> array) {
		performArgs(left, array);
		performArgs(right, array);
		return array;
	}
	
	public Object evaluate(ArrayList<Object> array, boolean arrayInit) {
		perform(left, array, arrayInit);
		perform(right, array, arrayInit);
		return array;
	}
	
	public int precedence() { return 150; }
}
