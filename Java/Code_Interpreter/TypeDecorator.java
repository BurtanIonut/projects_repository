
//Clasa abstracta pentru Decoratorul de Noduri

import java.util.Vector;

abstract class TypeDecorator implements Node {

	protected Node tempNode;

	public TypeDecorator(Node newNode) {

		tempNode = newNode;
	}

	public Node addNode(Node n) {
		
		return tempNode.addNode(n);
	}

	public void setParent(Node n) {
		
		tempNode.setParent(n);
	}
	
	public Node getParent() {
		
		return tempNode.getParent();
	}
	
	public Vector<Node> getChilds() {
		
		return tempNode.getChilds();
	}
	
	public String getVal() {
		
		return tempNode.getVal();
	}
	
	public Node getChild(int nr) {
		
		return tempNode.getChild(nr);
	}
}
