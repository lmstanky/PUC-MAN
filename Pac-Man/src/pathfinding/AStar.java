package pathfinding;

import main.Game;

import java.util.ArrayList;
import java.util.Stack;

import entities.Ghost;

public class AStar
{
	private ArrayList<Node> open = new ArrayList<Node>();
	private ArrayList<Node> closed = new ArrayList<Node>();
	private ArrayList<Node> temp = new ArrayList<Node>();
	
	Node upNeighbour;
	Node rightNeighbour;
	Node downNeighbour;
	Node lefttNeighbour;
	
	private int ii, ij;
	public int ti, tj;
	private Game game;
	private Ghost ghost = null;
	
	
	public AStar(int initI, int initJ, int targetI, int targetJ, Game game, Ghost ghost)
	{
		ii = initI;
		ij = initJ;
		ti = targetI;
		tj = targetJ;
		this.game = game;
		this.ghost = ghost;
		
	}

	public Stack<Node> algorithm()
	{
		open.clear();
		closed.clear();
		temp.clear();
		
		
		// 1. Create the initial Node for the search list. This is the Ghost's starting point
		Node q = new Node(ii, ij, 0);
		q.calculateH(ti, tj);
		q.calculateF(ti, tj);
		open.add(q); // q represents the current working Node
		
		
		while(!open.isEmpty())
		{
			// 2. Since we've reached q, remove it from the open list
			q = open.remove(0);
			
			closed.add(q);
			if(ghost.state != "returning" && ghost.state != "eaten")
			{
				try
				{
					int[] behind = getSpaceBehind();
					if(q.i == behind[0] && q.j == behind[1])
						continue;
				}
				catch(NullPointerException e)
				{
					continue;
				}
			}
			
			// Don't use the tunnel if the Ghost is in Scatter
			if(!ghost.inTunnel && ghost.state == "scatter" && (q.i == 5 || q.i == 22) && q.j == 14)
				continue;
			
			
			
			if(isGoal(q))
				return constructPath(q);
			
			// 3. Generate Nodes for q's neighbours (Skip nodes with prohibited coordinates)
			try
			{
				if(q.i < 28 && q.i > -1 && (game.maze.map[q.j-1][q.i].isBlank || game.maze.map[q.j-1][q.i].isDot)
						&& !(q.i == 12 && q.j-1 == 10)
						&& !(q.i == 15 && q.j-1 == 10)
						&& !(q.i == 12 && q.j-1 == 22)
						&& !(q.i == 15 && q.j-1 == 22))
				{
					Node upNeighbour = new Node(q.i, q.j-1, q.g+1);
					temp.add(upNeighbour);
					
				}
				
				if(q.i+1 > 27)
				{
					Node rightNeighbour = new Node(0, q.j, q.g+1);
					temp.add(rightNeighbour);
				}
				
				else if(q.i+1 < 28 && q.i > -1 && (game.maze.map[q.j][q.i+1].isBlank || game.maze.map[q.j][q.i+1].isDot)
						&& !(q.i+1 == 20 && q.j == 14 && ghost.state == "scatter"))
				{
					Node rightNeighbour = new Node(q.i+1, q.j, q.g+1);
					temp.add(rightNeighbour);
				}
				
				
				if(q.i < 28 && q.i > -1 && (game.maze.map[q.j+1][q.i].isBlank || game.maze.map[q.j+1][q.i].isDot)
						&& !(q.i == 18 && q.j+1 == 18))
				{
					Node downNeighbour = new Node(q.i, q.j+1, q.g+1);
					temp.add(downNeighbour);
				}
				
				if(q.i-1 < 0)
				{
					Node leftNeighbour = new Node(27, q.j, q.g+1);
					temp.add(leftNeighbour);
				}
				
				else if(game.maze.map[q.j][q.i-1].isBlank || game.maze.map[q.j][q.i-1].isDot)
				{
					
					Node leftNeighbour = new Node(q.i-1, q.j, q.g+1);
					temp.add(leftNeighbour);
				}
			}catch(ArrayIndexOutOfBoundsException | NullPointerException e)
			{
				System.out.println(ghost.name + ": Can't find a path outside the maze bounds!");
			}
			
			// 4. Check to see if the node already exists in the closed list
			// If so, compare G values
			// If current G < existing G, update the parent
			for(int i = 0; i < temp.size(); i++)
			{
				if(checkList(temp.get(i), closed) != null) // Check if node is already on the closed list
				{
					Node prior = new Node(0, 0, 0);
					prior = checkList(temp.get(i), closed);
					if(temp.get(i).g < prior.g)
					{
						closed.remove(getIndex(prior, closed));
						temp.get(i).calculateH(ti, tj);
						temp.get(i).calculateF(ti, tj);
						temp.get(i).setParent(q);
						open.add(temp.get(i));
					}
				}
				
				else
				{
					temp.get(i).calculateH(ti, tj);
					temp.get(i).calculateF(ti, tj);
					temp.get(i).setParent(q);
					open.add(temp.get(i));
				}					
			}
			temp.clear();
			
		}
		return null;
	}
	
	private Stack<Node> constructPath(Node n)
	{
		Stack<Node> path = new Stack<Node>();
		//ArrayList<Node> path = new ArrayList<Node>();
		while(n.parent != null)
		{
			path.push(n);
			n = n.parent;
		}
		path.push(n);
		return path;
	}

	private Node checkList(Node node, ArrayList<Node> list)
	{
		for(int i = 0; i < list.size(); i++)
			if(node.i == list.get(i).i && node.j == list.get(i).j)
				return list.get(i);
		return null;
	}
	
	private int getIndex(Node node, ArrayList<Node> list)
	{
		for(int i = 0; i < list.size(); i++)
			if(node.i == list.get(i).i && node.j == list.get(i).j)
				return list.indexOf(list.get(i));
		return -1;
	}
	
	private boolean isGoal(Node node)
	{
		if(node.i == ti && node.j == tj)
		{
			return true;
		}
			
		return false;
	}
	
	private int[] getSpaceBehind()
	{
		if(ghost.getDirection() == "right")
		{
			int[] temp = {ghost.i-1, ghost.j};
			return temp;
		}
		else if(ghost.getDirection() == "down")
		{
			int[] temp = {ghost.i, ghost.j-1};
			return temp;
		}
		else if(ghost.getDirection() == "left")
		{
			int[] temp = {ghost.i+1, ghost.j};
			return temp;
		}
		else
		{
			int[] temp = {ghost.i, ghost.j+1};
			return temp;
		}
	}
}
