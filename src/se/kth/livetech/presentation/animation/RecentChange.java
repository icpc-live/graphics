package se.kth.livetech.presentation.animation;

import java.util.HashMap;
import java.util.Map;

public class RecentChange<Key, Value> {
	protected Map<Key, State> states = new HashMap<Key, State>();
	public boolean set(Key key, Value value) {
		State state;
		if (states.containsKey(key)) {
			state = states.get(key);
			if (!value.equals(state.value)) {
				state.chain(value);
				return true;
			}
		}
		else {
			state = new State(value);
			states.put(key, state);
			return true; //?
		}
		return false;
	}
	public Value get(Key key) {
		State state = states.get(key);
		if (state == null)	return null;
		while (state.chain != null)
			state = state.chain;
		return state.value;
	}
	public double recentProgress(Key key) {
		// TODO: Key comparator parameter
		State state = states.get(key);
		return state.changeTime;
	}
	public boolean advance(double advance) {
		boolean r = false;
		for (State state : this.states.values()) {
			r |= state.advance(advance);
		}
		return r;
	}
	protected class State {
		Value value;
		double changeTime;
		State chain;
		public State(Value value) {
			this.value = value;
			this.changeTime = 0;
		}
		public State(State state) {
			this.value = state.value;
			this.changeTime = state.changeTime;
		}
		public void chain(Value value) {
			this.chain = new State(this);
			this.value = value;
			this.changeTime = 0;
		}
		public boolean advance(double advance) {
			this.changeTime += advance;
			if (this.changeTime < 1) {
				if (this.chain != null) {
					this.chain.advance(advance);
				}
				return true;
			}
			else {
				this.changeTime = 1;
				this.chain = null;
				return false;
			}
		}
	}
}
