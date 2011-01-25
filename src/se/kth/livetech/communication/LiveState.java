package se.kth.livetech.communication;

import java.util.Set;

import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.properties.PropertyHierarchy;

public interface LiveState {

	public abstract void setContestSourceFlag(boolean contestSourceFlag);

	public abstract void addListeners(NodeUpdateListener connection);

	public abstract void removeListeners(NodeUpdateListener connection);

	public abstract boolean isSpider();

	public abstract PropertyHierarchy getHierarchy();

	public abstract ContestState getContest(ContestId id);

	public abstract Set<ContestId> getContests();

}