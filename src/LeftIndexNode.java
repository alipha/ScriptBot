import java.util.ArrayList;
import java.util.HashMap;


public class LeftIndexNode extends BinaryOperator {

	public LeftIndexNode() { super("["); }
	
	public Object perform(Object leftValue, Object rightValue) {
		String leftName = "<Complex Expression>";
		if(leftValue instanceof Variable) {
			leftName = ((Variable)leftValue).getName();
			leftValue = ((Variable)leftValue).getValue();
		}
		
		if(leftValue instanceof String) {
			String left = (String)leftValue;
			return "" + left.charAt(promoteInt(rightValue));
		} else if(leftValue instanceof ArrayList) {
			ArrayList<Object> left = (ArrayList<Object>)leftValue;
			return left.get(promoteInt(rightValue));
		} else {
			HashMap<String, Object> left = (HashMap<String, Object>)leftValue;
			Variable value = (Variable)left.get(rightValue.toString());
			
			if(value == null) {
				left.put(rightValue.toString(), new Variable(leftName + "[" + rightValue.toString() + "]", null));
			} else if(value.getValue() instanceof IFunction) {
				((IFunction)value.getValue()).setThis(left);
			}
			
			return left.get(rightValue.toString());
		}
	}
		
	public int precedence() {
		if(this.parent == null || this.left == null || (this.right != null && this.right instanceof RightIndexNode))
			return 900;
		else
			return 100;
	}
	
	public String toString() {
		return left.toString() + "[" + right.toString() + "]";
	}
}
