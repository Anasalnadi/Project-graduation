import java.util.HashSet;
import java.util.Set;

/* Ghost class controls the ghost. */
public class Ghost extends Mover
{ 
  /* Direction ghost is heading */
  char direction;

  /* Last ghost location*/
  int lastX;
  int lastY;

  /* Current ghost location */
  int x;
  int y;

  /* The pellet the ghost is on top of */
  int pelletX,pelletY;

  /* The pellet the ghost was last on top of */
  int lastPelletX,lastPelletY;

  /*Constructor places ghost and updates states*/
  public Ghost(int x, int y)
  {
    direction='L';
    pelletX=x/gridSize-1;
    pelletY=x/gridSize-1;
    lastPelletX=pelletX;
    lastPelletY=pelletY;
    this.lastX = x;
    this.lastY = y;
    this.x = x;
    this.y = y;
  }

  /* update pellet status */
  public void updatePellet()
  {
    int tempX,tempY;
    tempX = x/gridSize-1;
    tempY = y/gridSize-1;
    if (tempX != pelletX || tempY != pelletY)
    {
      lastPelletX = pelletX;
      lastPelletY = pelletY;
      pelletX=tempX;
      pelletY = tempY;
    }
     
  } 
 
  /* Determines if the location is one where the ghost has to make a decision*/ 
  public boolean isChoiceDest()
  {
    if (  x%gridSize==0&& y%gridSize==0 )
    {
      return true;
    }
    return false;
  }

  /* Chooses a new direction randomly for the ghost to move */
  public char newDirection()
  { 
    int random;
    char backwards='U';
    int newX=x,newY=y;
    int lookX=x,lookY=y;
    Set<Character> set = new HashSet<Character>();
    switch(direction)
    {
      case 'L':
         backwards='R';
         break;     
      case 'R':
         backwards='L';
         break;     
      case 'U':
         backwards='D';
         break;     
      case 'D':
         backwards='U';
         break;     
    }

    char newDirection = backwards;
    /* While we still haven't found a valid direction */
    while (newDirection == backwards || !isValidDest(lookX,lookY))
    {
      /* If we've tried every location, turn around and break the loop */
      if (set.size()==3)
      {
        newDirection=backwards;
        break;
      }

      newX=x;
      newY=y;
      lookX=x;
      lookY=y;
      
      /* Randomly choose a direction */
      random = (int)(Math.random()*4) + 1;
      if (random == 1)
      {
        newDirection = 'L';
        newX-=incrementGhost; 
        lookX-= incrementGhost;
      }
      else if (random == 2)
      {
        newDirection = 'R';
        newX+=incrementGhost; 
        lookX+= gridSize;
      }
      else if (random == 3)
      {
        newDirection = 'U';
        newY-=incrementGhost; 
        lookY-=incrementGhost;
      }
      else if (random == 4)
      {
        newDirection = 'D';
        newY+=incrementGhost; 
        lookY+=gridSize;
      }
      if (newDirection != backwards)
      {
        set.add(new Character(newDirection));
      }
    } 
    return newDirection;
  }

  /* Random move function for ghost */
  public void move()
  {
    lastX=x;
    lastY=y;
 
    /* If we can make a decision, pick a new direction randomly */
    if (isChoiceDest())
    {
      direction = newDirection();
    }
    
    /* If that direction is valid, move that way */
    switch(direction)
    {
      case 'L':
         if ( isValidDest(x-incrementGhost,y))
           x -= incrementGhost;
         break;     
      case 'R':
         if ( isValidDest(x+gridSize,y))
           x+= incrementGhost;
         break;     
      case 'U':
         if ( isValidDest(x,y-incrementGhost))
           y-= incrementGhost;
         break;     
      case 'D':
         if ( isValidDest(x,y+gridSize))
           y+= incrementGhost;
         break;     
    }
  }
}