package com.gamelogic.playerlogic;

import java.util.Map;

public record Character(String name, Map<String, String> directions, int startingHP){

}
