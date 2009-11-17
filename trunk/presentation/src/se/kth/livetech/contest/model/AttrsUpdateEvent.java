package se.kth.livetech.contest.model;

import java.util.Set;

/** Timestamped update. */
public interface AttrsUpdateEvent {
	public long getTime();

	public String getType();

	public Set<String> getProperties();

	public String getProperty(String name);

	/**
	 * Construct an Attrs instance with the information from contest updated
	 * with the properties of this event.
	 */
	public Attrs merge(Contest contest);
}
