
public class ValueNode extends TokenNode {
	public Object value;	
	
	public ValueNode(Object val) { value = val; }
	
	public TokenNode replaceChild(TokenNode child) {
		throw new RuntimeException("replaceChild on Value");
	}
	
	public Object evaluate() { return value;}
	public int precedence() { return 950; }
	public String toString() { return value.toString(); }
}
