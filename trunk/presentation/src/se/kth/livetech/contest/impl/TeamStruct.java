package se.kth.livetech.contest.impl;

public class TeamStruct {
    public String university, univ, nationality;
    public TeamStruct(String university, String univ, String nationality) {
	this.university = university;
	this.univ = tr(univ, true);
	this.nationality = nationality;
    }
    public static String tr(String s, boolean spacetr) {
	for (int i = 0; i < s.length(); ++i) {
	    if (s.charAt(i) > 127)
		s = s.substring(0, i) + 'x' + s.substring(i + 1);
	    else if (s.charAt(i) == ' ' && spacetr)
		s = s.substring(0, i) + '_' + s.substring(i + 1);
	}
	return s;
    }
}
