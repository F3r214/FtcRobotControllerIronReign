package org.firstinspires.ftc.teamcode.robots.bobobot.AutoActions;

import org.firstinspires.ftc.teamcode.robots.bobobot.Assign;
import org.firstinspires.ftc.teamcode.robots.bobobot.Bots.Autobot;

public class Mechanisms extends Assign {
    private Autobot autobot;
    public Mechanisms(Autobot autobot){
        this.autobot = autobot;
        delta = 0;
    }
    @Override
    public double getDelta() {
        return delta;
    }
    @Override
    public boolean run(){
        autobot.grip.autoOpen();
        autobot.grip.autoClose();
        return false;
    }
}