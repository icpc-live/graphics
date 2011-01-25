package se.kth.livetech.communication;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.properties.PropertyHierarchy;


public class LiveStateRedisImpl implements LiveState {

	private PropertyHierarchy hierarchy;
	private Map<ContestId, ContestState> contests;
	
	public LiveStateRedisImpl() {
		hierarchy = new PropertyHierarchy();
		contests = new TreeMap<ContestId, ContestState>();
		ContestId id = new ContestId("contest", 0);
		contests.put(id, new ContestState());
	}

	@Override
	public void setContestSourceFlag(boolean contestSourceFlag) {}

	@Override
	public void addListeners(NodeUpdateListener connection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListeners(NodeUpdateListener connection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSpider() { return false; }

	@Override
	public PropertyHierarchy getHierarchy() {
		// TODO Auto-generated method stub
		return hierarchy;
	}

	@Override
	public ContestState getContest(ContestId id) {
		return contests.get(id);
	}

	@Override
	public Set<ContestId> getContests() {
		return contests.keySet();
	}
}
