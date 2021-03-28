public class Path {
    Point startPoint;
    Point endPoint;
    Point arrPoint[];
    
    Path(Point s,Point e,Point p[]){
        this.startPoint=s;
        this.endPoint=e;
        this.arrPoint=p;
        
        for(int i=0;i<Pacman.c.crossroad.length-1;i++){
            if(startPoint.isequals(Pacman.c.crossroad[i])){
                Pacman.c.crossroad[i].addRoadAndDis(this.endPoint,p.length+1);
                
            }else if(endPoint.isequals(Pacman.c.crossroad[i])){
                Pacman.c.crossroad[i].addRoadAndDis(this.startPoint,p.length+1);
                
            }
        }
        
    }
   
}
