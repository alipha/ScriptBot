
public class TextNode extends ValueNode {
	
	public TextNode(String token) { super(parseString(token)); }
	
	public String toString() {
		return "\"" + ((String)value).replaceAll("\\\\", "\\\\").replaceAll("\"", "\\\"") + "\"";
	}
	
	public static String parseString(String token) {
		String text = "";
		boolean backslash = false;
		
		for(int i = 1; i < token.length() - 1; ++i) {
			char ch = token.charAt(i);
			
			if(!backslash && ch == '\\') {
				backslash = true;
			} else {
				text += ch;
				backslash = false;
			}
		}
		
		return text;
	}
}
