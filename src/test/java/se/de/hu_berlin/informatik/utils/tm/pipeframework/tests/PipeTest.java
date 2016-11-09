/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipeframework.tests;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.PipeLinker;

/**
 * @author SimHigh
 *
 */
public class PipeTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testShutdown() throws Exception {
		final AtomicInteger processedElements = new AtomicInteger(0);
		
		AbstractPipe<Integer, Integer> pipe = new AbstractPipe<Integer, Integer>(true) {
			@Override
			public Integer processItem(Integer item) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// nothing
				}
				processedElements.incrementAndGet();
				return item;
			}
		};
		
		AbstractPipe<Integer, Integer> pipe2 = new AbstractPipe<Integer, Integer>(true) {
			@Override
			public Integer processItem(Integer item) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// nothing
				}
				processedElements.incrementAndGet();
				return item;
			}
		};
		
		pipe.linkTo(pipe2);
		
		for (int i = 0; i < 20; ++i) {
			pipe.submit(i);
		}
		
		pipe.shutdown();
		
		assertEquals(40, processedElements.get());
	}
	
	@Test
	public void testManyInputs() throws Exception {
		final AtomicInteger processedElements = new AtomicInteger(0);
		
		AbstractPipe<Integer, Integer> pipe = new AbstractPipe<Integer, Integer>(true) {
			@Override
			public Integer processItem(Integer item) {
				processedElements.incrementAndGet();
				return item;
			}
		};
		
		AbstractPipe<Integer, Integer> pipe2 = new AbstractPipe<Integer, Integer>(true) {
			@Override
			public Integer processItem(Integer item) {
				processedElements.incrementAndGet();
				return item;
			}
		};
		
		pipe.linkTo(pipe2);
		
		for (int i = 0; i < 50000; ++i) {
			pipe.submit(i);
		}
		
		pipe.shutdown();
		
		assertEquals(100000, processedElements.get());
	}
	
	@Test
	public void testPipeLinker() throws Exception {
		final AtomicInteger processedElements = new AtomicInteger(0);
		PipeLinker linker = new PipeLinker();
		
		linker.append(
				new AbstractPipe<Integer, Integer>(true) {
					@Override
					public Integer processItem(Integer item) {
						processedElements.incrementAndGet();
						return item;
					}
				});
		
		linker.append(
				new AbstractPipe<Integer, Integer>(true) {
					@Override
					public Integer processItem(Integer item) {
						processedElements.incrementAndGet();
						return item;
					}
				},
				new AbstractPipe<Integer, Integer>(true) {
					@Override
					public Integer processItem(Integer item) {
						processedElements.incrementAndGet();
						return item;
					}
				});
		
		for (int i = 0; i < 5000; ++i) {
			linker.submit(i);
		}
		
		linker.shutdown();
		
		assertEquals(15000, processedElements.get());
	}
	
}