/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.util.concurrent.Callable;

/**
 * An abstract class that provides a simple API for a disruptor event handler
 * that processes a single input object at a time and produces output objects
 * that may be collected by a multiplexer thread.
 * 
 * @author Simon Heiden
 * 
 * @param <A>
 * the type of the input objects
 * @param <B>
 * the type of the output objects
 * 
 * @see Callable
 */
public abstract class EHWithInputAndReturn<A,B> extends DisruptorFCFSEventHandler<A> implements IMultiplexerInput<B> {

	/**
	 * The output object.
	 */
	private B output = null;
	
	private boolean hasNewOutput = false;
	private final Object lock = new Object();
	private IMultiplexer<B> multiplexer = null;

	@Override
	public void processEvent(A input) throws Exception {
		setOutputAndNotifyMultiplexer(processInput(input));
	}
	
	/**
	 * Processes a single item of type A and returns an item of type B (or {@code null}).
	 * Has to be instantiated by implementing classes.
	 * @param input
	 * the input item
	 * @return
	 * an item of type B
	 */
	public abstract B processInput(A input);
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#setMultiplexer(se.de.hu_berlin.informatik.utils.threaded.NToOneMultiplexer)
	 */
	@Override
	public void setMultiplexer(IMultiplexer<B> multiplexer) {
		if (multiplexer == null) {
			throw new IllegalStateException("No multiplexer given (null).");
		}
		this.multiplexer = multiplexer;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#getLock()
	 */
	@Override
	public Object getLock() {
		return lock;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#getOutput()
	 */
	@Override
	public B getOutput() {
		return output;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#setOutput(java.lang.Object)
	 */
	@Override
	public boolean setOutput(B item) {
		output = item;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#outputItemIsValid()
	 */
	@Override
	public boolean outputItemIsValid() {
		return hasNewOutput;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#setOutputItemValid()
	 */
	@Override
	public void setOutputItemValid() {
		hasNewOutput = true;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#setOutputItemInvalid()
	 */
	@Override
	public void setOutputItemInvalid() {
		hasNewOutput = false;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#getMultiplexer()
	 */
	@Override
	public IMultiplexer<B> getMultiplexer() {
		return multiplexer;
	}
	
}
