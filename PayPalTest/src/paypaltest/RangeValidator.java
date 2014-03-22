package paypaltest;

/**
 * Validates if the value is within the given Range
 * @param <T> value type
 */
public class RangeValidator<T extends Comparable<T>> implements Validatable<T> {

	private final Range<T> range;
	
	public RangeValidator(final Range<T> range) {
		this.range = range;
	}
	
	@Override
	public boolean isValid(final T amount) {
		return amount.compareTo(range.getMin()) >= 0 && amount.compareTo(range.getMax()) <= 0;
	}
	
}
