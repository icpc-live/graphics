package se.kth.livetech.contest.model;

import java.util.Set;

/**
 * Attrs is a map of attribute names to attribute values. It should be copied
 * for every update.
 */
public interface Attrs {
	public String getType();

	public Set<String> getProperties();

	public String getProperty(String name);
}
