package se.kth.livetech.contest.model;

/**
 * A submission of a run or clarification (or printjob, balloonjob, ...).
 */
public interface Sub extends Attrs {
	/** New, selected and judged */
	public enum Status {
		fresh, active, done
	};

	/** Run-id or Clar-id */
	public int getId();

	/** Time in minutes */
	public int getTime();

	/** Problem-id */
	public int getProblem();

	/** Team-id */
	public int getTeam();

	public Status getStatus();
}
