package org.firstinspires.ftc.teamcode.robots.bobobot.TeleOpSystems;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Locale;

public class OpDrive {
    private Telemetry telemetry;
    private HardwareMap hardwareMap;
    private DcMotorEx rightFront = null;
    private DcMotorEx leftBack = null;
    private DcMotorEx leftFront = null;
    private DcMotorEx rightBack = null;

    // robot motors
    private double powerFrontLeft = 0;
    private double powerFrontRight = 0;
    private double powerBackLeft = 0;
    private double powerBackRight = 0;
    // power input for each respective wheel
    BNO055IMU imu;
    // IMU
    Orientation angles;
    Acceleration gravity;
    public double heading;

    double robotSpeed = 1;
    public OpDrive(Telemetry telemetry, HardwareMap hardwareMap)
    {
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
    }
    public void mecanumDrive(double forward, double strafe, double turn) {
        forward = -forward;
        turn = -turn;
        double r = Math.hypot(strafe, forward);
        double robotAngle = Math.atan2(forward, strafe) - Math.PI / 4;
        double rightX = turn;
        powerFrontLeft = r * Math.cos(robotAngle) - rightX;
        powerFrontRight = r * Math.sin(robotAngle) + rightX;
        powerBackLeft = r * Math.sin(robotAngle) - rightX;
        powerBackRight = r * Math.cos(robotAngle) + rightX;
        leftFront.setPower(powerFrontLeft*robotSpeed);
        rightFront.setPower(powerFrontRight*robotSpeed);
        leftBack.setPower(powerBackLeft*robotSpeed);
        rightBack.setPower(powerBackRight*robotSpeed);
    }
    public void telemetryOutput()
    {
        // Acquiring the angles is relatively expensive; we don't want
        // to do that in each of the three items that need that info, as that's
        // three times the necessary expense.
        angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        gravity  = imu.getGravity();
        heading = angles.firstAngle;
        telemetry.addData("Back Right Position \t", rightBack.getCurrentPosition());
        telemetry.addData("Back Left Position \t", leftBack.getCurrentPosition());
        telemetry.addData("Front Right Position \t", rightFront.getCurrentPosition());
        telemetry.addData("Front Left Position \t", leftFront.getCurrentPosition());
        telemetry.addData("Average Motor Position \t", getMotorAvgPosition());
        telemetry.addData("Heading \t", formatAngle(angles.angleUnit, heading));
        telemetry.addData("Front Right Power \t", rightBack.getPower());


    }
    public double getMotorAvgPosition(){return (double)(Math.abs(leftFront.getCurrentPosition())+Math.abs(rightFront.getCurrentPosition())+Math.abs(leftBack.getCurrentPosition())+Math.abs(rightBack.getCurrentPosition()))/4.0;}
    //motor broken rn so use 3; when fixed change back to 4
    // UPDATE: New chassis, new motors... Problem solved?
    public void motorInit()
    {
        leftFront = this.hardwareMap.get(DcMotorEx.class, "leftFront");
        leftBack = this.hardwareMap.get(DcMotorEx.class, "leftBack");
        rightFront = this.hardwareMap.get(DcMotorEx.class, "rightFront");
        rightBack = this.hardwareMap.get(DcMotorEx.class, "rightBack");
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        leftFront.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample OpMode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }
    String formatDegrees(double degrees){
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }
    public double getHeading(){
        return heading;
    }
    public double getExternalHeadingVelocity(){
        return (double) imu.getAngularVelocity().zRotationRate;
    }
}
