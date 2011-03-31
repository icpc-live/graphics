package se.kth.livetech.presentation.layout;

import java.util.Collection;
import java.util.Collections;

import se.kth.livetech.presentation.layout.LayoutDescriptionUpdater.ContentUpdater;

public class LayoutContent implements LayoutDescription {
	private Object key;
	private double fixedWidth;
	private double fixedHeight = 1d;
	private double stretchWeight;
	private double margin = 0d;
	private Content content;

	public LayoutContent(Object key, double fixedWidth, double stretchWeight, double margin, Content content) {
		this.key = key;
		this.fixedWidth = fixedWidth;
		this.stretchWeight = stretchWeight;
		this.margin = margin;
		this.content = content;
	}

	@Deprecated
	public static LayoutContent fixed(Object key, double weight, double margin, Content content) {
		return new LayoutContent(key, weight, 0, margin, content);
	}

	@Deprecated
	public static LayoutContent stretch(Object key, double weight, double margin, Content content) {
		return new LayoutContent(key, 0, weight, margin, content);
	}

	public static ContentUpdater fixed(Object key, double width, double margin, LayoutDescriptionUpdater updater) {
		LayoutDescriptionUpdater sub = updater.getSubLayoutUpdater(key);
		//sub.setMargin(margin);
		sub.setWeights(width, 1d, 0d);
		return sub.getContentUpdater();
	}

	public static ContentUpdater stretch(Object key, double weight, double margin, LayoutDescriptionUpdater updater) {
		LayoutDescriptionUpdater sub = updater.getSubLayoutUpdater(key);
		//sub.setMargin(margin);
		sub.setWeights(0d, 1d, weight);
		return sub.getContentUpdater();
	}

	@Override
	public Object getKey() {
		return this.key;
	}

	@Override
	public double getFixedWidth() {
		return this.fixedWidth;
	}

	@Override
	public double getFixedHeight() {
		return this.fixedHeight;
	}

	@Override
	public double getStretchWeight() {
		return this.stretchWeight;
	}

	@Override
	public boolean hasContent() {
		return true;
	}

	@Override
	public Content getContent() {
		return this.content;
	}

	@Override
	public double getTopMargin() {
		return this.margin;
	}

	@Override
	public double getBottomMargin() {
		return this.margin;
	}

	@Override
	public double getLeftMargin() {
		return this.margin;
	}

	@Override
	public double getRightMargin() {
		return this.margin;
	}

	@Override
	public double getAspectMin() {
		return 0;
	}

	@Override
	public double getAspectMax() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public Direction getDirection() {
		return Direction.ON_TOP;
	}

	@Override
	public Collection<Object> getSubOrder() {
		return Collections.emptyList();
	}

	@Override
	public Collection<LayoutDescription> getSubs() {
		return Collections.emptyList();
	}

	@Override
	public LayoutDescription getSub(Object key) {
		return null;
	}
}
