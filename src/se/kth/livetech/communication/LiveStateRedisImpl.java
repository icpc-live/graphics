package se.kth.livetech.communication;

import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.properties.PropertyHierarchy;


public class LiveStateRedisImpl implements LiveState {

	@Override
	public void setContestSourceFlag(boolean contestSourceFlag) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addListeners(NodeConnection connection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListeners(NodeConnection connection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSpider() {
		return false;
	}

	@Override
	public PropertyHierarchy getHierarchy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContestState getContest(ContestId id) {
		// TODO Auto-generated method stub
		return null;
	}
}
