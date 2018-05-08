//Nod de tip For

public class For_N extends TypeDecorator {
	
	public For_N (Node newNode) {
		super(newNode);
	}
	
	@Override
	public Node addNode(Node n) {
		
		return tempNode.addNode(n);
	}

	@Override
	public void setParent(Node n) {
		
		tempNode.setParent(n);
	}
	
	@Override
	public Node getParent() {
		
		return tempNode.getParent();
	}

	@Override
	public String accept(Visitor v) {
		
		return v.visit(this);
	}
	
	public String getVal() {
		
		return tempNode.getVal();
	}

	@Override
	public void visited() {
		
		tempNode.visited();
		
	}

	@Override
	public boolean isVisited() {

		return tempNode.isVisited();
	}
	
	@Override
	public void removeChild(int nr) {
		
		tempNode.removeChild(nr);
	}
	
	@Override
	public Node getChild(int nr) {
		
		return tempNode.getChild(nr);
	}
}
