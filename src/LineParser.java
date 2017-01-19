
public class LineParser {
	public String message;
	public String sender;
	public String action;
	public String actionArg;
	
	public void parse(String line) {
		int colon = line.indexOf(':', 1);
		
		if(colon == -1) {
			colon = line.length();
		}
		
		String[] tokens = line.substring(0, colon).split(" ");
		message = "";
		sender = "";
		action = "";
		actionArg = "";
		
		if(colon < line.length()) {
			message = line.substring(colon + 1);
		}
		
		if(tokens.length == 0) {
			return;
		}
		
		int actionIndex = 0;
		
		if(tokens[0].charAt(0) == ':') {
			int bang = tokens[0].indexOf('!');
			
			if(bang == -1) {
				bang = tokens[0].length();
			}
			
			sender = tokens[0].substring(1, bang);
			actionIndex = 1;
		}
		
		if(tokens.length <= actionIndex) {
			return;
		}

		action = tokens[actionIndex];
		
		if(tokens.length > actionIndex + 1) {
			actionArg = tokens[actionIndex + 1];
		}
	}
}
