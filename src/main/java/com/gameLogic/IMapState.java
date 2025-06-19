package com.gameLogic;

 public interface IMapState {
     void changeLevel(int levelDelta);
     void setLevel(int level);
     int getLevel();
     void resetMap();
     String getMapValue(Coordinates coordinates);
 }
