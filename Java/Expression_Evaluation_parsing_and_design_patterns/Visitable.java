package pckT3;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *
 * @param <T>
 *            The Visitable interface of the Visitor design pattern
 */
public interface Visitable<T> {

	public T getResult(Visitor<?> visitor);
}
