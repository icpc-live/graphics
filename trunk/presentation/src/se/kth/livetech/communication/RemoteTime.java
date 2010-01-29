package se.kth.livetech.communication;

public interface RemoteTime {
	public long getRemoteTimeMillis();

	public static class LocalTime implements RemoteTime {
		public long getRemoteTimeMillis() {
			return System.currentTimeMillis();
		}
	}
}
