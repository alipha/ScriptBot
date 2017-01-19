import java.util.HashMap;


public class LeftBracketNode extends UnaryOperator {
	
	public LeftBracketNode() { super("{"); }

	public Object evaluate() { 
		return new Function(this, Memory.getCurrentContext()); 
	}
	
	public Object run(Object arg, Memory context) {
		return run(arg, context, null);
	}
	
	public Object run(Object arg, Memory context, HashMap<String, Object> thisObject) {
//		System.out.print("Before "); Memory.print();
		Memory.pushFrame(context);
//		System.out.print("Create "); Memory.print();
		
		if(arg != null)
			Memory.setLocalVariable("arg", arg);
		
		if(thisObject != null)
			Memory.setLocalVariable("this", thisObject);
		
//		System.out.print("Add "); Memory.print();
		Object value = null;
		try {
			value = operand.evaluate();
		} catch(ReturnStatementException ex) {
			if(arg == null)
				throw ex;
			value = ex.getValue();
		} finally {
			Memory.popFrame();
		}
//		System.out.print("After "); Memory.print();
		return value;
	}

	public int precedence() {
		if(this.parent == null || (this.operand != null && this.operand instanceof RightBracketNode))
			return 950;
		else
			return 100;
	}
	
	public String toString() {
		return "{ " + operand.toString() + " }";
	}

	public boolean expectBinaryOperator() { return false; }
	
	public boolean rightAssociative() { return false; }
}
