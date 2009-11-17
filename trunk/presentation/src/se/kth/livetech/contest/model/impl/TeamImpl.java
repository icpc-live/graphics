package se.kth.livetech.contest.model.impl;

import java.util.Map;

import se.kth.livetech.contest.model.Team;

public class TeamImpl extends EntityImpl implements Team {
	String university, univ, nationality;

	public TeamImpl(Map<String, String> attrs) {
		super(attrs);
		// Extract university and nationality from a "Name (University/NN)"
		// format
		university = "";
		nationality = "";
		int i2 = name.lastIndexOf(')');
		if (i2 > 0) {
			int i1 = name.lastIndexOf('(', i2);
			if (i1 > 0) {
				university = name.substring(i1 + 1, i2);
				name = name.substring(0, i1).trim();
				int i = university.lastIndexOf('/');
				if (i > 0) {
					nationality = university.substring(i + 1);
					university = university.substring(0, i);
				}
			}
		}
		univ = "";
		for (int i = 0; i < university.length(); ++i) {
			int s = university.indexOf(' ', i);
			if (s >= i) {
				String t = university.substring(i, s);
				if (t.equalsIgnoreCase("University") || t.equals("of")
						|| t.equals("International")
						|| t.equals("Universiteit"))
					i = s;
				else {
					univ = t;
					break;
				}
			} else {
				univ = university.substring(i);
				break;
			}
		}
		for (int i = 0; i < univ.length(); ++i) {
			if (univ.charAt(i) > 127)
				univ = univ.substring(0, i) + 'x' + univ.substring(i + 1);
			else if (univ.charAt(i) == '-')
				univ = univ.substring(0, i);
		}
	}

	public String getUniversity() {
		return university;
	}

	public String getUniv() {
		return univ;
	}

	public String getNationality() {
		return nationality;
	}
}
