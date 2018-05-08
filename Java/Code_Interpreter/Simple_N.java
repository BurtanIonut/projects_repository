
import java.util.Vector;

//Nod Simplu, utilizat de TypeDecorator, serveste si ca radacina pentu arborele expresiei

public class Simple_N implements Node {

	Vector<Node> childs = new Vector<Node>();
	Node parent;
	String val;
	String retVal;
	int visited = 0;
	
	public Simple_N() {
		
	}
	
	public Simple_N(String x) {
		
		this.val = x;
	}

	@Override
	public Node addNode(Node n) {
		
		childs.add(n);
		n.setParent(this);
		return n;
	}

	@Override
	public void setParent(Node n) {
		
		parent = n;
	}

	@Override
	public Node getParent() {
		
		return parent;
	}

	@Override
	public Vector<Node> getChilds() {
		
		return childs;
	}

	@Override
	public String accept(Visitor v) {
		
		return v.visit(this);
	}

	public String getVal() {
		
		return val;
	}

	@Override
	public void visited() {
		
		visited = 1;
	}

	@Override
	public boolean isVisited() {
		
		if (visited == 1) {
			
			return true;
		}
		else {
			
			return false;
		}
	}

	@Override
	public void removeChild(int nr) {
		
		childs.remove(nr);
	}

	@Override
	public Node getChild(int nr) {
		
		return childs.get(nr);
	}
	
}
