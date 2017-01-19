import java.util.HashMap;


public class DotNode extends BinaryOperator {

	public DotNode() { super("."); }
	
	public Object perform(Object leftValue, Object right) {
		String leftName = "<Complex Expression>";
		String rightValue = ((VariableNode)right).name;
		
		if(leftValue instanceof Variable) {
			leftName = ((Variable)leftValue).getName();
			leftValue = ((Variable)leftValue).getValue();
		}
		
		if(leftValue instanceof HashMap) {
			HashMap<String, Object> left = (HashMap<String, Object>)leftValue;
			Variable value = (Variable)left.get(rightValue);
			
			if(value == null) {
				left.put(rightValue, new Variable(leftName + "." + rightValue, null));
			} else if(value.getValue() instanceof IFunction) {
				((IFunction)value.getValue()).setThis(left);
			}
			
			return left.get(rightValue.toString());
		} else {
			Class<?> classType = leftValue.getClass();
			return new JavaMethodCall(leftValue, classType, rightValue);
		}
	}
	
	public Object evaluate() {
		Object leftValue = left.evaluate();
		return perform(leftValue, right);
	}

	public int precedence() { return 900; }
	
	public String toString() { return left.toString() + "." + right.toString(); }
}
