import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class Permutari {

	public static void main(String[] args) {

		String alph = new String();			//String ce contine toate litere din alfabet in ordine inversa
		for (int i = 0; i < 26; i++) {
			alph += ((char) (122 - i));
		}
		String topo = new String();			//Va servi drept variabila de retur, dupa ce s-a efectuat sortarea
		String stack = new String();
		HashMap<Character, Node> hash = new HashMap<Character, Node>();		//Retin graful intr-un hash
		String crtWord = null;
		String prevWord = null;
		Node aux;

		BufferedReader f = null;
		PrintWriter w = null;

		try {
			f = new BufferedReader(new FileReader("permutari.in"));
			w = new PrintWriter(new BufferedWriter(new FileWriter("permutari.out")));
			prevWord = f.readLine();						//Numarul de cuvinte, neimportant
			prevWord = f.readLine();						//Primul cuvant
			crtWord = f.readLine();							//Urmatorul cuvant
			while (crtWord != null) {						//Cat timp mai exista cuvinte de citit
				for (int i = 0; i < prevWord.length(); i++) {
					if (i == crtWord.length()) {			//Caz in care cuvantul precedent are o lungime mai mare
															 //ca a celui curent si contine cuvantul curent
						System.out.println(prevWord.length());
						w.print("Imposibil");
						w.close();
						f.close();
						return;
					}
					if (prevWord.charAt(i) != crtWord.charAt(i)) {		//Daca literele de pe aceleasi pozitii difera
						if (!hash.containsKey(prevWord.charAt(i))) {	//Daca nu exista litera din cuvantul precedent in graf
							hash.put(prevWord.charAt(i), new Node(prevWord.charAt(i)));
							stack += prevWord.charAt(i);				//Se introduce pe 'stiva' o noua litera
																		 //'stiva' dicteaza ordinea de parcurgere a nodurilor in DFS
						}
						aux = hash.get(prevWord.charAt(i));
						if (!hash.containsKey(crtWord.charAt(i))) {		//Daca nu exista litera din cuvantul curent deja in graf
							hash.put(crtWord.charAt(i), new Node(crtWord.charAt(i)));
						}
						aux.putEdg(hash.get(crtWord.charAt(i)));		//Adaug muchie intre litera din cuvantul precedent si
						 												 //litera din cuvantul curent
						break;
					}
				}
				prevWord = crtWord;				//Cuvantul precedent devine cel curent
				crtWord = f.readLine();			//Se actualizeaza cuvantul curent cu urmatorul cuvant din fisier
			}
			String sort = Statics.dfs(hash, stack);				//Aplic algoritmul DFS pentru sortarea topologica a literelor
			if (sort.compareTo("Imposibil") == 0) {
				w.print("Imposibil");
				w.close();
				f.close();
				return;
			} else {
				for (int i = 0; i < sort.length(); i++) {		//Introduc literele ordonate la stringul de return
					topo += sort.charAt(i);
				}
				for (int i = 0; i < alph.length(); i++) {		//Introduc literele ramase din alfabet in stringul de return
					if (!sort.contains(Character.toString(alph.charAt(i)))) {
						topo += alph.charAt(i);
					}
				}
			}
			StringBuffer buf = new StringBuffer(topo);
			w.print(buf.reverse());						//Scriu rezultatul in fisier
			w.close();
			f.close();
		} catch (IOException e) {
			System.out.println("Unavailable file");
		}
	}
}
