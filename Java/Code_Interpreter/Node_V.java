

import java.util.HashMap;

public class Node_V implements Visitor {

	HashMap<String, Integer> hash = new HashMap<String, Integer>(); //Aici se retin numele de variabile cu valorile asociate
	String aux, aux2;
	String retVal = null;
	String fail = "Check failed";
	boolean hasRet = false;			//True daca expresia se termina cu un return valid (fara "Check failed")
	boolean errAsrt = false;		//True daca exisa un assert care se evalueaza la fals
	String exp;						//Expresia ce trebuie evaluata

	public boolean check(String exp) {	//Intoarce false daca 'exp' se evalueza la stringul "Check failed"

		if (exp != null) {
			if (exp.compareTo(fail) == 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String visit(Node n1) {	//Se verifica tipul fiecarui nod vizitat

		if (n1 instanceof If_N) {		//Caz de nod If: Se evalueaza conditia si in functie de valoarea de adevar se sterge
										 //unul din nodurile vecine.
			exp = visit(n1.getChild(0));
			if (check(exp)) {			
				if (exp.compareTo("true") == 0) {
					n1.removeChild(2);
				} else {
					n1.removeChild(1);
				}
				visit(n1.getChild(1));
				return "";
			}
			return fail;			//Echivalentul lui "Check failed" pentru conditia if-ului
		} 
		
		else if (n1 instanceof For_N) { 	//Caz de nod For: 
			exp = visit(n1.getChild(0)); 	//Se retine valoarea iteratorului
			if (check(exp)) {
				n1.removeChild(0); 				//Se sterge nodul care contine expresia iteratorului
				exp = visit(n1.getChild(0));
				if (check(exp)) {
					while (exp.compareTo("true") == 0) {	//Cat timp condita de iesire nu este satisfacuta
						exp = visit(n1.getChild(2));		//Se efectueaza un pas de iteratie
						if (check(exp)) {
							exp = visit(n1.getChild(1));	//Se incrementeaza iteratorul
							if (check(exp)) {
								exp = visit(n1.getChild(0));	//Se verifica conditia de iesire
							} else {
								return fail;
							}
						} else {
							return fail;
						}
					}
					return "";
				}
			}
			return fail;
		} 
		
		else if (n1 instanceof Val_N) {		//Caz nod Valoare: se intarce valoarea sub forma de String
			n1.visited();
			return n1.getVal();
		} 
		
		else if (n1 instanceof Sym_N) {		//Caz nod Simbol (variablia): se intoarce numele variabilei
			n1.visited();
			return n1.getVal();
		} 
		
		else if (n1 instanceof Op_N) {		//Caz nod Operatie: Se viziteza operanzii si apoi se evalueaza expresia in functie de
											 //operatie 
			String exp1 = visit(n1.getChild(0));
			String exp2 = visit(n1.getChild(1));
			return eval(n1.getVal(), exp1, exp2);
		} 
		
		else if (n1 instanceof Prg_N) {		//Caz nod Program: Se viziteaza cele 2 noduri copil pe rand
			exp = visit(n1.getChild(0));
			if(check(exp)) {
				exp = visit(n1.getChild(1));
				if(check(exp)) {
					return "";
				}
			}
			return fail;
		}
		
		else if (n1 instanceof Asr_N) {		//Caz nod Assert: In functie de rezultatul vizitei nodului copil returneaza un string.
											 //Aici se seteaza, desigur, si valoarea variabilei 'errAsrt' in caz de "Assertion failed"
			aux = visit(n1.getChild(0));
			if(check(aux)){
				if (aux.compareTo("false") == 0) {
					errAsrt = true;
				}
				return "";
			}
			return fail;
		} 
		
		else if (n1 instanceof Asg_N) {		//Caz nod Asignare: Asociaza unui nume de variabila o valoare, exceptand cazul "Check failed"
			aux = visit(n1.getChild(1));	//Se verifica mai intai valoarea care trebuie asignata
			if(check(aux)){
				Integer val;
				if (Character.isAlphabetic(aux.charAt(0))) {
					if (!hash.containsKey(aux)) {
						return fail;
					} else {
						val = hash.get(aux);
					}
				} else {
					val = Integer.parseInt(aux);
				}
				aux2 = visit(n1.getChild(0));
				if(check(aux2)){
					if (hash.containsKey(aux2)) {
						hash.remove(aux2);
					}
					hash.put(aux2, val);	//Se introduce variabila, impreuna cu valoarea, in hash
					return "";
				}
			}
			return fail;
		} 
		
		else if (n1 instanceof Ret_N) {		//Caz de nod Return: are rolul de a indica existenta unui 'return' valid in expresie

			aux = visit(n1.getChild(0));
			if (Character.isDigit(aux.charAt(0))) {
				retVal = aux;
				hasRet = true;
			} else {
				if (hash.containsKey(aux)) {
					Integer q = hash.get(aux);
					retVal = q.toString();
					hasRet = true;
				} else {
					return fail;
				}
			}
			return "";
		} 
		
		else if (n1 instanceof Simple_N) {		//Caz nod Radacina: are rolul de a intoarce valoarea ce va fi scrisa in fisier
			exp = visit(n1.getChild(0));
			if (check(exp)) {
				if (!hasRet) {
					return "Missing return";
				}
				if (errAsrt) {
					return "Assert failed";
				}
				return retVal;
			}
			return fail;
		}
		return null;
	}

	public String eval(String op, String exp1, String exp2) {	//Functia de aplicare a unei operatii pe 2 operanzi, in 
																 //functie de Strigul 'op'
		Integer v1, v2;
		Integer aux;
		if (Character.isAlphabetic(exp1.charAt(0))) {
			if (hash.containsKey(exp1)) {
				v1 = hash.get(exp1);
			} else {
				return fail;
			}
		} else {
			v1 = Integer.parseInt(exp1);
		}
		if (Character.isAlphabetic(exp2.charAt(0))) {
			if (hash.containsKey(exp2)) {
				v2 = hash.get(exp2);
			} else {
				return fail;
			}
		} else {
			v2 = Integer.parseInt(exp2);
		}
		switch (op) {
		case "+":
			aux = v1 + v2;
			return aux.toString();
		case "*":
			aux = v1 * v2;
			return aux.toString();
		case "<":
			if (v1 < v2) {
				return "true";
			} else
				return "false";
		case "==":
			if (v1 == v2) {
				return "true";
			} else
				return "false";
		}
		return null;
	}
}
