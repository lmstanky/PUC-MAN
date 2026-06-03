package mainPackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

@SuppressWarnings("unused")
public class map
{
	public static JLabel[][] layout;
	
	public map(int i, int j)
	{
		layout = new JLabel[i][j];
	}
	
	public static void populate(String s) throws NumberFormatException, IOException
	{
		int i;
		int j;
		InputStream in = map.class.getResourceAsStream("/PacMap.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String delim = "\\s+";
		int numRows = 31;
		int numCols = 29;
		
		for(i = 0; i < numRows; i++)
		{
			String line = br.readLine();
			String[] temp = line.split(delim);
			for(j = 0; j < numCols; j++)
				layout[i][j].setText(temp[j]);
				
		}
		
		for(i = 0; i < numRows; i++)
			for(j = 0; j < numCols; j++)
				if(layout[i][j].getText().equals("B"))
					layout[i][j].setText(" ");
				else if(layout[i][j].getText().equals("O"))
					layout[i][j].setText("\u25A0");
				else if(layout[i][j].getText().equals("P"))
					layout[i][j].setText("\u25CF");
		br.close();
	}
}
