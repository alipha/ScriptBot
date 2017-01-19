
public abstract class ArithmeticBinaryOperator extends BinaryOperator {

	abstract int performInt(int leftValue, int rightValue);
	abstract double performDouble(double leftValue, double rightValue);

	
	public ArithmeticBinaryOperator(String op) { super(op); }
	
	
	public Object perform(Object leftValue, Object rightValue) {
		if(isDouble(leftValue) || isDouble(rightValue)) {
			return new Double(performDouble(promoteDouble(leftValue), promoteDouble(rightValue)));
		} else {
			return new Integer(performInt(promoteInt(leftValue), promoteInt(rightValue)));
		}
	}
}
