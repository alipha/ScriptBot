import java.util.ArrayList;


public class LeftFunctionCallNode extends BinaryOperator {

	public LeftFunctionCallNode() { super("("); }
	
	public Object perform(Object leftValue, Object right) { //throw new RuntimeException("perform on LeftFunctionCallNode"); }
		if(leftValue instanceof Variable)
			leftValue = ((Variable)leftValue).getValue();
		IFunction left = (IFunction)leftValue;
		Object arg = ((RightParenthesisNode)right).evaluate(new ArrayList<Object>());
		return left.run(arg);
	}
	
	public Object evaluate() {
		return perform(left.evaluate(), right); 
	}
	
	/*
	public Object evaluate() {
		
	}
	*/
		
	public int precedence() {
		if(this.parent == null || this.left == null || (this.right != null && this.right instanceof RightParenthesisNode)) {
			if(this.parent != null && this.parent instanceof FuncDefNode)
				return 950;
			else 
				return 900;
		} else {
			return 100;
		}
	}
	
	public String toString() {
		return left.toString() + "(" + right.toString() + ")";
	}
}
