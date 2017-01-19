import java.util.ArrayList;
import java.util.HashMap;


public class FuncDefNode extends LeftBracketNode {
	public LeftFunctionCallNode args;
	public ArrayList<VariableNode> names;
	
	public TokenNode replaceChild(TokenNode child) {
		if(args == null) {
			args = (LeftFunctionCallNode)child;
			args.left = this;
			return null;
		}
/*		
		if(operand == null) {
			if(!(child instanceof LeftBracketNode))
				throw new RuntimeException("Expected { after function arguments");
			operand = child;
			return null;
		}
		
		throw new RuntimeException("Too many operands for FuncDefNode");
*/
		TokenNode old = operand;
		operand = child;
		return old;
	}

	public Object evaluate() {
		RightParenthesisNode rightParen = (RightParenthesisNode)args.right;
		names = new ArrayList<VariableNode>();
		rightParen.evaluateArgs(names);
		return super.evaluate();
	}
	
	public Object run(Object arg, Memory context) {
		return run(arg, context, null);
	}
	
	public Object run(Object arg, Memory context, HashMap<String, Object> thisObject) {
		int i = 0;
		
		Memory.pushFrame(context);

		if(arg != null) {
			Memory.setLocalVariable("arg", arg);
			
			ArrayList<Object> args = (ArrayList<Object>)arg;
			for(VariableNode name : names) {
				Memory.setLocalVariable(name.name, ((Variable)args.get(i++)).getValue());
			}
		}
		
		if(thisObject != null)
			Memory.setLocalVariable("this", thisObject);
		
		if(!(operand instanceof LeftBracketNode)) {
			LeftBracketNode wrapper = new LeftBracketNode();
			wrapper.replaceChild(operand);
			operand = wrapper;
		}
		
		LeftBracketNode leftBracket = (LeftBracketNode)operand;
		Object value = null;
		try {
			value = leftBracket.operand.evaluate();
		} catch(ReturnStatementException ex) {
			if(arg == null)
				throw ex;
			value = ex.getValue();
		} finally {
			Memory.popFrame();
		}
		return value;
	}
	
	public int precedence() {
		if(this.parent == null || (this.operand != null && this.operand instanceof LeftBracketNode))
			return 900;
		else
			return 150;
	}
	
	public boolean expectBinaryOperator() { return true; }
	
	public boolean rightAssociative() { return true; }
	
	public String toString() {
		return "func(" + args.right.toString() + ") " + operand.toString();
	}
}
