/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

/**
 * An interface that provides basic functionalities of transmitters that can be linked together.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 * @param <B>
 * is the type of the output object
 */
public interface Transmitter<A,B> extends Consumer<A>, Processor<A,B>, Producer<B> {
	
	/**
	 * Consumes an item of type {@code A}, processes it and produces an item of type {@code B}.
	 * @param item
	 * the item to be processed
	 */
	@Override
	default void consume(A item) {
		produce(process(item));
	}


	/**
	 * Should be overwritten by implementing transmitters that may collect
	 * input items without immediately processing them. This method should
	 * process possibly remaining collected items and/or return the result
	 * (or null if there is no result).
	 * @return
	 * the result of unprocessed collected items
	 */
	default public B getResultFromCollectedItems(){
		return null;
	}
	
	/**
	 * Should cut all loose ends.
	 * @return
	 * true if successful
	 */
	default public boolean finalShutdown() {
		return true;
	}
	
	/**
	 * Creates a pipe object from this transmitter that has the transmitter's 
	 * functionality. Has to return a reference to the same object if called
	 * multiple times.
	 * @return
	 * a pipe, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	public AbstractPipe<A,B> asPipe() throws UnsupportedOperationException;
	
	/**
	 * Creates a module object from this transmitter that has the transmitter's 
	 * functionality. Has to return a reference to the same object if called
	 * multiple times.
	 * @return
	 * a module, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	public AbstractModule<A,B> asModule() throws UnsupportedOperationException;
	
	/**
	 * Creates an event handler from this transmitter that has the transmitter's 
	 * functionality. Has to return a reference to the same object if called
	 * multiple times.
	 * @return
	 * a new event handler, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	public EHWithInputAndReturn<A,B> asEH() throws UnsupportedOperationException;

}
