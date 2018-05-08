package pckT3;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *         <p>
 *         This class defines the basic node of a binary-tree(a
 *         {@link pckT3.Tree#Tree() Tree}), with information on an expression's
 *         {@link pckT3.Term#Term() terms}. It is also set to allow creation of
 *         "Visitable" {@link pckT3.Node#Node() nodes}, as it implements the
 *         {@link pckT3.Visitable Visitable} interface
 */
abstract class Node implements Visitable<Object> {

	private Term info;
	private Node c1 = null;
	private Node c2 = null;

	public Term getInfo() {

		return info;
	}

	public Node getRChild() {

		return c2;
	}

	public Node getLChild() {

		return c1;
	}

	public void setInfo(Term info) {

		this.info = info;
	}

	public void setLChid(Node child) {

		this.c1 = child;
	}

	public void setRChild(Node child) {

		this.c2 = child;
	}

}
