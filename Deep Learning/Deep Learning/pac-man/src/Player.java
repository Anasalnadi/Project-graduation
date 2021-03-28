
import java.util.HashSet;
import java.util.Set;

/* This is the pacman object */
public class Player extends Mover
{
  /* Direction is used in demoMode, currDirection and desiredDirection are used in non demoMode*/ 
  char direction;
  char currDirection;
  char desiredDirection;

  static boolean vaildDes=false;
  
  /* Keeps track of pellets eaten to determine end of game */
  int pelletsEaten;

  /* Last location */
  int lastX;
  int lastY;
 
  /* Current location */
  int x;
  int y;
 
  /* Which pellet the pacman is on top of */
  int pelletX;
  int pelletY;

  boolean teleport;/* teleport is true when travelling through the teleport tunnels*/
  
  /* Stopped is set when the pacman is not moving or has been killed */
  boolean stopped = false;

  /* Constructor places pacman in initial location and orientation */
  public Player(int x, int y)
  {

    teleport=false;
    pelletsEaten=0;
    pelletX = x/gridSize-1;
    pelletY = y/gridSize-1;
    this.lastX=x;
    this.lastY=y;
    this.x = x;
    this.y = y;
    currDirection='N'; //N-> mean No dirction 
    desiredDirection='N';//the desired Direction
  }

  /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
  public boolean isChoiceDest()
  {
    if (  x%gridSize==0&& y%gridSize==0 )
    {
      return true;
    }
    return false;
  }


  /* The move function moves the pacman for one frame in non demo mode */
  public void move()
  {
    vaildDes=false;
    int gridSize=20;
    lastX=x;
    lastY=y;
     
    /* Try to turn in the direction input by the user */
    /*Can only turn if we're in center of a grid*/
    if (x %20==0 && y%20==0 ||
       /* Or if we're reversing*/
       (desiredDirection=='L' && currDirection=='R')  ||
       (desiredDirection=='R' && currDirection=='L')  ||
       (desiredDirection=='U' && currDirection=='D')  ||
       (desiredDirection=='D' && currDirection=='U')
       )
    {
      switch(desiredDirection)
      {
        case 'L':
           if ( isValidDest(x-incrementPlayer,y)){
             x -= incrementPlayer;
           vaildDes=true;}
           break;     
        case 'R':
           if ( isValidDest(x+incrementPlayer,y)){
             x+= incrementPlayer;
           vaildDes=true;}
           break;     
        case 'U':
           if ( isValidDest(x,y-incrementPlayer)){
             y-= incrementPlayer;
           vaildDes=true;}
           break;     
        case 'D':
           if ( isValidDest(x,y+incrementPlayer)){
            y+= incrementPlayer;
            vaildDes=true;}
           break;     
      }
    }
    /* If we haven't moved, then move in the direction the pacman was headed anyway */
    if (lastX==x && lastY==y && vaildDes)
    {
      switch(currDirection)
      {
        case 'L':
           if ( isValidDest(x-incrementPlayer,y))
             x -= incrementPlayer;
           else if (y == 9*gridSize && x < 2 * gridSize)
           {
             x = max - gridSize*1;
             teleport = true; 
           }
           break;     
        case 'R':
           if ( isValidDest(x+gridSize,y))
             x+= incrementPlayer;
           else if (y == 9*gridSize && x > max - gridSize*2)
           {
             x = 1*gridSize;
             teleport=true;
           }
           break;     
        case 'U':
           if ( isValidDest(x,y-incrementPlayer))
             y-= incrementPlayer;
           break;     
        case 'D':
           if ( isValidDest(x,y+gridSize))
             y+= incrementPlayer;
           break;     
      }
    }

    /* If we did change direction, update currDirection to reflect that */
    else if(vaildDes)
    {
        
      currDirection=desiredDirection;
    }
   
    /* If we didn't move at all, set the stopped flag */    
    if (lastX == x && lastY==y)
      stopped=true;
  
    /* Otherwise, clear the stopped flag and increment the frameCount for animation purposes*/
    else
    {
      stopped=false;
      frameCount ++;
    }
  }

  /* Update what pellet the pacman is on top of */
  public void updatePellet()
  {
    if (x%gridSize ==0 && y%gridSize == 0)
    {
    pelletX = x/gridSize-1;
    pelletY = y/gridSize-1;
    }
  } 
}