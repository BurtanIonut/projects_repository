package pack;

/**
 * 
 * @author Burtan-Ionut_325CB
 *         <p>
 *         This class describes a Vector with elements of type
 *         {@link pack.Node#Node(String, char, char, int) Node} It extends the
 *         abstract class {@link pack.AdjacentNodes#AdjacentNodes()
 *         AdjacentNodes} and Overrides the necessary methods. In addition it
 *         presents an auxiliary method that copies the contents of a Vector
 *         into another Vector
 *         {@link pack.VectNodes#copyVect(VectNodes, VectNodes, int, int)
 *         copyVect}
 * 
 */
public class VectNodes extends AdjacentNodes {

	Node[] vect = null;// The Vector with (link-to)Nodes elements
	int length = 0;// The maximum number of elements that can be stored
	int pozCrt = 0;// The position after the last inserted element

	public VectNodes() {

	}

	/**
	 * Constructor that creates a Vector of
	 * {@link pack.Node#Node(String, char, char, int) Nodes} of dimension
	 * 'length' and sets the 'int length' field and the 'int pozCrt' field to 0,
	 * naturally.
	 * 
	 * @param length
	 *            The maximum number of position the Vector can store
	 *            information on
	 */
	public VectNodes(int length) {

		vect = new Node[length];
		this.length = length;
		pozCrt = 0;
	}

	/**
	 * This method copies the contents of a vector of {@link pack.Node#Node(String, char, char, int) Node} elements to
	 * another one
	 * 
	 * @param dest
	 *            The Vector to be copied to
	 * @param src
	 *            The Vector to be copied from
	 * @param startOff
	 *            An offset of positions after which to copy elements
	 * @param len
	 *            Number of elements to be copied
	 * @return
	 *         Resulting "dest" Vector
	 */
	public VectNodes copyVect(VectNodes dest, VectNodes src, int startOff, int len) {

		int i = 0;
		for (i = 0; i < len; i++) {
			dest.vect[dest.pozCrt + i] = src.vect[i + startOff];
		}
		dest.pozCrt = i + dest.pozCrt;
		return dest;
	}

	@Override
	public void addLink(Node nod) {
		if (vect == null) {
			vect = new Node[10];
			vect[0] = nod;
			length = 10;
			pozCrt = 1;
			return;
		}
		vect[pozCrt] = nod;
		pozCrt++;
		if (pozCrt == length) {// If the inserted element fills the Vector
								// completely, the Vector will be resized
			VectNodes aux = new VectNodes(length * 2);
			aux = copyVect(aux, this, 0, length);
			vect = aux.vect;
			this.length = aux.length;
			this.pozCrt = aux.pozCrt;
		}
	}

	@Override
	public void delLink(Node nod) {

		VectNodes aux = new VectNodes(this.length);
		int poz = 0;
		while (vect[poz] != nod) {
			if (poz == length - 1) {
				return;
			}
			poz++;
		}
		if (poz == 0) {
			aux = copyVect(aux, this, 1, this.pozCrt);
			vect = aux.vect;
			this.pozCrt = pozCrt - 1;
			return;
		}
		aux = copyVect(aux, this, 0, poz);
		aux = copyVect(aux, this, poz + 1, this.pozCrt);
		vect = aux.vect;
		this.pozCrt = pozCrt - 1;
	}

}
