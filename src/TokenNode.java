
public abstract class TokenNode {
	
	public TokenNode parent;
	
	public abstract TokenNode replaceChild(TokenNode child);
	public abstract Object evaluate();
	public abstract int precedence();
	
	public boolean rightAssociative() { return false; }
	
	public boolean expectBinaryOperator() { return true; }
}


