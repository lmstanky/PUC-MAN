package pathfinding;

public class Node
{	
	// Coordinate specific values
	public int x;
	public int y; 
	public Node parent;
	
	// Algorithm specific values
	int g, f, h; 
	
	public Node(int inX, int inY, int inG)
	{
		x = inX;
		y = inY;
		g = inG;
	}
	
	public void calculateH(int tx, int ty)
	{
		h = Math.abs(x - tx) + Math.abs(y - ty);
	}
	
	public void calculateF(int tx, int ty)
	{
		f = (g + h);
	}
	
	public void setParent(Node par)
	{
		parent = par;
	}
}
