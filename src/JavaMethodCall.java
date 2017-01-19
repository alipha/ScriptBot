import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;


public class JavaMethodCall implements IFunction {
	public Object object;
	public Class<?> classType;
	public String methodName;
	
	public void setThis(HashMap<String, Object> thisObject) {}
	
	public JavaMethodCall(Object object, Class<?> classType, String methodName) {
		this.object = object;
		this.classType = classType;
		this.methodName = methodName;
	}
	
	public Object run(Object arg) {
		ArrayList<Object> args = (ArrayList<Object>)arg;
		Object[] argValues = new Object[args.size()];
		Class<?>[] argTypes = new Class<?>[args.size()];
		Method method = null;
		Object ret = null;
		
		for(int i = 0; i < args.size(); i++) {
			Object argValue = ((Variable)args.get(i)).getValue();
			argValues[i] = argValue;
			
			if(argValue instanceof Integer)
				argTypes[i] = int.class;
			else if(argValue instanceof Double)
				argTypes[i] = double.class;
			else
				argTypes[i] = argValue.getClass();
		}
		
		try {
			method = classType.getDeclaredMethod(methodName, argTypes);
			ret = method.invoke(object, argValues);
		} catch (Exception e) {
			String argTypeList = "(";
			
			for(Class<?> argType : argTypes) {
				argTypeList += argType.getName() + ", ";
			}
			
			if(argTypes.length > 0)
				argTypeList = argTypeList.substring(0, argTypeList.length() - 2);
			
			argTypeList += ")";
			
			throw new RuntimeException(methodName + argTypeList + " does not exist on " + classType.getName());
		}
		
		return ret;
	}
	
	public String toString() {
		return classType.getName() + "." + methodName;
	}
}
