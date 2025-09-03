/**
 * Write a description of class MazeRunner here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */


public class MazeRunner
{
    public static void main(String[]args){
        MazeBoard maze = new MazeBoard(60); 
        maze.randomizeWalls();  
        maze.makeBoard(); 
        maze.playMaze(); 
    }
}
