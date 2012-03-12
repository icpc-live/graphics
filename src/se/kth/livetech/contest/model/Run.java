package se.kth.livetech.contest.model;


/** A problem submission. */
public interface Run extends Sub {
	public String getLanguage();

	@Deprecated
	public String getResult();

	@Deprecated
	public boolean isSolved();

	@Deprecated
	public boolean isJudged();
	
	@Deprecated
	public boolean isPenalty();

	public RunJudgement getRunJudgement();

	public interface RunJudgement {
		public String getResult();

		public boolean isSolved();

		public boolean isJudged();

		public boolean isPenalty();
	}

	public RunSource getRunSource();

	public interface RunSource {

		// TODO: public List<RunSourceFile> getFiles();

		public interface RunSourceFile {

			public String getName();

			public boolean isMain();

			public byte[] getContents();
		}
	}
}
