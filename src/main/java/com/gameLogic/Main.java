package com.gameLogic;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {

        try {
            Application.main(args);
            PrintStream out = new PrintStream("log.txt");
            System.setOut(out);
            System.setErr(out);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
