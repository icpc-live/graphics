package se.kth.livetech.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.Team;

public class TeamReader {
	private static class TeamEntry {
		private int id;
		private String teamName;
		private String[] members;

		public TeamEntry(int id, String teamName, String[] members) {
			super();
			this.setId(id);
			this.setTeamName(teamName);
			this.setMembers(members);
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getTeamName() {
			return teamName;
		}

		public void setTeamName(String teamName) {
			this.teamName = teamName;
		}

		public String[] getMembers() {
			return members;
		}

		public void setMembers(String[] members) {
			this.members = members;
		}
	}

	private Map<Integer, TeamEntry> teams;

	public TeamReader(String fileName) throws IOException {
		this(new File(fileName));
	}

	public TeamReader(File file) throws IOException {
		this(new FileInputStream(file));
	}

	public TeamReader(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		teams = new TreeMap<Integer, TeamEntry>();

		for (int i = 0; true; ++i) {
			String line = br.readLine();

			if (line == null) {
				break;
			}

			String[] elems = line.split("\t");
			teams.put(
					i,
					new TeamEntry(
							i,
							elems[0],
							new String[] {
								elems[1],
								elems[2],
								elems[3]
								      }
							)
					);
		}
	}

	public String[] getTeamMembers(int id) {
		return teams.get(id).getMembers();
	}

	public boolean isConsistent(Contest c) {
		boolean consistent = true;

		for (int id : c.getTeams()) {
			if (teams.containsKey(id)) {
				TeamEntry team1 = teams.get(id);
				Team team2 = c.getTeam(id);

				if (team1.getTeamName() != team2.getName()) {
					consistent = false;
					DebugTrace.trace("Team name of team %d not consistent (\"%s\" != \"%s\").", id, team1.getTeamName(), team2.getName());
				}
			} else {
				consistent = false;
				DebugTrace.trace("Team ID %d (%s) present in system but not in file.", id, c.getTeam(id).getName());
			}
		}

		for (int id : teams.keySet()) {
			if (!c.getTeams().contains(id)) {
				consistent = false;
				DebugTrace.trace("Team ID %d (%s) present in file but not in system.", id, teams.get(id).getTeamName());
			}
		}

		return consistent;
	}
	
	public Set<Integer> getIds(){
		return teams.keySet();
	}
}
