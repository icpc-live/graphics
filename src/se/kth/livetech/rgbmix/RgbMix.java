package se.kth.livetech.rgbmix;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import se.kth.livetech.presentation.layout.LivePresentation;
import se.kth.livetech.util.Frame;

public class RgbMix {
	private JComponent component;
	int N = 4;
	int n;
	int w = 1280, h = 720;
	int pf = 10, pw = w / pf, ph = h / pf, ppf = 5;
	RgbInput[] in;
	SrcControl[] ctrl;

	public RgbMix(JComponent component) {
		this.component = component;
		this.in = new RgbInput[N];
		for (int i = 0; i < N; ++i) {
			in[i] = new RgbInput(this, i);
		}
		Frame fr = new Frame("AutoLiveInput", control());
	}

	JPanel program;
	BufferedImage im, im2, im3;
	int[] imBuf;

	private JPanel control() {
		JPanel panel = new JPanel();

		ctrl = new SrcControl[N];
		ButtonGroup selGroup = new ButtonGroup();
		Box b = new Box(BoxLayout.Y_AXIS);
		b.add(new JLabel("Inputs:"));
		for (int i = 0; i < N; ++i) {
			SrcControl ci = new SrcControl(selGroup, i);
			b.add(ci);
			ctrl[i] = ci;
		}

		im = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		im2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		im3 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		imBuf = ((DataBufferInt) im.getRaster().getDataBuffer()).getData();
		program = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(im3, 0, 0, pw * ppf, ph * ppf, null);
			}
		};
		program.setPreferredSize(new Dimension(pw * ppf, ph * ppf));
		b.add(program);

		panel.add(b);
		return panel;
	}

	class SrcControl extends JPanel {
		JPanel preview;
		BufferedImage im;
		int[] imBuf;
		JTextArea report;
		public SrcControl(ButtonGroup selGroup, final int i) {
			Box c = new Box(BoxLayout.X_AXIS);
			// select this input
			JRadioButton sel = new JRadioButton();
			selGroup.add(sel);
			sel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					n = i;
				}
			});
			c.add(sel);
			// start/stop this input
			final JCheckBox enable = new JCheckBox();
			enable.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (enable.isSelected()) {
						in[i].start();
					} else {
						in[i].stop();
					}
				}
			});
			c.add(enable);
			// select team/local
			final JTextField teamField = new JTextField(5);
			teamField.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int team = Integer.parseInt(teamField.getText());
					in[i].setTeam(team);
				}
			});
			c.add(teamField);
			// input preview
			im = new BufferedImage(pw, ph, BufferedImage.TYPE_INT_ARGB);
			imBuf = ((DataBufferInt) im.getRaster().getDataBuffer()).getData();
			preview = new JPanel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(im, 0, 0, null);
				}
			};
			preview.setPreferredSize(new Dimension(pw, ph));
			c.add(preview);
			// report
			report = new JTextArea(5, 40);
			JScrollPane scroll = new JScrollPane(report);
			scroll.setPreferredSize(new Dimension(pw * 2, ph));
			c.add(scroll);
			this.add(c);
		}
	}

	public void report(int i, String line) {
		System.out.println("RgbMix" + i + " " + line);
		JTextArea ta = ctrl[i].report;
		ta.setText(line);
	}

	int frame = 0;
	public void frame(int i, byte[] buf) {
		//preview
		{
			int[] pbuf = ctrl[i].imBuf;
			int a = 0, pa = 0;
			for (int y = 0; y < ph; ++y) {
				for (int x = 0; x < pw; ++x) {
					int r = buf[a++] & 0xff;
					int g = buf[a++] & 0xff;
					int b = buf[a++] & 0xff;
					pbuf[pa++] = 0xff000000 | r << 16 | g << 8 | b;
					a += (pf - 1) * 3;
				}
				a += w * (pf - 1) * 3;
			}
			ctrl[i].preview.repaint();
		}
		//program
		if (i == n) {
			++frame;
			int[] pbuf = imBuf;
			int a = 0, pa = 0;
			for (int y = 0; y < h; ++y) {
				for (int x = 0; x < w; ++x) {
					int r = buf[a++] & 0xff;
					int g = buf[a++] & 0xff;
					int b = buf[a++] & 0xff;
					pbuf[pa++] = 0xff000000 | r << 16 | g << 8 | b;
				}
			}
			{
				Graphics g = im2.getGraphics();
				this.component.setBounds(0, 0, w, h);
				this.component.setSize(w, h);
				((LivePresentation) this.component).paintComponent(g, w, h);
				g.dispose();
			}
			{
				Graphics g = im.getGraphics();
				g.drawImage(im2, 0, 0, null);
				g.dispose();
			}
			{
				Graphics g = im3.getGraphics();
				g.drawImage(im, 0, 0, null);
				g.dispose();
			}
			program.repaint();
		}
	}
}
