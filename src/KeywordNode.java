
public abstract class KeywordNode extends TokenNode {
	public TokenNode nextStatement;
	
	public boolean expectBinaryOperator() { return false; }
	
	public boolean rightAssociative() { return true; }
}
