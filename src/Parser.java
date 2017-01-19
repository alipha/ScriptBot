import java.util.ArrayList;
import java.util.Stack;


public class Parser {
	
	enum Stage {
		Whitespace,
		Identifier,
		Operator,
		Number,
		DoubleQuote,
		SingleQuote,
		BeginRegex,
		RegexPrefix,
		Regex,
		RegexPostfix,
		FinishStage
	}

	public Parser() {
		
	}
	
	public Object execute(String line, boolean wrap) {
		TokenNode tree = createTree(tokenize(line));
		Object value = null;
		
		if(wrap) {
			LeftBracketNode wrapper = new LeftBracketNode();
			wrapper.operand = tree;
			value = wrapper.run(null, Memory.getGlobalContext());
		} else
			value = tree.evaluate();
		
		if(value instanceof Variable)
			value = ((Variable)value).getValue();
		return value;
	}
	
	public TokenNode createTree(ArrayList<String> tokens) {
		TokenFactory factory = new TokenFactory();
		TokenNode rootNode = factory.createRoot();
		TokenNode currentNode = rootNode;
		Stack<String> tokenPairs = new Stack<String>();
		
		for(String token : tokens) {
			switch(token) {
			case "{": 
			case "[": 
			case "(": 
				tokenPairs.push(token); 
				break;
			case "}":
				if(!tokenPairs.pop().equals("{"))
					throw new RuntimeException("Mismatched }");
				break;
			case "]":
				if(!tokenPairs.pop().equals("["))
					throw new RuntimeException("Mismatched ]");
				break;
			case ")":
				if(!tokenPairs.pop().equals("("))
					throw new RuntimeException("Mismatched )");
				break;
			}
			
/*			String topToken = null;
			if(!tokenPairs.isEmpty())
				topToken = tokenPairs.peek();
*/			
			TokenNode newNode = factory.create(token, currentNode.expectBinaryOperator()); //, topToken);
			placeNode(currentNode, newNode);
			currentNode = newNode;
			
/*			if(newNode instanceof WhitespaceNode) {
				newNode = factory.create(token, false, topToken);
				placeNode(currentNode, newNode);
				currentNode = newNode;
			}
*/		}
		
		if(!tokenPairs.isEmpty())
			throw new RuntimeException("Mismatched tokens: " + tokenPairs);
		
		return rootNode;
	}
	
	private void placeNode(TokenNode currentNode, TokenNode newNode) {
		while(newNode.precedence() < currentNode.precedence() || (newNode.precedence() == currentNode.precedence() && !currentNode.rightAssociative())) {
			currentNode = currentNode.parent;
		}
		
		TokenNode oldNode = currentNode.replaceChild(newNode);
		newNode.parent = currentNode;
		
		if(oldNode != null)
			newNode.replaceChild(oldNode);
	}
	
	public ArrayList<String> tokenize(String expression) {
		String token = "";
		Stage stage = Stage.Whitespace;
		ArrayList<String> tokens = new ArrayList<String>();
		boolean finishToken = false;
		boolean backslash = false;
		boolean regexSub = false;
		
		expression += " ";
		
		for(int i = 0; i < expression.length(); ++i) {
			char ch = expression.charAt(i);
			
			if("ms/".indexOf(ch) >= 0 && (stage == Stage.BeginRegex || stage == Stage.RegexPrefix)) {
				if(stage == Stage.BeginRegex)
					finishToken = true;
				
				if(ch == 's')
					regexSub = true;
				
				if(ch == '/')
					stage = Stage.Regex;
				else
					stage = Stage.RegexPrefix;
				
			} else if(stage == Stage.Regex) {
				if(ch == '/') {
					if(regexSub)
						regexSub = false;
					else
						stage = Stage.RegexPostfix;
				}
			} else if("gi".indexOf(ch) >= 0 && stage == Stage.RegexPostfix) {
				/* do nothing */
			} else if(ch == '"' && stage != Stage.SingleQuote && !backslash) {
				if(stage == Stage.DoubleQuote) {
					stage = Stage.FinishStage;
				} else {
					stage = Stage.DoubleQuote;
					finishToken = true;
				}
			} else if(ch == '\'' && stage != Stage.DoubleQuote && !backslash) {
				if(stage == Stage.SingleQuote) {
					stage = Stage.FinishStage;
				} else {
					stage = Stage.SingleQuote;
					finishToken = true;
				}
			} else if(stage == Stage.SingleQuote || stage == Stage.DoubleQuote) {
				if(ch == '\\' && !backslash) {
					backslash = true;
				} else {
					backslash = false;
				}
			} else if(ch == ' ') {
				if(stage != Stage.Whitespace && stage != Stage.BeginRegex) {
					stage = Stage.Whitespace;
					finishToken = true;
				}
			} else if((ch >= '0' && ch <= '9') || (stage == Stage.Number && ch == '.')) {
				if(stage != Stage.Identifier && stage != Stage.Number) {
					stage = Stage.Number;
					finishToken = true;
				}
			} else if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_' || ch == '$' || ch == '@') {
				if(stage != Stage.Identifier) {
					stage = Stage.Identifier;
					finishToken = true;
				}
			} else if("(),.;:[]{}".indexOf(ch) >= 0){
				stage = Stage.FinishStage;
				finishToken = true;
			} else {
				if(stage != Stage.Operator) {
					stage = Stage.Operator;
					finishToken = true;
				}
				
				switch(ch) {
				case '*':
					if(!token.equals("*"))
						finishToken = true;
					break;
				case '&':
					if(!token.equals("&"))
						finishToken = true;
					break;
				case '-':
					if(!token.equals("-"))
						finishToken = true;
					break;
				case '+':
					if(!token.equals("+"))
						finishToken = true;
					break;
				case '|':
					if(!token.equals("|"))
						finishToken = true;
					break;
				case '<':
					if(!token.equals("<"))
						finishToken = true;
					break;
				case '>':
					if(!token.equals("<") && !token.equals(">"))
						finishToken = true;
					break;
				case '~':
					if(token.equals("=") || token.equals("!")) {
						stage = Stage.BeginRegex;
					} else {
						finishToken = true;
					}
					break;
				case '=':
					if(token.length() == 1 && "!%^&*-+|<>/=".indexOf(token) >= 0) {
						/* do nothing */
					} else if(token.length() == 2) {
						if(token.equals("<<") || token.equals(">>")) {
							/* do nothing */
						} else {
							finishToken = true;
						}
					} else {
						finishToken = true;
					}
					break;
				default:
					finishToken = true;
				}
			}
			
			if(finishToken) {
				if(!token.equals(""))
					tokens.add(token);
				token = "";
				finishToken = false;
			}
			
			if(ch != ' ' || stage == Stage.SingleQuote || stage == Stage.DoubleQuote)
				token += ch;
		}
		
		return tokens;
	}
}


