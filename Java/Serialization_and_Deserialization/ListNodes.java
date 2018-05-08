package pack;

/**
 * 
 * @author Burtan-Ionut_325CB
 *         <p>
 *         The class describes a Linked List of {@link pack.Cell#Cell(Node)
 *         Cell} elements, as well as "Overriding" the necessary methods, as it
 *         extends the abstract class {@link pack.AdjacentNodes#AdjacentNodes()
 *         AdjacentNodes}
 *
 */
public class ListNodes extends AdjacentNodes {

	Cell head = null;// First element of the list
	Cell crt = head;// Last element of the list

	public ListNodes() {

	}

	@Override
	public void addLink(Node nod) {

		if (head == null) {
			head = new Cell(nod);
			crt = head;
			return;
		}
		Cell aux = new Cell(nod);
		crt.next = aux;
		crt = aux;

	}

	@Override
	public void delLink(Node nod) {

		if (head == null) { // If adaugat ulterior
			return;
		}
		if (head.info == nod && head == crt) {
			head = null;
			crt = head;
			return;
		}
		if (head.info == nod) {
			head = head.next;
			return;
		}
		Cell aux = head.next;
		Cell prev = head;
		while (aux != null) {

			if (aux.info == nod) {
				prev.next = aux.next;
			}
			prev = aux;
			if (aux.next == crt) {
				this.crt = aux;
			}
			aux = aux.next;
		}
	}
}
