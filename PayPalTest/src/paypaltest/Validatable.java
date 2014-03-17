package paypaltest;

public interface Validatable<T> {
	boolean isValid(T value);
}

