import java.util.Vector;

public class Node {
							//Clasa Node pentru constructia noodurilor din graf
	private Character val;
	private int mark = 0;
	private Vector<Node> adj = new Vector<Node>();

	public Node(Character val) {	//Construieste nod cu o litera specificata

		this.val = val;
	}

	public Character getVal() {

		return val;
	}

	public Vector<Node> getAdj() {		//Intoarce lista de adiacenta cu nodurile vecine

		return adj;
	}

	public void putEdg(Node nbh) {		//Introduce o noua muchie in lista de adiacenta

		adj.add(nbh);
	}

	public int marked() {				//Intoarce starea noduli: 0-nevizitat; 1-in curs de vizitare; 2-vizitat

		if (mark == 0) {
			return 0;
		} else if (mark == 1) {
			return 1;
		} else {
			return 2;
		}
	}

	public void mark() {			//Seteaza starea nodului

		if (mark == 0) {
			mark = 1;
		} else if (mark == 1) {
			mark = 2;
		}
	}
}
