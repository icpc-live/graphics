package se.kth.livetech.communication;

import org.apache.thrift.TException;

import se.kth.livetech.communication.thrift.ContestEvent;
import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.communication.thrift.LiveService;
import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.impl.AttrsUpdateEventImpl;

/** Listens to AttrsUpdateEvents and feed them to a Spider for further distribution to interested scoreboards */
public class SpiderFeeder implements AttrsUpdateListener {
	LiveService.Client client;
	ContestId contest;

	public SpiderFeeder(LiveService.Client client, ContestId contest) {
		this.client = client;
		this.contest = contest;
	}

	public void finish() {
	}

	public void attrsUpdated(AttrsUpdateEvent e) {
		ContestEvent event = new ContestEvent();
		event.time = e.getTime();
		event.type = e.getType();
		//event.properties = new TreeMap<String, String>();

		AttrsUpdateEventImpl i = (AttrsUpdateEventImpl) e;

		for(String a: i.getProperties()) {
			event.attributes.put(a, i.getProperty(a));
		}
		try {
			client.contestUpdate(contest, event);
		} catch (TException te) {
			//TODO
			te.printStackTrace();
		}
	}
}
