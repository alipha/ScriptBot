 
public abstract class Operator extends TokenNode {

	public String operator;
	
	
	public Operator(String op) { operator = op; }
	
	
	public static boolean isString(Object value) {
		if(value instanceof Variable) {
			value = ((Variable)value).getValue();
		}
		
		return value instanceof String;
	}
	
	public static boolean isDouble(Object value) {
		if(value instanceof Variable) {
			value = ((Variable)value).getValue();
		}
		
		return value instanceof Double;
	}
	
	
	public static boolean isInteger(Object value) {
		if(value instanceof Variable) {
			value = ((Variable)value).getValue();
		}
		
		return value instanceof Integer;
	}
	
	
	public static double promoteDouble(Object value) {
		if(value instanceof Variable) {
			value = ((Variable)value).getValue();
		}
		
		if(value instanceof Double) {
			return (Double)value;
		} else if(value instanceof Integer) {
			return (Integer)value;
		} else if(value instanceof String) {
			try {
				return Double.parseDouble((String)value);
			} catch(NumberFormatException ex) {
				return 0.0;
			}
		} else {
			return 0.0;  // shouldn't happen
		}
	}

	public static int promoteInt(Object value) {
		if(value instanceof Variable) {
			value = ((Variable)value).getValue();
		}
		
		if(value instanceof Double) {
			return (int)(double)value;
		} else if(value instanceof Integer) {
			return (Integer)value;
		} else if(value instanceof String) {
			try {
				return Integer.parseInt((String)value);
			} catch(NumberFormatException ex) {
				return 0;
			}
		} else {
			return 0;  // shouldn't happen
		}
	}
}
