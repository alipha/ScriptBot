
public abstract class IncrementOperator extends UnaryOperator {
	
	public IncrementOperator(String op) { super(op); }
	
	public abstract Object performInt(Variable operand, int value);
	public abstract Object performDouble(Variable operand, double value);
	
	public Object evaluate() {
		Variable operand = (Variable)this.operand.evaluate();
		
		/*
		if(!(operand instanceof Variable)) {
			throw new RuntimeException("Operand of " + operator + " must be a variable");
		}
		*/
		
		Object value = operand.getValue();
		
		if(isDouble(value)) {
			return performDouble(operand, promoteDouble(value));
		} else {
			return performInt(operand, promoteInt(value));
		}
	}
	
	public TokenNode replaceChild(TokenNode token) {
		return super.replaceChild(token);
	}

	public int precedence() { return 600; }
	
	public boolean rightAssociative() { return true; }
}
