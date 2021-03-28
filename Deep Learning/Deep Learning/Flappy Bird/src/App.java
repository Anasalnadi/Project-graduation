
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;

public class App {

    public static int WIDTH = 500;
    public static int HEIGHT = 510;
    static int numBird = 10;
    static int countDeadBird;
    static boolean boolX = true;
    static boolean boolY = true;
    static int BirdWithMaxDis1=0;
    static int BirdWithMaxDis2=0;
    static Random r=new Random();
    public static void main(String[] args) {
        startgame();
        deepLearning();
    }

    public static void startgame() {
        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);//put location in middle
        GamePanel panel = new GamePanel();
        frame.add(panel);
        initialPopulation();
    }
    static int count;
    static TimerTask TT;
    static Timer T;
//------------------------------
    public static void deepLearning() {
        countDeadBird = 0;
        
        T=new Timer();
        TT=new TimerTask() {
            @Override
            public void run() {        
                int disx,disy;
                for(int i=0;i<numBird;i++){
                    disx=getDistance_X_FromPipe(Game.bird[i]);
                    disy=getDistance_Y_FromPipe(Game.bird[i]);
                    
                    
                    if(!Game.bird[i].dead & (Game.bird[i].DisXJump>=disx)
                    & (Game.bird[i].DisRangeYup<=disy)
                    & (Game.bird[i].DisRangeYdown>=disy)){
                        Game.bird[i].Jump();
                    }
                    
                    if(Game.bird[i].dead)
                        countDeadBird++;
                }
                
                if(countDeadBird==numBird)
                    restart();
                else
                    countDeadBird=0;
            }
        };
        
        Game.started = true;
        T.scheduleAtFixedRate(TT, 50, 1);
        
    }

    public static void restart() {
        T.cancel();
        Game.started = false;
        Game.pipeDelay = 0;
        
        selection();
        crossover(BirdWithMaxDis1,BirdWithMaxDis2);
        if(r.nextInt(10)<3)
            mutation();
        
        for(int i=0;i<numBird;i++){
            Game.bird[i].score=0;
            Game.bird[i].dead=false;
            Game.bird[i].y=150;
            Game.bird[i].yvel = 0;
            Game.bird[i].jumpDelay = 0;
            Game.bird[i].rotation = 0.0;
            Game.bird[i].distance=0;
        }
        Game.pipes = new ArrayList<Pipe>();
        deepLearning();
    }

    public static int getDistance_X_FromPipe(Bird b) {
        if ((Game.pipes.get(0).x - b.x > 0 && boolX) ||Game.pipes.size()==2) {
            return Game.pipes.get(0).x - b.x;
        } else if (Game.pipes.get(2).x - b.x > 0) {
            boolX = false;
            return Game.pipes.get(2).x - b.x;
        } else {
            boolX = true;
            getDistance_X_FromPipe(b);
        } 
        return 0;
    }

    static int h0, h1;

    public static int getDistance_Y_FromPipe(Bird b) {
        if ((Game.pipes.get(0).x > 0 && boolY) ||Game.pipes.size()==2) {
            h0 = Game.pipes.get(0).y;
            h1 = Game.pipes.get(1).y + Game.pipes.get(1).height;
        } else if (Game.pipes.get(2).x > 0) {
            boolY = false;
            h0 = Game.pipes.get(2).y;
            h1 = Game.pipes.get(3).y + Game.pipes.get(3).height;
        } else {
            boolY = true;
            getDistance_Y_FromPipe(b);
        }
        return b.y -((h0 + h1) / 2);
    }
    
    public static void initialPopulation(){
        for(int i=0;i<numBird;i++){
            Game.bird[i].DisXJump=r.nextInt(WIDTH);
            Game.bird[i].DisRangeYup=r.nextInt(HEIGHT)-HEIGHT/2;
            Game.bird[i].DisRangeYdown=r.nextInt(HEIGHT)-HEIGHT/2;
        }
    }// mnjhz albird
    
    public static void selection(){

        if(Game.bird[0].distance>Game.bird[1].distance){
            BirdWithMaxDis1=0;
            BirdWithMaxDis2=1;
        }else{
            BirdWithMaxDis1=1;
            BirdWithMaxDis2=0;
        }
        
        for(int i=2;i<numBird;i++){
            if(Game.bird[BirdWithMaxDis1].distance<Game.bird[i].distance){
                BirdWithMaxDis2=BirdWithMaxDis1;
                BirdWithMaxDis1=i;
            }else if(Game.bird[BirdWithMaxDis2].distance<Game.bird[i].distance){
                BirdWithMaxDis2=i;
            }
        }
    }//selection
    
    public static void crossover(int b1,int b2){
        Bird B1=Game.bird[b1];
        Bird B2=Game.bird[b2];
        Bird arr[]=new Bird[numBird];
        for(int i=0;i<numBird;i+=2){
            int Switch=r.nextInt(3);
            int operation=r.nextInt(2);// + -
            int DivBy2=r.nextInt(2)+1;
                Switch(i,B1,B2,Switch,operation,DivBy2);
                Switch(i+1,B2,B1,Switch,operation,DivBy2);
        }
    }//crossover
    
    public static void Switch(int i,Bird b1, Bird b2, int Switch, int operation, int DivBy2) {
        if (Switch == 0) {
            if (operation == 0) {
                Game.bird[i].DisXJump=(b1.DisXJump+b2.DisXJump)/DivBy2;
            }else{
                Game.bird[i].DisXJump=(b1.DisXJump-b2.DisXJump)/DivBy2;
            }
        } else if (Switch == 1) {
            if (operation == 0) {
                Game.bird[i].DisRangeYup=(b1.DisRangeYup+b2.DisRangeYup)/DivBy2;
            }else {
                Game.bird[i].DisRangeYup=(b1.DisRangeYup-b2.DisRangeYup)/DivBy2;
            }
        } else {
            if (operation == 0) {
                Game.bird[i].DisRangeYdown=(b1.DisRangeYdown+b2.DisRangeYdown)/DivBy2;
            }else{
                Game.bird[i].DisRangeYdown=(b1.DisRangeYdown-b2.DisRangeYdown)/DivBy2;
            }
        }
    }
    
    public static void mutation(){
            
            for(int i=0;i<numBird/4;i++){
                int RandomB= r.nextInt(numBird);
                int fetness=r.nextInt(2);
                if(fetness==0)
                    Game.bird[RandomB].DisXJump=r.nextInt(WIDTH);
                else{
                   Game.bird[RandomB].DisRangeYup=r.nextInt(HEIGHT)-HEIGHT/2;
                   Game.bird[RandomB].DisRangeYdown=r.nextInt(HEIGHT)-HEIGHT/2; 
                }
            }
        
    }
    
}
