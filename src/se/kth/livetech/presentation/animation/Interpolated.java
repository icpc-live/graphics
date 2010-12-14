package se.kth.livetech.presentation.animation;

public interface Interpolated<T> {
	public void interpolateTo(T to, double fraction);

	public static class Double implements Interpolated<Integer> {
		private double value;
		public Double(double value) {
			this.value = value;
		}
		@Override
		public void interpolateTo(Integer to, double fraction) {
			double p = Math.min(Math.max(0, 1 - fraction), 1), q = 1 - p;
			value = p * value + q * to;
		}
		public double getValue() {
			return value;
		}
	}
}
