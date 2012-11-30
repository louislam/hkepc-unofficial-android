package net.louislam.hkepc;

import java.util.Stack;

import net.louislam.hkepc.page.NonHistoryPage;

public class HistoryStack<E> extends Stack<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see java.util.Stack#push(java.lang.Object)
	 */
	@Override
	public E push(E object) {
		
		if (object instanceof NonHistoryPage) {
			return object;
		}
		
		return super.push(object);
	}
	
	

}
