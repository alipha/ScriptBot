
public class ElseNode extends KeywordNode {
	public TokenNode block;
	
	public TokenNode replaceChild(TokenNode child) {		
		if(block == null || !(block instanceof LeftBracketNode)) {
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
		
		if((Integer)Memory.getVariable("else", 0).getValue() == 1) {
			Memory.setLocalVariable("else", 0);
			
			if(!(block instanceof LeftBracketNode)) {
				LeftBracketNode wrapper = new LeftBracketNode();
				wrapper.replaceChild(block);
				block = wrapper;
			}
			
			IFunction func = (IFunction)block.evaluate();
			ret = func.run(null);
		} else {
			ret = Memory.getVariable("if");
		}
		
		if(nextStatement != null)
			return nextStatement.evaluate();
		else
			return ret;
	}
	
	public int precedence() { return 150; }
	
	public String toString() {
		String output = "else " + block.toString();
		
		if(nextStatement != null)
			return output + " " + nextStatement;
		else
			return output;
	}
}
