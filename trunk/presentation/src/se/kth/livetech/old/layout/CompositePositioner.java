package se.kth.livetech.old.layout;

import java.awt.geom.Rectangle2D;

public class CompositePositioner implements Positioner {
	Positioner newPositioner, oldPositioner;
	double progress = 0, linear = 1;

	public CompositePositioner(Positioner newPositioner, Positioner oldPositioner) {
		this.newPositioner = newPositioner;
		this.oldPositioner = oldPositioner;
	}

	private Rectangle2D compositePosition(Rectangle2D newPosition, Rectangle2D oldPosition) {
		double p = 1 - progress, q = progress;
		if (p < .5 - linear / 2) {
			p = getAcceleration() * p * p;
			q = 1 - p;
		}
		else if (.5 + linear / 2 < p) {
			q = getAcceleration() * q * q;
			p = 1 - q;
		}
		else {
			/*
			double s = .5 - linear / 2;
			double m = (1 - linear) / (1 + linear);
			double k = (1 - 2 * m) / linear;
			p = m + (p - s) * k;
			q = 1 - p;
			*/
			double k = 2 / (1 + linear);
			p = .5 + k * (p - .5);
			q = 1 - p;
		}
		return new Rectangle2D.Double(
				p * oldPosition.getX() + q * newPosition.getX(),
				p * oldPosition.getY() + q * newPosition.getY(),
				p * oldPosition.getWidth() + q * newPosition.getWidth(),
				p * oldPosition.getHeight() + q * newPosition.getHeight());
	}

	public Rectangle2D getRect(int row) {
		Rectangle2D newPosition = newPositioner.getRect(row);
		Rectangle2D oldPosition = oldPositioner.getRect(row);
		return compositePosition(newPosition, oldPosition);
	}

	public Rectangle2D getRect(int row, Part part) {
		Rectangle2D newPosition = newPositioner.getRect(row, part);
		Rectangle2D oldPosition = oldPositioner.getRect(row, part);
		return compositePosition(newPosition, oldPosition);
	}

	public Rectangle2D getRect(int row, Part part, int column) {
		Rectangle2D newPosition = newPositioner.getRect(row, part, column);
		Rectangle2D oldPosition = oldPositioner.getRect(row, part, column);
		return compositePosition(newPosition, oldPosition);
	}

	public double getAcceleration() {
		//double x = Math.max(.5 - linear / 2, 1e-3);
		//return .5 / x / (linear + x);
		return 2 / Math.max(1 - linear * linear, 1e-3);
	}

	public double getLinear() {
		return linear;
	}

	public void setLinear(double linear) {
		this.linear = Math.min(Math.max(linear, 0), 1);
	}

	public Positioner getNewPositioner() {
		return newPositioner;
	}

	public void setNewPositioner(Positioner newPositioner) {
		this.newPositioner = newPositioner;
	}

	public Positioner getOldPositioner() {
		return oldPositioner;
	}

	public void setOldPositioner(Positioner oldPositioner) {
		this.oldPositioner = oldPositioner;
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

}
