
public class WhileNode extends KeywordNode {
	public LeftParenthesisNode condition;
	public TokenNode block;
	
	public TokenNode replaceChild(TokenNode child) {
		if(condition == null) {
			condition = (LeftParenthesisNode)child;
			return null;
		}
		
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
		long startTime = System.currentTimeMillis();
		Object ret = 0;
		
		if(!(block instanceof LeftBracketNode)) {
			LeftBracketNode wrapper = new LeftBracketNode();
			wrapper.replaceChild(block);
			block = wrapper;
		}
		
		IFunction func = (IFunction)block.evaluate();

		while(Operator.promoteInt(condition.evaluate()) != 0) {
			if(System.currentTimeMillis() - startTime > 30000)
				throw new RuntimeException("Program terminated after 30 seconds");
			
			try {
				ret = func.run(null);
			} catch(BreakStatementException ex) {
				if(!ex.isContinue())
					break;
			}
		}
		
		if(nextStatement != null)
			return nextStatement.evaluate();
		else
			return ret;
	}
	
	public int precedence() { return 150; }
	
	public String toString() {
		String output = "while" + condition.toString() + " " + block.toString();
		
		if(nextStatement != null)
			return output + " " + nextStatement;
		else
			return output;
	}
}
