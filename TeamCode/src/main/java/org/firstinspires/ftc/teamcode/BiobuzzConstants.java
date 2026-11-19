package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

public class BiobuzzConstants {

    //Austin test commit



    //region - Hardware
    DcMotorEx intakef;
    CRServo intakeb;

    DcMotor fr;
    DcMotor fl;
    DcMotor br;
    DcMotor bl;

    GoBildaPinpointDriver odo;
    Pose2D odoorigin = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);

    DcMotorEx flywheel;
    DcMotor transfer;

    Servo turret;
    Servo hood;

    Limelight3A limelight;


    //endregion

    //region - Drive Constants
    double frpower;
    double flpower;
    double brpower;
    double blpower;

    double angvel = 0;
    double xvel = 0;
    double yvel = 0;

    double distToGoalTot;


    //endregion

    //region - Flywheel

    public static PIDFCoefficients PIDF = new PIDFCoefficients(160, 0, 0, 15.2); //adjust/tune these
//was 67
    boolean launching = false;

    double idleSpeed = 2000;

    double offsetf = 0;

    double targetFlywheelSpeed = 0;

    double flywheelautostart = 2475.0 / 60 * 28;


    String side = "Audience";
    String teamColor = "Red";


    //endregion

    //region -Interpolating Calculatios
    static final double[] distanceFromTarget = {16, 18, 20, 22.3, 26, 30, 35, 39, 42};
    static final double[] optimalVelocity = {2380, 2380, 2405, 2470, 2470, 2500, 2500, 2515, 2535};
    static final double[] optimalHoodPosition = {.72, .74, .729, .729, .784, .784, .784, .81, .81};


    public static double interpolateVelo(double x) { //x is dist from goal

        // Find the two points surrounding x
        for (int i = 0; i < distanceFromTarget.length - 1; i++) {

            if (x >= distanceFromTarget[i] && x <= distanceFromTarget[i + 1]) {

                double x1 = distanceFromTarget[i];
                double x2 = distanceFromTarget[i + 1];

                double y1 = optimalVelocity[i];
                double y2 = optimalVelocity[i + 1];

                // Linear interpolation formula
                return y1 + (x - x1) *
                        (y2 - y1) / (x2 - x1);
            }
        }

        return optimalVelocity[optimalVelocity.length-1];
    }
    public static double interpolateHood(double x) {
        //x is distance from target

        // Find the two points surrounding x
        for (int i = 0; i < distanceFromTarget.length - 1; i++) {

            if (x >= distanceFromTarget[i] && x <= distanceFromTarget[i + 1]) {

                double x1 = distanceFromTarget[i];
                double x2 = distanceFromTarget[i + 1];

                double y1 = optimalHoodPosition[i];
                double y2 = optimalHoodPosition[i + 1];

                // Linear interpolation formula
                return y1 + (x - x1) * //returns optimal hood angle
                        (y2 - y1) / (x2 - x1);
            }
        }

        return optimalHoodPosition[optimalHoodPosition.length-1];
    }
    //endregion

    //region - Auto Positions

    Pose2D redGoalAud = new Pose2D(DistanceUnit.INCH, -10, -21, AngleUnit.DEGREES, 0);
    Pose2D redGoalBack = new Pose2D(DistanceUnit.INCH, -5, 21, AngleUnit.DEGREES, 0);
    Pose2D blueGoalBack = new Pose2D(DistanceUnit.INCH, 14, 21, AngleUnit.DEGREES, 0);
    Pose2D blueGoalAud = new Pose2D(DistanceUnit.INCH, 14, -21, AngleUnit.DEGREES, 0);
//blu back was 10,21, same with blu close
    //red was at -10 x

    Pose2D goal = redGoalAud; //change this to whatever target goal is

    Position usePos = new Position(DistanceUnit.INCH, 0, 0, 0, 0);

    //red meet 1 poses

    Pose2D startPoseR = new Pose2D(DistanceUnit.INCH, -10, -64, AngleUnit.DEGREES, 90);
    Pose2D intakePreLineR = new Pose2D(DistanceUnit.INCH, -56.5, -42, AngleUnit.DEGREES, 180);
    Pose2D intakePostLineR = new Pose2D(DistanceUnit.INCH, -56.5, -45, AngleUnit.DEGREES, 180);
    Pose2D intakeLineR = new Pose2D(DistanceUnit.INCH, -56.5, -54, AngleUnit.DEGREES, 180);
    Pose2D parkWaypointR = new Pose2D(DistanceUnit.INCH, -39, -31.5, AngleUnit.DEGREES, 180);
    Pose2D parkPosR = new Pose2D(DistanceUnit.INCH, -52, 28, AngleUnit.DEGREES, 180);
    Pose2D intakeLineSideR = new Pose2D(DistanceUnit.INCH, -55.5, -64, AngleUnit.DEGREES, 90);
    Pose2D intakeLineSideWaypointR = new Pose2D(DistanceUnit.INCH, -48, -60, AngleUnit.DEGREES, 45);

    //blue meet 1 poses (red inversed, untested) all pos to neg and vice versa, rotated 180 deg

    Pose2D startPoseB = new Pose2D(DistanceUnit.INCH, 10, 64, AngleUnit.DEGREES, -90);
    Pose2D intakePreLineB = new Pose2D(DistanceUnit.INCH, 56.5, 42, AngleUnit.DEGREES, 0);
    Pose2D intakePostLineB = new Pose2D(DistanceUnit.INCH, 56.5, 45, AngleUnit.DEGREES, 0);
    Pose2D intakeLineB = new Pose2D(DistanceUnit.INCH, 56.5, 54, AngleUnit.DEGREES, 0);
    Pose2D parkWaypointB = new Pose2D(DistanceUnit.INCH, 39, 31.5, AngleUnit.DEGREES, 0);
    Pose2D parkPosB = new Pose2D(DistanceUnit.INCH, 52, -28, AngleUnit.DEGREES, 0);
    Pose2D intakeLineSideB = new Pose2D(DistanceUnit.INCH, 55.5, 64, AngleUnit.DEGREES, -90);
    Pose2D intakeLineSideWaypointB = new Pose2D(DistanceUnit.INCH, 48, 60, AngleUnit.DEGREES, -135);







    //endregion

    //region - Turret

    //turret poses for different angles, n = negative
    double t180 = .822;
    double t90 = .654;
    double t0 = .5;
    double tn90 = .353;
    double tn180 = .182;

    double turretautostart = 0.00175667 * 94 + 0.5022;

    double offsett = 0;

    boolean tTracking = false; //is tracking
    boolean tPress = false; // is button for turret pressed


    public double tAng(double tpos){

        double tAng = (568.91241 * tpos) - 285.70781;


        return tAng;
    }

    public double goalAng(Pose2D odo, Pose2D GOAL, String Side) {


        double ga = Math.toDegrees(Math.atan((GOAL.getX(DistanceUnit.INCH) - odo.getX(DistanceUnit.INCH)) /
                (GOAL.getY(DistanceUnit.INCH) - odo.getY(DistanceUnit.INCH))));

        if (Side == "Back"){
            if (ga < 0){
                ga = ga + 180;
            } else {
                ga = ga - 180;
            }

        }

        return ga;
    }

    public double tGoalAng(double tAng, Pose2D odo, Pose2D GOAL, String Side){
        double tGoalAng = goalAng(odo,GOAL,Side) + odo.getHeading(AngleUnit.DEGREES);

        if (tGoalAng > 225){
            tGoalAng = tGoalAng - 360;
        } else if (tGoalAng < -225){
            tGoalAng = tGoalAng + 360;
        }

        return tGoalAng; //add stuff like bounds
    }

    public double tTarget(double tAng, Pose2D odo, Pose2D GOAL, double offset, String Side){

        double tTarget = 0.00175667 * (tGoalAng(tAng,odo,GOAL,Side) + offset) + 0.5022;


        return tTarget;
    }

    //endregion

    //region - Hood

    double targetHoodAngle;

    double hooddown = .88;
    double hoodup = .72;
    double hoodautostart = .81;

    //endregion

    //region - Publics General
    public void configureOdo(boolean reset) {
        double xodooffset = 75; //calculate these sometime
        double yodooffset = 0;

        odo.setOffsets(xodooffset, yodooffset, DistanceUnit.MM);
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        if (reset) {
            odo.resetPosAndIMU();
        }
    }
    public void navreset(boolean reset_opods, boolean recal_opods) {
        if (reset_opods) {
            odo.resetPosAndIMU(); //resets the position to 0 and recalibrates the IMU
        }

        if (recal_opods) {
            odo.recalibrateIMU(); //recalibrates the IMU without resetting position
        }
    }
    public double goalDC(Pose2D pos, Pose2D g) {
        double gdx = g.getX(DistanceUnit.INCH) - pos.getX(DistanceUnit.INCH);
        double gdy = g.getY(DistanceUnit.INCH) - pos.getY(DistanceUnit.INCH);
        return Math.sqrt(Math.pow(gdx, 2) + Math.pow(gdy, 2));
    }
    public void transfer (){
        transfer.setPower(1);
        intakef.setPower(1);
        intakeb.setPower(1);
    }
    //endregion



}
