package se.kth.livetech.presentation.animation;

import java.util.HashMap;
import java.util.Map;

public class AnimationStack<Key, Position extends Comparable<Position>> {
	protected Map<Key, State> states = new HashMap<Key, State>();

	public void setPosition(Key key, Position position) {
		State state;
		if (states.containsKey(key)) {
			state = states.get(key);
			if (position.compareTo(state.position) != 0) {
				state.chain(position);
			}
		}
		else {
			state = new State(position);
			states.put(key, state);
		}
	}

	public boolean isUp(Key key) {
		State state = states.get(key);
		return state.up;
	}

	public void interpolate(Key key, Interpolated<Position> interpolator) {
		State state = states.get(key);
		if (state != null) {
			double fraction = 1;
			while (state.chain != null) {
				double progress = state.chainTime;
				//boolean up = state.position.compareTo(state.chain.position) < 0;
				progress = Acceleration.getPosition(progress, state.up ? 0 : .5);

				fraction *= 1 - progress;
				interpolator.interpolateTo(state.chain.position, fraction);
				state = state.chain;
			}
		}
	}

	public boolean advance(double advance) {
		boolean r = false;
		for (State state : this.states.values()) {
			r |= state.advance(advance);
		}
		return r;
	}

	protected class State {
		public State(Position position) {
			this.chain = null;
			this.chainTime = 1;
			this.position = position;
			this.up = false;
		}
		public State(State state) {
			this.chain = state.chain;
			this.chainTime = state.chainTime;
			this.position = state.position;
			this.up = state.up;
		}
		State chain;
		double chainTime;
		Position position;
		boolean up;
		public void chain(Position newPosition) {
			chain = new State(this);
			chainTime = 0;
			this.position = newPosition;
			up = this.position.compareTo(chain.position) < 0;
		}
		public boolean advance(double advance) {
			this.chainTime += advance;
			if (this.chainTime < 1) {
				if (this.chain != null) {
					this.chain.advance(advance);
				}
				return true;
			}
			else {
				this.chain = null;
				this.chainTime = 1;
				return false;
			}
		}
	}
}

