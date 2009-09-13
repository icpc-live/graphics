package se.kth.livetech.communication;

import java.util.ArrayList;

import kth.communication.Attr;
import kth.communication.Attrs;
import kth.communication.SpiderService;
import se.kth.livetech.contest.AttrsUpdateEvent;
import se.kth.livetech.contest.AttrsUpdateListener;
import se.kth.livetech.contest.impl.AttrsUpdateEventImpl;

import com.facebook.thrift.TException;

/** Listens to AttrsUpdateEvents and feed them to a Spider for further distribution to interested scoreboards */
public class SpiderFeeder implements AttrsUpdateListener {
	SpiderService.Client client;

	public SpiderFeeder(SpiderService.Client client) {
		this.client = client;
	}

	public void finish() {
	}

	public void attrsUpdated(AttrsUpdateEvent e) {
		Attrs attrs = new Attrs();
		attrs.time = e.getTime();
		attrs.type = e.getType();
		attrs.properties = new ArrayList<Attr>();

		AttrsUpdateEventImpl i = (AttrsUpdateEventImpl) e;

		for(String a: i.getProperties()) {
			Attr attr = new Attr();
			attr.key = a;
			attr.value = i.getProperty(a);
			attrs.properties.add(attr);
		}
		try {
			client.contestUpdate(attrs);
		} catch (TException te) {
			//TODO
			te.printStackTrace();
		}
	}
}
