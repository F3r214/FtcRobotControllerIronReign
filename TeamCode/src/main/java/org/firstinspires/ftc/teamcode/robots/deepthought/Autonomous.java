package org.firstinspires.ftc.teamcode.robots.deepthought;

import static org.firstinspires.ftc.teamcode.robots.deepthought.IntoTheDeep_6832.alliance;
import static org.firstinspires.ftc.teamcode.robots.deepthought.IntoTheDeep_6832.field;
import static org.firstinspires.ftc.teamcode.util.utilMethods.futureTime;
import static org.firstinspires.ftc.teamcode.util.utilMethods.isPast;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.robots.deepthought.field.Field;
import org.firstinspires.ftc.teamcode.robots.deepthought.subsystem.Robot;
import org.firstinspires.ftc.teamcode.robots.deepthought.subsystem.Trident;
import org.firstinspires.ftc.teamcode.robots.deepthought.util.Constants;
import org.firstinspires.ftc.teamcode.robots.deepthought.util.DTPosition;
import org.firstinspires.ftc.teamcode.robots.deepthought.util.TelemetryProvider;

import java.util.LinkedHashMap;
import java.util.Map;


@Config(value = "AA_CS_Auton")
public class Autonomous implements TelemetryProvider {
    public static int numCycles = 4;
    private Robot robot;
    private HardwareMap hardwareMap;

    //
    public enum AutonState {
        INIT,
        DRIVE_TO_BASKET,
        OUTTAKE_TO_BASKET,
        DRIVE_TO_SUB,

    }


    public AutonState autonState = AutonState.INIT;

    @Override
    public Map<String, Object> getTelemetry(boolean debug) {
        Map<String, Object> telemetryMap = new LinkedHashMap<>();
        telemetryMap.put("autonState\t ", autonState);
        telemetryMap.put("auton index\t", autonIndex);
        telemetryMap.put("outtake state", robot.trident.outtakeIndex);
        telemetryMap.put("num cycles\t", numCycles);
        telemetryMap.put("selectedPath\t", selectedPath);
        return telemetryMap;
    }

    @Override
    public String getTelemetryName() {
        return "AUTONOMOUS";
    }

    // autonomous routines

    public static int selectedPath;
    public int allianceMultiplier = 1;


    public static double FIELD_INCHES_PER_GRID = 23.5;
    public static double AUTON_START_DELAY = 0;

    public Autonomous(Robot robot) {
        this.robot = robot;
        this.hardwareMap = robot.hardwareMap;
        autonIndex = 0;
    }

    public static int autonIndex;
    public long autonTimer = futureTime(10);

    public boolean execute(TelemetryPacket packet) {
        if (!alliance.isRed()) {
            allianceMultiplier = -1;
        }
        switch (autonIndex) {
            case 0:
                autonState = AutonState.INIT;
                robot.positionCache.update(new DTPosition(robot.driveTrain.pose, robot.trident.crane.getCurrentPosition(), robot.trident.slide.getCurrentPosition()), true);
                autonTimer = futureTime(AUTON_START_DELAY);
                autonIndex++;
                break;
            case 1:
                if (isPast(autonTimer)) {
                    autonState = AutonState.DRIVE_TO_BASKET;
                    numCycles--;
                    autonIndex++;
                }
                break;
            case 2:
                if (robot.driveTrain.strafeToPose(field.basket.getPose(), packet)) {
                    robot.trident.outtakeIndex = 0;
                    Trident.enforceSlideLimits = false;
                    robot.articulate(Robot.Articulation.OUTTAKE);
                    autonTimer = futureTime(4);
                    autonIndex++;
                }
                break;
            case 3:
//                todo - test & tune preload score here
                if (robot.articulation.equals(Robot.Articulation.MANUAL) && isPast(autonTimer)) {
                    autonIndex++;
                }
                break;
            case 4:
                robot.trident.beaterPower = 1;
                autonTimer = futureTime(2);
                autonIndex++;
                break;
            case 5:
                if (isPast(autonTimer)) {
                    autonIndex++;
                    robot.trident.beaterPower = 0;
                }
                break;
            case 6:
                if (robot.driveTrain.strafeToPose(field.ground1.getPose(), packet)) {
                    autonTimer = futureTime(3);
                    autonIndex++;

                }
                break;
            case 7:
                Trident.intakeIndex = 0;
                robot.articulate(Robot.Articulation.INTAKE);
                if (isPast(autonTimer)) {
                    Trident.enforceSlideLimits = false;
                    robot.trident.slideTargetPosition = 1500;
                    autonTimer = futureTime(1.5);
                    autonIndex++;
                }
            case 8:
                if (isPast(autonTimer)) {
                    robot.trident.adjustElbow(-Trident.ELBOW_ADJUST_ANGLE * 3);
                    Trident.intakeIndex = 2;
                    autonTimer = futureTime(2);
                    autonIndex++;
                }
                break;
            case 9:
                robot.trident.slideTargetPosition -=50;
                if(robot.trident.articulation == Trident.Articulation.MANUAL){
                    robot.articulate(Robot.Articulation.TRAVEL);
                    autonTimer = futureTime(3);
                    autonIndex++;
                }
                break;
            case 10:
                if (robot.driveTrain.strafeToPose(field.basket.getPose(), packet)) {;
                    return true;
                }
                break;
        }
        return false;
    }


}