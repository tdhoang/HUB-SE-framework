package se.de.hu_berlin.informatik.utils.experiments.evo;

import se.de.hu_berlin.informatik.utils.experiments.evo.EvoMutation.MutationHistory;

public interface EvoItem<T,F extends Comparable<F>, K extends Comparable<K>> extends Comparable<F> {

	/**
	 * @return
	 * the fitness of the item
	 */
	public F getFitness();
	
	/**
	 * @param fitness
	 * to set
	 */
	public void setFitness(F fitness);
	
	/**
	 * @return
	 * the item
	 */
	public T getItem();
	
	/**
	 * @param item
	 * to set
	 */
	public void setItem(T item);
	
	/**
	 * Cleans up any traces of this item in case it is
	 * not part of the population any more. This may clean up
	 * directory structures or nullify object pointers, etc.
	 * @return
	 * true if successful; false otherwise
	 */
	public boolean cleanUp();
	
	public History<T, K> getHistory();
	
	default public void addMutationIdToHistory(EvoID<K> id) {
		this.getHistory().addMutationId(id);
	}
	
	@Override
	default public int compareTo(F o) {
		if (o == null) {
			throw new NullPointerException();
		}
		return this.getFitness().compareTo(o);
	}
	
	public static class History<T, K extends Comparable<K>> {
		
		private MutationHistory<K> mutationHistory;
		private T ancestor = null;
		
		private EvoID<K> recombinationId = null;
		
		private History<T, K> parentHistory1 = null;
		private History<T, K> parentHistory2 = null;
		
		private Integer hashCode = 17;
		
		/**
		 * Creates a new History object.
		 * @param origin
		 * a reference to the original item
		 */
		public History(T origin) {
			this.mutationHistory = new MutationHistory<>();
			this.ancestor = origin;
			updateStaticHashCodePart();
		}
		
		/**
		 * Copy constructor.
		 * @param c
		 * the history to copy
		 */
		public History(History<T, K> c) {
			this.mutationHistory = new MutationHistory<>(c.getMutationHistory());
			this.ancestor = c.getAncestor();
			this.recombinationId = c.getRecombinationId();
			this.parentHistory1 = c.getParentHistory1() == null ? null : new History<>(c.getParentHistory1());
			this.parentHistory2 = c.getParentHistory2() == null ? null : new History<>(c.getParentHistory2());
			updateStaticHashCodePart();
		}
		
		/**
		 * Copy constructor that creates a new History for a child object and then 
		 * adds the given mutation id afterwards to the new History object.
		 * @param c
		 * the history to copy
		 * @param mutationId
		 * the mutation id to add
		 */
		public History(History<T, K> c, EvoID<K> mutationId) {
			this(c);
			this.addMutationId(mutationId);
		}
		
		/**
		 * Copy constructor that creates a new History for a child object and then 
		 * adds the given recombination afterwards to the new History object.
		 * @param parentHistory1
		 * the first parent's history
		 * @param parentHistory2
		 * the second parent's history
		 * @param recombinationId
		 * the recombination id
		 */
		public History(History<T, K> parentHistory1, History<T, K> parentHistory2, EvoID<K> recombinationId) {
			this.mutationHistory = new MutationHistory<>();
			this.ancestor = null;
			this.recombinationId = recombinationId;
			this.parentHistory1 = parentHistory1 == null ? null : new History<>(parentHistory1);
			this.parentHistory2 = parentHistory2 == null ? null : new History<>(parentHistory2);
			updateStaticHashCodePart();
		}
		
		private void updateStaticHashCodePart() {
			updateHashCode(this.ancestor);
			updateHashCode(this.recombinationId);
			updateHashCode(this.parentHistory1);
			updateHashCode(this.parentHistory2);
		}

		public boolean addMutationId(EvoID<K> id) {
			return mutationHistory.add(id);
		}
		
		public EvoID<K> getRecombinationId() {
			return recombinationId;
		}
		
		public MutationHistory<K> getMutationHistory() {
			return mutationHistory;
		}
		
		public boolean hasParents() {
			return parentHistory1 != null && parentHistory2 != null;
		}
		
		public T getAncestor() {
			return ancestor;
		}
		
		public History<T, K> getParentHistory1() {
			return parentHistory1;
		}
		
		public History<T, K> getParentHistory2() {
			return parentHistory2;
		}
		
		private void updateHashCode(Object h) {
			hashCode = 31 * hashCode + (h == null ? 0 : h.hashCode());
		}
		
		@Override
		public int hashCode() {
			return 31 * hashCode + mutationHistory.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof History) {
				History<?,?> o = (History<?,?>) obj;
				//must have the same ancestor (or both null)
				if (this.getAncestor() != o.getAncestor()) {
					return false;
				}
				//must have the same recombination IDs (or both null)
				if (this.getRecombinationId() != o.getRecombinationId()) {
					return false;
				}
				//mutation histories must be equal
				if (!this.getMutationHistory().equals(o.getMutationHistory())) {
					return false;
				}
				//both must have parents (histories not null) or no parents (all null)
				if (!this.hasParents() && !o.hasParents()) {
					return true;
				}
				if (this.hasParents() && !o.hasParents()) {
					return false;
				}
				if (!this.hasParents() && o.hasParents()) {
					return false;
				}
				//both parent histories have to be equal
				if (!this.getParentHistory1().equals(o.getParentHistory1())) {
					return false;
				}
				if (!this.getParentHistory2().equals(o.getParentHistory2())) {
					return false;
				}
				return true;
			} else {
				return false;
			}
		}

		public History<T, K> copy() {
			return new History<>(this);
		}
		
		
		
	}
	
}
