package se.kth.livetech.control.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.ui.CheckBox;
import se.kth.livetech.properties.ui.Slider;
import se.kth.livetech.properties.ui.Text;
import se.kth.livetech.properties.ui.ToggleButton;

@SuppressWarnings("serial")
public class ProductionPanel extends JPanel implements ActionListener {

	final String PROPERTIES_FILE = System.getenv("HOME") + "/livetech/presentation/conf/interviewpresets.properties";
    final int INTERVIEW_SECOND_NAME_POSITION = 32;

    Properties interviewPresets;
    List<String> presetsList;

    ListComboBoxModel comboModel;
	JComboBox combo;
	ProductionSettingsFrame presentationFrame;
	ContestReplayFrame contestReplayFrame;
	IProperty base;
	
	public ProductionPanel(IProperty base){

    this.interviewPresets = new Properties();
    this.presetsList = new ArrayList<String>();

		this.base = base;
//		DebugTrace.trace("Production panel from: "+base.toString());
		this.presentationFrame = new ProductionSettingsFrame(base);
		this.contestReplayFrame = new ContestReplayFrame(base.get("replay"));
		Box b = new Box(BoxLayout.Y_AXIS);
		Box c;

		// Scoreboard
		c = new Box(BoxLayout.X_AXIS);
		c.add(new ToggleButton(base.get("mode"), "score", "Scoreboard"));
		c.add(new JLabel("Page: "));
		c.add(new Text(base.get("score.page")));
		
		TitledBorder scoreBoarder;
		scoreBoarder = BorderFactory.createTitledBorder("Scoreboard");
		scoreBoarder.setTitleJustification(TitledBorder.CENTER);
		c.setBorder(scoreBoarder);
		
		
		JButton presentationButton = new JButton("Presentation");
		presentationButton.addActionListener(new ActionListener() { @Override
		public void actionPerformed(ActionEvent ae) { ProductionPanel.this.presentationFrame.setVisible(true); } } );
		c.add(presentationButton);
		
		JButton resolverButton = new JButton("Resolver");
		resolverButton.addActionListener(new ActionListener() { @Override
		public void actionPerformed(ActionEvent ae) { ProductionPanel.this.contestReplayFrame.setVisible(true); } } );
		c.add(resolverButton);
		
		
//		String s = "org.icpc_cli.presentation.contest.internal.presentations.ProductionPresentation";
//		c.add(new ToggleButton(base.get("presentation"), s, "Production"));
		//c.add(new JLabel("Teams: "));
		//c.add(new Text(base.get("score.teams")));
		b.add(c);
		//b.add(new JSeparator(SwingConstants.HORIZONTAL));

		//Overlays
		c = new Box(BoxLayout.X_AXIS);
		c.add(new ToggleButton(base.get("mode"), "blank", "Blank"));
		c.add(new CheckBox(base.get("show_queue"), "Queue"));
		c.add(new CheckBox(base.get("show_clock"), "Clock"));
		c.add(new CheckBox(base.get("show_nologo"), "No Logo"));
		c.add(new CheckBox(base.get("old_views"), "Old Views"));
		
		TitledBorder overlay;
		overlay = BorderFactory.createTitledBorder("Overlays");
		overlay.setTitleJustification(TitledBorder.CENTER);
		c.setBorder(overlay);
		
		b.add(c);
		//b.add(new JSeparator(SwingConstants.HORIZONTAL));

		// Interview
		c = new Box(BoxLayout.X_AXIS);
		Box d = new Box(BoxLayout.Y_AXIS);
		d.add(new ToggleButton(base.get("mode"), "interview", "Interview"));
		d.add(new ToggleButton(base.get("mode"), "layout", "Layout"));
            final JButton load = new Button("Load Presets");
            load.addActionListener(new ActionListener() {
            
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateInterviewPresets();
                    comboModel.actionPerformed(new ActionEvent(load, 0, "update"));
                }
            });
        d.add(load);
		c.add(d);
		d = new Box(BoxLayout.Y_AXIS);
		Box e = new Box(BoxLayout.X_AXIS);
		e.add(new JLabel("Name: "));
		Text t1 = new Text(base.get("interview.name"));
		t1.setPreferredSize(new Dimension(100, 28));
		e.add(t1);
		d.add(e);
		e = new Box(BoxLayout.X_AXIS);
		e.add(new JLabel("Title: "));
		Text t2 = new Text(base.get("interview.title"));
		t2.setPreferredSize(new Dimension(100, 28));
		e.add(t2);
		d.add(e);
		e = new Box(BoxLayout.X_AXIS);
		e.add(new JLabel("Preset: "));
        this.comboModel = new ListComboBoxModel(this.presetsList);
		this.combo = new JComboBox();
        this.combo.setModel(this.comboModel);
		this.combo.addActionListener(this);
		this.combo.setPreferredSize(new Dimension(100, 28));

		e.add(this.combo);
		d.add(e);
		c.add(d);
		b.add(c);
		//b.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		TitledBorder interviewBorder;
		interviewBorder = BorderFactory.createTitledBorder("Interview");
		interviewBorder.setTitleJustification(TitledBorder.CENTER);
		c.setBorder(interviewBorder);
		
		// Team
		c = new Box(BoxLayout.X_AXIS);
		TitledBorder teamBorder;
		teamBorder = BorderFactory.createTitledBorder("Team");
		teamBorder.setTitleJustification(TitledBorder.CENTER);
		c.setBorder(teamBorder);
		c.add(new ToggleButton(base.get("mode"), "team", "Team"));
		c.add(new CheckBox(base.get("team.show_members"), "Members"));
		c.add(new CheckBox(base.get("team.show_extra"), "Extra"));
		c.add(new CheckBox(base.get("team.show_results"), "Results"));
		b.add(c);

		//b.add(new JSeparator(SwingConstants.HORIZONTAL));

		// Team selection, surveillance, clearing
		c = new Box(BoxLayout.X_AXIS);
		
		
		c.add(new JLabel("Team #"));
		c.add(new Text(base.get("team.team")));
		c.add(new ToggleButton(base.get("mode"), "vnc", "Vnc"));
		c.add(new ToggleButton(base.get("mode"), "cam", "Cam"));
		c.add(new CheckBox(base.get("clear"), "Clear"));
		b.add(c);

		
		c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel("Team #"));
		c.add(new Slider.Int(base.get("team.team"), 1, 105));
		b.add(c);

		c = new Box(BoxLayout.X_AXIS);
		c.add(new ToggleButton(base.get("mode"), "countdown", "Countdown"));
		c.add(new Text(base.get("countdown_from")));

		b.add(c);
		
		/*
		b.add(new JSeparator(SwingConstants.HORIZONTAL));
		b.add(new ContestReplayPanel(base.get("replay")));
		*/
		
		// Problemboard and Timeline
		c = new Box(BoxLayout.X_AXIS);
		c.add(new ToggleButton(base.get("mode"), "problemboard", "Problem board"));
		c.add(new ToggleButton(base.get("mode"), "timeline", "Timeline"));
		
		b.add(c);
	
		this.add(b);
		
		
		
	
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String s = (String)this.combo.getSelectedItem();
		if ( s == null ) {
			s = "";
		}

		if(s.indexOf("|") >= 0) {
            String[] namesTitles[0] = s.split("\\|");
            String[] names = namesTitles[0].split("#");
            String[] titles = namesTitles[1].split("#");

            String name = names[0];
            if(names.length > 1) {
                name = formatString(name, names[1], "and");
            }

            String title = titles[0];
            if(titles.length > 1) {
                title = formatString(title, titles[1], "    ");
            }

            this.base.get("interview.name").setValue(name);
            this.base.get("interview.title").setValue(title);
        }
    }

    private String formatString(String str1, String str2, String separator) {
        String ret = str1;
        String spaces = "";
        int numberOfSpaces = INTERVIEW_SECOND_NAME_POSITION - ret.length();
        if(numberOfSpaces > 4) {
            for(int i = 0; i<numberOfSpaces; i++) {
                spaces += " ";
            }
            ret += spaces + str2;
        } else {
            ret += " " + separator + " " + str2;
        }
        return ret;

    }

    private void updateInterviewPresets() {
        try {
            interviewPresets.load(new FileInputStream(PROPERTIES_FILE));
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if(this.presetsList.size() > 0) {
            this.presetsList.clear();
        }
        int numberOfPresets = Integer.parseInt(interviewPresets.getProperty("interview.presets"));
        for(int i=0; i<numberOfPresets; i++){
            String name = interviewPresets.getProperty("interview.name" + (i+1));
            String title = interviewPresets.getProperty("interview.title" + (i+1));
            String post = name + "|" + title;
            this.presetsList.add(post);
        }
    }

    private class ListComboBoxModel implements ComboBoxModel, ActionListener {
    
        protected List<String> data;
        protected List<ListDataListener> listeners;
        protected Object selected;

        public ListComboBoxModel(List<String> list) {
            this.listeners = new ArrayList<ListDataListener>();
            this.data = list;
            if(list.size() > 0) {
                selected = list.get(0);
            }
        }

        @Override
        public Object getSelectedItem() {
            return this.selected;
        }

        @Override
        public void setSelectedItem(Object anItem) {
            this.selected = anItem;

        }

        @Override
        public void addListDataListener(ListDataListener listener) {
            this.listeners.add(listener);
        }

        @Override
        public Object getElementAt(int i) {
            return this.data.get(i);
        }

        @Override
        public int getSize() {
            return this.data.size();
        }

        @Override
        public void removeListDataListener(ListDataListener listener) {
            this.listeners.remove(listener);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("update")) {
                this.fireUpdate();
            }
        }

        public void fireUpdate() {
            ListDataEvent le = new ListDataEvent(this,
                ListDataEvent.CONTENTS_CHANGED,
                0,
                data.size());
            for(int i=0; i<listeners.size(); i++) {
                ListDataListener l = (ListDataListener)listeners.get(i);
                l.contentsChanged(le);
            }
        }
	}
}
