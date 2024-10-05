package org.firstinspires.ftc.teamcode.robots.deepthought;

import static org.firstinspires.ftc.teamcode.robots.deepthought.util.Constants.FIELD_INCHES_PER_GRID;
import static org.firstinspires.ftc.teamcode.robots.deepthought.util.Utils.P2D;

import com.acmerobotics.roadrunner.Pose2d;

public class POI {
    //TODO - does this need to be diff for individual pois?
    //also needs actual testing
    public static double POI_ERROR_RADIUS = .25;

    POI(double x, double y, double heading, String name) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.pose = P2D(x, y, heading);
    }

    public Pose2d pose;
    public double heading;
    public double x;

    public double y;

    public final String name;

    public Pose2d getPose() {
        return pose;
    }

    public POI flipOnX() {
        return new POI(this.x, -this.y, -this.heading, this.name);
    }

    public boolean atPOI(Pose2d pose) {
        return Math.hypot(pose.position.x / FIELD_INCHES_PER_GRID - x, pose.position.y / FIELD_INCHES_PER_GRID - y) < POI_ERROR_RADIUS;
    }
}


