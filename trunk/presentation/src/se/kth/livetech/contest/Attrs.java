package se.kth.livetech.contest;

import java.util.Set;

/** Attrs is a map of attribute names to attribute values.
  * It should be copied for every update.  */
public interface Attrs {
    public Set<String> getProperties();
    public String getProperty(String name);
}
