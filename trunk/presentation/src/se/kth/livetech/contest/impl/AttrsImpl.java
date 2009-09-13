package se.kth.livetech.contest.impl;

import java.util.Map;
import java.util.Set;

import se.kth.livetech.contest.Attrs;

/** {@link Attrs} */
public class AttrsImpl implements Attrs {
    protected AttrsImpl(Map<String, String> attrs) {
	this.attrs = attrs;
    }
    
    protected Map<String, String> attrs;
    
    public Set<String> getProperties() {
	return attrs.keySet();
    }

    public String getProperty(String name) {
	return attrs.get(name);
    }

    public String toString() {
	StringBuffer s = new StringBuffer();
	s.append(getClass().getCanonicalName());
	s.append('{');
	for (String p : getProperties()) {
	    if (!p.equals("id"))
		s.append(", ");
	    s.append(p);
	    s.append('=');
	    s.append(getProperty(p));
	}
	s.append('}');
	return s.toString();
    }
}
