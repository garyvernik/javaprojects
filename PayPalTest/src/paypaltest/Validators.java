package paypaltest;

public class Validators {
	
	public static final Range<Double> BORROW_AMOUNT_RANGE = new Range<Double>(0.01d, 1000000000000d);
	public static final Range<Double> APR_RANGE = new Range<Double>(0.000001d, 100d);
	public static final Range<Integer> TERM_RANGE = new Range<Integer>(1, 1000000);
	
	private static final Validatable<Double> BORROW_AMOUT_VALIDATOR = new RangeValidator<Double>(BORROW_AMOUNT_RANGE); 
	private static final Validatable<Double> APR_VALIDATOR = new RangeValidator<Double>(APR_RANGE);
	private static final Validatable<Integer> TERM_RANGE_VALIDATOR = new RangeValidator<Integer>(TERM_RANGE);
	private static final Validatable<Double> TERM_VALIDATOR = new TermValidator();

	public static Validatable<Double> getBorrowAmountValidator() { return BORROW_AMOUT_VALIDATOR; }
	public static Validatable<Double> getAprValidator() { return APR_VALIDATOR; }
	public static Validatable<Double> getTermValidator() { return TERM_VALIDATOR; }

	private Validators() {
		// no-op
	}
	
	private static class TermValidator implements Validatable<Double> {

		@Override
		public boolean isValid(final Double value) {
			return value == value.intValue() && TERM_RANGE_VALIDATOR.isValid(value.intValue()); 
		}
		
	}
	
}
