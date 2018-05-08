

import java.util.Vector;

public interface Node extends Visitable{

	public Node addNode (Node n);
	
	public void setParent (Node n);
	
	public Node getParent ();
	
	public Vector<Node> getChilds ();
	
	public String getVal();
	
	public void visited();
	
	public boolean isVisited();
	
	public void removeChild(int nr);
	
	public Node getChild(int nr);
}
