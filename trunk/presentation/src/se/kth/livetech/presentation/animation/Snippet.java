package se.kth.livetech.presentation.animation;

import java.util.Map;

class Rect { }
class CompositePositioner extends Positioner {
	public CompositePositioner(Positioner newP, Positioner oldP) { }
	public void setLinear(double l) { }
	public void setProgress(double p) { }
	public double getProgress() { return 0; }
}
class Positioner {
	//public Rect getRect(int row) { return null; }
	public Rect getRowRect(int row) { return null; }
}
class ITeam {
	public String getName() { return null; }
}

public class Snippet {
	Positioner screenPositioner;
	public Positioner createTeamPositioner(Rect rect) { return null; }
	public Positioner getTeamPositioner(String teamKey, int row) {
		TeamState state;
		if (states.containsKey(teamKey)) {
			state = states.get(teamKey);
			if (state.row != row) {
				//Positioner positioner = this.createTeamPositioner(this.mainPositioner.getRect(row));
				Positioner positioner = this.createTeamPositioner(this.screenPositioner.getRowRect(row));
				state.chain(row, positioner);
			}
		}
		else {
			//Positioner positioner = this.createTeamPositioner(this.mainPositioner.getRect(row));
			Positioner positioner = this.createTeamPositioner(this.screenPositioner.getRowRect(row));
			state = new TeamState(row, positioner);
			states.put(teamKey, state);
		}
		//state.advance(0.03); //TODO: this MUST be timed!!!!1!
		return state.currentPositioner;		
	}

	public Positioner getTeamPositioner(ITeam team, int row) {
		return getTeamPositioner(team == null ? "" : team.getName(), row);
	}

	public boolean incrementTime(float dt) {
		if (this.states != null) {
			for (TeamState state : this.states.values()) {
				state.advance(dt);
			}
		}
		return true;
	}

	protected Map<String, TeamState> states;

	protected class TeamState {
		public TeamState(int row, Positioner positioner) {
			this.row = row;
			this.currentPositioner = positioner;
		}
		int row;
		Positioner currentPositioner;

		TeamState chain;
		public void chain(int row, Positioner newPositioner) {
			chain = new TeamState(this.row, this.currentPositioner);
			boolean up = row < this.row;
			this.row = row;
			this.newPositioner = newPositioner;
			CompositePositioner compositePositioner = new CompositePositioner(this.newPositioner, this.currentPositioner);
			if (up)
				compositePositioner.setLinear(0);
			else
				compositePositioner.setLinear(0.5);
			this.currentPositioner = compositePositioner;
		}
		double chainTime;
		Positioner newPositioner;
		public void advance(double advance) {
			if (this.chain != null) {
				this.chain.advance(advance);
				if (this.currentPositioner instanceof CompositePositioner) {
					CompositePositioner compositePositioner = (CompositePositioner) this.currentPositioner;
					double progress = compositePositioner.getProgress();
					if (progress + advance < 1)
						compositePositioner.setProgress(progress + advance);
					else {
						compositePositioner.setProgress(1);
						this.currentPositioner = this.newPositioner;
						this.chain = null; // TODO: retain chain to advance it if animations have different durations
					}
				}
			}
		}
	}
}

