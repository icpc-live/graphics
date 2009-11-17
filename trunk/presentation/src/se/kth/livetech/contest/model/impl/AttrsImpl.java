package se.kth.livetech.contest.model.impl;

import java.util.Map;
import java.util.Set;

import se.kth.livetech.contest.model.Attrs;

/** {@link Attrs} */
public class AttrsImpl implements Attrs {
	protected AttrsImpl(Map<String, String> attrs) {
		this.attrs = attrs;
	}

	@Override
	public int hashCode() {
		return this.attrs.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (this.getClass() != o.getClass())
			return false;
		AttrsImpl that = (AttrsImpl) o;
		return this.attrs.equals(that.attrs);
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
