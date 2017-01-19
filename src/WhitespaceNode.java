import java.util.ArrayList;


public class WhitespaceNode extends BinaryOperator {

	public WhitespaceNode() { super(" "); }
	
	public Object perform(Object leftValue, Object right) {
		ArrayList<Object> array = new ArrayList<Object>();
		
		if(leftValue instanceof Variable)
			leftValue = ((Variable)leftValue).getValue();
		IFunction left = (IFunction)leftValue;
		
		if(right instanceof WhitespaceNode) {
			((WhitespaceNode)right).evaluate(array);
		} else if(right instanceof CommaNode) {
			((CommaNode)right).evaluate(array, false);
		} else {
			Object rightValue = this.right.evaluate();
			
			if(rightValue instanceof Variable)
				rightValue = ((Variable)rightValue).getValue();

			if(!(rightValue instanceof Variable))
				rightValue = new Variable("<Array>[" + array.size() + "]", rightValue);
			array.add(rightValue);	
		}
		
		return left.run(array);
	}
	
	public Object evaluate() {
		return perform(left.evaluate(), right); 
	}
	
	public void performArgList(TokenNode node, ArrayList<Object> array) {
		if(node instanceof WhitespaceNode) {
			((WhitespaceNode)node).evaluate(array);
		} else if(node != null){
			Object value = node.evaluate();
			
			if(value instanceof Variable)
				value = ((Variable)value).getValue();

			if(!(value instanceof Variable))
				value = new Variable("<array>[" + array.size() + "]", value);
			array.add(value);
		}
	}
	
	public Object evaluate(ArrayList<Object> array) {
		performArgList(left, array);
		performArgList(right, array);
		return array;
	}

	public int precedence() { return 150; }

	public boolean rightAssociative() { return true; }
	
	public String toString() {
		return "(" + left.toString() + " . " + right.toString() + ")";
	}
}
