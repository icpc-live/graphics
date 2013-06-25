package se.kth.livetech.contest.graphics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ICPCStrings {
	private static Map<Integer, String> teamShortNames = null;
	public static synchronized String getTeamShortName(int teamId) {
		if (teamShortNames == null) {
			teamShortNames = new HashMap<Integer, String>();
			BufferedReader reader;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream("shortnames.txt")));
				for (int i = 1; ; ++i) {
					String line = reader.readLine();
					if (line == null) {
						break;
					}
					teamShortNames.put(i, line);
				}
			} catch (IOException e) {
				// pass
			}
		}
		return teamShortNames.get(teamId);
	}
}
