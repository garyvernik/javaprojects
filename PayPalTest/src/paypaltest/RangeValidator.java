package paypaltest;

public class RangeValidator<T extends Comparable<T>> implements Validatable<T> {

	private final Range<T> range;
	
	public RangeValidator(Range<T> range) {
		this.range = range;
	}
	
	@Override
	public boolean isValid(T amount) {
		return ( amount.compareTo(range.getMin()) >= 0 && amount.compareTo(range.getMax()) <= 0);
	}
	
}
