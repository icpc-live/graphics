package se.kth.livetech.blackmagic;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JPanel;

import se.kth.livetech.presentation.layout.LivePresentation;
import se.kth.livetech.presentation.layout.Rect;

@SuppressWarnings("serial")
public class MagicPanel extends JPanel {
	public static final boolean IS_720_P = false;
	public static final int W = IS_720_P ? 1280 : 1920;
	public static final int H = IS_720_P ? 720 : 1080;
	JComponent component;
	int deviceN;
	BufferedImage img;
	int[] buffer;
	Exception err;

	Object device, output;
	Method displayIntArrayFrameSync;

	SortedSet<Long> paintTimes = new TreeSet<Long>();

	public MagicPanel(JComponent component, int deviceN) {
		add(component);
		setBackground(Color.BLACK);

		this.component = component;
		this.deviceN = deviceN;
		this.img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
		this.buffer = ((DataBufferInt) this.img.getRaster().getDataBuffer()).getData();

		Class<?> deckLinkDeviceClass;
		Class<?> deckLinkOutputClass;
		Class<?> deckLinkTestClass;
		try {
			deckLinkDeviceClass = Class.forName("se.kth.livetech.blackmagic.DeckLink$Device");
			deckLinkOutputClass = Class.forName("se.kth.livetech.blackmagic.DeckLink$Output");
			deckLinkTestClass = Class.forName("se.kth.livetech.blackmagic.DeckLinkTest");
		} catch (ClassNotFoundException e) {
			this.err = e;
			e.printStackTrace();
			return;
		}

		Method setupOutput, getOutput;
		try {
			setupOutput = deckLinkTestClass.getMethod(IS_720_P ? "setup720p50" : "setup1080i50", int.class);
			getOutput = deckLinkDeviceClass.getMethod("getOutput");
			this.displayIntArrayFrameSync = deckLinkOutputClass.getMethod("displayIntArrayFrameSync", int.class, int.class, int[].class);
		} catch (NoSuchMethodException e) {
			this.err = e;
			e.printStackTrace();
			return;
		}

		try {
			this.device = setupOutput.invoke(null, deviceN);
			this.output = getOutput.invoke(this.device);
		} catch (SecurityException e) {
			this.err = e;
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			this.err = e;
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			this.err = e;
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			this.err = e;
			e.printStackTrace();
		}
	}

	private void displayIntArrayFrameSync(int w, int h, int[] frame) {
		try {
			this.displayIntArrayFrameSync.invoke(this.output, w, h, frame);
		} catch (IllegalArgumentException e) {
			this.err = e;
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			this.err = e;
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			this.err = e;
			e.printStackTrace();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(IS_720_P ? W : W/2, IS_720_P ? H : H/2);
	}

	@Override
	public void paintChildren(Graphics gr) {
		// Do not paint children, but schedule this panel to repaint...
		repaint(20);
	}

	@Override
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);

		// FIXME: Thread away drawing and magic frame sync
		{
			Graphics2D g = (Graphics2D) this.img.getGraphics();
			g.setPaint(new Color(0,0,0,0));
			g.setComposite(AlphaComposite.Clear);
			g.fillRect(0, 0, W, H);
			g.setComposite(AlphaComposite.SrcOver);
			this.component.setBounds(0, 0, W, H);
			this.component.setSize(W, H);
			//System.err.println("MagicSize " + component.getWidth() + "x" + component.getHeight());
			//this.component.paint(g); // FIXME: Does not paint?
			//this.component.paintAll(g); // FIXME: Does not paint?
			//((LayoutPresentation) ((LivePresentation) component).getCurrentView()).paintComponent(g, W, H);
			((LivePresentation) this.component).paintComponent(g, W, H);
			g.dispose();
		}

		{
			if (this.output != null) {
				displayIntArrayFrameSync(W, H, this.buffer);
			}
		}

		Rectangle2D rect = new Rectangle(0, 0, getWidth(), getHeight());
		double aspect = (double) W / H;
		rect = Rect.aspect(rect, aspect, aspect);
		final boolean DRAW_IMG = true;
		if (DRAW_IMG) {
			gr.drawImage(this.img,
					(int) rect.getMinX(), (int) rect.getMinY(), (int) rect.getMaxX(), (int) rect.getMaxY(),
					0, 0, W, H,
					this);
		} else {
			gr.setColor(Color.RED);
			gr.drawLine((int) rect.getMinX(), (int) rect.getMinY(), (int) rect.getMaxX(), (int) rect.getMaxY());
			gr.drawLine((int) rect.getMinX(), (int) rect.getMaxY(), (int) rect.getMaxX(), (int) rect.getMinY());
		}

		if (this.err != null) {
			gr.setColor(Color.RED);
			gr.drawLine(0, 0, 20, 20);
			gr.drawLine(0, 20, 20, 0);
			gr.drawString(this.err.toString(), 10, 40);
		}

		{
			long time = System.currentTimeMillis();
			while (!this.paintTimes.isEmpty() && this.paintTimes.first() < time - 1000) {
				this.paintTimes.remove(this.paintTimes.first());
			}
			this.paintTimes.add(time);

			double fps = (this.paintTimes.size() - 1) / ((this.paintTimes.last() - this.paintTimes.first()) / 1000.0);
			gr.setColor(Color.GREEN);
			gr.drawString(String.format("%.1f fps", fps), 20, 20);
			if (this.paintTimes.size() <= 1) {
				repaint(20);
			}
		}

		repaint(20); // Always repaint..
	}
}
