package se.kth.livetech.control;

import java.util.Timer;
import java.util.TimerTask;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Run;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.replay.ContestReplayer;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

public class ContestReplayControl implements PropertyListener, ContestUpdateListener {
	
	private ContestReplayer replayer;
	private IProperty propertyReplay, propertyBase, propertyScore;
	private int bronzeMedals, silverMedals, goldMedals, blankMedals, medals;
	private int resolveRow = -1;
	private int stepCounter = 0;
	private boolean showingPresentation = false;
	private boolean hasFinishedReplayer = false;
	private String state = "";
	private int replayDelay = 0;
	private int resolveProblemDelay = 0;
	private int resolveTeamDelay = 0;
	private Timer timer;
	
	public ContestReplayControl(ContestReplayer replayer, IProperty propertyBase) {
		this.replayer = replayer;
		this.propertyReplay = propertyBase.get("replay");
		this.propertyScore = propertyBase.get("score");
		this.propertyBase = propertyBase;
		propertyBase.addPropertyListener(this);
	}
	
	@Override
	public void propertyChanged(IProperty changed) {		
		state = propertyReplay.get("state").getValue();
		bronzeMedals = propertyReplay.get("bronzeMedals").getIntValue();
		silverMedals = propertyReplay.get("silverMedals").getIntValue();
		goldMedals = propertyReplay.get("goldMedals").getIntValue();
		blankMedals = propertyReplay.get("blankMedals").getIntValue();
		medals = bronzeMedals + silverMedals + goldMedals + blankMedals;
		replayDelay = propertyReplay.get("replayDelay").getIntValue();
		resolveProblemDelay = propertyReplay.get("resolveProblemDelay").getIntValue();
		resolveTeamDelay = propertyReplay.get("resolveTeamDelay").getIntValue();
		
		int freezeTime = propertyReplay.get("freezeTime").getIntValue();
		if(freezeTime>0)
			replayer.setFreezeTime(freezeTime);
		int untilTime = propertyReplay.get("untilTime").getIntValue();
		if(untilTime>0)
			replayer.setUntilTime(untilTime);

		if(state.equals("pause"))
			replayer.setState(ContestReplayer.State.PAUSED);
		else if(state.equals("live"))
			replayer.setState(ContestReplayer.State.LIVE);
		else if(state.equals("replay")) {
			replayer.setIntervals(replayDelay, 0);
			replayer.setState(ContestReplayer.State.UNTIL_INTERVAL);
		}
		else if(state.equals("resolver")) {
			// Assuming no new runs will be added from now and onwards.
			
			// Init resolver and ensure all earlier runs has been processed.
			if(!hasFinishedReplayer) {
				replayer.setState(ContestReplayer.State.UNTIL_INTERVAL);
				while(replayer.processPendingState());
				while(replayer.processEarliestRun());
				hasFinishedReplayer = true;
			}
			replayer.setState(ContestReplayer.State.PAUSED);
			initResolveRank();
			// Ensure task is running.
			if(timer == null) {
				timer = new Timer();
				timer.schedule(new ResolverTask(), 0);
			}
		}
		
		int stepUntil = propertyReplay.get("presentationStep").getIntValue();
		while(stepCounter < stepUntil) {
			initResolveRank();
			step(true);
		}
	}
	
	private void highlightNext() {
		propertyScore.set("highlightRow", String.valueOf(resolveRow));
		int problemId = -1;
		int runId = replayer.getHighestRankedRun();
		if(runId>=0 && resolveRow>0) {
			Run run = replayer.getContest().getRun(runId);
			Team team = replayer.getContest().getRankedTeam(resolveRow);
			if(run!=null && team!=null && team.getId()==run.getTeam()) {
				problemId = run.getProblem();
			}
		}
		propertyScore.set("highlightProblem", String.valueOf(problemId));
	}
	
	private void showWinnerPresentation(int teamId, String award) {
		IProperty awardProperty = propertyBase.get("awards");
		awardProperty.get("team").setIntValue(teamId);
		awardProperty.set("award", award);
		propertyBase.set("mode", "award");
	}
	
	private void showScoreboard() {
		propertyBase.set("mode", "score");
	}
	
	private void showBronzeMedal(int row) {
		propertyScore.get("color").set(String.valueOf(row), "bronze");
	}
	
	private void showSilverMedal(int row) {
		propertyScore.get("color").set(String.valueOf(row), "silver");
	}
	
	private void showGoldMedal(int row) {
		propertyScore.get("color").set(String.valueOf(row), "gold");
	}

	private void initResolveRank() {
		if(resolveRow==-1) {
			Contest contest = replayer.getContest();
			resolveRow = contest.getTeams().size();
			if(resolveRow==0) {
				System.err.println("No teams in contest!");
				resolveRow = -1;
				return;
			}
			highlightNext();
		}
	}
	
	private class ResolverTask extends TimerTask {
		public void run() {
			if(resolveRow<=medals || !state.equals("resolver")) {
				timer.cancel();
				timer = null;
				return;
			}
			// System.out.println("ResolveRank " + resolveRow);
			int stepValue = step(false);
			switch(stepValue) {
			case -1: // No processed run.
				timer.cancel();
				timer = null;
				return;
			case 0: // Team changed row.
			case 2: // Highlight moved to next row.
			case 3: // Bronze medal. Should not happen.
			case 4: // Silver medal. Should not happen.
			case 5: // Gold medal. Should not happen.
			case 6: // World champion. Should not happen.
			case 7: // Toggling between presentation/scoreboard. Should not happen.
				timer.schedule(new ResolverTask(), resolveTeamDelay);
				break;
			case 1: // Processed run for this row.
				timer.schedule(new ResolverTask(), resolveProblemDelay);
				break;
			default:
				System.out.println("Unknown return code: "+stepValue);
			}
		}
	}

	private int step(boolean updateStepCounter) {
		Contest contest = replayer.getContest();
		//String winnerString = "2010 World Champion";
		String winnerString = this.propertyReplay.get("winnerString").getValue();
		if (winnerString == null || winnerString.length() == 0) {
			//winnerString = "ICPC 2011 Champions";
			winnerString = "ICPC 2012 Champions";
		}
		if(updateStepCounter)
			++stepCounter;
		if(resolveRow<0) return -1;
		if(resolveRow==0) {
			// Toggle between scoreboard and champion presentation.
			if(showingPresentation) {
				showingPresentation = false;
				showScoreboard();
			} else {
				showingPresentation = true;
				Team team = contest.getRankedTeam(1);
				showWinnerPresentation(team.getId(), winnerString);
			}
			return 7;
		}
		int runId = replayer.getHighestRankedRun();
		Run run = null;
		if(runId>=0) run = contest.getRun(runId);
		Team team = contest.getRankedTeam(resolveRow);
		if(run != null && run.getTeam() == team.getId()) {
			showingPresentation = false;
			// Current row has more runs.
			// System.out.println("Next run on row "+resolveRow + ", run id "+run.getId());
			replayer.processProblem(run.getTeam(), run.getProblem());
			highlightNext();
			Team team2 = replayer.getContest().getRankedTeam(resolveRow);
			if(team.getId()==team2.getId()) return 1;
			return 0;
		} else if(resolveRow>silverMedals+goldMedals+bronzeMedals || showingPresentation) {
			if(showingPresentation)
				showScoreboard();
			showingPresentation = false;
			// Highlight next row
			--resolveRow;
			highlightNext();
			return 2;
		} else if(resolveRow>silverMedals+goldMedals) {
			showingPresentation = true;
			System.out.println("Bronze medal to team " + team.getId() + " on row " + resolveRow);
			showWinnerPresentation(team.getId(), "Bronze");
			showBronzeMedal(resolveRow);
			return 3;
		} else if(resolveRow>goldMedals) {
			showingPresentation = true;
			System.out.println("Silver medal to team " + team.getId() + " on row " + resolveRow);
			showWinnerPresentation(team.getId(), "Silver");
			showSilverMedal(resolveRow);
			return 4;
		} else if(resolveRow>1) {
			showingPresentation = true;
			System.out.println("Gold medal to team " + team.getId() + " on row " + resolveRow);
			showWinnerPresentation(team.getId(), "Gold");
			showGoldMedal(resolveRow);
			return 5;
		} else {
			showingPresentation = true;
			System.out.println("World champion team " + team.getId() + " on row " + resolveRow);
			showWinnerPresentation(team.getId(), winnerString);
			showGoldMedal(resolveRow);
			return 6;
		}
	}

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		// TODO Auto-generated method stub
		//System.out.println(e);
	}		
}
