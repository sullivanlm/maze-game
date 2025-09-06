/**
 * Write a description of class MazeBoard here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

import csteutils.myro.*; 
import java.awt.*; 
import java.util.ArrayList; 
import java.util.Scanner; 

public class MazeBoard
{
    private int[][] board = new int[0][0];
    private ArrayList<int[]> fronteir = new ArrayList<int[]>(); 
    private ArrayList<int[]> maze = new ArrayList<int[]>();
    private ArrayList<MyroRectangle[]> recs = new ArrayList<MyroRectangle[]>(); 
    private MyroCanvas canvas = new MyroCanvas("canvas", 600,600);
    
    //Two contrcutors, one to randomize the size of the board, another the specifically set the size of the board
    public MazeBoard(int width){
        board = new int[width][width];
    }

    public MazeBoard(){
        int width = (int)(Math.random()*31+20); 
        board = new int[width][width];
    }

    //Uses a Prim's simplified algorithm to create a guaranteed maze without loop or circular paths
    public void randomizeWalls(){
        //selects a rondom cell to start the maze
        int randomY = (int) (Math.random()*board.length); 
        int randomX = (int)(Math.random()*board[0].length); 

        int [] startCoor = {randomY, randomX};
        board[startCoor[0]][startCoor[1]] = 1;
        maze.add(startCoor); 
        
        //direction will be used both to map potential neighbors and check if the neighbors are valid
        int [][] directions = { {0,1}, {-1,0}, {1,0}, {0,-1} };
        addFronteir(directions, startCoor); 
        while(!fronteir.isEmpty()){
            //randomly selected fronteirs become neighbors. They are removed from the fronteir list
            //The maze is completed when there are no longer any fronteirs (potential neighbors) in the fronteir list
            int [] neighbor = fronteir.remove((int)(Math.random()*fronteir.size())); 
            int openCells =0; 
            
            
            for(int x = 0; x<directions.length; x++){
                //Check the adjacent cells of the neighbor to see in the cell is valid to become part of the path
                //The cell is only valid to become part of the maze if it only has one adjacent cell that is part of the path already
                //prevents loops or boxed sections from being made
                if((neighbor[0] + directions[x][0]>=0) && (neighbor[0] + directions[x][0]< board.length) && (neighbor[1]+directions[x][1]>=0)&& (neighbor[1]+directions[x][1]<board[0].length))
                {
                    int [] adjacent = {neighbor[0] + directions[x][0], neighbor[1]+directions[x][1]}; 
                    if(board[adjacent[0]][adjacent[1]] ==1){
                        openCells +=1; 
                    }
                }   
            }

            if(openCells ==1){
                maze.add(neighbor);
                board[neighbor[0]][neighbor[1]] = 1;
                //neighbor becomes the new main cell
                addFronteir(directions, neighbor); 
            }

        }
    }
    
    // A frontier is a cell located in any of the 4 directions of the current main cell (excluding diagnoal of course)
    // Has the pontential of becomnig part of the path if selected randomly and meet criteria
    // Fronteirs are only added when they exist in the grid space
    public void addFronteir(int[][] directions, int [] currentCell){
        for(int x = 0; x<directions.length; x++){
            if((currentCell[0] + directions[x][0]>=0) && (currentCell[0] + directions[x][0]< board.length) && (currentCell[1]+directions[x][1]>=0)&& (currentCell[1]+directions[x][1]<board[0].length))
            {
                fronteir.add(new int[] {currentCell[0] + directions[x][0], currentCell[1] + directions[x][1]}); 
            }   
        }
    }

    public void playMaze(){
        int [] startCell = new int[2];
        // The first open cell in the first row is the beginning position for the player to start playing
        // It is not always guaranteed that the start position will be at 0,0
        for(int x = 0; x<board[0].length; x++){
            if(board[0][x]==1){
                startCell[1] = x; 
                startCell[0] = 0; 
                MyroRectangle [] row = recs.get(0); 
                MyroRectangle rec = row[x]; 
                rec.makeFilled();
                rec.setFillColor(new Color(0,255,0)); 
                rec.visible();
                break;
            }
        }

        Scanner keyListener = new Scanner(System.in);
        System.out.println("use the WASD keys to play the maze"); 
        boolean mazeSolved = false; 
        String key;
        
        while(mazeSolved==false){
             
            key = keyListener.nextLine();
            int xCell = startCell[1]; 
            int yCell = startCell[0]; 
            //changes the position of the cell based on the inputed direction move
            if(key.equalsIgnoreCase("w")){
                yCell--;  
            }
            else if(key.equalsIgnoreCase("s")){
                yCell++;
            }
            else if(key.equalsIgnoreCase("a")){
                xCell--;
            }
            else if(key.equalsIgnoreCase("d")){
                xCell++;
            }

            //makes sure the direction move results in the movement to a cell that is within the board and is a valid part of the maze
            if(yCell>=0 && yCell<board.length && xCell>=0 && xCell<board[0].length && board[yCell][xCell]!=0){
                MyroRectangle [] row = recs.get(yCell); 
                MyroRectangle rec = row[xCell]; 
                rec.makeFilled();
                rec.setFillColor(new Color(0,255,0)); 
                rec.visible(); 
                
                //removes color from the previous block which makes it easier to keep track of where your piece is
                MyroRectangle [] prevRow = recs.get(startCell[0]); 
                MyroRectangle prevRec = prevRow[startCell[1]]; 
                prevRec.setFillColor(new Color(255, 255, 255)); 
                
                startCell[0] = yCell; 
                startCell[1] = xCell; 
                
                canvas.repaint(); 
            }
            
            // To win the maze, you piece must be on the last collum and within 4 places of the bottom right corner
            // Also not guaranteed that the end position will be at width-1,width-1
            if(startCell[1]==getBoard().length-1 && startCell[0]>getBoard().length-3){
                System.out.println("Great Job! You  completed the maze!"); 
                mazeSolved =true; 
            }
        }

    }

    public int [][] getBoard(){
        return board; 
    }

    //displays the board using myroRectangles and the values of the board 2D array
    public void makeBoard(){
        int sideLength = 12; 
        int startingX = 0; 
        int startingY = 0; 
        for(int i = 0; i<board.length; i++){
            MyroRectangle[] recRow = new MyroRectangle[getBoard().length]; 
            for (int j = 0; j<board[0].length; j++){
                MyroRectangle rec = new MyroRectangle(canvas, startingX, startingY, 12, 12);
                recRow[j] = rec; 
                rec.makeFilled();
                if(board[i][j] == 1){
                    rec.setFillColor(new Color(255,255,255)); 
                }
                else if(board[i][j] ==0){
                    rec.setFillColor(new Color(0,0,0));
                }
                startingX +=12; 
                rec.visible(); 
            }
            recs.add(recRow); 
            startingX = 0; 
            startingY+=12; 
        }

    }
}