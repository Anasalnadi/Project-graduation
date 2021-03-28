public class Point {
    int x;
    int y;
    Point roadPoints[];
    int disCost[];
    int i=0;
    Point(int x,int y){
        this.x=x;
        this.y=y;
    }
    
    public boolean isequals(Point p){
        if(this.x==p.x&&this.y==p.y)
            return true;
        
        return false;
    }
    
    public void addRoadAndDis(Point p,int dis){  
        roadPoints[i]=p;
        disCost[i]=dis;
        i++;
    }
}
