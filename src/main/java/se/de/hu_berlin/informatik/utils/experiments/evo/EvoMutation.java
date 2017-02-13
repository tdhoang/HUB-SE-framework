package se.de.hu_berlin.informatik.utils.experiments.evo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface EvoMutation<T,L> {

	/**
	 * Mutates the target object based on the given location.
	 * @param target
	 * the target object to mutate
	 * @param location
	 * the location at which to mutate the target object
	 * @return
	 * the mutated object
	 */
	public T applyTo(T target, L location);
	
	/**
	 * Returns an id for the next mutation that will be applied by calling 
	 * {@link #applyTo(Object, Object)} with the given target and location.
	 * Any random decisions within the mutation procedure should be
	 * made by this point to be able to compute a unique id that reflects
	 * these random decisions. This id is used to track the history of the item.
	 * @param target
	 * the target object
	 * @param location
	 * the location
	 * @return
	 * the id
	 */
	public EvoID getIDofNextMutation(T target, L location);
	
	public static class MutationHistory implements List<EvoID> {

		private final List<EvoID> history;
		private Integer hashCode = 17;
		
		public MutationHistory() {
			history = new ArrayList<>();
		}
		
		public MutationHistory(MutationHistory c) {
			history = new ArrayList<>(c);
			hashCode = c.hashCode();
		}
		
		public MutationHistory(int capacity) {
			history = new ArrayList<>(capacity);
		}
		
		private void updateHashCode(Collection<? extends EvoID> c) {
			for (EvoID e : c) {
				updateHashCode(e);
			}
		}
		
		private void updateHashCode(EvoID e) {
			hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MutationHistory) {
				MutationHistory o = (MutationHistory) obj;
				//must have the same number of elements
				if (this.size() != o.size()) {
					return false;
				}
				Iterator<EvoID> iterator1 = this.iterator();
				Iterator<EvoID> iterator2 = o.iterator();
				while(iterator1.hasNext()) {
					if (!iterator1.next().equals(iterator2.next())) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}

		@Override
		public int size() {
			return history.size();
		}

		@Override
		public boolean isEmpty() {
			return history.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return history.contains(o);
		}

		@Override
		public Iterator<EvoID> iterator() {
			return history.iterator();
		}

		@Override
		public Object[] toArray() {
			return history.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return history.toArray(a);
		}

		@Override
		public boolean add(EvoID e) {
			updateHashCode(e);
			return history.add(e);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return history.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends EvoID> c) {
			updateHashCode(c);
			return history.addAll(c);
		}

		@Override
		public EvoID get(int index) {
			return history.get(index);
		}
		@Override
		public int indexOf(Object o) {
			return history.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			return history.lastIndexOf(o);
		}

		@Override
		public ListIterator<EvoID> listIterator() {
			return history.listIterator();
		}

		@Override
		public ListIterator<EvoID> listIterator(int index) {
			return history.listIterator(index);
		}
		
		@Override
		public boolean remove(Object o) throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean addAll(int index, Collection<? extends EvoID> c) throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public EvoID set(int index, EvoID element) throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(int index, EvoID element) throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public EvoID remove(int index) throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<EvoID> subList(int fromIndex, int toIndex) throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}
		
	}
	
}
