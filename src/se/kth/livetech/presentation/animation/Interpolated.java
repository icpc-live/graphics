package se.kth.livetech.presentation.animation;

import java.awt.geom.Rectangle2D;

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
	
	public static class Rectangle<Rectangle2DType extends Rectangle2D> implements Interpolated<Rectangle2DType> {
		private Rectangle2DType value;
		public Rectangle(Rectangle2DType value) {
			this.value = value;
		}
		
		@Override
		public void interpolateTo(Rectangle2DType to, double fraction) {
			double p = Math.min(Math.max(0, 1 - fraction), 1), q = 1 - p;
			double x = p * value.getX() + q * to.getX();
			double y = p * value.getY() + q * to.getY();
			double w = p * value.getWidth() + q * to.getWidth();
			double h = p * value.getHeight() + q * to.getHeight();
			value.setRect(x, y, w, h);
		}
		
		public Rectangle2DType getValue() {
			return value;
		}
	}
}
