import java.util.HashMap;


public interface IFunction {
	public void setThis(HashMap<String, Object> thisObject);
	public Object run(Object arg);
}
