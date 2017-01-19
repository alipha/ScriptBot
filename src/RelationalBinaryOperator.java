
public abstract class RelationalBinaryOperator extends BinaryOperator {

	abstract boolean performInt(int leftValue, int rightValue);
	abstract boolean performDouble(double leftValue, double rightValue);
	abstract boolean performString(String leftValue, String rightValue);

	public RelationalBinaryOperator(String op) { super(op); }
	
	
	public Object perform(Object leftValue, Object rightValue) {
		if(isString(leftValue) || isString(rightValue)) {
			return new Integer(performString(leftValue.toString(), rightValue.toString()) ? 1 : 0);
		} else if(isDouble(leftValue) || isDouble(rightValue)) {
			return new Integer(performDouble(promoteDouble(leftValue), promoteDouble(rightValue)) ? 1 : 0);
		} else {
			return new Integer(performInt(promoteInt(leftValue), promoteInt(rightValue)) ? 1 : 0);
		}
	}
}
