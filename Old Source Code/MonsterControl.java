package com.adventure_logic;

import java.util.Objects;

public class MonsterControl {

    Adventure adventure;
    MonsterControl(Adventure adventure) {
        this.adventure = adventure;
    }
    public static void attack() {
        Adventure.controller.updateGUI("What monster?", 0);
        Adventure.commandLocation = 4;
        Adventure.controller.clearInput();
    }

    public static void attacking() {
        String monster = Adventure.controller.getCommand();
        Adventure.controller.clearInput();
        if (Objects.equals(monster, "") || monster == null) {
            Adventure.controller.updateGUI("Missed", 0);
            Adventure.commandLocation = 0;
            return;
        }
        Adventure.controller.clearInput();
        Adventure.gameMapController.attackMonster(monster, Adventure.playerController.getAttack(), Adventure.playerController.getCords());
        Adventure.minimapItems();
        Adventure.commandLocation = 0;
        for (Double i : Adventure.gameMapController.getMonstersAttacks(Adventure.playerController.getCords())) {
            MonsterAttack(i);
        }
    }

    public static void MonsterAttack(Double damage) {
        Adventure.controller.updateGUI("Monster hits you for: " + damage, 0);
        Adventure.playerController.damage(damage);
        if (Adventure.playerController.getHealth() <= 0) {
            if (Adventure.playerController.getHealing_items() == null) {
                Adventure.gameOver();
            } else {
                Adventure.playerController.EmergencyUse();
            }
        }
    }
}