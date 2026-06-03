package pathfinding;

public class Node
{	
	// Coordinate specific values
	public int i;
	public int j; 
	
	// Algorithm specific values
	int g, f, h;
	public Node parent;
	
	public Node(int inI, int inJ, int inG)
	{
		i = inI;
		j = inJ;
		g = inG;
	}
	
	public void calculateH(int ti, int tj)
	{
		h = Math.abs(i - ti) + Math.abs(j - tj);
	}
	
	public void calculateF(int ti, int tj)
	{
		f = (g + h);
	}
	
	public void setParent(Node par)
	{
		parent = par;
	}
}
