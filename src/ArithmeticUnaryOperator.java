
public abstract class ArithmeticUnaryOperator extends UnaryOperator {

	ArithmeticUnaryOperator(String operator) { super(operator); }
	

	abstract int performInt(int value);
	abstract double performDouble(double value);

	
	public Object evaluate() {
		return perform(operand.evaluate());
	}
	
	public Object perform(Object value) {
		if(isDouble(value)) {
			return new Double(performDouble(promoteDouble(value)));
		} else {
			return new Integer(performInt(promoteInt(value)));
		}
	}
}
