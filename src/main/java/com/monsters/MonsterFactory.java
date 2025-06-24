package com.monsters;

import com.monsters.caverns.GiantBat;
import com.monsters.caverns.WitheredSkeleton;
import com.monsters.overworld.*;
import com.monsters.theDarkness.DarkenedWisp;
import com.monsters.theDarkness.OrbOfDarkness;
import com.monsters.theVoid.Consumed;
import com.monsters.theVoid.Cultist;
import com.monsters.theVoid.theAvatar;
import com.monsters.underground.Bat;
import com.monsters.underground.Dwarf;

public class MonsterFactory {

    public MonsterFactory(){

    }
    public Monster MonsterFac(int damage, int health, String name, int number){
        return switch (name.toLowerCase()) {
            case "goblin" -> new Goblin(damage, health,number);
            case "skeleton" -> new Skeleton(damage, health,number);
            case "zombie" -> new Zombie(damage, health,number);
            case "giant bat" ->  new GiantBat(damage, health,number);
            case "withered skeleton" ->  new WitheredSkeleton(damage, health,number);
            case "giant" ->  new Giant(damage, health,number);
            case "snake" ->  new Snake(damage, health,number);
            case "darkened wisp" ->  new DarkenedWisp(damage, health,number);
            case "orb" ->  new OrbOfDarkness(damage, health,number);
            case "consumed" ->  new Consumed(damage, health,number);
            case "cultist" ->  new Cultist(damage, health,number);
            case "avatar" ->  new theAvatar(damage, health);
            case "dwarf" ->  new Dwarf(damage, health,number);
            case "bat" ->  new Bat(damage, health,number);
            default -> throw new IllegalStateException("Unknown Monster.");
        };
    }
}
