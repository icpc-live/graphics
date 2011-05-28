package se.kth.livetech.control.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyHierarchy;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.properties.ui.PropertyFrame;

@SuppressWarnings("serial")
public class ProductionFrame extends JFrame implements PropertyListener {

	// Preview preview=null;
	IProperty clients;
	IProperty base;
	// PMAbstractClient pmClient;
	PropertyFrame propertyFrame;
	PropertyHierarchy hierarchy;
	private JMenuBar menuBar = new JMenuBar();
	private JMenuItem mainPanelItem;
	private JMenuItem  resolverItem;
	private JMenuItem  closeItem;
	private JMenuItem  propertiesItem;
	private JMenuItem  printPropItem;
	ContestReplayFrame contestReplayFrame;
	
	Box c = null;

	public ProductionFrame(final PropertyHierarchy hierarchy, IProperty base,
			IProperty clients/* , PMAbstractClient pmClient */) {
		this.hierarchy = hierarchy;
		this.propertyFrame = new PropertyFrame("", hierarchy);
		this.contestReplayFrame = new ContestReplayFrame(clients);
		this.clients = clients;
		this.base = base;
		// this.pmClient=pmClient;
		int numClients = 1;
		if (System.getenv("M") != null) {
			numClients = Integer.parseInt(System.getenv("M"));
		}
		String title = base.getName();
		int titleDot = title.lastIndexOf('.');
		if (titleDot >= 0) {
			title = title.substring(titleDot + 1);
		}
		this.setTitle(title);

		// TestContestDataSource ds = new TestContestDataSource(true);
		// ds.addListener(pmClient);
		// ds.start();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setJMenuBar(menuBar);
		JMenu addMenu = new JMenu("Add");
		JMenu adminMenu = new JMenu("Admin");
		mainPanelItem = addMenu.add("Main Panel");
		resolverItem = addMenu.add("Resolver");
		addMenu.addSeparator();
		closeItem = addMenu.add("Close");
		
		propertiesItem = adminMenu.add("Properties");
		printPropItem = adminMenu.add("Print Properties");
		
		menuBar.add(addMenu);
		menuBar.add(adminMenu);
		
		base.get("preview").addPropertyListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c = new Box(BoxLayout.Y_AXIS);
		c.add(new JSeparator(SwingConstants.HORIZONTAL));
		Box b = new Box(BoxLayout.X_AXIS);
		for (int m = 1; m <= numClients; m++) {
			b.add(new ProductionControlPanel(base, clients, m));
			b.add(new JSeparator(SwingConstants.VERTICAL));
		}
		Box a = new Box(BoxLayout.Y_AXIS);
		JButton fullscreenOnButton = new JButton("Enable fullscreen");
		JButton fullscreenOffButton = new JButton("Leave fullscreen");

		mainPanelItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				//TODO
			}});
		
		resolverItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				contestReplayFrame.setVisible(true);
			}
		});
		
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setVisible(false);
				dispose();
			}
		});
		
		propertiesItem.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent ae) {
				propertyFrame.setVisible(true);
			}
		});
		
		printPropItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				hierarchy.print();
			}
		});
		
		fullscreenOnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fullScreen(true);
			}
		});
		
		fullscreenOffButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fullScreen(false);
			}
		});
		
		QuickControlPanel qcp = new QuickControlPanel(base, clients);
		qcp.setAlignmentX(0);
		a.add(qcp);
		b.add(a);
		c.add(b);
		getContentPane().add(c);
		pack();
		setVisible(true);
	}

	public void setupLinks(boolean linea) {
		setupMaster(hierarchy.getProperty("live.clients.team"));
		setupMaster(hierarchy.getProperty("live.clients.scoreboard"));
		setupMaster(hierarchy.getProperty("live.clients.super"));
		setupMaster(hierarchy.getProperty("live.clients.clock"));

		setupClient(hierarchy.getProperty("live.clients.dell-1"), hierarchy
				.getProperty("live.clients.scoreboard"), "HD", "scoreboard");
		setupClient(hierarchy.getProperty("live.clients.dell-2"), hierarchy
				.getProperty("live.clients.scoreboard"), "SD", "scoreboard");
		setupClient(hierarchy.getProperty("live.clients.dell-3"), hierarchy
				.getProperty("live.clients.team"), "SD", "cam");
		setupClient(hierarchy.getProperty("live.clients.dell-4"), hierarchy
				.getProperty("live.clients.team"), "HD", "vnc");
		setupClient(hierarchy.getProperty("live.clients.magic-1"), hierarchy
				.getProperty("live.clients.super"), "HD", "interview");
		setupClient(hierarchy.getProperty("live.clients.magic-2"), hierarchy
				.getProperty("live.clients.super"), "SD", "interview");
		setupClient(hierarchy.getProperty("live.clients.magic-3"), hierarchy
				.getProperty("live.clients.clock"), "HD", "blank");

		String[] teamGroup = { "live.clients.magic-1", "live.clients.magic-2",
				"live.clients.magic-3", "live.clients.dell-3",
				"live.clients.dell-4" };

		for (String s : teamGroup) {
			hierarchy.getProperty(s + ".team.team").setLink(
					"live.clients.team.team.team");
		}

		hierarchy.getProperty("live.clients.magic-1.team.team").setLink(
				"live.clients.team.team.team");
		hierarchy.getProperty("live.clients.magic-2.team.team").setLink(
				"live.clients.team.team.team");

		if (linea) {
			base.get("panel1.name").setValue("team");
			base.get("panel2.name").setValue("scoreboard");
		} else {
			base.get("panel1.name").setValue("super");
			base.get("panel2.name").setValue("clock");
		}
	}

	public void setupMaster(IProperty base) {
		base.get("vnc.host").setValue("icpc-01.csc.kth.se");
		base.get("vnc.port").setValue("59000");
		base.get("cam.host").setValue("icpc-01.csc.kth.se");
		base.get("cam.port").setValue("58000");
		base.get("show_queue").setBooleanValue(false);
		base.get("show_clock").setBooleanValue(false);
		base.get("show_nologo").setBooleanValue(false);
		base.get("old_view").setBooleanValue(false);
		base.get("team.show_members").setBooleanValue(false);
		base.get("team.show_extra").setBooleanValue(false);
		base.get("clear").setBooleanValue(false);
		base.get("presentation").setValue("org.icpc_cli.presentation.contest.internal.presentations.ProductionPresentation");

	}

	public void setupClient(IProperty base, IProperty target, String hd,
			String mode) {
		base.get("control").setValue("exitfullscreen");
		String[] linked = { "presentation", "score.page", "production",
				"vnc.host", "vnc.port", "cam.host", "cam.port", "mode",
				"show_queue", "show_clock", "show_nologo", "old_view", "interview.name",
				"interview.title", "team.show_members", "team.show_extra",
				"clear", };
		for (String s : linked) {
			base.get(s).setLink(target.getName() + "." + s);
		}
		base.get("format").setValue(hd);
		base.get("mode").setValue(mode);
		// base.get("presentation").setValue("org.icpc_cli.presentation.contest.internal.presentations.ProductionPresentation");
	}

	public void fullScreen(boolean enable) {
		if (enable) {
			hierarchy.getProperty("live.clients.dell-1.control").setValue(
					"fullscreen0");
			hierarchy.getProperty("live.clients.dell-2.control").setValue(
					"fullscreen0");
			hierarchy.getProperty("live.clients.dell-3.control").setValue(
					"fullscreen0");
			hierarchy.getProperty("live.clients.dell-4.control").setValue(
					"fullscreen0");
			hierarchy.getProperty("live.clients.magic-1.control").setValue(
					"fullscreen1");
			hierarchy.getProperty("live.clients.magic-2.control").setValue(
					"fullscreen1");
			hierarchy.getProperty("live.clients.magic-3.control").setValue(
					"fullscreen1");
		} else {
			hierarchy.getProperty("live.clients.dell-1.control").setValue(
					"exitfullscreen");
			hierarchy.getProperty("live.clients.dell-2.control").setValue(
					"exitfullscreen");
			hierarchy.getProperty("live.clients.dell-3.control").setValue(
					"exitfullscreen");
			hierarchy.getProperty("live.clients.dell-4.control").setValue(
					"exitfullscreen");
			hierarchy.getProperty("live.clients.magic-1.control").setValue(
					"exitfullscreen");
			hierarchy.getProperty("live.clients.magic-2.control").setValue(
					"exitfullscreen");
			hierarchy.getProperty("live.clients.magic-3.control").setValue(
					"exitfullscreen");

		}
	}

	public void propertyChanged(IProperty changed) {
		String panel = base.get("preview").getValue();
		String client = base.get(panel + ".name").getValue();
		if (client.equals("")) {
			client = "default";
		}
		// TODO: DebugTrace.trace(clients.get(client).toString());
		/*
		 * if(c!=null){ if(preview==null){base.get("presentation").setValue(
		 * "org.icpc_cli.presentation.contest.internal.presentations.ProductionPresentation"
		 * ); this.preview=new Preview(new Dimension(1066, 600),
		 * clients.get(client), pmClient);
		 * base.get("preview").addPropertyListener(this); c.add(preview, 0); }
		 * this.preview.setProperty(clients.get(client));
		 * this.preview.propertyChanged(changed); }
		 */
		pack();
		this.validate();
		// IProperty
		// i=clients.get(base.get(base.get("preview").getValue()+".name").getValue());
		// i.get("presentation").setValue("org.icpc_cli.presentation.contest.internal.presentations.ProductionPresentation");
		// preview.setProperty(i);
	}
}
