package se.kth.livetech.presentation.layout;


public interface LayoutDescription {
	public Object getKey();
	public double getFixedWidth();
	public double getFixedHeight();
	public double getStretchWeight();

	public double getTopMargin(); // 0-1
	public double getBottomMargin(); // 0-1
	public double getLeftMargin(); // 0-1
	public double getRightMargin(); // 0-1
	
	public double getAspectMin();
	public double getAspectMax();

	public boolean hasContent();
	public Content getContent();

	public enum Direction {
		ON_TOP,
		HORIZONTAL,
		VERTICAL,
	}
	public Direction getDirection();
	public Iterable<Object> getSubOrder();
	public LayoutDescription getSub(Object key);
}
