package se.de.hu_berlin.informatik.utils.experiments.evo;

import java.util.Collection;

public interface EvoRecombinationProvider<T> {

	public static enum RecombinationTypeSelectionStrategy {
		RANDOM
	}
	
	/**
	 * Produces a recombination that can be applied to two objects of type T.
	 * Uses the given strategy to pick a recombination.
	 * @param strategy
	 * the strategy to use when choosing a recombination
	 * @return
	 * the recombination
	 */
	public EvoRecombination<T> getNextRecombinationType(RecombinationTypeSelectionStrategy strategy);
	
	/**
	 * Produces a recombination that can be applied to two objects of type T.
	 * Uses the provider's own strategy to pick a recombination.
	 * @return
	 * the recombination
	 */
	default public EvoRecombination<T> getNextRecombinationType() {
		return getNextRecombinationType(getRecombinationTypeSelectionStrategy());
	}
	
	/**
	 * Adds the given recombination to the collection of possible recombination.
	 * @param recombination
	 * the recombination to add
	 * @return
	 * true if successful; false otherwise
	 */
	public boolean addRecombinationTemplate(EvoRecombination<T> recombination);
	
	/**
	 * Adds the given recombination to the collection of possible recombination.
	 * @param recombinations
	 * the recombinations to add
	 * @return
	 * true if successful; false otherwise
	 */
	default public boolean addRecombinationTemplates(Collection<EvoRecombination<T>> recombinations) {
		boolean result = true;
		for (EvoRecombination<T> recombination : recombinations) {
			result &= addRecombinationTemplate(recombination);
		}
		return result;
	}
	
	/**
	 * Adds the given recombinations to the collection of possible recombinations.
	 * @param recombinations
	 * the recombinations to add
	 * @return
	 * true if successful; false otherwise
	 */
	default public boolean addRecombinationTemplates(@SuppressWarnings("unchecked") EvoRecombination<T>... recombinations) {
		boolean result = true;
		for (EvoRecombination<T> recombination : recombinations) {
			result &= addRecombinationTemplate(recombination);
		}
		return result;
	}
	
	/**
	 * @return
	 * the collection of recombinations in the pool
	 */
	public Collection<EvoRecombination<T>> getRecombinations();
	
	public EvoRecombinationProvider<T> setRecombinationTypeSelectionStrategy(RecombinationTypeSelectionStrategy recombinationTypeSelectionStrategy);
	
	public RecombinationTypeSelectionStrategy getRecombinationTypeSelectionStrategy();
	
}