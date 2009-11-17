package se.kth.livetech.contest.model;

import java.util.Set;

/** Contest gathers all data about a contest. */
public interface Contest {
	public Info getInfo();

	public Set<Integer> getTeams();

	public Team getTeam(int i);

	public Set<Integer> getProblems();

	public Problem getProblem(int i);

	public Set<String> getLanguages();

	public Language getLanguage(String i);

	public Set<String> getJudgements();

	public Judgement getJudgement(String i);

	public Set<Integer> getRuns();

	public Run getRun(int i);

	public Set<Integer> getClars();

	public Clar getClar(int i);

	public int getRuns(int team, int problem);

	public Run getRun(int team, int problem, int i);

	public TeamScore getTeamScore(int team);

	public int getTeamRank(int team);

	public int getTeamRow(int team);

	public Team getRankedTeam(int rank);
}
