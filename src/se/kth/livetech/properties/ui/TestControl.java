package se.kth.livetech.properties.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import se.kth.livetech.properties.PropertyHierarchy;

public class TestControl {
	public TestControl(String clientId, PropertyHierarchy properties)  {
		JFrame f = new JFrame("PMControlClient " + clientId);
		Box b = new Box(BoxLayout.Y_AXIS);

		b.add(new TestTriangle(properties.getProperty("live")));
		b.add(new Text(properties.getProperty("live.l")));
		b.add(new Slider(properties.getProperty("live.l"), 1, 10));
		b.add(new Text(properties.getProperty("live.a")));
		b.add(new Slider(properties.getProperty("live.a"), 0, Math.PI * 2));
		//	b.add(new Hue(properties.getProperty("live.color")));
		//	b.add(new Text(properties.getProperty("live.color.r")));
		//	b.add(new Text(properties.getProperty("live.color.g")));
		//	b.add(new Text(properties.getProperty("live.color.b")));
		//	b.add(new Text(properties.getProperty("live.color.h")));
		//	b.add(new Text(properties.getProperty("live.color.s")));
		//	b.add(new Text(properties.getProperty("live.color.v")));
		b.add(new Text(properties.getProperty("live.clients.client-1.presentation")));
		b.add(new Text(properties.getProperty("live.clients.client-1.control")));
		b.add(new Text(properties.getProperty("live.clients.client-1.ProblemsLayoutPresentation.teamsPerScreen")));
		b.add(new Slider(properties.getProperty("live.clients.client-1.ProblemsLayoutPresentation.scroll"), 0, 2000));
		b.add(new Text(properties.getProperty("live.clients.client-1.SingleTeamLayoutPresentation.team")));

		JScrollPane jScrollPane1 = new JScrollPane();
		jScrollPane1.setViewportView(new PropertyOutline(properties.getProperty("live")));
		b.add(jScrollPane1);

		DefaultListModel l = new DefaultListModel();
		//	l.addElement("1");
		//	l.addElement("2");
		//	l.addElement("3");
		for (String name : new String[] {
				"BlankPresentation",
				"GreenScreenPresentation",
				"ZeroTimePresentation"
		}) {
			l.addElement("org.icpc_cli.presentation.core.internal.presentations." + name);
		}
		for (String name : new String[] {
				"ProblemsPresentation",
				"ProblemsLayoutPresentation",
				"SingleTeamPresentation",
				"SingleTeamLayoutPresentation",
				"SingleTeamWithGraphicsPresentation",
				"TimelinePresentation",
				"JudgeQueuePresentation",
				"LeaderboardPresentation",
				"LogScreenPresentation",
				"LogoPresentation",
				"ProblemSummaryChart",
				"LanguageSummaryChart",
				"TotalProblemsChart",
				"ProblemSummaryPresentation",
				"ProblemDetailChart",
				"LargeMessagePresentation",
				"FireworksPresentation"
		}) {
			l.addElement("org.icpc_cli.presentation.contest.internal.presentations." + name);
		}
		b.add(new List(properties.getProperty("live.clients.client-1.presentation"), l));
		b.add(new Text(properties.getProperty("live.list")));
		properties.getProperty("live.l").setDoubleValue(3);
		properties.getProperty("live.a").setDoubleValue(0);
//		b.add(new Preview(new Dimension(640, 360), properties.getProperty("live.clients.client-1.presentation"), this));

		f.add(b);
		f.pack();
		f.setVisible(true);
	}
}
