
public class VariableNode extends TokenNode {
//	public Memory lastContext;
//	public Variable variable;
	public String name;
	
	public VariableNode(String name) {
		this.name = name;
	}
	
	public TokenNode replaceChild(TokenNode child) {
		throw new RuntimeException("replaceChild on Value");
	}
	
	public Object evaluate() { 
/*		if(variable == null || Memory.getCurrentContext() != lastContext) {
			lastContext = Memory.getCurrentContext();
			variable = Memory.getVariable(name);
		}
		
		return variable; 
*/
		return Memory.getVariable(name);
	}
	public int precedence() { return 950; }
	public String toString() { return name; }
	
}