import java.util.HashMap;

public class Statics {
						//Pentru fiecare nod vizat de literele din 'stiva' se apeleaza algoritmul de DFS
	public static String dfs(HashMap<Character, Node> hash, String stack) {

		Character crt;
		String sort = new String();
		String rez = null;

		for (int i = 0; i < stack.length(); i++) {
			crt = stack.charAt(i);
			hash.get(crt).mark();
			rez = sortNode(hash, hash.get(crt));	//Se gaseste urmatoarea ordine de litere pentru nodul curent prin DFS
			if (rez.compareTo("Imposibil") == 0) {
				return rez;
			}
			for (int j = 0; j < rez.length(); j++) {
				if (!sort.contains(Character.toString(rez.charAt(j)))) {
					sort += rez.charAt(j);		//Se introduc in sortarea finala litere neduplicate
				}
			}
		}
		return sort;
	}

	public static String sortNode(HashMap<Character, Node> hash, Node node) {
										//Functia ce realizeaza parcurgerea in adancime
		String sort = new String();
		String auxStr = new String();
		Node aux;
		for (int i = 0; i < node.getAdj().size(); i++) {	//Pentru fiecare nod invecinat cu nodul curent
															 //se reapeleaza functia pana la gasirea unei frunze sau
															 //a unui nod in curs de vizitare / vizitat
			aux = node.getAdj().get(i);
			if (aux.marked() == 1) {			//Daca s-a ajuns la un nod care este in curs de vizita inseamna ca s-a
												 //gasit un ciclu, programul se opreste si intoarce "Imposibil"
				return "Imposibil";
			}
			if (aux.marked() == 0) {
				aux.mark();						//Se marcheaza nodul ca in curs de vizitare
				auxStr = sortNode(hash, aux);
				if (auxStr.compareTo("Imposibil") == 0) {
					return auxStr;
				}
				sort += auxStr;
			}
		}
		node.mark();							//Se marcheaza nodul ca vizitat
		return (sort += node.getVal());
	}
}
