package se.kth.livetech.communication;

import se.kth.livetech.communication.thrift.NodeId;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.properties.PropertyListener;

public interface NodeUpdateListener extends AttrsUpdateListener, PropertyListener {

	public abstract NodeId getId();

}