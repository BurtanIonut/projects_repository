package pckT3;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 * 
 *
 */
public class Statics {
	/**
	 * 
	 * @param op
	 *            String holding a symbol
	 * @return true if input symbol relates to an arithmetic operator
	 */
	public static boolean isOperation(String op) {

		if (op.compareTo("+") == 0 || op.compareTo("/") == 0 || op.compareTo("*") == 0 || op.compareTo("-") == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param op1
	 *            a {@link pckT3.NodeOp#NodeOp(Op) NodeOp}
	 * @param op2
	 *            a {@link pckT3.NodeOp#NodeOp(Op) NodeOp}
	 * @return true if op1 has greater arithmetic priority than op2
	 */
	public static boolean hasPriority(String op1, String op2) {

		int priority1;
		int priority2;
		if (isOperation(op1) == false) {
			return false;
		}
		if (op1.compareTo("*") == 0 || op1.compareTo("/") == 0) {
			priority1 = 1;
		} else {
			priority1 = 0;
		}
		if (op2.compareTo("*") == 0 || op2.compareTo("/") == 0) {
			priority2 = 1;
		} else {
			priority2 = 0;
		}
		if (priority1 >= priority2) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param t1
	 *            left value of the expression
	 * @param t2
	 *            right value of the expression
	 * @param op
	 *            operator
	 * @return the result of "t1 op t2" evaluated from left to right
	 */
	public static Object doOperation(Object t1, Object t2, String op) {

		switch (op) {
		case "+":
			if (t1 instanceof Integer && t2 instanceof Integer) {
				return (Integer) t1 + (Integer) t2;
			} else if (t1 instanceof Integer && t2 instanceof Double) {
				return (Integer) t1 + (Double) t2;
			} else if (t1 instanceof Integer && t2 instanceof String) {
				return t1.toString().concat(((String) t2));
			} else if (t1 instanceof Integer && t2 instanceof NaN) {
				return t2;
			} else if (t1 instanceof Double && t2 instanceof Double) {
				return (Double) t1 + (Double) t2;
			} else if (t1 instanceof Double && t2 instanceof Integer) {
				return (Double) t1 + (Integer) t2;
			} else if (t1 instanceof Double && t2 instanceof String) {
				t1 = Math.round((Double) t1 * 100.0) / 100.0;
				return t1.toString().concat(((String) t2));
			} else if (t1 instanceof Double && t2 instanceof NaN) {
				((NaN) t2).setDouble();
				return t2;
			} else if (t1 instanceof String && t2 instanceof String) {
				return ((String) t1).concat(((String) t2));
			} else if (t1 instanceof String && t2 instanceof Integer) {
				return ((String) t1).concat(t2.toString());
			} else if (t1 instanceof String && t2 instanceof Double) {
				t2 = Math.round((Double) t2 * 100.0) / 100.0;
				return ((String) t1).concat(t2.toString());
			} else if (t1 instanceof String && t2 instanceof NaN) {
				return ((String) t1).concat("NaN");
			} else if (t1 instanceof NaN && t2 instanceof NaN) {
				if (((NaN) t1).getType() instanceof Integer && ((NaN) t2).getType() instanceof Integer) {
					return t1;
				} else {
					((NaN) t1).setDouble();
					return t1;
				}
			} else if (t1 instanceof NaN && t2 instanceof Integer) {
				return t1;
			} else if (t1 instanceof NaN && t2 instanceof Double) {
				((NaN) t1).setDouble();
				return t1;
			} else if (t1 instanceof NaN && t2 instanceof String) {
				String aux = "NaN";
				return aux.concat(((String) t2));
			}
		case "-":
			if (t1 instanceof Integer && t2 instanceof Integer) {
				return (Integer) t1 - (Integer) t2;
			} else if (t1 instanceof Integer && t2 instanceof Double) {
				return (Integer) t1 - (Double) t2;
			} else if (t1 instanceof Integer && t2 instanceof String) {
				return (Integer) t1 - ((String) t2).length();
			} else if (t1 instanceof Integer && t2 instanceof NaN) {
				return t2;
			} else if (t1 instanceof Double && t2 instanceof Double) {
				return (Double) t1 - (Double) t2;
			} else if (t1 instanceof Double && t2 instanceof Integer) {
				return (Double) t1 - (Integer) t2;
			} else if (t1 instanceof Double && t2 instanceof String) {
				return (Double) t1 - ((String) t2).length();
			} else if (t1 instanceof Double && t2 instanceof NaN) {
				((NaN) t2).setDouble();
				return t2;
			} else if (t1 instanceof String && t2 instanceof String) {
				return ((String) t1).length() - ((String) t2).length();
			} else if (t1 instanceof String && t2 instanceof Integer) {
				if ((Integer) t2 < 0) {
					String pad = "";
					for (int i = 0; i < Math.abs((Integer) t2); i++) {
						pad = pad.concat("#");
					}
					return ((String) t1).concat(pad);
				}
				if ((Integer) t2 > ((String) t1).length()) {
					return "";
				} else {
					return (((String) t1).substring(0, ((String) t1).length() - (Integer) t2));
				}
			} else if (t1 instanceof String && t2 instanceof Double) {
				return ((String) t1).length() - (Double) t2;
			} else if (t1 instanceof String && t2 instanceof NaN) {
				if (((NaN) t2).getType() instanceof Integer) {
					return t1;
				} else {
					return t2;
				}
			} else if (t1 instanceof NaN && t2 instanceof NaN) {
				if (((NaN) t1).getType() instanceof Integer && ((NaN) t2).getType() instanceof Integer) {
					return t1;
				} else {
					((NaN) t1).setDouble();
					return t1;
				}
			} else if (t1 instanceof NaN && t2 instanceof Integer) {
				return t1;
			} else if (t1 instanceof NaN && t2 instanceof Double) {
				((NaN) t1).setDouble();
				return t1;
			} else if (t1 instanceof NaN && t2 instanceof String) {
				return t1;
			}
		case "*":
			if (t1 instanceof Integer && t2 instanceof Integer) {
				return (Integer) t1 * (Integer) t2;
			} else if (t1 instanceof Integer && t2 instanceof Double) {
				return (Integer) t1 * (Double) t2;
			} else if (t1 instanceof Integer && t2 instanceof String) {
				if ((Integer) t1 <= 0) {
					return "";
				} else {
					String rez = "";
					for (int i = 0; i < (Integer) t1; i++) {
						rez = rez.concat((String) t2);
					}
					return rez;
				}
			} else if (t1 instanceof Integer && t2 instanceof NaN) {
				return t2;
			} else if (t1 instanceof Double && t2 instanceof Double) {
				return (Double) t1 * (Double) t2;
			} else if (t1 instanceof Double && t2 instanceof Integer) {
				return (Double) t1 * (Integer) t2;
			} else if (t1 instanceof Double && t2 instanceof String) {
				return (Double) t1 * ((String) t2).length();
			} else if (t1 instanceof Double && t2 instanceof NaN) {
				((NaN) t2).setDouble();
				return t2;
			} else if (t1 instanceof String && t2 instanceof String) {
				return ((String) t1).length() * ((String) t2).length();
			} else if (t1 instanceof String && t2 instanceof Integer) {
				if ((Integer) t2 <= 0) {
					return "";
				} else {
					String rez = "";
					for (int i = 0; i < (Integer) t2; i++) {
						rez = rez.concat((String) t1);
					}
					return rez;
				}
			} else if (t1 instanceof String && t2 instanceof Double) {
				return (Double) t2 * ((String) t1).length();
			} else if (t1 instanceof String && t2 instanceof NaN) {
				if (((NaN) t2).getType() instanceof Integer) {
					return "";
				} else {
					return t2;
				}
			} else if (t1 instanceof NaN && t2 instanceof NaN) {
				if (((NaN) t1).getType() instanceof Integer && ((NaN) t2).getType() instanceof Integer) {
					return t1;
				} else {
					((NaN) t1).setDouble();
					return t1;
				}
			} else if (t1 instanceof NaN && t2 instanceof Integer) {
				return t1;
			} else if (t1 instanceof NaN && t2 instanceof Double) {
				((NaN) t1).setDouble();
				return t1;
			} else if (t1 instanceof NaN && t2 instanceof String) {
				return t1;
			}
		case "/":
			if (t1 instanceof Integer && t2 instanceof Integer) {
				if ((Integer) t2 == 0) {
					return new NaN("Integer");
				} else {
					return (Integer) t1 / (Integer) t2;
				}
			} else if (t1 instanceof Integer && t2 instanceof Double) {
				if ((Double) t2 == 0.0) {
					return new NaN("Double");
				} else {
					return (Integer) t1 / (Double) t2;
				}
			} else if (t1 instanceof Integer && t2 instanceof String) {
				return doOperation((Integer) t1, (Integer) ((String) t2).length(), "/");
			} else if (t1 instanceof Integer && t2 instanceof NaN) {
				return t2;
			} else if (t1 instanceof Double && t2 instanceof Double) {
				if ((Double) t2 == 0.0) {
					return new NaN("Double");
				} else {
					return (Double) t1 / (Double) t2;
				}
			} else if (t1 instanceof Double && t2 instanceof Integer) {
				if ((Integer) t2 == 0) {
					return new NaN("Double");
				} else {
					return (Double) t1 / (Integer) t2;
				}
			} else if (t1 instanceof Double && t2 instanceof String) {
				return doOperation((Double) t1, (Integer) ((String) t2).length(), "/");
			} else if (t1 instanceof Double && t2 instanceof NaN) {
				((NaN) t2).setDouble();
				return t2;
			} else if (t1 instanceof String && t2 instanceof String) {
				return doOperation((Integer) ((String) t1).length(), (Integer) ((String) t2).length(), "/");
			} else if (t1 instanceof String && t2 instanceof Integer) {
				if ((Integer) t2 <= 0) {
					return t1;
				} else {
					return (((String) t1).substring(0, ((String) t1).length() / (Integer) t2));
				}
			} else if (t1 instanceof String && t2 instanceof Double) {
				return doOperation((Integer) ((String) t1).length(), (Double) t2, "/");
			} else if (t1 instanceof String && t2 instanceof NaN) {
				if (((NaN) t2).getType() instanceof Integer) {
					return t1;
				} else {
					return t2;
				}
			} else if (t1 instanceof NaN && t2 instanceof NaN) {
				if (((NaN) t1).getType() instanceof Integer && ((NaN) t2).getType() instanceof Integer) {
					return t1;
				} else {
					((NaN) t1).setDouble();
					return t1;
				}
			} else if (t1 instanceof NaN && t2 instanceof Integer) {
				return t1;
			} else if (t1 instanceof NaN && t2 instanceof Double) {
				((NaN) t1).setDouble();
				return t1;
			} else if (t1 instanceof NaN && t2 instanceof String) {
				return t1;
			}
		}
		return "Undefined Operation";
	}
}
