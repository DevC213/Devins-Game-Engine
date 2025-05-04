package com.Monsters;

import com.Monsters.caverns.GiantBat;
import com.Monsters.caverns.WitheredSkeleton;
import com.Monsters.overworld.*;
import com.Monsters.theDarkness.DarkenedWisp;
import com.Monsters.theDarkness.OrbOfDarkness;
import com.Monsters.theVoid.Consumed;
import com.Monsters.theVoid.Cultist;
import com.Monsters.theVoid.theAvatar;
import com.Monsters.underground.Dwarf;

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
            default -> throw new IllegalStateException("Unknown Monster.");
        };
    }
}
