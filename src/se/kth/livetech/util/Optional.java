package se.kth.livetech.util;

public class Optional<T> {
	private T value;
	public Optional() {
	}
	public Optional(T value) {
		set(value);
	}
	public boolean is() {
		return value != null;
	}
	public T get() {
		return value;
	}
	public void set(T value) {
		this.value = value;
	}
	public void clear() {
		this.value = null;
	}
	
	public static <T> Optional<T> no() {
		return new Optional<T>();
	}
	public static <T> Optional<T> v(T value) {
		return new Optional<T>(value);
	}
}
