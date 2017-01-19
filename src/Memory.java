import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.*;

public class Memory {

	private static ArrayList<Memory> frames = initMemory();

	private Memory parent;
	public HashMap<String, Variable> variables;
	
	
	public Memory() {
		parent = null;
		variables = new HashMap<String, Variable>();
	}
	
	public Memory(Memory parent) {
		this.parent = parent;
		this.variables = new HashMap<String, Variable>();
	}
	
	private static ArrayList<Memory> initMemory() {
		ArrayList<Memory> frames = new ArrayList<Memory>();
		
		Memory systemMemory = new Memory();
//		addFunction(systemMemory, "if", new IfFunction());
//		addFunction(systemMemory, "while", new WhileFunction());
		addFunction(systemMemory, "save", new SaveFunction());
		addFunction(systemMemory, "load", new LoadFunction());
		addFunction(systemMemory, "unset", new UnsetFunction());
		addFunction(systemMemory, "len", new LenFunction());
		addFunction(systemMemory, "append", new AppendFunction());
//		addFunction(systemMemory, "new", new NewFunction());
		addFunction(systemMemory, "send", new SendFunction());
		frames.add(systemMemory);
		frames.add(new Memory(systemMemory));
		return frames;
	}
	
	private static void addFunction(Memory systemMemory, String name, IFunction func) {
		systemMemory.variables.put(name, new Variable(name, func));
	}
	
	public static void print() {
		for(int i = 1; i < frames.size(); ++i) {
			System.out.println("Frame " + i + ":");
			
			for(Map.Entry<String, Variable> entry : frames.get(i).variables.entrySet()) {
				System.out.println("\t" + entry.getKey() + " = " + entry.getValue().toString());
			}
		}
	}
	
	public static Memory getSystemContext() {
		return frames.get(0);
	}
	
	public static Memory getGlobalContext() {
		return frames.get(1);
	}
	
	public static Memory getCurrentContext() {
		return frames.get(frames.size() - 1);
	}
	
	public static void pushFrame(Memory context) {
		if(frames.size() > 1002) {
			Memory.reset(false);
			throw new RuntimeException("Stack overflow - more than 1000 function calls");
		}
		
		Memory frame = new Memory(context);
		frames.add(frame);
	}
	
	public static void reset(boolean global) {
		Memory systemFrame = getSystemContext();
		
		if(global) {
			frames.clear();
			frames.add(systemFrame);
			frames.add(new Memory(systemFrame));
		} else {
			Memory globalFrame = getGlobalContext();
			frames.clear();
			frames.add(systemFrame);
			frames.add(globalFrame);
		}
	}
	
	public static void save() throws IOException {
		Memory globalFrame = getGlobalContext();
		
		PrintStream out = new PrintStream("memory.txt");
		
		for(Map.Entry<String, Variable> entry : globalFrame.variables.entrySet()) {
			try {
				String line = entry.getKey() + " = " + entry.getValue().toString() + ";";
				out.println(line);
			} catch(Exception ex) {
				ex.printStackTrace(System.out);
			}
		}
		
		out.close();
	}
	
	public static void load() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("memory.txt"));
		String line;
		Parser parser = new Parser();
		frames.add(Memory.getGlobalContext());
		
		while((line = in.readLine()) != null) {
			parser.execute(line, false);
		}

		Memory.popFrame();
		in.close();
	}
	
	public static void popFrame() {
		/*Memory frame = frames.get(frames.size() - 1);
		
		for(Map.Entry<String, Variable> entry : frame.variables.entrySet()) {
			entry.getValue().setValue(null);
		}
		*/
		frames.remove(frames.size() - 1);
	}
	
	public static Variable getVariable(String name) {
		return getVariable(name, null);
	}
	
	public static Variable getVariable(String name, Object value) {
		Memory currentFrame = getCurrentContext();
		Memory frame = currentFrame;
		
		while(frame != null && (name.charAt(0) != '$' || frame != getGlobalContext())) {
			Variable var = frame.variables.get(name);
			
			if(var != null) {
				return var;
			}
			
			frame = frame.parent;
		}
		
		Variable var = new Variable(name, value);
		currentFrame.variables.put(name, var);
		return var;
	}
	
	public static void setLocalVariable(String name, Object value) {
		getCurrentContext().variables.put(name, new Variable(name, value));
	}
	
	public static boolean unsetVariable(String name) {
		Memory currentFrame = getCurrentContext();
		Memory frame = currentFrame;
		
		while(frame != null) {
			if(frame.variables.containsKey(name)) {
				frame.variables.remove(name);
				return true;
			}
			
			frame = frame.parent;
		}
		
		return false;
	}
}

/*
class IfFunction extends SystemFunction {
	
	public Object perform(ArrayList<Object> arg) {
		Object cond = ((Variable)arg.get(0)).getValue();
		
		if(cond instanceof LeftBracketNode) {
			cond = runArg(cond);
		}
		
		if(Operator.promoteInt(cond) != 0) {
			return runArg(arg.get(1));
		} else if(arg.size() == 3) {
			return runArg(arg.get(2));
		} else {
			return 0;
		}
	}
	
	public String toString() { return "<If Function>"; }
}

class WhileFunction extends SystemFunction {
	
	public Object perform(ArrayList<Object> arg) {
		Object result = 0;
		long startTime = System.currentTimeMillis();
		
		while(Operator.promoteInt(runArg(arg.get(0))) != 0) {
			if(System.currentTimeMillis() - startTime > 30000)
				throw new RuntimeException("Program terminated after 30 seconds");
			result = runArg(arg.get(1));
		}
		return result;
	}
	
	public String toString() { return "<While Function>"; }
}
*/

class SaveFunction implements IFunction {
	public void setThis(HashMap<String, Object> thisObject) {}
	
	public Object run(Object arg) {
		try {
			Memory.save();
		} catch(IOException ex) {
			throw new RuntimeException(ex.getMessage());
		}
		return 1;
	}
	
	public String toString() { return "\"<Save Function>\""; }
}

class LoadFunction implements IFunction {
	public void setThis(HashMap<String, Object> thisObject) {}
	
	public Object run(Object arg) {
		try {
			Memory.load();
		} catch(IOException ex) {
			throw new RuntimeException(ex.getMessage());
		}
		return 1;
	}
	
	public String toString() { return "\"<Load Function>\""; }
}

class UnsetFunction implements IFunction {
	public void setThis(HashMap<String, Object> thisObject) {}
	
	public Object run(Object arg) {
		ArrayList<Object> args = (ArrayList<Object>)arg;
		Object name = ((Variable)args.get(0)).getValue();
		
		//Memory.pushFrame(Memory.getGlobalContext());
		if(Memory.unsetVariable((String)name))
			return 1;
		//Memory.popFrame();
		return 0;
	}
	
	public String toString() { return "\"<Unset Function>\""; }
}

class LenFunction implements IFunction {
	public void setThis(HashMap<String, Object> thisObject) {}
	
	public Object run(Object arg) {
		ArrayList<Object> args = (ArrayList<Object>)arg;
		arg = ((Variable)args.get(0)).getValue();
		
		if(arg instanceof String) {
			return ((String)arg).length();
		} else if(arg instanceof ArrayList) {
			return ((ArrayList<Object>)arg).size();
		} else {
			return 0;
		}
	}
}

class AppendFunction implements IFunction {
	public void setThis(HashMap<String, Object> thisObject) {}
	
	public Object run(Object arg) {
		ArrayList<Object> args = (ArrayList<Object>)arg;
		Variable arrayVar = (Variable)args.get(0);
		ArrayList<Object> array = (ArrayList<Object>)arrayVar.getValue();
		Object item = ((Variable)args.get(1)).getValue();
		
		array.add(new Variable(arrayVar.getName() + "[" + array.size() + "]", item));
		return array;
	}
}

class SendFunction implements IFunction {
	public void setThis(HashMap<String, Object> thisObject) {}
	
	public Object run(Object arg) {
		ArrayList<Object> args = (ArrayList<Object>)arg;
		Object message = ((Variable)args.get(0)).getValue();
		Memory.getSystemContext().variables.put("*send", new Variable("*send", message));
		return message;
	}
}

class NewFunction implements IFunction {
	public void setThis(HashMap<String, Object> thisObject) {}
	
	public Object run(Object arg) {
		ArrayList<Object> args = (ArrayList<Object>)arg;
		Object name = ((Variable)args.get(0)).getValue();
		Class<?> classType = null;
		
		try {
			classType = Class.forName((String)name);
		} catch(Exception ex) {
			throw new RuntimeException("Class " + name.toString() + " does not exist.");
		}
		
		Object[] argValues = new Object[args.size() - 1];
		Class<?>[] argTypes = new Class<?>[args.size() - 1];
		
		for(int i = 0; i < args.size() - 1; i++) {
			Object argValue = ((Variable)args.get(i + 1)).getValue();
			argValues[i] = argValue;
			
			if(argValue instanceof Integer)
				argTypes[i] = int.class;
			else if(argValue instanceof Double)
				argTypes[i] = double.class;
			else
				argTypes[i] = argValue.getClass();
		}
		
		try {
			return classType.getDeclaredConstructor(argTypes).newInstance(argValues);
		} catch(Exception ex) {
			String argTypeList = "(";
			
			for(Class<?> argType : argTypes) {
				argTypeList += argType.getName() + ", ";
			}
			
			if(argTypes.length > 0)
				argTypeList = argTypeList.substring(0, argTypeList.length() - 2);
			
			argTypeList += ")";
			
			throw new RuntimeException("Constructor" + argTypeList + " does not exist on " + classType.getName());
		}
	}
}