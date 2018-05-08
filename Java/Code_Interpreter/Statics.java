
//Metode auxiliare

public class Statics {

	public static String process(String exp) {		//Metoda pregateste Stringul expresie pentru a fi incarcat intr-un arbore:
													 //inlatura tab-urle, newline-urile si caracterul '[' dupa care adauga un space
													  //inaintea fiecarui caracter ']'
		String exp2 = new String();
		for (int i = 0; i < exp.length(); i++) {
			if (exp.charAt(i) != '\t' && exp.charAt(i) != '\n' && exp.charAt(i) != '[') {
				exp2 = exp2 + exp.charAt(i);
			}
		}
		exp2 = exp2.replaceAll("]", " ]");
		return exp2;
	}

	public static Node createArb(String exp) {		//Metoda creeaza un arbore de numar arbitrat de noduri copil (in principiu doar 2),
													 //pe baza String-ului "exp". Metoda utilizeaza TypeDecorator pentru constructia nodurilor

		Node arb = new Simple_N();
		Node crt = arb;
		String buf = new String();
		char aux;
		for (int i = 0; i < exp.length(); i++) {
			aux = exp.charAt(i);
			if (aux == ']') {
				crt = crt.getParent();
			} else if (aux == ';') {
				crt = crt.addNode(new Prg_N(new Simple_N(buf)));
			} else if (aux != ' ') {
				buf += aux;
			} else if (aux == ' ' && buf.length() == 0) {
				continue;
			} else if (buf.compareTo("return") == 0) {
				crt = crt.addNode(new Ret_N(new Simple_N(buf)));
				buf = new String();
			} else if (buf.compareTo("assert") == 0) {
				crt = crt.addNode(new Asr_N(new Simple_N(buf)));
				buf = new String();
			} else if (buf.compareTo("for") == 0) {
				crt = crt.addNode(new For_N(new Simple_N(buf)));
				buf = new String();
			} else if (buf.compareTo("if") == 0) {
				crt = crt.addNode(new If_N(new Simple_N(buf)));
				buf = new String();
			} else if (buf.charAt(0) == '+') {
				crt = crt.addNode(new Op_N(new Simple_N(buf)));
				buf = new String();
			} else if (buf.charAt(0) == '*') {
				crt = crt.addNode(new Op_N(new Simple_N(buf)));
				buf = new String();
			} else if (buf.charAt(0) == '<') {
				crt = crt.addNode(new Op_N(new Simple_N(buf)));
				buf = new String();
			} else if (buf.compareTo("==") == 0) {
				crt = crt.addNode(new Op_N(new Simple_N(buf)));
				buf = new String();
			} else if (buf.charAt(0) == '=') {
				crt = crt.addNode(new Asg_N(new Simple_N(buf)));
				buf = new String();
			} else if (Character.isLetter(buf.charAt(0)) && (aux == ' ' || aux == ']')) {
				crt.addNode(new Sym_N(new Simple_N(buf)));
				buf = new String();
			} else if (Character.isDigit(buf.charAt(0)) && (aux == ' ' || aux == ']')) {
				crt.addNode(new Val_N(new Simple_N(buf)));
				buf = new String();
			}
		}
		return arb;
	}
}
