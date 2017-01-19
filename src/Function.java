import java.util.HashMap;


public class Function implements IFunction {
	public LeftBracketNode block;
	public Memory context;
	public HashMap<String, Object> thisObject;
	
	public Function(LeftBracketNode block, Memory context) {
		this.block = block;
		this.context = context;
	}
	
	public void setThis(HashMap<String, Object> thisObject) {
		this.thisObject = thisObject;
	}
	
	public Object run(Object arg) {
		return block.run(arg, context, thisObject);
	}
	
	public String toString() { return block.toString(); }
}
