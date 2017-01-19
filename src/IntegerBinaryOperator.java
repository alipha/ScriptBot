
public abstract class IntegerBinaryOperator extends BinaryOperator {

	public abstract int performInt(int leftValue, int rightValue);

	public IntegerBinaryOperator(String op) { super(op); }

	public Object perform(Object leftValue, Object rightValue) {
		return new Integer(performInt(promoteInt(leftValue), promoteInt(rightValue)));
	}
}
