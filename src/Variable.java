
public class Variable {
	public String name;
	public Object value;
	
	public Variable(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	public String getName() { return name; }
	
	public Object getValue() {
		if(value == null)
			throw new RuntimeException("Use of undefined variable: " + name);
		return value;
	}
	
	public String toString() {
		return value.toString();
	}
}
