import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Game {

    public static final int PIPE_DELAY = 100;

    public static int pipeDelay;

    public static Bird bird[];
    
    public static ArrayList<Pipe> pipes;
    public static Boolean started;

    public Game() {
        restart();
    }

    public static void restart() {
        started = false;
        pipeDelay = 0;
        bird= new Bird[App.numBird];
        for(int i=0;i<App.numBird;i++){
            bird[i]=new Bird();
        }
        pipes = new ArrayList<Pipe>();
    }

    public void update() {

        if (!started)
            return;

        for(int i=0;i<App.numBird;i++){
           bird[i].update();
        }

        movePipes();
        checkForCollisions();
    }

    public ArrayList<Render> getRenders() {
        ArrayList<Render> renders = new ArrayList<Render>();
        renders.add(new Render(0, 0, "lib/background.png"));
        for (Pipe pipe : pipes){
            renders.add(pipe.getRender());  
        }
        
        renders.add(new Render(0, 0, "lib/foreground.png"));
        
        for(int i=0;i<App.numBird;i++){
             renders.add(bird[i].getRender("lib/bird"+((i%10)+1)+".png"));  
        }
        
        return renders;
    }

    private void movePipes() {
        pipeDelay--;

        if (pipeDelay < 0) {
            pipeDelay = PIPE_DELAY;
            Pipe northPipe = null;
            Pipe southPipe = null;

            // Look for pipes off the screen
            for (Pipe pipe : pipes) {
                if (pipe.x - pipe.width < 0) {
                    if (northPipe == null) {
                        northPipe = pipe;
                    } else if (southPipe == null) {
                        southPipe = pipe;
                        break;
                    }
                }
            }

            if (northPipe == null) {
                Pipe pipe = new Pipe("north");
                pipes.add(pipe);
                northPipe = pipe;
            } else {
                northPipe.reset();
            }

            if (southPipe == null) {
                Pipe pipe = new Pipe("south");
                pipes.add(pipe);
                southPipe = pipe;
            } else {
                southPipe.reset();
            }

            northPipe.y = southPipe.y + southPipe.height + 175;
        }

        for (Pipe pipe : pipes) {
            pipe.update();
        }
    }

    private void checkForCollisions() {

        for (Pipe pipe : pipes) {
             for(int i=0;i<App.numBird;i++){
                if (bird[i].dead==true|pipe.collides(bird[i].x, bird[i].y, bird[i].width, bird[i].height)) {
                    bird[i].dead = true;
                }else if(bird[i].y<=0||bird[i].y>=App.HEIGHT){
                    bird[i].dead = true;
                }else if (pipe.x == bird[i].x && pipe.orientation.equalsIgnoreCase("south")) {
                bird[i].score++;
                } 
                
                if(!bird[i].dead)
                    bird[i].distance++;
             }
        }

    }
}
