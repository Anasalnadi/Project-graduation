import com.sun.net.httpserver.Filter;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.JApplet;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Pacman extends JApplet {

    long titleTimer = -1;/*These timers are used to kill title game over, and victory screens after a set idle period (5 seconds)*/
    long timer = -1;
    
    Board b = new Board();/* Create a new board */
    
    Timer frameTimer;/* This timer is used to do request new frames be drawn*/
    
    boolean lose = false;
    
    Scanner s=new Scanner(System.in);
    
    static Scope scope[];
    static Point crossroad[];
    static String Visited[]=new String[41];//لفحص اذا قمنا بزيارة النقطة من قبل 
    static Pacman c;
    
    ArrayList<Character> AvaliableDir=new ArrayList();//for determin Available Direction
    boolean startgame=false;
    int Dis_Ghost1,Dis_Ghost2,Dis_Ghost3,Dis_Ghost4;
    
    public static void main(String[] args) {
        c = new Pacman();
        
        //===========================
        scope=new Scope[4];
        crossroad=new Point[41];//1 for virtual Point if p1 not crossroad
        InitalizeCrossroad();
        InitalizeScope();
        //===========================
    }

    public Pacman() {
        JFrame f = new JFrame();
        f.setSize(420, 460);
        f.add(b, BorderLayout.CENTER);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        f.setResizable(false);

        /* Create a timer that calls stepFrame every 30 milliseconds */
        frameTimer = new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stepFrame(false);
            }
        });

        frameTimer.start();/* Start the timer */

        b.requestFocus();//run listener kayboard
        
        //===================================
        Timer movePlayer = new Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GenerationPlayerDirection();
            }
        });
        movePlayer.start();
        //====================================
    }

    public void stepFrame(boolean New) {
        //when player die
        if (b.stopped) {
            b.repaint();
            b.stopped = false;
            lose = true;
            reset();

            return;
        }

        /* run game */
        if (!b.stopped) {//b.stopped=true whin player is dead
            repaint();
        }

    }//stepFrame

    public void repaint() {
        b.repaint(0, 0, 600, 20);
        b.repaint(0, 420, 600, 40);
    }
    
    public void GenerationPlayerDirection(){
       if(!startgame){
           startgame=!startgame;
           Move1Step('T');//to start game and hide title screen
       }else{
        Random r = new Random();
        availableDirection();//add char avaliable diriction to (( AvaliableDir ))
        
        //==============================
        Point nearerPellet=new Point(-1,-1);
        int nearerPelletDis=9999;//
        for(int i=0;i<b.pellets.length-1;i++){
            for(int j=0;j<b.pellets[i].length-1;j++){
                if(b.pellets[i][j]){//return true if pellet not eat yet
                    int getDis=Dijkstra(new Point(b.player.x,b.player.y),new Point((i+1)*20,(j+1)*20));
                    if(nearerPelletDis>getDis){
                       nearerPelletDis=getDis;
                       nearerPellet.x=(i+1)*20;
                       nearerPellet.y=(j+1)*20;
                    }
                }
            }
        }
        //==========================================
        
        double percentage[]=new double[AvaliableDir.size()];//تحديد نسبة افضلية كل اتجاه متاح

        for(int i=0;i<AvaliableDir.size();i++){//set percentage by asking "What if i move in __ Direction "
            Dis_Ghost1=Dijkstra(expectPlayerPosition(AvaliableDir.get(i)),new Point(b.ghost1.x,b.ghost1.y));
            Dis_Ghost2=Dijkstra(expectPlayerPosition(AvaliableDir.get(i)),new Point(b.ghost2.x,b.ghost2.y));
            Dis_Ghost3=Dijkstra(expectPlayerPosition(AvaliableDir.get(i)),new Point(b.ghost3.x,b.ghost3.y));
            Dis_Ghost4=Dijkstra(expectPlayerPosition(AvaliableDir.get(i)),new Point(b.ghost4.x,b.ghost4.y));
            percentage[i]=((Dis_Ghost1)*(Dis_Ghost2)*(Dis_Ghost3)*(Dis_Ghost4))/100.0;
        }
        
        for(int i=0;i<AvaliableDir.size();i++){
            if(percentage[i]>100){
                int PelletDis=Dijkstra(expectPlayerPosition(AvaliableDir.get(i)),new Point(nearerPellet.x,nearerPellet.y));
                if(PelletDis<nearerPelletDis){
                    percentage[i]+=2000;
                }
            }
        }
        //==========================================
        int max=0;//max percentage
        int min=0;//min percentage
        ArrayList<Integer> sameDirPercentage=new ArrayList();
        sameDirPercentage.add(max);

        System.out.print(percentage[0]);
        for(int i=1;i<percentage.length;i++){
            if(percentage[i]>percentage[max]){
                sameDirPercentage=new ArrayList();
                max=i;
                sameDirPercentage.add(i);
            }else if(percentage[i]==percentage[max]){
                sameDirPercentage.add(i);
            }
            
            if(percentage[i]<percentage[min]){
                min=i;
            }
        }
        
            Move1Step(AvaliableDir.get(sameDirPercentage.get(r.nextInt(sameDirPercentage.size()))));
        //}
        AvaliableDir=new ArrayList();
       }
        
    }  

    public void Move1Step(char e) {
        /* Pressing a key in the title screen starts a game */
        if (b.titleScreen) {
            b.titleScreen = false;
            return;
        } /* Pressing a key in the win screen or game over screen goes to the title screen */ 
        else if (b.winScreen || b.overScreen) {
            b.titleScreen = true;
            b.winScreen = false;
            b.overScreen = false;
            return;
        }
        /* Pressing a key during a demo kills the demo mode and starts a new game */

        char desDer = e;
        b.player.desiredDirection = desDer;
        b.player.move();
 
        b.oops = b.DetectCollision(false);
        b.ghost1.move();
        b.ghost2.move();
        b.ghost3.move();
        b.ghost4.move();
        
        b.player.updatePellet();
        b.ghost1.updatePellet();
        b.ghost2.updatePellet();
        b.ghost3.updatePellet();
        b.ghost4.updatePellet();
        repaint();

    }

    public void reset() {
        System.out.println("Player is death");
    }
    
    public int Dijkstra(Point p1,Point p2){//Shortest distance player from Point p(x,y)
        //======== variables ===========
        int NumScopeP1=-1,NumScopeP2=-1;
        int NumPathP1=-1,NumPathP2=-1;
        int NumCrossroadP1=-1,NumCrossroadP2=-1;
        int NumIndexP1=-1,NumIndexP2=-1;
        Point startCrossroad=null,endCrossroad=null;
        Point virtual=null;
        int startPoint[]=new int[41];
        int dis[]=new int[41];
        ArrayList<Integer> ar=new ArrayList();//نضع بها النقاط التي يجب زيارة الرود بوينت لها     
        int ShortestPathDis;//the shourt path Distance
        //===============================
        
            InitalizeVisited();//set visited point to ""
        
            NumCrossroadP1=getNumCrossroad(p1);
            if(NumCrossroadP1==-1){
                //determin scope number for point 1
                NumScopeP1=getNumScope(p1);
                //determin path number for point 1
                NumPathP1=getNumPath(p1, NumScopeP1);
                //determin index number for point 1
                NumIndexP1=getNumIndex(p1, NumScopeP1,NumPathP1);
                
                NumCrossroadP1=40;
                crossroad[NumCrossroadP1]=p1;
                crossroad[NumCrossroadP1].roadPoints=new Point[2];
                crossroad[NumCrossroadP1].roadPoints[0]=scope[NumScopeP1].path[NumPathP1].startPoint;
                crossroad[NumCrossroadP1].roadPoints[1]=scope[NumScopeP1].path[NumPathP1].endPoint;
                crossroad[NumCrossroadP1].disCost=new int[2];
                crossroad[NumCrossroadP1].disCost[0]=NumIndexP1+1;
                crossroad[NumCrossroadP1].disCost[1]=scope[NumScopeP1].path[NumPathP1].arrPoint.length-NumIndexP1;
            }
            
            NumCrossroadP2=getNumCrossroad(p2);
            if(NumCrossroadP2==-1){
                //determin scope number for point 2
                NumScopeP2=getNumScope(p2);
                //determin path number for point 2
                NumPathP2=getNumPath(p2, NumScopeP2);
                //determin index number for point 1
                NumIndexP2=getNumIndex(p2, NumScopeP2,NumPathP2);
                
                setVisited(getNumCrossroad(scope[NumScopeP2].path[NumPathP2].startPoint),-1);  
                setVisited(getNumCrossroad(scope[NumScopeP2].path[NumPathP2].endPoint),-1); 
            }
        
            if(NumPathP1!=-1&&NumScopeP1==NumScopeP2&&NumPathP1==NumPathP2){//if the Point in same Path no need for Dikstra
                ShortestPathDis=Math.abs(NumIndexP1-NumIndexP2);
                //System.out.println("the Point In same Path and Dis = "+ShortestPathDis);
                return ShortestPathDis;
            }
        
        startPoint[NumCrossroadP1]=-1;
        dis[NumCrossroadP1]=0;
        ar.add(NumCrossroadP1);
        setVisited(NumCrossroadP1,-1); 

        while(!ar.isEmpty()){
            int p=ar.get(0);
            //System.out.println("at.get = "+p);
            for(int i=0;i<crossroad[p].roadPoints.length;i++){
                int numRoadPoint=getNumCrossroad(crossroad[p].roadPoints[i]);
                int RoadDisformStartPoint=dis[p]+crossroad[p].disCost[i];
                boolean checkPointagain=false;
                //System.out.println(numRoadPoint +" "+RoadDisformStartPoint);
                if(RoadDisformStartPoint<dis[numRoadPoint]||dis[numRoadPoint]==0&&NumCrossroadP1!=numRoadPoint){
                    if(RoadDisformStartPoint<dis[numRoadPoint]){
                        checkPointagain=true;
                    }
                    dis[numRoadPoint]=RoadDisformStartPoint;
                    startPoint[numRoadPoint]=p;
                    ar.add(numRoadPoint);
                }
                if(!isVisited(numRoadPoint,p)||numRoadPoint==NumCrossroadP2||checkPointagain){
                    ar.add(numRoadPoint); 
                    setVisited(numRoadPoint,p);
                }
            }//add and check road point
            ar.remove(0);
        }
       /* for(int i=0;i<crossroad.length;i++){
            System.out.print(i);
            for(int n=String.valueOf(i).length();n<=3;n++){
                System.out.print(" ");
            }
            
        }
        System.out.println();
        for(int i=0;i<startPoint.length;i++){
            System.out.print(startPoint[i]);
            for(int n=String.valueOf(startPoint[i]).length();n<=3;n++){
                System.out.print(" ");
            }
        }
        System.out.println();
        
        for(int i=0;i<dis.length;i++){
            System.out.print(dis[i]);
            for(int n=String.valueOf(dis[i]).length();n<=3;n++){
                System.out.print(" ");
            }
        }
        System.out.println();
        */
        if(NumIndexP2!=-1){
            int SPDSP=dis[getNumCrossroad(scope[NumScopeP2].path[NumPathP2].startPoint)];//ShortestPathDisStartPoint
            int SPDEP=dis[getNumCrossroad(scope[NumScopeP2].path[NumPathP2].endPoint)];//ShortestPathDisEndPoint
            ShortestPathDis=SPDSP+NumIndexP2+1<SPDEP+scope[NumScopeP2].path[NumPathP2].arrPoint.length-NumIndexP2?SPDSP+NumIndexP2+1:SPDEP+scope[NumScopeP2].path[NumPathP2].arrPoint.length-NumIndexP2;
            //System.out.println(SPDSP+" "+SPDEP);
            //System.out.println(SPDSP+NumIndexP2+1+" "+(SPDEP+scope[NumScopeP2].path[NumPathP2].arrPoint.length-NumIndexP2));
        }else{
            ShortestPathDis=dis[NumCrossroadP2];
        }
        
        //System.out.println("ShortestPathDis = "+ShortestPathDis);
        return ShortestPathDis;//return the Shorten the distance between the two points
    }
     
    public static void InitalizeScope(){
        Path p[];
        
        //=================  scope 0 ==========================
        p=new Path[6];
        Point arr1[]={new Point(80,20),new Point(60,20),new Point(40,20),new Point(20,20),new Point(20,40)};
        p[0]=new Path(new Point(100,20),new Point(20,60),arr1); 
        
        Point arr2[]={new Point(100,40)};
        p[1]=new Path(new Point(100,20),new Point(100,60),arr2); 
        
        Point arr3[]={new Point(120,20),new Point (140,20),new Point(160,20),new Point(180,20),new Point(180,40)};
        p[2]=new Path(new Point(100,20),new Point(180,60),arr3); 
        
        Point arr4[]={new Point(320,20),new Point(340,20),new Point(360,20),new Point(380,20),new Point(380,40)};
        p[3]=new Path(new Point(300,20),new Point(380,60),arr4); 
        
        Point arr5[]={new Point(300,40)};
        p[4]=new Path(new Point(300,20),new Point(300,60),arr5); 
        
        Point arr6[]={new Point(280,20),new Point(260,20),new Point(240,20),new Point(220,20),new Point(220,40)};
        p[5]=new Path(new Point(300,20),new Point(220,60),arr6); 
        
        
        scope[0]=new Scope(p);
        
        //=================  scope 1 ==========================
        
         p=new Path[20];
        Point arr7[]={new Point(40,60),new Point(60,60),new Point(80,60)};
        p[0]=new Path(new Point(20,60),new Point(100,60),arr7);
        
        Point arr8[]={new Point(20,80),new Point(20,100),new Point(40,100),new Point(60,100),new Point(80,100)};
        p[1]=new Path(new Point(20,60),new Point(100,100),arr8);
        
        Point arr9[]={new Point(120,60)};
        p[2]=new Path(new Point(100,60),new Point(140,60),arr9);
        
        Point arr10[]={new Point(100,80)};
        p[3]=new Path(new Point(100,60),new Point(100,100),arr10);
        
        Point arr11[]={new Point(160,60)};
        p[4]=new Path(new Point(140,60),new Point(180,60),arr11);
        
        Point arr12[]={new Point(140,80),new Point(140,100),new Point(160,100),new Point(180,100),new Point(180,120)};
        p[5]=new Path(new Point(140,60),new Point(180,140),arr12);
        
        Point arr13[]={new Point(200,60)};
        p[6]=new Path(new Point(180,60),new Point(220,60),arr13);
        
        Point arr14[]={new Point(240,60)};
        p[7]=new Path(new Point(220,60),new Point(260,60),arr14);
        
        Point arr15[]={new Point(280,60)};
        p[8]=new Path(new Point(260,60),new Point(300,60),arr15);
        
        Point arr16[]={new Point(260,80),new Point(260,100),new Point(240,100),new Point(220,100),new Point(220,120)};
        p[9]=new Path(new Point(260,60),new Point(220,140),arr16);
        
        Point arr17[]={new Point(320,60),new Point(340,60),new Point(360,60)};
        p[10]=new Path(new Point(300,60),new Point(380,60),arr17);
        
        Point arr18[]={new Point(300,80)};
        p[11]=new Path(new Point(300,60),new Point(300,100),arr18);
        
        Point arr19[]={new Point(380,80),new Point(380,100),new Point(360,100),new Point(340,100),new Point(320,100)};
        p[12]=new Path(new Point(380,60),new Point(300,100),arr19);
        
        Point arr20[]={new Point(100,120),new Point(100,140),new Point(100,160)};
        p[13]=new Path(new Point(100,100),new Point(100,180),arr20);
        
        Point arr21[]={new Point(300,120),new Point(300,140),new Point(300,160),};
        p[14]=new Path(new Point(300,100),new Point(300,180),arr21);
        
        Point arr22[]={};
        p[15]=new Path(new Point(180,140),new Point(200,140),arr22);
        
        Point arr23[]={new Point(160,140),new Point(140,140),new Point(140,160)};
        p[16]=new Path(new Point(180,140),new Point(140,180),arr23);
        
        Point arr24[]={};
        p[17]=new Path(new Point(200,140),new Point(220,140),arr24);
        
        Point arr25[]={new Point(200,160)};
        p[18]=new Path(new Point(200,140),new Point(200,180),arr25);
        
        Point arr26[]={new Point(240,140),new Point(260,140),new Point(260,160)};
        p[19]=new Path(new Point(220,140),new Point(260,180),arr26);
        
        scope[1]=new Scope(p);
        
        //=================  scope 2 ==========================
        
        p=new Path[13];
        Point arr27[]={new Point(40,180),new Point(60,180),new Point(80,180)};
        p[0]=new Path(new Point(20,180),new Point(100,180),arr27);
        
        Point arr28[]={new Point(120,180)};
        p[1]=new Path(new Point(100,180),new Point(140,180),arr28);
        
        Point arr29[]={new Point(100,200),new Point(100,220),new Point(100,240)};
        p[2]=new Path(new Point(100,180),new Point(100,260),arr29);
        
        Point arr30[]={new Point(140,200)};
        p[3]=new Path(new Point(140,180),new Point(140,220),arr30);
        
        Point arr31[]={};
        p[4]=new Path(new Point(180,180),new Point(200,180),arr31);
        
        Point arr32[]={};
        p[5]=new Path(new Point(200,180),new Point(220,180),arr32);
        
        Point arr33[]={new Point(260,200)};
        p[6]=new Path(new Point(260,180),new Point(260,220),arr33);
        
        Point arr34[]={new Point(280,180)};
        p[7]=new Path(new Point(260,180),new Point(300,180),arr34);
        
        Point arr35[]={new Point(320,180),new Point(340,180),new Point(360,180)};
        p[8]=new Path(new Point(300,180),new Point(380,180),arr35);
        
        Point arr36[]={new Point(300,200),new Point(300,220),new Point(300,240)};
        p[9]=new Path(new Point(300,180),new Point(300,260),arr36);
        
        Point arr37[]={new Point(140,240)};
        p[10]=new Path(new Point(140,220),new Point(140,260),arr37);
        
        Point arr38[]={new Point(160,220),new Point(180,220),new Point(200,220),new Point(220,220),new Point(240,220)};
        p[11]=new Path(new Point(140,220),new Point(260,220),arr38);
        
        Point arr39[]={new Point(260,240)};
        p[12]=new Path(new Point(260,220),new Point(260,260),arr39);
        
        scope[2]=new Scope(p);
        
        //=================  scope 3 ==========================
        
        p=new Path[20];
        Point arr40[]={new Point(80,260),new Point(60,260),new Point(40,260)
                ,new Point(20,260),new Point(20,280),new Point(20,300),new Point(40,300),new Point(60,300),new Point(60,320)};
        
        p[0]=new Path(new Point(100,260),new Point(60,340),arr40);
        
        Point arr41[]={new Point(100,280)};
        p[1]=new Path(new Point(100,260),new Point(100,300),arr41);
        
        Point arr42[]={new Point(120,260)};
        p[2]=new Path(new Point(100,260),new Point(140,260),arr42);
        
        Point arr43[]={new Point(160,260),new Point(180,260),new Point(180,280)};
        p[3]=new Path(new Point(140,260),new Point(180,300),arr43);
        
        Point arr44[]={new Point(240,260),new Point(220,260),new Point(220,280)};
        p[4]=new Path(new Point(260,260),new Point(220,300),arr44);
        
        Point arr45[]={new Point(280,260)};
        p[5]=new Path(new Point(260,260),new Point(300,260),arr45);
        
        Point arr46[]={new Point(300,280)};
        p[6]=new Path(new Point(300,260),new Point(300,300),arr46);
        
        Point arr47[]={new Point(320,260),new Point(340,260),new Point(360,260),new Point(380,260),new Point(380,280)
                ,new Point(380,300),new Point(360,300),new Point(340,300),new Point(340,320)};
        
        p[7]=new Path(new Point(300,260),new Point(340,340),arr47);
        
        Point arr48[]={new Point(100,320),new Point(100,340),new Point(80,340)};
        p[8]=new Path(new Point(100,300),new Point(60,340),arr48);
        
        Point arr49[]={new Point(120,300)};
        p[9]=new Path(new Point(100,300),new Point(140,300),arr49);
        
        Point arr50[]={new Point(160,300)};
        p[10]=new Path(new Point(140,300),new Point(180,300),arr50);
        
        Point arr51[]={new Point(140,320),new Point(140,340),new Point(160,340),new Point(180,340),new Point(180,360)};
        p[11]=new Path(new Point(140,300),new Point(180,380),arr51);
        
        Point arr52[]={new Point(200,300)};
        p[12]=new Path(new Point(180,300),new Point(220,300),arr52);
        
        Point arr53[]={new Point(240,300)};
        p[13]=new Path(new Point(220,300),new Point(260,300),arr53);
        
        Point arr54[]={new Point(280,300)};
        p[14]=new Path(new Point(260,300),new Point(300,300),arr54);
        
        Point arr55[]={new Point(260,320),new Point(260,340),new Point(240,340),new Point(220,340),new Point(220,360)};
        p[15]=new Path(new Point(260,300),new Point(220,380),arr55);
        
        Point arr56[]={new Point(300,320),new Point(300,340),new Point(320,340)};
        p[16]=new Path(new Point(300,300),new Point(340,340),arr56);
        
        Point arr57[]={new Point(40,340),new Point(20,340),new Point(20,360),new Point(20,380),new Point(40,380),
            new Point(60,380),new Point(80,380),new Point(100,380),new Point(120,380),new Point(140,380),new Point(160,380)};
        p[17]=new Path(new Point(60,340),new Point(180,380),arr57);
        
        Point arr58[]={new Point(360,340),new Point(380,340),new Point(380,360),new Point(380,380),new Point(360,380)
            ,new Point(340,380),new Point(320,380),new Point(300,380),new Point(280,380),new Point(260,380),new Point(240,380)};
        p[18]=new Path(new Point(340,340),new Point(220,380),arr58);
        
        Point arr59[]={new Point(200,380)};
        p[19]=new Path(new Point(180,380),new Point(220,380),arr59);
        
        scope[3]=new Scope(p);
        
    }
    
    public static void InitalizeCrossroad(){
        crossroad[0]=new Point(100,20);crossroad[0].roadPoints=new Point[3];crossroad[0].disCost=new int[3];
        crossroad[1]=new Point(300,20);crossroad[1].roadPoints=new Point[3];crossroad[1].disCost=new int[3];
        crossroad[2]=new Point(20,60);crossroad[2].roadPoints=new Point[3];crossroad[2].disCost=new int[3];
        crossroad[3]=new Point(100,60);crossroad[3].roadPoints=new Point[4];crossroad[3].disCost=new int[4];
        crossroad[4]=new Point(140,60);crossroad[4].roadPoints=new Point[3];crossroad[4].disCost=new int[3];
        crossroad[5]=new Point(180,60);crossroad[5].roadPoints=new Point[3];crossroad[5].disCost=new int[3];
        crossroad[6]=new Point(220,60);crossroad[6].roadPoints=new Point[3];crossroad[6].disCost=new int[3];
        crossroad[7]=new Point(260,60);crossroad[7].roadPoints=new Point[3];crossroad[7].disCost=new int[3];
        crossroad[8]=new Point(300,60);crossroad[8].roadPoints=new Point[4];crossroad[8].disCost=new int[4];
        crossroad[9]=new Point(380,60);crossroad[9].roadPoints=new Point[3];crossroad[9].disCost=new int[3];
        crossroad[10]=new Point(100,100);crossroad[10].roadPoints=new Point[3];crossroad[10].disCost=new int[3];
        crossroad[11]=new Point(300,100);crossroad[11].roadPoints=new Point[3];crossroad[11].disCost=new int[3];
        crossroad[12]=new Point(180,140);crossroad[12].roadPoints=new Point[3];crossroad[12].disCost=new int[3];
        crossroad[13]=new Point(200,140);crossroad[13].roadPoints=new Point[3];crossroad[13].disCost=new int[3];
        crossroad[14]=new Point(220,140);crossroad[14].roadPoints=new Point[3];crossroad[14].disCost=new int[3];
        crossroad[15]=new Point(20,180);crossroad[15].roadPoints=new Point[1];crossroad[15].disCost=new int[1];
        crossroad[16]=new Point(100,180);crossroad[16].roadPoints=new Point[4];crossroad[16].disCost=new int[4];
        crossroad[17]=new Point(140,180);crossroad[17].roadPoints=new Point[3];crossroad[17].disCost=new int[3];
        crossroad[18]=new Point(180,180);crossroad[18].roadPoints=new Point[1];crossroad[18].disCost=new int[1];
        crossroad[19]=new Point(200,180);crossroad[19].roadPoints=new Point[3];crossroad[19].disCost=new int[3];
        crossroad[20]=new Point(220,180);crossroad[20].roadPoints=new Point[1];crossroad[20].disCost=new int[1];
        crossroad[21]=new Point(260,180);crossroad[21].roadPoints=new Point[3];crossroad[21].disCost=new int[3];
        crossroad[22]=new Point(300,180);crossroad[22].roadPoints=new Point[4];crossroad[22].disCost=new int[4];
        crossroad[23]=new Point(380,180);crossroad[23].roadPoints=new Point[1];crossroad[23].disCost=new int[1];
        crossroad[24]=new Point(140,220);crossroad[24].roadPoints=new Point[3];crossroad[24].disCost=new int[3];
        crossroad[25]=new Point(260,220);crossroad[25].roadPoints=new Point[3];crossroad[25].disCost=new int[3];
        crossroad[26]=new Point(100,260);crossroad[26].roadPoints=new Point[4];crossroad[26].disCost=new int[4];
        crossroad[27]=new Point(140,260);crossroad[27].roadPoints=new Point[3];crossroad[27].disCost=new int[3];
        crossroad[28]=new Point(260,260);crossroad[28].roadPoints=new Point[3];crossroad[28].disCost=new int[3];
        crossroad[29]=new Point(300,260);crossroad[29].roadPoints=new Point[4];crossroad[29].disCost=new int[4];
        crossroad[30]=new Point(100,300);crossroad[30].roadPoints=new Point[3];crossroad[30].disCost=new int[3];
        crossroad[31]=new Point(140,300);crossroad[31].roadPoints=new Point[3];crossroad[31].disCost=new int[3];
        crossroad[32]=new Point(180,300);crossroad[32].roadPoints=new Point[3];crossroad[32].disCost=new int[3];
        crossroad[33]=new Point(220,300);crossroad[33].roadPoints=new Point[3];crossroad[33].disCost=new int[3];
        crossroad[34]=new Point(260,300);crossroad[34].roadPoints=new Point[3];crossroad[34].disCost=new int[3];
        crossroad[35]=new Point(300,300);crossroad[35].roadPoints=new Point[3];crossroad[35].disCost=new int[3];
        crossroad[36]=new Point(60,340);crossroad[36].roadPoints=new Point[3];crossroad[36].disCost=new int[3];
        crossroad[37]=new Point(340,340);crossroad[37].roadPoints=new Point[3];crossroad[37].disCost=new int[3];
        crossroad[38]=new Point(180,380);crossroad[38].roadPoints=new Point[3];crossroad[38].disCost=new int[3];
        crossroad[39]=new Point(220,380);crossroad[39].roadPoints=new Point[3];crossroad[39].disCost=new int[3];
        
        /*for(int i=0;i<crossroad.length;i++){
            crossroad[i].isCrossroad=true;
        }*/
        
    }
    
    public static void InitalizeVisited(){
        for(int i=0;i<Visited.length;i++){
            Visited[i]="";
        }
    }
    
    public int getNumScope(Point p){
        int NumScope;
        
        if(p.y>=20&&p.y<=40)
            NumScope=0;
        else if(p.y>=60&&p.y<=160)
            NumScope=1;
        else if(p.y>=180&&p.y<=240)
            NumScope=2;
        else
            NumScope=3;
        
        return NumScope;
    }
    
    public int getNumPath(Point p,int NumScope){
        int NumPath=-1;      
        boolean isSeparative=false;
        
        for(int i=0;i<scope[NumScope].path.length;i++){
            if(scope[NumScope].path[i].startPoint.isequals(p) || scope[NumScope].path[i].endPoint.isequals(p)){
                NumPath=i;
                break;
            }
            
            for(int j=0;j<scope[NumScope].path[i].arrPoint.length;j++){
                if(scope[NumScope].path[i].arrPoint[j].isequals(p)){
                    NumPath=i;
                    break;  
                }
            }
        }
        return NumPath;
    }
    
    public int getNumIndex(Point p,int NumScope,int NumPath){
        for(int i=0;i<scope[NumScope].path[NumPath].arrPoint.length;i++){
            if(scope[NumScope].path[NumPath].arrPoint[i].isequals(p)){
                return i;
            }
        }
        return -1;
    }
    
    public int getNumCrossroad(Point p){
        for(int i=0;i<crossroad.length-1;i++){
            if (crossroad[i].isequals(p)){
                return i;
            }
        }
        return -1;
    }
    
    public void setVisited(int NumCrossRoad ,int VisitedFrom){
        Visited[NumCrossRoad]+=VisitedFrom+",";
    }
    
    public boolean isVisited(int NumCrossRoad,int VisitedFrom){
        if(Visited[NumCrossRoad].startsWith("-1"))
            return true;
        else if(!Visited[NumCrossRoad].equals("")){
           String arr[]= Visited[NumCrossRoad].split(",");
           for(int i=0;i<arr.length;i++){
               if(Integer.parseInt(arr[i])==VisitedFrom){
                   return true;
               }
           }
        }
        return false;
    }

    public void availableDirection(){
        if ( Mover.isValidDest(b.player.x-b.player.incrementPlayer,b.player.y)){
            AvaliableDir.add('L');
        }

        if ( Mover.isValidDest(b.player.x+b.player.incrementPlayer,b.player.y)){
            AvaliableDir.add('R');
        }

        if ( Mover.isValidDest(b.player.x,b.player.y-b.player.incrementPlayer)){
            AvaliableDir.add('U');
        }

        if ( Mover.isValidDest(b.player.x,b.player.y+b.player.incrementPlayer)){
            AvaliableDir.add('D');
        }
    }
    
    public Point expectPlayerPosition(char c){
        switch (c) {
            case 'L':
                return new Point(b.player.x-b.player.incrementPlayer,b.player.y);
            case 'R':
                return new Point(b.player.x+b.player.incrementPlayer,b.player.y);
            case 'U':
                return new Point(b.player.x,b.player.y-b.player.incrementPlayer);
            default:
                return new Point(b.player.x,b.player.y+b.player.incrementPlayer);
        }
    }

}//class
