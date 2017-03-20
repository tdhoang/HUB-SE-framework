/**
 * 
 */
package se.de.hu_berlin.informatik.utils.files.processors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;
import se.de.hu_berlin.informatik.utils.processors.sockets.ConsumingProcessorSocketGenerator;
import se.de.hu_berlin.informatik.utils.threaded.ThreadedFileWalker;
import se.de.hu_berlin.informatik.utils.threaded.ThreadedFileWalker.Builder;

/**
 * Starts a threaded file walker with a provided callable class on a submitted input path.
 * 
 * @author Simon Heiden
 * 
 * @see ThreadedFileWalker
 */
public class ThreadedFileWalkerProcessor extends AbstractProcessor<Path,Boolean> {

	final private String pattern;
	final private int threadCount;
	
	private boolean searchDirectories = false;
	private boolean searchFiles = false;
	private boolean includeRootDir = false;
	
	private boolean skipAfterFind = false;
	private ConsumingProcessorSocketGenerator<Path> callableFactory;
	
	/**
	 * Creates a new {@link ThreadedFileWalkerProcessor} object with the given parameters. 
	 * @param pattern
	 * the pattern that describes the files that should be processed by this file walker
	 * @param threadCount
	 * the number of threads that shall be run in parallel
	 */
	public ThreadedFileWalkerProcessor(String pattern, int threadCount) {
		super();
		this.pattern = pattern;
		this.threadCount = threadCount;
	}
	
	/**
	 * Sets the factory.
	 * @param callableFactory
	 * a factory that provides instances of callable classes 
	 * @return
	 * this
	 */
	public ThreadedFileWalkerProcessor call(ConsumingProcessorSocketGenerator<Path> callableFactory) {
		this.callableFactory = callableFactory;
		return this;
	}
	
	/**
	 * Enables searching for files.
	 * @return
	 * this
	 */
	public ThreadedFileWalkerProcessor searchForFiles() {
		this.searchFiles = true;
		return this;
	}
	
	/**
	 * Enables searching for directories.
	 * @return
	 * this
	 */
	public ThreadedFileWalkerProcessor searchForDirectories() {
		this.searchDirectories = true;
		return this;
	}
	
	/**
	 * Skips recursion to subtree after found items.
	 * @return
	 * this
	 */
	public ThreadedFileWalkerProcessor skipSubTreeAfterMatch() {
		this.skipAfterFind = true;
		return this;
	}
	
	/**
	 * Includes the root directory in the search.
	 * @return
	 * this
	 */
	public ThreadedFileWalkerProcessor includeRootDir() {
		this.includeRootDir = false;
		return this;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public Boolean processItem(Path input) {
		//declare a threaded FileWalker
		ThreadedFileWalker.Builder builder = new Builder(pattern, threadCount);
		if (includeRootDir) {
			builder.includeRootDir();
		}
		if (searchDirectories) {
			builder.searchForDirectories();
		}
		if (searchFiles) {
			builder.searchForFiles();
		}
		if (skipAfterFind) {
			builder.skipSubTreeAfterMatch();
		}
		builder.call(callableFactory);
		
		ThreadedFileWalker walker = builder.build();
		delegateTrackingTo(walker);
		
		//traverse the file tree
		try {
			Files.walkFileTree(input, Collections.emptySet(), Integer.MAX_VALUE, walker);
		} catch (IOException e) {
			Log.abort(this, e, "IOException thrown.");
		}
		
		//we are done! Shutdown is necessary!
		walker.shutdown();
		walker.delegateTrackingTo(this);
		return true;
	}

}