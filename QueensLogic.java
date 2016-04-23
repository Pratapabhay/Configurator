
/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Stavros Amanatidis
 *
 */
import java.util.*;

import net.sf.javabdd.*;

public class QueensLogic {
    private int x = 0;
    private int y = 0;
    private int[][] board;
    private BDDFactory bddFact;
    private BDD bdd;
    private int boardSize;
    public int auto;
    public int counter = 0;
    
    
   
    public QueensLogic() {
       //constructor
    }

    public void initializeGame(int size) {
        this.x = size;
        this.y = size;
        this.board = new int[x][y];
        
        boardSize=size;
        auto=boardSize;
        
        buildBDD(size);
    }

    // Set up the Binary Decision Diagram using the BDD API
    public BDD buildBDD(int size)
    {
		bddFact = JFactory.init(20000000,200000);
		bddFact.setVarNum(size * size);
		bdd = bddFact.one();
		buildTheRules();
		return bdd;
    }
    
    
    // Make the rules for all of the cells on the game board
    public void buildTheRules()
    {
    	for (int j = 0; j < x; j++) {
        	BDD bdd_d = bddFact.zero();

        for (int i = 0; i < x; i++) {
         	bdd_d = bdd_d.or(this.bddFact.ithVar(j*boardSize+i));
        }
                this.bdd = this.bdd.and(bdd_d);
	}
        
    	for(int i=0; i < boardSize; i++)
    	{
    		for(int j = 0; j < boardSize; j++)
    		{
    			buildRule(i, j);
    		}
    	}
    }
    
    
    // Creates all the rules for the individual cell
    public void buildRule(int i, int j)
    {
    	BDD noneTrue = bddFact.one();
    	BDD bdd_d = bddFact.zero();
    	
    	// Check the column for other queens
    	for(int k=0; k < boardSize; k++)
    	{
    		if(!(k==i))
    			noneTrue = noneTrue.and(bddFact.nithVar(j * boardSize + k));
    	}
    	
    	// Check the row for other queens
    	for(int l=0; l < x; l++)
    	{
    		if(!(l==j))
    			noneTrue = noneTrue.and(bddFact.nithVar(l * boardSize + i));
    	}
    	
    	// Check diagonals for queens (first direction)
    	for(int m=0; m < x; m++)
    	{
    		if(!(m==i))
    		{
    			if((j+m-i < boardSize) && (j+m-i > 0))
    				{noneTrue = noneTrue.and(this.bddFact.nithVar((j+m-i)*x+m));}
    		}		
    	}
    	
    	// Check diagonals for queens (second direction)
    	for(int n=0; n < x; n++)
    	{
    		if(!(n==i))
    		{
    			if((j-n+i < x) && (j-n+i > 0))
    				{noneTrue = noneTrue.and(this.bddFact.nithVar((j-n+i)*x+n));}
    		}	
    	} 
    	  	
    	bdd_d = bdd_d.or(this.bddFact.nithVar(j*boardSize+i));
    	bdd_d = bdd_d.or(noneTrue);
    	this.bdd = this.bdd.and(bdd_d);
    }
    
    
   // Update the spots on the game board that are no longer valid
   public void updateGameBoard()
   {
	   for(int i = 0; i < boardSize; i++)
	   {
		   for(int j=0; j < boardSize; j++)
		   {
			   if(bdd.restrict(bddFact.ithVar(j*boardSize + i)).equals(bddFact.zero()))
				   board[i][j]= -1;				   
		   }
	   }
	   	   
   }
    
   
    public int[][] getGameBoard() {
        return board;
    }
	
	// Method for inserting a New Queen at a cell
    public boolean insertQueen(int column, int row) {

        if (board[column][row] == -1 || board[column][row] == 1) {
            return true;
        }
        
        board[column][row] = 1;
        --auto;
        
        // put some logic here..
        this.bdd = this.bdd.restrict(this.bddFact.ithVar(row*boardSize+column));
        updateGameBoard();
        
        // For counting number of valid cells remaining
 	
 	for(int i=0; i < boardSize; i++)
    	{
    		for(int j = 0; j < boardSize; j++)
    		{
    			if (board[i][j] == 0)
    			counter=counter+1;
    		}
    	} 
    	
    	// Auto filling if number of valid cells equals number of Queens remaining
    	
    	if(auto==counter)
    	{	
    		for(int i=0; i < boardSize; i++)
    		{
    			for(int j = 0; j < boardSize; j++)
    			{
    				if (board[i][j] == 0)
    				{	
    					board[i][j] = 1;
    					this.bdd = this.bdd.restrict(this.bddFact.ithVar(j*boardSize+i));
    					updateGameBoard();
    				}
    			}
    		}
    	}      
        counter=0;
        
      
        return true;
    }
    
    
    
}
