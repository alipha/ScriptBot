import java.util.ArrayList;


public class AssignmentNode extends BinaryOperator {

	public BinaryOperator binaryOp;
	
	public AssignmentNode() { super("="); }
	
	public AssignmentNode(BinaryOperator op) { 
		super(op.operator + "=");
		binaryOp = op;
	}
	
	public Object perform(Object leftValue, Object rightValue) { throw new RuntimeException("perform on AssignmentNode"); }
	
	public Object evaluate() {		
		Object rightValue = right.evaluate();
		
		if(rightValue instanceof Variable) {
			rightValue = ((Variable)rightValue).getValue();
		}
		
		if(left instanceof LeftArrayNode) {
			if(!(rightValue instanceof ArrayList))
				throw new RuntimeException("Assigning non-array to array initializer");
			
			ArrayList<Object> leftArray = (ArrayList<Object>)((LeftArrayNode)left).evaluate(true);
			ArrayList<Object> rightArray = (ArrayList<Object>)rightValue;
			
			for(int i = 0; i < leftArray.size(); ++i) {
				if(binaryOp != null)
					((Variable)leftArray.get(i)).setValue(binaryOp.perform(leftArray.get(i), ((Variable)rightArray.get(i)).getValue()));
				else
					((Variable)leftArray.get(i)).setValue(((Variable)rightArray.get(i)).getValue());
			}
			return leftArray;
			
		} else {
			Object leftValue = this.left.evaluate();
			
			/*
			if(!(left instanceof Variable)) {
				throw new RuntimeException("Left-side of assignment must be a variable");
			}
			*/
			
			if(binaryOp != null)
				rightValue = binaryOp.perform(leftValue, rightValue);
			
			((Variable)leftValue).setValue(rightValue);
			return leftValue;
		}
	}

	public int precedence() { return 200; }
	
	public boolean rightAssociative() { return true; }
}
