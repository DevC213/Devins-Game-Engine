package com.gamelogic.commands;
enum CommandState {
    NONE,  // 0 - Not in a command
    TAKE,  // 2 - Taking item
    HEAL,  // 3 - Using health item
    ATTACK, // 4 - Attacking
    HATTACK, //5 - Using health item during fight
}
