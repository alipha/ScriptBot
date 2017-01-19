
public abstract class IntegerUnaryOperator extends UnaryOperator {

	public IntegerUnaryOperator(String op) { super(op); }
	
	public abstract int performInt(int value);
	
	public Object evaluate() { return new Integer(performInt(promoteInt(operand.evaluate()))); }
}