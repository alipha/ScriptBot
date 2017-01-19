
public class IfNode extends KeywordNode {
	public LeftParenthesisNode condition;
	public TokenNode block;
	
	public TokenNode replaceChild(TokenNode child) {
		if(condition == null) {
			condition = (LeftParenthesisNode)child;
			return null;
		}
		
		if(!(child instanceof ElseNode) && nextStatement == null && (block == null || !(block instanceof LeftBracketNode))) {
			TokenNode old = block;
			block = child;
			return old;
		}
		
		TokenNode old = nextStatement;
		nextStatement = child;
		return old;
	}

	public Object evaluate() {
		Object ret = 0;
		
		if(Operator.promoteInt(condition.evaluate()) != 0) {
			Memory.setLocalVariable("else", 0);
			
			if(!(block instanceof LeftBracketNode)) {
				LeftBracketNode wrapper = new LeftBracketNode();
				wrapper.replaceChild(block);
				block = wrapper;
			}
			
			IFunction func = (IFunction)block.evaluate();
			ret = func.run(null);
			Memory.setLocalVariable("if", ret);
		} else {
			Memory.setLocalVariable("else", 1);
		}
		
		if(nextStatement != null)
			return nextStatement.evaluate();
		else
			return ret;
	}
	
	public int precedence() { return 150; }
	
	public String toString() {
		String output = "if" + condition.toString() + " " + block.toString();
		
		if(nextStatement != null)
			return output + " " + nextStatement;
		else
			return output;
	}
}
