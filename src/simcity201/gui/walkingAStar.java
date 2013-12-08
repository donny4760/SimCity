package simcity201.gui;

import Buildings.Building;
import agents.PassengerAgent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.swing.ImageIcon;

public class walkingAStar
{
      private Map<Integer,mapTile> tileNames= new HashMap<Integer, mapTile>();
      public mapTile[][] map=new mapTile[6][10];
      public Map<String,mapTile> buildingMap=new HashMap<String, mapTile>();
//      public static final int bankX=305;
//      public static final int bankY=40;
//      public static final int marketX=305;
//      public static final int marketY=445;
//      public static final int apartmentX=305;
//      public static final int apartmentY=755;
//      public static final int houseX=730;
//      public static final int houseY=40;
//      public static final int rest1X=730;
//      public static final int rest1Y=405;
//      public static final int rest2X=880;
//      public static final int rest2Y=405;
//      public static final int rest3X=1030;
//      public static final int rest3Y=405;
//      public static final int rest4X=730;
//      public static final int rest4Y=445;
//      public static final int rest5X=880;
//      public static final int rest5Y=445;
//      public static final int rest6X=1030;
//      public static final int rest6Y=445;
      
      
      
      public walkingAStar(){      
         int indexCounter=1;
         for(int i=0;i<6;i++){
               for(int j=0;j<10;j++){
                  int xCoordinate=0, yCoordinate=0;
                  
                  switch (i){
                  case 0:  yCoordinate=70;
                           break;
                  case 1:  yCoordinate=140;
                           break;
                  case 2:  yCoordinate=375;
                           break;
                  case 3:  yCoordinate=425;
                           break;
                  case 4:  yCoordinate=715;
                           break;
                  case 5:  yCoordinate=825;
                           break;
                  default: break;
                  }
                  
                  switch (j){
                  case 0:  xCoordinate=0;
                           break;
                  case 1:  xCoordinate=40;
                           break;
                  case 2:  xCoordinate=305;
                           break;
                  case 3:  xCoordinate=570;
                           break;
                  case 4:  xCoordinate=605;
                           break;
                  case 5:  xCoordinate=730;
                           break;
                  case 6:  xCoordinate=880;
                           break;
                  case 7:  xCoordinate=1030;
                           break;
                  case 8:  xCoordinate=1125;
                           break;
                  case 9:  xCoordinate=1165;
                           break;
                  default: break;
                  }
                  mapTile temp=new mapTile(xCoordinate,yCoordinate, false);
                  map[i][j]=temp;
               }
            }
         for(int i=0;i<6;i++){
            for(int j=0;j<10;j++){
               tileNames.put(indexCounter,map[i][j]);
               indexCounter++;
            }
         }
         
         buildingMap.put("Bank", map[1][2]);
         buildingMap.put("Market", map[2][2]);
         buildingMap.put("Apart", map[4][2]);
         buildingMap.put("House1", map[1][5]);
         buildingMap.put("House2", map[1][6]);
         buildingMap.put("House3", map[1][7]);
         buildingMap.put("Rest1", map[2][5]);
         buildingMap.put("Rest2", map[2][6]);
         buildingMap.put("Rest3", map[2][7]);
         buildingMap.put("Rest4", map[3][5]);
         buildingMap.put("Rest5", map[3][6]);
         buildingMap.put("Rest6", map[3][7]);
         
      }
      
   
   
   
   
   //GETTER/SETTERS
   public Map<Integer,mapTile> getTileNames(){
      return tileNames;
   }
   //END GETTER/SETTERS
   
   //HELPER FUNCTIONS
   public mapTile findMapTile(int x, int y){
      mapTile temp=new mapTile();
      for(int i=0;i<6;i++){
         for(int j=0;j<6;j++){
            if(map[i][j].xCoordinate==x && map[i][j].yCoordinate==y){
               temp=map[i][j];
               break;
            }
         }
      }
      return temp;
   }
   
   public int findIndex(mapTile tile){
      int index=0;
      for(int i=1;i<=60;i++){
         if(tileNames.get(i)==tile){
            index=i;
            break;
         }
      }
      return index;
   }
   
   public int findHScore(mapTile start, mapTile destination){
      int hScore=0;
      int startX=start.xCoordinate;
      int startY=start.yCoordinate;
      int endX=destination.xCoordinate;
      int endY=destination.yCoordinate;
      
      hScore=10*(Math.abs(startX-endX)+Math.abs(startY-endY));
      return hScore;
   
   }
   
   public void findSurroundingTiles(int tileIndex){
      mapTile current=tileNames.get(tileIndex);
      boolean up=true,down=true,left=true,right=true;
      if(tileIndex%6==1){
         left=false;
      }
      if(tileIndex%6==0){
         right=false;
      }
      if(tileIndex<=6){
         up=false;
      }
      if(tileIndex>=31){
         down=false;
      }
      
      if(left!=false){
         current.openList.add(tileNames.get(tileIndex-1));
      }
      if(right!=false){
         current.openList.add(tileNames.get(tileIndex-1));
      }
      if(up!=false){
         current.openList.add(tileNames.get(tileIndex-6));
      }
      if(up!=false){
         current.openList.add(tileNames.get(tileIndex+6));
      }
   }
   
   private mapTile calculateBestTile(mapTile current_, int endIndex){
      mapTile current=current_;
      mapTile destination=tileNames.get(endIndex);
      mapTile bestTile=new mapTile();
      int bestScore=0;
      for(int i=0;i<current.openList.size();i++){
         int currentScore=findHScore(current.openList.get(i),destination);
         if(currentScore>bestScore){
            bestScore=currentScore;
            bestTile=current.openList.get(i);
         }
      }
      bestTile.parent=current;
      return bestTile;
   }
   
   //
   //MAIN FIND PATH FUNCTION
   public List<mapTile> findPath(mapTile start, mapTile destination){
      List<mapTile> path=new ArrayList<mapTile>();
      
      List<mapTile> openList=new ArrayList<mapTile>();
      List<mapTile> closedList=new ArrayList<mapTile>();
      
//      mapTile start=findMapTile(startX,startY);
//      mapTile destination=findMapTile(targetX,targetY);
      mapTile current=new mapTile();
      
      int startIndex=findIndex(start);      
      int endIndex=findIndex(destination);
      int currentIndex=0;
      
      currentIndex=startIndex;
      current=start;
      current.hScore=findHScore(current,destination);
      
      openList.add(current); //Add starting tile into openList
      
      if(start==destination){
         return path;
      }
      
      //System.out.println("Set current tile to starting tile");
      
      while(!closedList.contains(destination) || openList.isEmpty()){
         //Now check surrounding tiles and add to openList is accessible    
         //System.out.println("Current Tile's Score: "+current.hScore);
         
         for(int i=0;i<openList.size();i++){
            if(openList.get(i).hScore<current.hScore){
               current=openList.get(i);
               currentIndex=findIndex(current);
               openList.remove(current);
               closedList.add(current);
               //System.out.println("changed current tile to the one with best g score on the open list");
            }
         }
         
         if(tileNames.get(currentIndex-1)!=null){
            mapTile leftTile=tileNames.get(currentIndex-1);
            if(!openList.contains(leftTile) && !closedList.contains(leftTile)){
               leftTile.parent=current;
               leftTile.hScore=findHScore(leftTile,destination);
               openList.add(leftTile);
            }
         }
         if(tileNames.get(currentIndex+1)!=null){
            mapTile rightTile=tileNames.get(currentIndex+1);
            if(!openList.contains(rightTile) && !closedList.contains(rightTile)){
               rightTile.parent=current;
               rightTile.hScore=findHScore(rightTile,destination);
               openList.add(rightTile);
            }
         }
         if(tileNames.get(currentIndex-10)!=null){
            mapTile upTile=tileNames.get(currentIndex-10);
            if(!openList.contains(upTile) && !closedList.contains(upTile)){
               upTile.parent=current;
               upTile.hScore=findHScore(upTile,destination);
               openList.add(upTile);
            }
         }
         if(tileNames.get(currentIndex+10)!=null){
            mapTile downTile=tileNames.get(currentIndex+10);
            if(!openList.contains(downTile) && !closedList.contains(downTile)){
               downTile.parent=current;
               downTile.hScore=findHScore(downTile,destination);
               openList.add(downTile);
            }
         }
         
//         System.out.println("added all available adjacent tiles to open list with g score");
//         for(int i=0;i<openList.size();i++){
//          System.out.println("Tile: "+findIndex(openList.get(i))+"; hScore: "+openList.get(i).hScore);
//       }
         
         
//         System.out.println("removed current tile from open list and added to closed list");
//         System.out.println("OPEN LIST SIZE: "+openList.size());
//         System.out.println("ClOSED LIST SIZE: "+closedList.size());
      }
      
      mapTile temp=destination;
      while(temp!=start){
         path.add(temp);
         temp=temp.parent;
  //       System.out.println("added tile to the path");
      }
      
      path.add(start);
  //    System.out.println("traversed the path and added it to the path tile list"+"\n");
      Collections.reverse(path);
      return path;
      
      //startIndex=findIndex(start);
      //
      //endIndex=findIndex(destination);
      //
      //current=start;
      //currentIndex=startIndex;
      //
      //while(currentIndex!=endIndex){
      //   findSurroundingTiles(currentIndex);
      //   current.openList.remove(current.parent);
      //   current=calculateBestTile(current,endIndex);
      //   currentIndex=findIndex(current);
      //}
      //
      //while(current.parent!=null){
      //   start.path.add(current.parent);
      //   current=current.parent;
      //}
      //
      //for(int i=0;i<start.path.size();i++){
      //   start.tilePath.add(start.path.remove());
      //}
      //return start.tilePath;
   }
   
   public mapTile findClosestTile(int xCoordinate, int yCoordinate){
      double lowestDistance;
   
      int firstXDifference=xCoordinate-tileNames.get(1).xCoordinate;
      int firstYDifference=yCoordinate-tileNames.get(1).yCoordinate;
      double firstDistance=Math.sqrt( Math.pow(firstYDifference, 2)+Math.pow(firstYDifference, 2));
      
      lowestDistance=firstDistance;
      mapTile closestTile=tileNames.get(1);
      
      for(int i=2;i<=tileNames.size();i++){
         int xDifference=xCoordinate-tileNames.get(i).xCoordinate;
         int yDifference=yCoordinate-tileNames.get(i).yCoordinate;
         double currentDistance=Math.sqrt( Math.pow(xDifference, 2)+Math.pow(yDifference, 2));
         if(currentDistance<lowestDistance){
            lowestDistance=currentDistance;
            closestTile=tileNames.get(i);
         }
      }
      return closestTile;
     
   }
   public List<mapTile> testSearch(mapTile tile, mapTile destination){
      List<mapTile> openList=new ArrayList<mapTile>();
      List<mapTile> closedList=new ArrayList<mapTile>();
            
      int currentIndex=findIndex(tile);
      mapTile current=tile;
      
      
      if(tileNames.get(currentIndex-1)!=null){
         mapTile leftTile=tileNames.get(currentIndex-1);
         openList.add(leftTile);
         leftTile.parent=current;
         leftTile.hScore=findHScore(leftTile,destination);
      }
      if(tileNames.get(currentIndex+1)!=null){
         mapTile rightTile=tileNames.get(currentIndex+1);
         openList.add(rightTile);
         rightTile.parent=current;
         rightTile.hScore=findHScore(rightTile,destination);
      }
      if(tileNames.get(currentIndex-9)!=null){
         mapTile upTile=tileNames.get(currentIndex-9);
         openList.add(upTile);
         upTile.parent=current;
         upTile.hScore=findHScore(upTile,destination);
      }
      if(tileNames.get(currentIndex+9)!=null){
         mapTile downTile=tileNames.get(currentIndex+9);
         openList.add(downTile);
         downTile.parent=current;
         downTile.hScore=findHScore(downTile,destination);
      }
      
      return openList;
   }
   //TILE MAP 
   public static void main (String[] args){
      walkingAStar simCity=new walkingAStar();
      int indexCounter=0;
      List<mapTile> path=new ArrayList<mapTile>();
//      for(int i=0;i<6;i++){     
//         for(int j=0;j<6;j++){
////            System.out.println("Row: "+i+"; Column: "+j+"\n"+"xCoordinate: "+simCity.map[i][j].xCoordinate+" ; yCoordinate: "+simCity.map[i][j].yCoordinate);
//              System.out.println("Row: "+i+"; Column: "+j+"\n"+"Tile: "+simCity.getTileNames().get(indexCounter));
//              indexCounter++;
//         }
//      }
      
//      for(int i=0;i<6;i++){
//         for(int j=0;j<10;j++){
//            System.out.println("Tile Index: "+ simCity.findIndex(simCity.map[i][j]));
//         }
//      }
      
//      System.out.println(simCity.findMapTile(40,40).);
//      System.out.println(simCity.findMapTile(570,0));
//      simCity.findSurroundingTiles(3);
//      List<mapTile> openList_=simCity.getTileNames().get(3).openList;
//      for(int i=0;i<openList_.size();i++){
//         int index=simCity.findIndex(openList_.get(i));
//         System.out.println(index);
//      }
      
      
 //     int xPos=40,yPos=40;
//      mapTile start=simCity.findMapTile(xPos,yPos);
//      mapTile destination=simCity.buildingMap.get("Bank");
//      System.out.println("PATH 1:\n");
//      path=simCity.findPath(simCity.buildingMap.get("Bank"), simCity.buildingMap.get("Rest1"));
//      
//    for(int i=0;i<path.size();i++){
//       System.out.println("Tile: "+simCity.findIndex(path.get(i))+"; hScore: "+path.get(i).hScore);
//    }
//    System.out.println("PATH 2:\n");
//    path=simCity.findPath(simCity.buildingMap.get("Rest1"), simCity.buildingMap.get("Apartment"));
//    
//    for(int i=0;i<path.size();i++){
//       System.out.println("Tile: "+simCity.findIndex(path.get(i))+"; hScore: "+path.get(i).hScore);
//    }
//    System.out.println("PATH 3:\n");
//    path=simCity.findPath(simCity.buildingMap.get("Apartment"), simCity.buildingMap.get("Market"));
//    
//    for(int i=0;i<path.size();i++){
//       System.out.println("Tile: "+simCity.findIndex(path.get(i))+"; hScore: "+path.get(i).hScore);
//    }
      
      int xPos=1325,yPos=215;
      mapTile closestTile=simCity.findClosestTile(xPos,yPos);
      System.out.println("Closest Tile: "+simCity.findIndex(closestTile));
    
//      for(int i=0;i<path.size();i++){
//         System.out.println("Tile: "+simCity.findIndex(path.get(i)));
//      }
      
      
//        System.out.println(simCity.buildingMap.get("Bank"));
//        System.out.println(simCity.findMapTile(40,40));
      
//      path=simCity.testSearch(simCity.map[4][1],simCity.map[3][0]);
//      for(int i=0;i<path.size();i++){
//         System.out.println("Tile: "+simCity.findIndex(path.get(i))+"; hScore: "+path.get(i).hScore);
//      }
   }
}