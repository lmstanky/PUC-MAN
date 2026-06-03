package pathfinding;

import mainPackage.map;

import java.util.ArrayList;
import java.util.Collections;

public class AStar
{
	private ArrayList<Node> open = new ArrayList<Node>();
	private ArrayList<Node> closed = new ArrayList<Node>();
	private ArrayList<Node> temp = new ArrayList<Node>();
	
	Node upNeighbour;
	Node rightNeighbour;
	Node downNeighbour;
	Node lefttNeighbour;
	
	public int ix, iy, tx, ty, bx, by;
	
	
	public AStar(int initX, int initY, int targetX, int targetY, int blockedX, int blockedY)
	{
		ix = initX;
		iy = initY;
		tx = targetX;
		ty = targetY;
		bx = blockedX;
		by = blockedY;
	}
	
	public ArrayList<Node> algorithm()
	{
		open.clear();
		closed.clear();
		
		if(ix == bx && iy == by)
		{
			bx = -1;
			by = -1;
		}
			
		
		// Create the initial Node for the search list. This is the Ghost's starting point
		Node q = new Node(ix, iy, 0);
		q.calculateH(tx, ty);
		q.calculateF(tx, ty);
		open.add(q);
		
		// q also represents the current working Node
		
		while(!open.isEmpty())
		{
			q = open.remove(0);
			
			if(q.x == bx && q.y == by)
				continue;
			// 2. Since we've reached q, remove it from the open list
			closed.add(q);
			
			if(isGoal(q))
			{
				return constructPath(q);
			}
				
			
			// 3. Generate Nodes for q's neighbours
			if(map.layout[q.x-1][q.y].getText() != "\u25A0")
			{
				Node upNeighbour = new Node(q.x-1, q.y, q.g+1);
				temp.add(upNeighbour);
			}
			
			if(q.y+1 > 28)
			{
				Node rightNeighbour = new Node(q.x, 0, q.g+1);
				temp.add(rightNeighbour);
			}
			
			else if(map.layout[q.x][q.y+1].getText() != "\u25A0")
			{
				Node rightNeighbour = new Node(q.x, q.y+1, q.g+1);
				temp.add(rightNeighbour);
			}
			
			if(map.layout[q.x+1][q.y].getText() != "\u25A0")
			{
				Node downNeighbour = new Node(q.x+1, q.y, q.g+1);
				temp.add(downNeighbour);
			}
			
			if(q.y-1 < 0)
			{
				Node leftNeighbour = new Node(q.x, 28, q.g+1);
				temp.add(leftNeighbour);
			}
			
			else if(map.layout[q.x][q.y-1].getText() != "\u25A0")
			{
				Node leftNeighbour = new Node(q.x, q.y-1, q.g+1);
				temp.add(leftNeighbour);
			}
			
			for(int i = 0; i < temp.size(); i++)
			{
				if(checkList(temp.get(i), closed) != null)
				{
					Node prior = new Node(0, 0, 0);
					prior = checkList(temp.get(i), closed);
					if(temp.get(i).g < prior.g)
					{
						closed.remove(getIndex(prior, closed));
						temp.get(i).calculateH(tx, ty);
						temp.get(i).calculateF(tx, ty);
						temp.get(i).setParent(q);
						open.add(temp.get(i));
					}
				}
				
				else
				{
					temp.get(i).calculateH(tx, ty);
					temp.get(i).calculateF(tx, ty);
					temp.get(i).setParent(q);
					open.add(temp.get(i));
				}					
			}
			temp.clear();
			
		}
		return null;
	}
	
	private ArrayList<Node> constructPath(Node n)
	{
		ArrayList<Node> path = new ArrayList<Node>();
		while(n.parent != null)
		{
			path.add(n);
			n = n.parent;
		}
		Collections.reverse(path);
		return path;		
	}

	private Node checkList(Node node, ArrayList<Node> list)
	{
		for(int i = 0; i < list.size(); i++)
			if(node.x == list.get(i).x && node.y == list.get(i).y)
				return list.get(i);
		return null;
	}
	
	private int getIndex(Node node, ArrayList<Node> list)
	{
		for(int i = 0; i < list.size(); i++)
			if(node.x == list.get(i).x && node.y == list.get(i).y)
				return list.indexOf(list.get(i));
		return -1;
	}
	
	private boolean isGoal(Node node)
	{
		if(node.x == tx && node.y == ty)
		{
			return true;
		}
			
		return false;
	}
}
