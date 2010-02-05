package se.kth.livetech.control.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.properties.ui.CheckBox;
import se.kth.livetech.properties.ui.Text;
import se.kth.livetech.properties.ui.ToggleButton;

@SuppressWarnings("serial")
public class ScoreboardAutoScrollPanel extends JPanel implements Runnable, PropertyListener {
	private IProperty base;

	public ScoreboardAutoScrollPanel(IProperty base) {
		this.base = base;
		Box b = new Box(BoxLayout.Y_AXIS);
		Box c;

		c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel("Page: "));
		c.add(new Text(base.get("score.page")));
		b.add(c);

		b.add(new ToggleButton(base.get("score.page"), Integer.toString(1), "Page 1"));
		b.add(new ToggleButton(base.get("score.page"), Integer.toString(2), "Page 2"));
		b.add(new ToggleButton(base.get("score.page"), Integer.toString(3), "Page 3"));
		b.add(new ToggleButton(base.get("score.page"), Integer.toString(4), "Page 4"));
		b.add(new ToggleButton(base.get("score.page"), Integer.toString(5), "Page 5"));

		c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel("First delay (s): "));
		c.add(new Text(base.get("score.auto.firstDelay")));
		b.add(c);

		c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel("Rest delay (s): "));
		c.add(new Text(base.get("score.auto.restDelay")));
		b.add(c);

		c = new Box(BoxLayout.X_AXIS);
		c.add(new CheckBox(base.get("score.auto.enabled"), "Auto"));
		b.add(c);

		this.add(b);
		base.get("score.auto.enabled").addPropertyListener(this);
	}

	boolean destroyed;

	@Override
	public void run() {
		IProperty enabled = base.get("score.auto.enabled");
		IProperty firstDelay = base.get("score.auto.firstDelay");
		IProperty restDelay = base.get("score.auto.restDelay");
		IProperty page = base.get("score.page");

		while (!destroyed && enabled.getBooleanValue()) {
			try {
				if (page.getIntValue() == 1) {
					Thread.sleep(firstDelay.getIntValue() * 1000);
				} else {
					Thread.sleep(restDelay.getIntValue() * 1000);
				}
			} catch (InterruptedException e) {}

			page.setIntValue((page.getIntValue() % 5) + 1);
		}

		base.get("score.auto.enabled").setBooleanValue(false);
	}

	@Override
	public void propertyChanged(IProperty changed) {
		if (changed == base.get("score.auto.enabled")) {
			if (changed.getBooleanValue() == true) {
				new Thread(this).start();
			}
		}
	}
}
