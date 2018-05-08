package pckT3;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *         <p>
 *         The class representing the Vistior of the Visitor design pattern
 *
 */
public class SolverVisitor implements Visitor<Object> {

	/**
	 * @see pckT3.Visitor#visit(pckT3.NodeElement)
	 */
	@Override
	public Object visit(NodeElement nodeEl) {
		return nodeEl.getValue();
	}

	/**
	 * @see pckT3.Visitor#visit(pckT3.NodeOp)
	 * 
	 */
	@Override
	public Object visit(NodeOp nodeOp) {
		if (nodeOp.isFixed() == false) {
			SolverVisitor vs = new SolverVisitor();
			nodeOp.setValue(Statics.doOperation(nodeOp.getLChild().getResult(vs), nodeOp.getRChild().getResult(vs),
					nodeOp.getInfo().getId()));
			nodeOp.setFixed();
			return nodeOp.getValue();
		} else {
			return nodeOp.getValue();
		}
	}
}
