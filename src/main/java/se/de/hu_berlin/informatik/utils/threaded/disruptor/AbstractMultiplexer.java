package se.de.hu_berlin.informatik.utils.threaded.disruptor;

/**
 * Abstract multiplexer that collects output generated by multiple
 * threads and processes it.
 * 
 * @author Simon Heiden
 * @param <B>
 * the type of objects that are processed
 */
public abstract class AbstractMultiplexer<B> implements Multiplexer<B> {

	private MultiplexerInput<B>[] handlers;
	
	private Thread thread = null;
	private boolean shouldStop;

	private boolean isRunning = false;
	
	public AbstractMultiplexer() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexer#setHandlers(se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput[])
	 */
	@Override
	public void connectHandlers(MultiplexerInput<B>[] handlers) {
		this.handlers = handlers;
	}

	
	@Override
	public boolean isRunning() {
		return isRunning;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexer#start()
	 */
	@Override
	public void start() {
		if (!isRunning) {
//			Log.out(this, "Creating new Multiplexer thread.");
			thread = new Thread(this);
			thread.start();
			isRunning = true;
		}
	}
	
	@Override
	public void run() {
		shouldStop = false;
		boolean shouldDefinitivelyStop = false;
		if (handlers == null || handlers.length == 0) {
			throw new IllegalStateException("No handlers given to multiplexer.");
		}
		//collect available output in an infinite loop
		while (true) {
			//iterate over all input threads (all handlers)
			processPendingItems(handlers);
			
			//test if shutdown condition is fulfilled 
			if (shouldStop) {
				//test if we already checked for any pending outputs
				if (shouldDefinitivelyStop) {
					//then we may return now...
					return;
				}

				//there might still be pending output items, so better check once more...
				//but next time around, we can definitively stop!
				shouldDefinitivelyStop = true;
			}
		}
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexer#shutdown()
	 */
	@Override
	public void shutdown() {
		shouldStop = true;
		while (thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		isRunning = false;
	}
	
}
