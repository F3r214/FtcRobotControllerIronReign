package org.firstinspires.ftc.teamcode.robots.reachRefactor.subsystem;

import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import java.util.LinkedHashMap;
import java.util.Map;

import org.firstinspires.ftc.teamcode.robots.reachRefactor.simulation.ServoSim;
import static org.firstinspires.ftc.teamcode.robots.reachRefactor.util.Utils.*;
import org.firstinspires.ftc.teamcode.statemachine.Stage;
import org.firstinspires.ftc.teamcode.statemachine.StateMachine;

@Config
public class Crane implements Subsystem {
    private static final String TELEMETRY_NAME = "Crane";

    public static int SHOULDER_HOME_PWM = 1500;
    public static int ELBOW_HOME_PWM = 1550;
    public static int WRIST_HOME_PWM = 1500;

    public static double SHOULDER_PWM_PER_DEGREE = 600.0 / 90.0;
    public static double ELBOW_PWM_PER_DEGREE = -600.0 / 90.0;
    public static double WRIST_PWM_PER_DEGREE = 750.0 / 180.0;

    public static double SHOULDER_DEG_MIN = -90; //negative angles are counter clockwise while looking at the left side of the robot
    public static double ELBOW_DEG_MIN = -60;
    public static double WRIST_DEG_MIN = -180;

    public static double SHOULDER_DEG_MAX = 90;
    public static double ELBOW_DEG_MAX = 140;
    public static double WRIST_DEG_MAX = 180;

    public static double DUMP_TIME = 2;

    public Turret turret;

    public Servo shoulderServo, elbowServo, wristServo;

    private int shoulderTargetPos, elbowTargetPos, wristTargetPos;
    private double turretTargetPos;

    private Articulation articulation;
  
    public Crane(HardwareMap hardwareMap, Turret turret, boolean simulated) {
        if(simulated) {
            shoulderServo = new ServoSim();
            elbowServo = new ServoSim();
            wristServo = new ServoSim();
        } else {
            shoulderServo = hardwareMap.get(Servo.class, "firstLinkServo");
            elbowServo = hardwareMap.get(Servo.class, "secondLinkServo");
            wristServo = hardwareMap.get(Servo.class, "bucketServo");
        }

        this.turret = turret;
        articulation = Articulation.MANUAL;
    }

    public enum Articulation {
        TEST_INIT(0, 0, 0, 0, 5,0),
        MANUAL(0, 0, 0, 0, 0,0),
        INIT(-90,0,70,0, 1.5f,0),
        HOME(0,0,0,0, 0,0),
      
        LOWEST_TIER(75,130,20,0, 1.5f, 130),
        MIDDLE_TIER(60,130,40,0, 1f, 150),
        HIGH_TIER(27, 130,70,0, 1f, 170),
        TRANSFER(-45,-50,-20,0, 0.75f,0),

        CAP(30, 140,0,0, 1, 170),
      
        //these articulations are meant to observe the motions and angles to check for belt skips
        VALIDATE_ELBOW90(0,90,90,0, .5f,0),
        VALIDATE_SHOULDER90(90,15,-90+15,0, .5f,0),
        VALIDATE_TURRET90R(0,0,0,45,2.5f,0),
        VALIDATE_TURRET90L(0,0,0,-45,2.5f,0),

        //auton articulations
        AUTON_REACH_RIGHT(40, 130,70,30, 1, 170),
        AUTON_REACH_LEFT(40, 130,70,-30, 1, 170);

        public int shoulderPos, elbowPos, wristPos;
        public double turretAngle;
        public float toHomeTime;
        public int dumpPos;

        Articulation(int shoulderPos, int elbowPos, int wristPos, double turretAngle, float toHomeTime, int dumpPos){
            this.shoulderPos = shoulderPos;
            this.elbowPos = elbowPos;
            this.wristPos = wristPos;
            this.turretAngle = turretAngle;
            this.toHomeTime = toHomeTime;
            this.dumpPos = dumpPos;
        }
    }

    private float currentToHomeTime = Articulation.HOME.toHomeTime;
    private int currentDumpPos = 0;
    private final Stage mainStage = new Stage();
    private final StateMachine main = getStateMachine(mainStage)
            .addTimedState(() -> currentToHomeTime, () -> setTargetPositions(Articulation.HOME), () -> {})
            .addTimedState(() -> articulation.toHomeTime, () -> setTargetPositions(articulation),
                    () -> {
                        currentToHomeTime = articulation.toHomeTime;
                        if(articulation.dumpPos!=0) currentDumpPos= articulation.dumpPos;
                    }
            )

            .build();

    private final Stage initStage = new Stage();
    private final StateMachine init = getStateMachine(initStage)
            .addTimedState(2f, () -> setTargetPositions(Articulation.INIT), () -> {})
            .build();

    public boolean articulate(Articulation articulation) {
        if(articulation.equals(Articulation.MANUAL))
            return true;
        else if(articulation.equals(Articulation.INIT)) {
            this.articulation = articulation;
            if(init.execute()) {
                this.articulation = Articulation.MANUAL;
                return true;
            }
        }
        else {
            this.articulation = articulation;
            if(main.execute()) {
                this.articulation = Articulation.MANUAL;
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(Canvas fieldOverlay){
        articulate(articulation);

        shoulderServo.setPosition(servoNormalize(shoulderTargetPos));
        elbowServo.setPosition(servoNormalize(elbowTargetPos));
        wristServo.setPosition(servoNormalize(wristTargetPos));
        turret.setTargetAngle(turretTargetPos);
    }

    @Override
    public String getTelemetryName() {
        return TELEMETRY_NAME;
    }

    @Override
    public Map<String, Object> getTelemetry(boolean debug) {
        Map<String, Object> telemetryMap = new LinkedHashMap<>();

        telemetryMap.put("Current Articulation", articulation);

        if(debug) {
            telemetryMap.put("Shoulder Target Position", shoulderTargetPos);
            telemetryMap.put("Elbow Target Position", elbowTargetPos);
            telemetryMap.put("Wrist Target Position", wristTargetPos);
        }

        telemetryMap.put("Turret:", "");
        Map<String, Object> turretTelemetryMap = turret.getTelemetry(debug);
        telemetryMap.putAll(turretTelemetryMap);

        return telemetryMap;
    }

    public void dump() {
        setWristTargetPos(currentDumpPos);
    }

    private void setTargetPositions(Articulation articulation) {
        setShoulderTargetPos(articulation.shoulderPos);
        setElbowTargetPos(articulation.elbowPos);
        setWristTargetPos(articulation.wristPos);

        this.turretTargetPos = articulation.turretAngle;

        turret.setTargetAngle(articulation.turretAngle);
    }

    //----------------------------------------------------------------------------------------------
    // Getters And Setters
    //----------------------------------------------------------------------------------------------

    //take the supplied relative-to-home target value in degrees
    //and convert to servo setting
    private double shoulderServoValue(double targetPos){
        double newPos = Range.clip(targetPos,SHOULDER_DEG_MIN, SHOULDER_DEG_MAX);
        newPos = newPos * SHOULDER_PWM_PER_DEGREE + SHOULDER_HOME_PWM;
        return newPos;
    }

    private double elbowServoValue(double targetPos){
        double newPos = Range.clip(targetPos,ELBOW_DEG_MIN, ELBOW_DEG_MAX);
        newPos = newPos * ELBOW_PWM_PER_DEGREE + ELBOW_HOME_PWM;
        return newPos;
    }

    private double wristServoValue(double targetPos){
        double newPos = Range.clip(targetPos,WRIST_DEG_MIN, WRIST_DEG_MAX);
        newPos = newPos * WRIST_PWM_PER_DEGREE + WRIST_HOME_PWM;
        return newPos;
    }

    public void setShoulderTargetPos(int shoulderTargetPos) {
        this.shoulderTargetPos = (int) shoulderServoValue(shoulderTargetPos);
    }

    public void setElbowTargetPos(int elbowTargetPos) {
        this.elbowTargetPos = (int) elbowServoValue(elbowTargetPos);
    }

    public void setWristTargetPos(int wristTargetPos) {
        this.wristTargetPos = (int) wristServoValue(wristTargetPos);
    }

    public void setShoulderTargetPosRaw(int shoulderTargetPos) {
        this.shoulderTargetPos = shoulderTargetPos;
    }

    public void setElbowTargetPosRaw(int elbowTargetPos) {
        this.elbowTargetPos = elbowTargetPos;
    }

    public void setWristTargetPosRaw(int wristTargetPos) {
        this.wristTargetPos = wristTargetPos;
    }

    public int getShoulderTargetPos() {
        return shoulderTargetPos;
    }

    public int getElbowTargetPos() {
        return elbowTargetPos;
    }

    public int getWristTargetPos() {
        return wristTargetPos;
    }

    public Articulation getArticulation() { return articulation; }
}
