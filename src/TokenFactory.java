
public class TokenFactory {

	public TokenNode createRoot() { return new RootNode(); }

	public TokenNode create(String token, boolean binary) { //, String topToken) {
		
		if(token.equals("else")) return new ElseNode();
	
		if(binary) {
			switch(token) {
			case ".": return new DotNode();
			case "**": return new ExponentNode();
			case "*": return new MultiplyNode();
			case "/": return new DivideNode();
			case "%": return new ModuloNode();
			case "+": return new AddNode();
			case "-": return new SubtractNode();
			case ">": return new GreaterThanNode();
			case ">=": return new GreaterThanEqualToNode();
			case "<": return new LessThanNode();
			case "<=": return new LessThanEqualToNode();
			case "==": return new EqualToNode();
			case "!=": return new NotEqualToNode();
			case "=": return new AssignmentNode();
			case "+=": return new AssignmentNode(new AddNode());
			case "-=": return new AssignmentNode(new SubtractNode());
			case "*=": return new AssignmentNode(new MultiplyNode());
			case "/=": return new AssignmentNode(new DivideNode());
			case "%=": return new AssignmentNode(new ModuloNode());
			case "&=": return new AssignmentNode(new BitwiseAndNode());
			case "^=": return new AssignmentNode(new XorNode());
			case "|=": return new AssignmentNode(new BitwiseOrNode());
			case "<<=": return new AssignmentNode(new ShiftLeftNode());
			case ">>=": return new AssignmentNode(new ShiftRightNode());
			case "<<": return new ShiftLeftNode();
			case ">>": return new ShiftRightNode();
			case "&&": return new AndNode();
			case "||": return new OrNode();
			case "&": return new BitwiseAndNode();
			case "|": return new BitwiseOrNode();
			case "^": return new XorNode();
			case ",": return new CommaNode();
			case "(": return new LeftFunctionCallNode();
			case ")": return new RightParenthesisNode();
			//case ")": return new RightFunctionCallNode();
			case "++": return new PostfixIncrementNode();
			case "--": return new PostfixDecrementNode();
			case "[": return new LeftIndexNode();
			case "=~": return new BindNode();
			case "!~": return new NotBindNode();
			}
		} else {
			switch(token) {
			case "if": return new IfNode();
			case "while": return new WhileNode();
			case "func":
			case "function":
				return new FuncDefNode();
			case "break": return new BreakNode();
			case "continue": return new ContinueNode();
			case "ret":
			case "return":
				return new ReturnNode();
			case "-": return new NegateNode();
			case "~": return new BitwiseNotNode();
			case "!": return new NotNode();
			case "++": return new PrefixIncrementNode();
			case "--": return new PrefixDecrementNode();
			case "(": return new LeftParenthesisNode();
			case ")": return new RightParenthesisNode();   // todo: RightFunctionCallNode
			case "[": return new LeftArrayNode();
			case "{": return new LeftBracketNode();
			}

			char ch = token.charAt(0);
			
			if(ch >= '0' && ch <= '9') { return new NumberNode(token); }
			if(ch == '"' || ch == '\'') { return new TextNode(token); }
			if(token.indexOf('/') >= 0) { return new RegexNode(token); }			
			if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_' || ch == '$' || ch == '@') { return new VariableNode(token); }
		}		
		
		if(token.equals(";")) return new SemicolonNode();
		if(token.equals("}")) return new RightBracketNode();
		if(token.equals("]")) return new RightIndexNode();
		
		//if(binary && (topToken == null || topToken.equals("{"))) {
		//	return new WhitespaceNode();
		//} else {
			throw new RuntimeException("Unexpected token");
		//}
	}
}


class RootNode extends UnaryOperator {
	public RootNode() { super(""); }
	public Object evaluate() { return operand.evaluate(); }
	public int precedence() { return 0; }
	public boolean expectBinaryOperator() { return false; }
}


class ExponentNode extends ArithmeticBinaryOperator {
	public ExponentNode() { super("**"); }
	public int performInt(int leftValue, int rightValue) { return (int)Math.pow(leftValue, rightValue); }
	public double performDouble(double leftValue, double rightValue) { return Math.pow(leftValue, rightValue); }
	public int precedence() { return 575; }
}

class MultiplyNode extends ArithmeticBinaryOperator {
	public MultiplyNode() { super("*"); }
	public int performInt(int leftValue, int rightValue) { return leftValue * rightValue; }
	public double performDouble(double leftValue, double rightValue) { return leftValue * rightValue; }
	public int precedence() { return 550; }
}

class DivideNode extends ArithmeticBinaryOperator {
	public DivideNode() { super("/"); }
	public int performInt(int leftValue, int rightValue) { return leftValue / rightValue; }
	public double performDouble(double leftValue, double rightValue) { return leftValue / rightValue; }
	public int precedence() { return 550; }
}

class ModuloNode extends IntegerBinaryOperator {
	public ModuloNode() { super("%"); }
	public int performInt(int leftValue, int rightValue) { return leftValue % rightValue; }
	public int precedence() { return 550; }
}


class BindNode extends BinaryOperator {
	public BindNode() { super("=~"); }
	public BindNode(String op) { super(op); }
	
	public Object perform(Object leftValue, Object rightValue) {
		return ((RegexValue)rightValue).execute(leftValue);
	}
	
	public int precedence() { return 585; }
	
	public String toString() { return left.toString() + " =~ " + right.toString(); }
}

class NotBindNode extends BindNode {
	public NotBindNode() { super("!~"); }
	
	public Object perform(Object leftValue, Object rightValue) {
		return (Integer)super.perform(leftValue, rightValue) == 0 ? 1 : 0;
	}
	
	public String toString() { return left.toString() + " !~ " + right.toString(); }
}


class AddNode extends ArithmeticBinaryOperator {
	public AddNode() { super("+"); }
	
	public Object perform(Object leftValue, Object rightValue) {
		if(isString(leftValue) || isString(rightValue)) {
			return leftValue.toString() + rightValue.toString();
		} else {
			return super.perform(leftValue, rightValue);
		}
	}
	
	public int performInt(int leftValue, int rightValue) { return leftValue + rightValue; }
	public double performDouble(double leftValue, double rightValue) { return leftValue + rightValue; }
	public int precedence() { return 500; }
	
	public String toString() { return left.toString() + " + " + right.toString(); }
}

class SubtractNode extends ArithmeticBinaryOperator {
	public SubtractNode() { super("-"); }
	public int performInt(int leftValue, int rightValue) { return leftValue - rightValue; }
	public double performDouble(double leftValue, double rightValue) { return leftValue - rightValue; }
	public int precedence() { return 500; }
}

class ShiftLeftNode extends IntegerBinaryOperator {
	public ShiftLeftNode() { super("<<"); }
	public int performInt(int leftValue, int rightValue) { return leftValue << rightValue; }
	public int precedence() { return 475; }
}

class ShiftRightNode extends IntegerBinaryOperator {
	public ShiftRightNode() { super(">>"); }
	public int performInt(int leftValue, int rightValue) { return leftValue >> rightValue; }
	public int precedence() { return 475; }
}

class LessThanNode extends RelationalBinaryOperator {
	public LessThanNode() { super("<"); }
	public boolean performInt(int leftValue, int rightValue) { return leftValue < rightValue; }
	public boolean performDouble(double leftValue, double rightValue) { return leftValue < rightValue; }
	public boolean performString(String leftValue, String rightValue) { return leftValue.compareTo(rightValue) < 0; }
	public int precedence() { return 450; }
}

class LessThanEqualToNode extends RelationalBinaryOperator {
	public LessThanEqualToNode() { super("<="); }
	public boolean performInt(int leftValue, int rightValue) { return leftValue <= rightValue; }
	public boolean performDouble(double leftValue, double rightValue) { return leftValue <= rightValue; }
	public boolean performString(String leftValue, String rightValue) { return leftValue.compareTo(rightValue) <= 0; }
	public int precedence() { return 450; }
}

class GreaterThanNode extends RelationalBinaryOperator {
	public GreaterThanNode() { super(">"); }
	public boolean performInt(int leftValue, int rightValue) { return leftValue > rightValue; }
	public boolean performDouble(double leftValue, double rightValue) { return leftValue > rightValue; }
	public boolean performString(String leftValue, String rightValue) { return leftValue.compareTo(rightValue) > 0; }
	public int precedence() { return 450; }
}

class GreaterThanEqualToNode extends RelationalBinaryOperator {
	public GreaterThanEqualToNode() { super(">="); }
	public boolean performInt(int leftValue, int rightValue) { return leftValue >= rightValue; }
	public boolean performDouble(double leftValue, double rightValue) { return leftValue >= rightValue; }
	public boolean performString(String leftValue, String rightValue) { return leftValue.compareTo(rightValue) >= 0; }
	public int precedence() { return 450; }
}

class EqualToNode extends RelationalBinaryOperator {
	public EqualToNode() { super("=="); }
	public boolean performInt(int leftValue, int rightValue) { return leftValue == rightValue; }
	public boolean performDouble(double leftValue, double rightValue) { return leftValue == rightValue; }
	public boolean performString(String leftValue, String rightValue) { return leftValue.equals(rightValue); }
	public int precedence() { return 400; }
}

class NotEqualToNode extends RelationalBinaryOperator {
	public NotEqualToNode() { super("!="); }
	public boolean performInt(int leftValue, int rightValue) { return leftValue != rightValue; }
	public boolean performDouble(double leftValue, double rightValue) { return leftValue != rightValue; }
	public boolean performString(String leftValue, String rightValue) { return !leftValue.equals(rightValue); }
	public int precedence() { return 400; }
}

class BitwiseAndNode extends IntegerBinaryOperator {
	public BitwiseAndNode() { super("&"); }
	public int performInt(int leftValue, int rightValue) { return leftValue & rightValue; }
	public int precedence() { return 375; }
}

class XorNode extends IntegerBinaryOperator {
	public XorNode() { super("^"); }
	public int performInt(int leftValue, int rightValue) { return leftValue ^ rightValue; }
	public int precedence() { return 350; }
}

class BitwiseOrNode extends IntegerBinaryOperator {
	public BitwiseOrNode() { super("|"); }
	public int performInt(int leftValue, int rightValue) { return leftValue | rightValue; }
	public int precedence() { return 325; }
}

class AndNode extends IntegerBinaryOperator {
	public AndNode() { super("&&"); }
	public int performInt(int leftValue, int rightValue) { return rightValue; }
	
	public Object evaluate() {
		Object leftValue = left.evaluate();
		if(promoteInt(leftValue) != 0)
			return perform(leftValue, right.evaluate());
		else
			return 0;
	}
	
	public int precedence() { return 300; }
}

class OrNode extends IntegerBinaryOperator {
	public OrNode() { super("||"); }
	public int performInt(int leftValue, int rightValue) { return rightValue; }
	
	public Object evaluate() {
		Object leftValue = left.evaluate();
		if(promoteInt(leftValue) == 0)
			return perform(leftValue, right.evaluate());
		else
			return leftValue;
	}
	
	public int precedence() { return 275; }
}

class NegateNode extends ArithmeticUnaryOperator {
	public NegateNode() { super("-"); }
	public int performInt(int value) { return -value; }
	public double performDouble(double value) { return -value; }
	public int precedence() { return 600; }
}

class NotNode extends IntegerUnaryOperator {
	public NotNode() { super("!"); }
	public int performInt(int value) { return value == 0 ? 1 : 0; }
	public int precedence() { return 600; }
}

class BitwiseNotNode extends IntegerUnaryOperator {
	public BitwiseNotNode() { super("~"); }
	public int performInt(int value) { return ~value; }
	public int precedence() { return 600; }
}


class PrefixIncrementNode extends IncrementOperator {
	public PrefixIncrementNode() { super("++"); }
	public Object performInt(Variable operand, int value) {
		operand.setValue(value + 1);
		return operand;
	}
	public Object performDouble(Variable operand, double value) {
		operand.setValue(value + 1);
		return operand;
	}
}

class PrefixDecrementNode extends IncrementOperator {
	public PrefixDecrementNode() { super("--"); }
	public Object performInt(Variable operand, int value) {
		operand.setValue(value - 1);
		return operand;
	}
	public Object performDouble(Variable operand, double value) {
		operand.setValue(value - 1);
		return operand;
	}
}

class PostfixIncrementNode extends IncrementOperator {
	public PostfixIncrementNode() { super("++"); }
	public Object performInt(Variable operand, int value) {
		operand.setValue(value + 1);
		return value;
	}
	public Object performDouble(Variable operand, double value) {
		operand.setValue(value + 1);
		return value;
	}
	public int precedence() { return 700; }
	
	public boolean expectBinaryOperator() { return true; }
	
	public String toString() {
		//if(operand.precedence() < this.precedence()) {
		//	return "(" + operand.toString() + ")" + operator;
		//}
		return operand.toString() + operator;
	} 
}

class PostfixDecrementNode extends IncrementOperator {
	public PostfixDecrementNode() { super("--"); }
	public Object performInt(Variable operand, int value) {
		operand.setValue(value - 1);
		return value;
	}
	public Object performDouble(Variable operand, double value) {
		operand.setValue(value - 1);
		return value;
	}
	public int precedence() { return 700; }
	
	public boolean expectBinaryOperator() { return true; }
	
	public String toString() {
		//if(operand.precedence() < this.precedence()) {
		//	return "(" + operand.toString() + ")" + operator;
		//}
		return operand.toString() + operator;
	} 
}


class BreakNode extends TokenNode {

	public TokenNode replaceChild(TokenNode child) { throw new RuntimeException("replaceChild on BreakNode"); }

	public Object evaluate() { throw new BreakStatementException(false); }

	public int precedence() { return 175; }
	
	public String toString() { return "break"; }
}


class ContinueNode extends TokenNode {

	public TokenNode replaceChild(TokenNode child) { throw new RuntimeException("replaceChild on ContinueNode"); }

	public Object evaluate() { throw new BreakStatementException(true); }

	public int precedence() { return 175; }
	
	public String toString() { return "continue"; }
}


class ReturnNode extends UnaryOperator {

	public ReturnNode() { super("return"); }

	public Object evaluate() { throw new ReturnStatementException(operand.evaluate()); }

	public int precedence() { return 175; }
	
	public String toString() {
		if(operand != null)
			return "ret " + operand.toString();
		else
			return "ret"; 
	}
}


class NumberNode extends ValueNode {
	
	public NumberNode(String token) { super(parseValue(token)); }
	
	public static Object parseValue(String token) {
		if(token.indexOf('.') >= 0)
			return Double.parseDouble(token);
		else
			return Integer.parseInt(token);
	}
}


class RegexNode extends ValueNode {

	public RegexNode(Object val) { super(new RegexValue(val.toString())); }

}