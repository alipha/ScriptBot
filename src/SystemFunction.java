import java.util.ArrayList;


public abstract class SystemFunction extends TokenNode {

	public abstract Object perform(ArrayList<Object> arg);
	
	public TokenNode replaceChild(TokenNode child) { throw new RuntimeException("replaceChild on SystemFunction"); }
	
	public Object evaluate() {
		Object arg = Memory.getVariable("arg").getValue();
		//Memory.unsetVariable("arg");
		return perform((ArrayList<Object>)arg); 
	}

	/*
	public Object runArg(Object func) {
		if(func instanceof Variable)
			func = ((Variable)func).getValue();
		return ((LeftBracketNode)func).run(null);
	}
	*/
	
	public int precedence() { throw new RuntimeException("precedence on SystemFunction"); }

	public String toString() { return "\"<System Function>\""; }
}
