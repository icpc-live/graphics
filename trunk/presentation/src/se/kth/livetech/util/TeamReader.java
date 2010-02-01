package se.kth.livetech.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

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
		
		while (true) {
			String line = br.readLine();
			
			if (line == null) {
				break;
			}
			
			String[] elems = line.split("\t");
			teams.put(
					Integer.parseInt(elems[0]),
					new TeamEntry(
							Integer.parseInt(elems[0]),
							elems[1],
							new String[] {
								elems[2],
								elems[3],
								elems[4]
								      }
							)
					);
		}
	}
	
	public String[] getTeamMembers(int id) {
		return teams.get(id).getMembers();
	}
}
