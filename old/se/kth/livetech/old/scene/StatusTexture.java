package se.kth.livetech.old.scene;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;

import se.kth.livetech.contest.graphics.ProblemScoreRenderer;
import se.kth.livetech.contest.model.ProblemScore;

import com.sun.j3d.utils.image.TextureLoader;


public class StatusTexture {
	public static class Quad {
		Dimension size;
		QuadArray q;
		public Quad(Dimension size) {
			this(size, 0);
		}
		public Quad(Dimension size, double z) {
			this.size = size;
			q = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.COLOR_3, 1, new int[] { 0 });
			double w = .05, h = .05;
			//double w = size.getWidth() * scale, h = size.getHeight() * scale;
			q.setCoordinate(0, new Point3d(-w, h, z));
			q.setCoordinate(1, new Point3d(-w, -h, z));
			q.setCoordinate(2, new Point3d(w, -h, z));
			q.setCoordinate(3, new Point3d(w, h, z));
			for (int i = 0; i < 4; ++i)
				q.setTextureCoordinate(0, i, new TexCoord2f(i/2, (i+3)/2%2));
		}
		public Shape3D coloured(Color c) {
			for (int i = 0; i < 4; ++i)
				q.setColor(i, new Color3f(c));
			return new Shape3D(q);
		}
		public Shape3D coloured(Color c1, Color c2) {
			for (int i = 0; i < 4; ++i)
				q.setColor(i, new Color3f(i % 2 == 0 ? c1 : c2));
			return new Shape3D(q);
		}
		BufferedImage i;
		Graphics2D g;
		public Graphics2D getGraphics() {
			i = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
			g = i.createGraphics();
			System.out.println("Graphics " + g);
			return g;
		}
		public Shape3D textured() {
			g.dispose();
			Appearance a = new Appearance();
			TextureLoader l = new TextureLoader(i);
			Texture t = l.getTexture();
			a.setTexture(t);
			return new Shape3D(q, a);
		}
	}
	public static Shape3D pscore(Dimension size, ProblemScore pscore) {
		Quad q = new Quad(size);
		Graphics2D g = q.getGraphics();
		Rectangle2D rect = new Rectangle2D.Double(0, 0, size.width, size.height);
		ProblemScoreRenderer psr = new ProblemScoreRenderer(pscore);
		g.translate(rect.getX(), rect.getY());
		int w = (int) rect.getWidth(), h = (int) rect.getHeight();
		psr.render(g, new Dimension(w, h));
		return q.textured();
	}
}
