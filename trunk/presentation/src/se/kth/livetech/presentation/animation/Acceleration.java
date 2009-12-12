package se.kth.livetech.presentation.animation;

public class Acceleration {
	public static double getPosition(double progress, double linear) {
		linear = Math.min(Math.max(linear, 0), 1);
		double p = 1 - progress, q = progress;
		if (p < .5 - linear / 2) {
			p = getAcceleration(linear) * p * p;
			q = 1 - p;
		}
		else if (.5 + linear / 2 < p) {
			q = getAcceleration(linear) * q * q;
			p = 1 - q;
		}
		else {
			double k = 2 / (1 + linear);
			p = .5 + k * (p - .5);
			q = 1 - p;
		}
		return q;
	}

	public static double getAcceleration(double linear) {
		linear = Math.min(Math.max(linear, 0), 1);
		return 2 / Math.max(1 - linear * linear, 1e-3);
	}
}
