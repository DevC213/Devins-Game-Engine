package com.gamelogic.commands;
enum CommandState {
    NONE,  // 0 - Not in a command
    TAKE,  // 2 - Taking item
    HEAL,  // 3 - Using health item
    ATTACK_CHOICE,
    ATTACK, // 4 - Attacking
    HEAL_IN_COMBAT, //5 - Using health item during fight
}
