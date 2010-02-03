package se.kth.livetech.control;

import java.util.Timer;
import java.util.TimerTask;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Run;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.replay.ContestReplayer;
import se.kth.livetech.presentation.layout.ScoreboardPresentation;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

public class ContestReplayControl implements PropertyListener, ContestUpdateListener {
	
	private ContestReplayer replayer;
	private ScoreboardPresentation sp;
	private IProperty propertyBase, propertyState;
	private int bronzeMedals, silverMedals, goldMedals, medals;
	private int resolveRank = -1;
	private int stepCounter = 0;
	private boolean showingPresentation = false;
	private boolean hasFinishedReplayer = false;
	private boolean isStateResolver = false;
	private Timer timer;
	
	public ContestReplayControl(ContestReplayer replayer, IProperty propertyBase, ScoreboardPresentation sp) {
		this.replayer = replayer;
		this.sp = sp;
		this.propertyBase = propertyBase;
		this.propertyState = propertyBase.get("state");
		propertyBase.addPropertyListener(this);
	}
	
	@Override
	public void propertyChanged(IProperty changed) {		
		String state = propertyState.getValue();
		bronzeMedals = propertyBase.get("bronzeMedals").getIntValue();
		silverMedals = propertyBase.get("silverMedals").getIntValue();
		goldMedals = propertyBase.get("goldMedals").getIntValue();
		medals = bronzeMedals + silverMedals + goldMedals;
		final int resolveProblemDelay = propertyBase.get("resolveProblemDelay").getIntValue();
		final int resolveTeamDelay = propertyBase.get("resolveTeamDelay").getIntValue();

		isStateResolver = false;
		if(state.equals("pause"))
			replayer.setState(ContestReplayer.State.PAUSED);
		else if(state.equals("live"))
			replayer.setState(ContestReplayer.State.LIVE);
		else if(state.equals("replay")) {
			replayer.setIntervals(propertyBase.get("replayDelay").getIntValue(), 0);
			replayer.setState(ContestReplayer.State.UNTIL_INTERVAL);
		}
		else if(state.equals("resolver")) {
			// Assuming no new runs will be added.
			// Make sure it has processed all old runs.
			isStateResolver = true;
			if(!hasFinishedReplayer) {
				replayer.setState(ContestReplayer.State.UNTIL_INTERVAL);
				while(replayer.processPendingState());
				while(replayer.processEarliestRun());
				hasFinishedReplayer = true;
			}
			replayer.setState(ContestReplayer.State.PAUSED);
			initResolveRank();
			class InnerTask extends TimerTask {
				public void run() {
					if(resolveRank<=medals || !isStateResolver) {
						timer.cancel();
						timer = null;
						return;
					}
					System.out.println("ResolveRank " + resolveRank);
					Contest contest = replayer.getContest();
					int runId = replayer.getHighestRankedRun();
					if(runId<0) {
						timer.cancel();
						timer = null;
						return;
					}
					Run run = contest.getRun(runId);
					Team team = contest.getRankedTeam(resolveRank);
					if(run.getTeam() == team.getId()) {
						// Current row has more runs.
						timer.schedule(new InnerTask(), resolveProblemDelay);
						replayer.processHighestRank();
					} else {
						timer.schedule(new InnerTask(), resolveTeamDelay);
						if(sp!=null)
							sp.highlightRank(--resolveRank);
					}
				}
			}
			if(timer == null) {
				timer = new Timer();
				timer.schedule(new InnerTask(), 0);
			}
		} else {
			System.err.println("Property changed to unknown state: "+state);
			replayer.setState(ContestReplayer.State.PAUSED);
		}
		
		int stepUntil = propertyBase.get("presentationStep").getIntValue();
		while(stepCounter < stepUntil) {
			initResolveRank();
			step(0);
		}
	}
	
	// TODO Team delay if team changes rank.
	// TODO Replay entire problem
	
	private void initResolveRank() {
		if(resolveRank==-1) {
			Contest contest = replayer.getContest();
			resolveRank = contest.getTeams().size();
			if(sp!=null)
				sp.highlightRank(resolveRank);
		}
	}

	private void step(int rankLimit) {
		Contest contest = replayer.getContest();
		++stepCounter;
		if(resolveRank<=0) return;
		int runId = replayer.getHighestRankedRun();
		Run run = null;
		if(runId>=0) run = contest.getRun(runId);
		Team team = contest.getRankedTeam(resolveRank);
		if(run !=null && run.getTeam() == team.getId()) {
			showingPresentation = false;
			// Current row has more runs.
			System.out.println("Next run on row "+resolveRank + ", run id "+run.getId());
			replayer.processHighestRank();
		} else if(resolveRank>medals || showingPresentation) {
			showingPresentation = false;
			// Highlight next row
			--resolveRank;
			System.out.println("Highlighting row " + resolveRank);
			if(sp!=null)
				sp.highlightRank(resolveRank);
		} else if(resolveRank>silverMedals+goldMedals) {
			showingPresentation = true;
			System.out.println("Bronze medal to team " + team.getId() + " on row " + resolveRank);
			if(sp!=null)
				sp.showBronzeMedal(team.getId());
		} else if(resolveRank>goldMedals) {
			showingPresentation = true;
			System.out.println("Silver medal to team " + team.getId() + " on row " + resolveRank);
			if(sp!=null)
				sp.showSilverMedal(team.getId());
		} else {
			showingPresentation = true;
			System.out.println("Gold medal to team " + team.getId() + " on row " + resolveRank);
			if(sp!=null)
				sp.showGoldMedal(team.getId());
		}	
	}

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		// TODO Auto-generated method stub
		//System.out.println(e);
	}
	
/* TODO
 * Why does fakecontest crash?
 * 	Only after a while
 * 	Reset after that three teams have solved all problems.
 * Step runs or problems?
 *	If runs, should they still be ordered by problem or by time?
 * What if we want to start manually before last 13 teams?
 *  New resolver here!
 */
		
/*
	State - Description
	0 - played until 4:00:00
	
	Update state counter when:
	(1) Pending run/problem -> Judged.
	(2) Highlighting next team.
	(3) Presentation.
	
	Description:
	Do 'things' until the number of 'things' we have done is equal to the property value.

 */
}
