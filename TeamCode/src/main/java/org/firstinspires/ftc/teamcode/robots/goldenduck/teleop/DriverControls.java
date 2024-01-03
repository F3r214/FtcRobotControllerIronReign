package org.firstinspires.ftc.teamcode.robots.goldenduck.teleop;

import static org.firstinspires.ftc.teamcode.util.utilMethods.servoNormalize;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;


@Config ("GoldenDuckGameVariables")
@TeleOp(name="Golden Duck OpMode", group="Challenge")
public class DriverControls extends OpMode {
    private boolean calibrate = false;
    DriveTrain driveTrain;
    Servo servoClaw;
    Servo clawWrist;
//    Servo clawWrist2;
    Servo servoRailgun;
    private DcMotor arm = null;
    //    private DcMotor arm2 = null;
    private DcMotorEx motorBackLeft = null;
    private DcMotorEx motorFrontLeft = null;
    private DcMotorEx motorBackRight = null;
    private DcMotorEx motorFrontRight = null;
    FtcDashboard dashboard;
    MultipleTelemetry dashTelemetry;

    @Override
    public void init() {

        dashboard = FtcDashboard.getInstance();
        dashTelemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
        dashTelemetry.setMsTransmissionInterval(25);

        driveTrain = new DriveTrain(telemetry, hardwareMap);
        driveTrain.motorInit();

        servoRailgun = hardwareMap.get(Servo.class, "servoRailgun");

        servoClaw = hardwareMap.get(Servo.class, "servoClaw");

        clawWrist = hardwareMap.get(Servo.class, "servoWrist");
//        clawWRist2 = hardwareMap.get(Servo.class, "servoWrist2");

        arm = this.hardwareMap.get(DcMotorEx.class, "motorShoulder");
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        arm2 = this.hardwareMap.get(DcMotorEx.class, "armMotor2");
//        arm2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorBackLeft = this.hardwareMap.get(DcMotorEx.class, "leftBack");
        motorFrontLeft = this.hardwareMap.get(DcMotorEx.class, "leftFront");
        motorBackRight = this.hardwareMap.get(DcMotorEx.class, "rightBack");
        motorFrontRight = this.hardwareMap.get(DcMotorEx.class, "rightFront");
        motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }

    @Override
    public void loop() {

        telemetry.addData("Middle OdoPod \t", motorBackRight.getCurrentPosition());
        telemetry.addData("Right OdoPod \t", motorBackLeft.getCurrentPosition());
        telemetry.addData("Left OdoPod \t", motorFrontLeft.getCurrentPosition());
        telemetry.addData("servsoWrist", clawWrist.getPosition());
//        telemetry.addData("servoWrist2". clawWrist2.getPositiion());
        telemetry.addData("servoClaw", servoClaw.getPosition());
        telemetry.addData("arm U/D position", arm.getCurrentPosition());
//        telemetry.addData("arm R/L position", arm2.getCurrentPosition());
        telemetry.addData("Railgun Shot", servoRailgun.getPosition());

        driveTrain.mecanumDrive(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        if (gamepad1.dpad_down) {
            calibrate = false;
        }

//        if (gamepad1.dpad_up) {
//            if (driveTrain.robotSpeed == 1)
//                driveTrain.robotSpeed = .5;
//            else
//                driveTrain.robotSpeed = 1;
////      speed reduction
//        }
//
//        if (gamepad1.dpad_down) {
//            if (driveTrain.robotSpeed == 0.5)
//                driveTrain.robotSpeed = 1;
//            else
//                driveTrain.robotSpeed = 0.5;
////      speed up/neutral
//        }

        if (gamepad1.x) {
            arm.setPower(0.1);
            arm.setTargetPosition(15);
            arm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            clawWrist.setPosition(servoNormalize(-500));
            //driving mode
        }

        if (gamepad1.y) {
            arm.setPower(0.2);
            //arm.setTargetPosition(105);
            arm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            clawWrist.setPosition(servoNormalize(-1000));
            //to pick up pixel
        }

        if (gamepad1.b) {
            arm.setPower(0.3);
            //arm.setTargetPosition(1389);
            arm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            clawWrist.setPosition(0);
            // mid score backboard
        }

        if (gamepad1.a) {
            arm.setPower(0.3);
            //arm.setTargetPosition(1615);
            arm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            clawWrist.setPosition(0);
//      low score backboard
        }

        if (gamepad1.right_bumper) {
            servoClaw.setPosition(servoNormalize(850));
//      claw open
        }

        if (gamepad1.left_bumper) {
            servoClaw.setPosition(servoNormalize(1300));
//      claw close
        }

        if (gamepad1.dpad_down) {
            servoRailgun.setPosition(servoNormalize(1821));
        }

//        if (gamepad1.dpad_right) {
//            arm2.setPower(xyz);
//            arm2.getCurrentPosition() + xyz;
//            arm2.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
//        }
//
//         if (gamepad1.dpad_right) {
//            arm2.setPower(xyz);
//            arm2.getCurrentPosition() - xyz;
//            arm2.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
//       }
    }
}