package com.gameLogic.PlayerLogic;

import java.util.Map;

public record Character(String skinID,Map<String, String> directions, int startingHP){

}
