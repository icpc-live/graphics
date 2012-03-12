package se.kth.livetech.contest.model.impl;

import java.util.Map;

import se.kth.livetech.contest.model.Run;

public class RunImpl extends SubImpl implements Run {
	String language, result;
	boolean judged, solved, penalty;
	RunJudgementImpl runJudgement;
	RunSourceImpl runSource;

	public RunImpl(Map<String, String> attrs) {
		super(attrs);
		language = attrs.get("language");
		result = attrs.get("result");
		judged = Boolean.valueOf(attrs.get("judged"));
		solved = Boolean.valueOf(attrs.get("solved"));
		if (attrs.containsKey("penalty")) {
			penalty = Boolean.valueOf(attrs.get("penalty"));
		}
		runJudgement = new RunJudgementImpl();
	}

	public String getType() {
		return "run";
	}
	
	public String getLanguage() {
		return language;
	}

	@Deprecated
	public String getResult() {
		return result;
	}

	@Deprecated
	public boolean isJudged() {
		return judged;
	}

	@Deprecated
	public boolean isSolved() {
		return solved;
	}
	
	@Deprecated
	public boolean isPenalty() {
		return penalty;
	}

	public Run.RunJudgement getRunJudgement() {
		return runJudgement;
	}

	public class RunJudgementImpl implements Run.RunJudgement {
		public String getResult() {
			return result;
		}

		public boolean isJudged() {
			return judged;
		}

		public boolean isSolved() {
			return solved;
		}

		public boolean isPenalty() {
			return penalty;
		}
	}

	public Run.RunSource getRunSource() {
		if (runSource == null) {
			runSource = new RunSourceImpl();
		}
		return runSource;
	}

	public class RunSourceImpl implements Run.RunSource {

		/* TODO: run source files implementation
		public List<Run.RunSourceFile> getFiles() {
		}

		public class RunSourceFileImpl implements Run.RunSource.RunSourceFile {

			public String getName() {
				return null;
			}

			public boolean isMain() {
				return false;
			}

			public byte[] getContents() {
				return null;
			}
		}
		*/
	}
}
