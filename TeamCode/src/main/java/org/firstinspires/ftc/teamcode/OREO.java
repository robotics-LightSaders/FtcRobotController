package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import android.graphics.Color;
import android.util.Log;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import java.util.Locale;

public class OREO{


    private ElapsedTime runtime = new ElapsedTime();

    //region - CALIBRATION (NEW): measured on THIS robot. 0 = not measured yet -> the OpMode runs measureDrive()
    // public static so FTC Dashboard can edit them live, if you use it.
    public static boolean CALIBRATE = false;     // true = run measureDrive() even when the numbers below are filled in
    public static double MAX_VEL_FWD = 50.1;       //was 50.1 // in/s     top speed driving forward at full power
    public static double MAX_ACCEL_FWD = 212;      // in/s^2   acceleration from rest at full forward power
    public static double MAX_VEL_STRAFE = 68.6;     // in/s     same, driving sideways
    public static double MAX_ACCEL_STRAFE = 237;   // in/s^2
    public static double MAX_TURN_RATE = 419;      // deg/s    same, spinning in place
    public static double MAX_TURN_ACCEL = 3172;     // deg/s^2
    public static double K_STATIC = .151;           // power lost to friction while moving (measureDrive measures it too)
    //endregion

    //region - Route settings (NEW): not robot specific
    public static double PLAN_POWER = 0.9;       // share of wheel power the speed plan may use; the rest is kept for corrections
    public static double POS_GAIN = 2.0;         // how hard to correct position error, in units of 1 / (the robot's time constant)
    public static double HEADING_GAIN = .5;     // same for heading was 4
    public static double VEL_GAIN = 1.0;         // how hard to correct speed error (damps the two above)
    public static double ACCEL_RAMP = 0;         // s      planned acceleration may swing from full braking to full speed-up no faster than this
    public static double POS_TOL = 0.5;          // in     the route is done when the robot is this close to the last point,
    public static double HEADING_TOL = 1.0;      // deg    pointing within this,
    public static double SPEED_TOL = 2.0;        // in/s   and slower than this
    public static double MAX_LAG = 2.0;          // in     if the robot falls this far behind the target, the target waits for it
    public static double VEL_WINDOW = 0.05;      // s      speed is measured over this much time (a single loop is too noisy)
    public static int SAMPLES_PER_SEGMENT = 200; // points per curve segment in the plan
    public static double CAL_BURST_S = 0.8;      // measureDrive: longest full-power burst (s)
    public static double CAL_MAX_TRAVEL = 30;    // measureDrive: stop a burst after this many inches...
    public static double CAL_MAX_TURN = 300;     // ...or this many degrees
    public static double CAL_WINDOW = 0.05;      // measureDrive: time window for speed and acceleration (s)
    //endregion

    //region - RUSHING SPLINE settings (NEW): used only by RushingSpline()
    public static double RUSH_FRACTION = 0.75;     // share of the route (by distance) that may trade accuracy for speed
    public static double RUSH_BLEND = 0.10;        // share of the route, at the end of the rush part, that fades back to strict
    public static double RUSH_PLAN_POWER = 1.0;    // wheel power the speed plan may use in the rush part (max 1.0)
    public static double RUSH_MAX_LAG = 6.0;       // in   target waits for the robot only when it is this far ahead
    public static double RUSH_CROSS_GAIN = 0.1;    // x    share of the normal correction for being off to the SIDE of the path used to be .3
    public static double RUSH_HEADING_GAIN = 0.3;  // x    share of the normal heading correction used ot be .5
    double[] planPower;                            // wheel power the plan may use at each sample (null = PLAN_POWER everywhere)
    //endregion

    double velX, velY, velH, velocity;           // FIX: robot velocity from GetVelo, in/s and rad/s, field frame
    double lastTimeVelo = -1;
    double lastHeadingVelo = 0;
    Pose2D lastPoseVelo = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
    double[] histT = new double[64], histX = new double[64], histY = new double[64], histH = new double[64];
    int histN = 0;                               // NEW: recent poses for GetVelo (oldest first)

    Pose2D splineOutput;
    double outX, outY, outH;                     // NEW: SplineMath's output as plain numbers (outH in degrees, not wrapped)

    int planN = 0;
    double[] planS;             // distance along the curve (in)
    double[] planX, planY;      // position (in)
    double[] planH;             // heading (rad, not wrapped)
    double[] planTx, planTy;    // direction of travel (unit vector)
    double[] planK;             // curvature = 1 / radius of the bend (1/in, + = bending left)
    double[] planHp, planHpp;   // heading change per inch, and its change per inch
    double[] planV, planA;      // planned speed (in/s) and acceleration along the curve (in/s^2)
    double[] planT;             // planned time to reach each sample (s)
    double planTime;            // planned time for the whole route (s)
    double[] calResult;         // measureDrive's numbers, in the CALIBRATION order
    //endregion


    double minPower = 0.17;
    double shutoffPower = 0.05;
    BiobuzzMecanumDrive drivetrain;
    private  LinearOpMode opMode;
    public OREO(BiobuzzMecanumDrive Drivetrain, LinearOpMode OpMode){
        drivetrain = Drivetrain;
        opMode = OpMode;


    }

    public boolean SplineSequence(double drivetimeout, Pose2D... points) {
        double splineStartTime = runtime.milliseconds();
        Log.d("Checkpoint","CHECKPOINT");
        //logs to determine if the pose its startingat is actually the pos it is at
        Pose2D actual = drivetrain.odoPose();
        Pose2D requested = points[0];

        Log.d("START_DEBUG", String.format(
                Locale.US,
                "Actual: (%.2f, %.2f, %.2f) | Requested: (%.2f, %.2f, %.2f)",
                actual.getX(DistanceUnit.INCH),
                actual.getY(DistanceUnit.INCH),
                actual.getHeading(AngleUnit.DEGREES),
                requested.getX(DistanceUnit.INCH),
                requested.getY(DistanceUnit.INCH),
                requested.getHeading(AngleUnit.DEGREES)
        ));
        //#region -Plan the route before moving (NEW): the curve, then the fastest speed at every spot on it
        buildTrajectory(points);
        planSpeeds();
        Log.d("Spline", "planned: " + planN + " samples, " + planS[planN - 1] + " in, " + planTime + " s");
        //#endregion

        double endX = planX[planN - 1], endY = planY[planN - 1], endH = planH[planN - 1];
        double kPos = POS_GAIN * MAX_ACCEL_FWD / MAX_VEL_FWD;            // 1 / time constant = accel / top speed
        double kHead = HEADING_GAIN * MAX_TURN_ACCEL / MAX_TURN_RATE;
        double tPlan = 0;                             // where the target is on the plan, in seconds
        int idx = 0;                                  // ...and as a sample number
        boolean holding = false;                      // plan finished: just hold the end pose
        boolean arrived = false;
        double axUsed = 0, ayUsed = 0, alphaUsed = 0; // feed-forward acceleration actually sent (ramped, see ACCEL_RAMP)
        lastTimeVelo = -1;                            // FIX: velocity starts fresh for every route

        //Loop: move the target along the plan, drive the plan's motion plus a correction toward the target
        while (opMode.opModeIsActive() && (runtime.milliseconds() < (splineStartTime + drivetimeout))) {
            drivetrain.odoUpdate();
            Pose2D pose = drivetrain.odoPose();
            double rx = pose.getX(DistanceUnit.INCH), ry = pose.getY(DistanceUnit.INCH);
            double rh = Math.toRadians(pose.getHeading(AngleUnit.DEGREES));
            double dt = GetVelo(pose, runtime.seconds());

            //#region -Done? (FIX: absolute value of the difference, heading wrapped, AND slow)
            double ex = endX - rx, ey = endY - ry, eh = wrapRad(endH - rh);
            if (Math.hypot(ex, ey) < POS_TOL && Math.abs(Math.toDegrees(eh)) < HEADING_TOL && velocity < SPEED_TOL) {
                arrived = true;
                break;
            }
            //#endregion

            //#region -Move the target along the plan (NEW: replaces u / uIncrement / tracingPoint)
            //The target moves at the planned speed. If the robot is more than MAX_LAG behind it (bumped,
            // blocked), the target waits instead of running ahead (Beta's target ran away).
            double lag = (planX[idx] - rx) * planTx[idx] + (planY[idx] - ry) * planTy[idx];
            if (lag < MAX_LAG) {
                tPlan += dt;
            }
            idx = sampleAtTime(tPlan, idx);
            holding = tPlan >= planTime;
            //#endregion

            if (!holding) {
                //#region -Follow the plan (NEW): planned motion one loop ahead (covers the loop delay) + correction toward the target
                int f = sampleAtTime(tPlan + dt, idx);
                double v = planV[f], a = planA[f], bend = v * v * planK[f];
                double vx = v * planTx[f] + kPos * (planX[idx] - rx);
                double vy = v * planTy[f] + kPos * (planY[idx] - ry);
                double ax = a * planTx[f] - bend * planTy[f];
                double ay = a * planTy[f] + bend * planTx[f];
                double w = planHp[f] * v + kHead * wrapRad(planH[idx] - rh);
                double alpha = planHpp[f] * v * v + planHp[f] * a;
                //#endregion
                axUsed = ramp(axUsed, ax, MAX_ACCEL_FWD, dt);
                ayUsed = ramp(ayUsed, ay, MAX_ACCEL_FWD, dt);
                alphaUsed = ramp(alphaUsed, alpha, Math.toRadians(MAX_TURN_ACCEL), dt);
                setDriveFromField(vx, vy, w, axUsed, ayUsed, alphaUsed, rh);
            } else {
                // last stretch: plain position hold on the end pose
                axUsed = ramp(axUsed, 0, MAX_ACCEL_FWD, dt);
                ayUsed = ramp(ayUsed, 0, MAX_ACCEL_FWD, dt);
                alphaUsed = ramp(alphaUsed, 0, Math.toRadians(MAX_TURN_ACCEL), dt);
                setDriveFromField(kPos * ex, kPos * ey, kHead * eh, axUsed, ayUsed, alphaUsed, rh);
            }
            Log.d("Odo Pos","X"+drivetrain.odoPose().getX(DistanceUnit.INCH));
            Log.d("Odo Pos","Y"+drivetrain.odoPose().getY(DistanceUnit.INCH));
            Log.d("Odo Pos","Z"+drivetrain.odoPose().getHeading(AngleUnit.DEGREES));

            //#region -Logs For Plan Position, Speed and Error
            Log.d("Spline", String.format(Locale.US, "t %.3f plan t %.3f sample %d/%d plan v %.1f robot v %.1f | to end %.2f in %.1f deg%s",
                    runtime.seconds(), tPlan, idx, planN, planV[idx], velocity, Math.hypot(ex, ey), Math.toDegrees(eh), holding ? " HOLD" : ""));
            Log.d("TracingDist","TracingDistError"+Math.hypot(rx - planX[idx], ry - planY[idx]));
            //#endregion
        }

        DriveStop();
        Pos(points[points.length - 1]);
        //#region Logs For Final Data
        Log.d("Checkpoint", "final checkpoint");
        Log.d("Spline", " arrived " + arrived + " planned time " + planTime);
        Log.d("Spline", " finalruntime: " + runtime.milliseconds());
        Log.d("Spline", " finalstart time +drive timeout: " + (splineStartTime+drivetimeout));
        //#endregion
        return arrived;
    }

    //region - NEW: RushingSpline - same route follower, but it trades accuracy for speed on the first RUSH_FRACTION of the route
    // The route is split by distance. On the first part the plan may use more wheel power (RUSH_PLAN_POWER), the
    // target waits for the robot less often (RUSH_MAX_LAG), and the robot is corrected less hard for being off
    // to the side of the path or pointing slightly wrong (RUSH_CROSS_GAIN, RUSH_HEADING_GAIN). Over the last
    // RUSH_BLEND of that part everything fades back to the strict settings, so the last quarter is driven exactly
    // like SplineSequence, and it finishes with the same arrival test (POS_TOL, HEADING_TOL, SPEED_TOL).
    public boolean RushingSpline(double drivetimeout, Pose2D... points) {
        double splineStartTime = runtime.milliseconds();
        Pose2D actual = drivetrain.odoPose();
        Pose2D requested = points[0];
        Log.d("START_DEBUG", String.format(
                Locale.US,
                "Actual: (%.2f, %.2f, %.2f) | Requested: (%.2f, %.2f, %.2f)",
                actual.getX(DistanceUnit.INCH),
                actual.getY(DistanceUnit.INCH),
                actual.getHeading(AngleUnit.DEGREES),
                requested.getX(DistanceUnit.INCH),
                requested.getY(DistanceUnit.INCH),
                requested.getHeading(AngleUnit.DEGREES)
        ));

        buildTrajectory(points);
        planSpeedsRush();
        Log.d("Spline", "rushing plan: " + planN + " samples, " + planS[planN - 1] + " in, " + planTime + " s");

        double endX = planX[planN - 1], endY = planY[planN - 1], endH = planH[planN - 1];
        double totalS = Math.max(planS[planN - 1], 1e-9);
        double kPos = POS_GAIN * MAX_ACCEL_FWD / MAX_VEL_FWD;
        double kHead = HEADING_GAIN * MAX_TURN_ACCEL / MAX_TURN_RATE;
        double tPlan = 0;
        int idx = 0;
        boolean holding = false;
        boolean arrived = false;
        double axUsed = 0, ayUsed = 0, alphaUsed = 0;
        lastTimeVelo = -1;

        while (opMode.opModeIsActive() && (runtime.milliseconds() < (splineStartTime + drivetimeout))) {
            drivetrain.odoUpdate();
            Pose2D pose = drivetrain.odoPose();
            double rx = pose.getX(DistanceUnit.INCH), ry = pose.getY(DistanceUnit.INCH);
            double rh = Math.toRadians(pose.getHeading(AngleUnit.DEGREES));
            double dt = GetVelo(pose, runtime.seconds());

            // Done? Same strict test as SplineSequence, so the endpoint accuracy is unchanged
            double ex = endX - rx, ey = endY - ry, eh = wrapRad(endH - rh);
            if (Math.hypot(ex, ey) < POS_TOL && Math.abs(Math.toDegrees(eh)) < HEADING_TOL && velocity < SPEED_TOL) {
                arrived = true;
                break;
            }

            // How much of the rush settings apply at the target's current spot on the route
            double rush = rushWeight(planS[idx] / totalS);
            double maxLag = MAX_LAG + rush * (RUSH_MAX_LAG - MAX_LAG);
            double kCross = kPos * (1 + rush * (RUSH_CROSS_GAIN - 1));
            double kHeadNow = kHead * (1 + rush * (RUSH_HEADING_GAIN - 1));

            double lag = (planX[idx] - rx) * planTx[idx] + (planY[idx] - ry) * planTy[idx];
            if (lag < maxLag) {
                tPlan += dt;
            }
            idx = sampleAtTime(tPlan, idx);
            holding = tPlan >= planTime;

            if (!holding) {
                int f = sampleAtTime(tPlan + dt, idx);
                double v = planV[f], a = planA[f], bend = v * v * planK[f];
                // split the error to the target into along-the-path and off-to-the-side parts
                double tx = planTx[idx], ty = planTy[idx];
                double toX = planX[idx] - rx, toY = planY[idx] - ry;
                double eAlong = toX * tx + toY * ty;
                double eSide = -toX * ty + toY * tx;
                double vx = v * planTx[f] + kPos * eAlong * tx - kCross * eSide * ty;
                double vy = v * planTy[f] + kPos * eAlong * ty + kCross * eSide * tx;
                double ax = a * planTx[f] - bend * planTy[f];
                double ay = a * planTy[f] + bend * planTx[f];
                double w = planHp[f] * v + kHeadNow * wrapRad(planH[idx] - rh);
                double alpha = planHpp[f] * v * v + planHp[f] * a;
                axUsed = ramp(axUsed, ax, MAX_ACCEL_FWD, dt);
                ayUsed = ramp(ayUsed, ay, MAX_ACCEL_FWD, dt);
                alphaUsed = ramp(alphaUsed, alpha, Math.toRadians(MAX_TURN_ACCEL), dt);
                setDriveFromField(vx, vy, w, axUsed, ayUsed, alphaUsed, rh);
            } else {
                // last stretch: strict position hold on the end pose, same as SplineSequence
                axUsed = ramp(axUsed, 0, MAX_ACCEL_FWD, dt);
                ayUsed = ramp(ayUsed, 0, MAX_ACCEL_FWD, dt);
                alphaUsed = ramp(alphaUsed, 0, Math.toRadians(MAX_TURN_ACCEL), dt);
                setDriveFromField(kPos * ex, kPos * ey, kHead * eh, axUsed, ayUsed, alphaUsed, rh);
            }

            Log.d("Spline", String.format(Locale.US, "RUSH %.2f t %.3f plan t %.3f sample %d/%d plan v %.1f robot v %.1f | side err %.2f in | to end %.2f in %.1f deg%s",
                    rush, runtime.seconds(), tPlan, idx, planN, planV[idx], velocity,
                    (planX[idx] - rx) * -planTy[idx] + (planY[idx] - ry) * planTx[idx],
                    Math.hypot(ex, ey), Math.toDegrees(eh), holding ? " HOLD" : ""));
        }

        DriveStop();
        Pos(points[points.length - 1]);
        Log.d("Spline", " arrived " + arrived + " planned time " + planTime);
        return arrived;
    }

    // Same speed plan as planSpeeds(), but with a bigger wheel-power budget on the rush part of the route.
    public void planSpeedsRush() {
        planPower = new double[planN];
        double total = Math.max(planS[planN - 1], 1e-9);
        double rushPower = Math.min(1.0, RUSH_PLAN_POWER);
        for (int i = 0; i < planN; i++) {
            planPower[i] = PLAN_POWER + rushWeight(planS[i] / total) * (rushPower - PLAN_POWER);
        }
        planSpeedsFromBudget();
    }

    // 1 = fully rushing, 0 = fully strict. frac = how far along the route (0 to 1, by distance).
    double rushWeight(double frac) {
        if (frac >= RUSH_FRACTION) {
            return 0;
        }
        if (RUSH_BLEND <= 0 || frac <= RUSH_FRACTION - RUSH_BLEND) {
            return 1;
        }
        return (RUSH_FRACTION - frac) / RUSH_BLEND;
    }

    // Wheel-power budget at plan sample i (PLAN_POWER unless planSpeedsRush set a per-sample budget)
    double powerAt(int i) {
        return (planPower != null && i < planPower.length) ? planPower[i] : PLAN_POWER;
    }
    //endregion

    public void SplineMath(Pose2D[] pts, double U, int phase) {
        //#region -The 4 points this segment is built from (FIX: works for 2 to 4 waypoints)
        //Each segment needs one point before its start and one after its end, to know which way to
        // leave and arrive. At the ends of the route there is no real neighbour, so we use a phantom
        // point: the neighbour mirrored through the end point (FIX: replaces the atan-based phantoms).
        int last = pts.length - 1;
        double[] hDeg = unwrappedHeadings(pts);
        double p1x = pts[phase].getX(DistanceUnit.INCH);
        double p1y = pts[phase].getY(DistanceUnit.INCH);
        double p1h = hDeg[phase];
        double p2x = pts[phase + 1].getX(DistanceUnit.INCH);
        double p2y = pts[phase + 1].getY(DistanceUnit.INCH);
        double p2h = hDeg[phase + 1];
        double p0x;
        double p0y;
        double p0h;
        double p3x;
        double p3y;
        double p3h;
        if (phase > 0) {
            p0x = pts[phase - 1].getX(DistanceUnit.INCH);
            p0y = pts[phase - 1].getY(DistanceUnit.INCH);
            p0h = hDeg[phase - 1];
        } else {
            p0x = 2 * p1x - p2x;
            p0y = 2 * p1y - p2y;
            p0h = 2 * p1h - p2h;
        }
        if (phase + 2 <= last) {
            p3x = pts[phase + 2].getX(DistanceUnit.INCH);
            p3y = pts[phase + 2].getY(DistanceUnit.INCH);
            p3h = hDeg[phase + 2];
        } else {
            p3x = 2 * p2x - p1x;
            p3y = 2 * p2y - p1y;
            p3h = 2 * p2h - p1h;
        }
        //#endregion

        //#region Variables For T, The Little Waypoints in Each Segment
        //FIX: the spacing comes from the 4 points of THIS segment (it used to always use the middle
        // segment's points, which put a kink at every middle waypoint). Heading uses the same spacing.
        double t0 = 0;
        double t1 = tnext(t0, p0x, p0y, p1x, p1y);
        double t2 = tnext(t1, p1x, p1y, p2x, p2y);
        double t3 = tnext(t2, p2x, p2y, p3x, p3y);

        double t1h = t1;
        double t2h = t2;
        double t3h = t3;

        double t = t1 + (U * (t2 - t1));
        double th = t;
        //#endregion
        //#region Calculations, Based on T, To come up with The X, Y, and Z coordinate for each Point
        //Calculations based off the barry goldman pyramid, where you sube in all of your little waypoints,
        // solve this pyramid of equations to get each x, y, and heading for each point along the spline,
        // based on U. U is a decimal between 0, and one, which tells this calculation how far along
        // the spline the point is, 0 is the start, and 1 is the end point
        double A1x = (p0x * ((t1 - t) / ((t1 - t0) + Math.pow(Math.E, -10)))) + (p1x * ((t - t0) / ((t1 - t0) + Math.pow(Math.E, -10))));
        double A2x = (p1x * ((t2 - t) / ((t2 - t1) + Math.pow(Math.E, -10))) + (p2x * ((t - t1) / ((t2 - t1) + Math.pow(Math.E, -10)))));
        double A3x = (p2x * ((t3 - t) / ((t3 - t2) + Math.pow(Math.E, -10)))) + (p3x * ((t - t2) / ((t3 - t2) + Math.pow(Math.E, -10))));

        double B1x = (A1x * ((t2 - t) / ((t2 - t0) + Math.pow(Math.E, -10))) + (A2x * ((t - t0) / ((t2 - t0) + Math.pow(Math.E, -10)))));
        double B2x = (A2x * ((t3 - t) / ((t3 - t1) + Math.pow(Math.E, -10)))) + (A3x * ((t - t1) / ((t3 - t1) + Math.pow(Math.E, -10))));

        double Cx = (B1x * ((t2 - t) / ((t2 - t1) + Math.pow(Math.E, -10)))) + (B2x * ((t - t1) / ((t2 - t1) + Math.pow(Math.E, -10))));

        double A1y = (p0y * ((t1 - t) / ((t1 - t0) + Math.pow(Math.E, -10)))) + (p1y * ((t - t0) / ((t1 - t0) + Math.pow(Math.E, -10))));
        double A2y = (p1y * ((t2 - t) / ((t2 - t1) + Math.pow(Math.E, -10))) + (p2y * ((t - t1) / ((t2 - t1) + Math.pow(Math.E, -10)))));
        double A3y = (p2y * ((t3 - t) / ((t3 - t2) + Math.pow(Math.E, -10)))) + (p3y * ((t - t2) / ((t3 - t2) + Math.pow(Math.E, -10))));

        double B1y = (A1y * ((t2 - t) / ((t2 - t0) + Math.pow(Math.E, -10))) + (A2y * ((t - t0) / ((t2 - t0) + Math.pow(Math.E, -10)))));
        double B2y = (A2y * ((t3 - t) / ((t3 - t1) + Math.pow(Math.E, -10)))) + (A3y * ((t - t1) / ((t3 - t1) + Math.pow(Math.E, -10))));

        double Cy = (B1y * ((t2 - t) / ((t2 - t1) + Math.pow(Math.E, -10)))) + (B2y * ((t - t1) / ((t2 - t1) + Math.pow(Math.E, -10))));

        double A1h = (p0h * ((t1h - th) / ((t1h - t0) + Math.pow(Math.E, -10)))) + (p1h * ((th - t0) / ((t1h - t0) + Math.pow(Math.E, -10))));
        double A2h = (p1h * ((t2h - th) / ((t2h - t1h) + Math.pow(Math.E, -10))) + (p2h * ((th - t1h) / ((t2h - t1h) + Math.pow(Math.E, -10)))));
        double A3h = (p2h * ((t3h - th) / ((t3h - t2h) + Math.pow(Math.E, -10)))) + (p3h * ((th - t2h) / ((t3h - t2h) + Math.pow(Math.E, -10))));

        double B1h = (A1h * ((t2h - th) / ((t2h - t0) + Math.pow(Math.E, -10))) + (A2h * ((th - t0) / ((t2h - t0) + Math.pow(Math.E, -10)))));
        double B2h = (A2h * ((t3h - th) / ((t3h - t1h) + Math.pow(Math.E, -10)))) + (A3h * ((th - t1h) / ((t3h - t1h) + Math.pow(Math.E, -10))));

        double Ch = (B1h * ((t2h - th) / ((t2h - t1h) + Math.pow(Math.E, -10)))) + (B2h * ((th - t1h) / ((t2h - t1h) + Math.pow(Math.E, -10))));
        //#endregion

        //FIX: the corner power ladder (220/160/60 deg) and the u increment are gone; planSpeeds does that job
        outX = Cx;
        outY = Cy;
        outH = Ch;
        splineOutput = new Pose2D(DistanceUnit.INCH, Cx, Cy, AngleUnit.DEGREES, Ch);
    }


    //#region Method to calculate The Waypoints T
    //FIX: takes the two points as numbers (so phantom points work too). alpha 0.5 = centripetal.
    public double tnext(double tat, double ax, double ay, double bx, double by) {
        double alpha = 0.5; //uniform = 0, centripetal = 0.5, chordal = 1
        double tdist = Math.max(Math.hypot(bx - ax, by - ay), 0.01);
        return Math.pow(tdist, alpha) + tat;
    }
    //#endregion

    // NEW: waypoint headings in degrees, each one moved by whole turns so every turn takes the short way
    double[] unwrappedHeadings(Pose2D[] pts) {
        double[] h = new double[pts.length];
        h[0] = pts[0].getHeading(AngleUnit.DEGREES);
        for (int i = 1; i < pts.length; i++) {
            h[i] = h[i - 1] + Math.toDegrees(wrapRad(Math.toRadians(pts[i].getHeading(AngleUnit.DEGREES) - h[i - 1])));
        }
        return h;
    }

    //region - NEW: buildTrajectory - sample the curve densely: position, direction, bend and heading rates
    public void buildTrajectory(Pose2D... pts) {
        int segs = pts.length - 1;
        int m = SAMPLES_PER_SEGMENT;
        planN = segs * m + 1;
        planS = new double[planN]; planX = new double[planN]; planY = new double[planN]; planH = new double[planN];
        planTx = new double[planN]; planTy = new double[planN]; planK = new double[planN];
        planHp = new double[planN]; planHpp = new double[planN];
        double d = 0.2 / m;   // small step in U to measure slopes
        int i = 0;
        for (int phase = 0; phase < segs; phase++) {
            for (int j = (phase == 0 ? 0 : 1); j <= m; j++) {
                double U = j / (double) m;
                SplineMath(pts, U - d, phase);
                double xa = outX, ya = outY, ha = Math.toRadians(outH);
                SplineMath(pts, U + d, phase);
                double xb = outX, yb = outY, hb = Math.toRadians(outH);
                SplineMath(pts, U, phase);
                double x = outX, y = outY, h = Math.toRadians(outH);
                // rates of change along U, then converted to "per inch of path"
                double dx = (xb - xa) / (2 * d), dy = (yb - ya) / (2 * d), dh = (hb - ha) / (2 * d);
                double ddx = (xb - 2 * x + xa) / (d * d), ddy = (yb - 2 * y + ya) / (d * d), ddh = (hb - 2 * h + ha) / (d * d);
                double sp = Math.max(Math.hypot(dx, dy), 1e-9);   // inches of path per unit of U
                double spp = (dx * ddx + dy * ddy) / sp;
                planX[i] = x;
                planY[i] = y;
                planH[i] = h;
                planTx[i] = dx / sp;
                planTy[i] = dy / sp;
                planK[i] = (dx * ddy - dy * ddx) / (sp * sp * sp);
                planHp[i] = dh / sp;
                planHpp[i] = (ddh * sp - dh * spp) / (sp * sp * sp);
                planS[i] = (i == 0) ? 0 : planS[i - 1] + Math.hypot(x - planX[i - 1], y - planY[i - 1]);
                i++;
            }
        }
    }
    //endregion

    //region - NEW: planSpeeds - the fastest speed at every sample that the wheels can actually deliver
    // Start and end stopped. Speeding up and slowing down are limited by what the motors can do at that
    // speed (from the CALIBRATION numbers), and every bend by the sideways push it needs.
    public void planSpeeds() {
        planPower = null;                        // normal route: PLAN_POWER at every sample
        planSpeedsFromBudget();
    }

    // The speed plan itself. The wheel-power budget at each sample comes from powerAt(i).
    void planSpeedsFromBudget() {
        planV = new double[planN];
        planA = new double[planN];
        for (int i = 0; i < planN; i++) {
            planV[i] = maxSpeedAt(i);
        }
        planV[0] = 0;
        planV[planN - 1] = 0;
        for (int i = 1; i < planN; i++) {            // speeding up: as fast as the motors allow
            double ds = planS[i] - planS[i - 1];
            double a = accelRange(i - 1, planV[i - 1])[1];
            planV[i] = Math.min(planV[i], Math.sqrt(Math.max(0, planV[i - 1] * planV[i - 1] + 2 * a * ds)));
        }
        for (int i = planN - 2; i >= 0; i--) {       // slowing down: start braking early enough
            double ds = planS[i + 1] - planS[i];
            double a = accelRange(i + 1, planV[i + 1])[0];
            planV[i] = Math.min(planV[i], Math.sqrt(Math.max(0, planV[i + 1] * planV[i + 1] - 2 * a * ds)));
        }
        planT = new double[planN];
        for (int i = 0; i < planN - 1; i++) {
            double ds = planS[i + 1] - planS[i];
            planT[i + 1] = planT[i];
            if (ds > 1e-9) {
                planA[i] = (planV[i + 1] * planV[i + 1] - planV[i] * planV[i]) / (2 * ds);
                planT[i + 1] += 2 * ds / Math.max(planV[i] + planV[i + 1], 1e-6);
            }
        }
        planTime = planT[planN - 1];
    }

    // Highest speed at sample i where some acceleration still keeps every wheel inside the power budget
    double maxSpeedAt(int i) {
        double lo = 0, hi = 1.5 * Math.max(MAX_VEL_FWD, MAX_VEL_STRAFE);
        if (feasible(i, hi)) {
            return hi;
        }
        for (int k = 0; k < 40; k++) {
            double mid = 0.5 * (lo + hi);
            if (feasible(i, mid)) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    boolean feasible(int i, double v) {
        double[] r = accelRange(i, v);
        return r[0] <= r[1];
    }

    // {lowest, highest} acceleration along the path at sample i, at speed v, that keeps every wheel's power
    // inside +-(the power budget at sample i). Wheel power = the same robot model setDriveFromField uses.
    double[] accelRange(int i, double v) {
        double c = Math.cos(planH[i]), sn = Math.sin(planH[i]);
        double tf = planTx[i] * c + planTy[i] * sn, tl = -planTx[i] * sn + planTy[i] * c;   // path direction, robot frame
        double nf = -tl, nl = tf;                                                           // 90 deg to its left
        double bend = v * v * planK[i];                                                     // sideways accel the bend needs
        double turnRate = Math.toRadians(MAX_TURN_RATE), turnAccel = Math.toRadians(MAX_TURN_ACCEL);
        // power on each axis = c + d * (acceleration along the path)
        double cF = v * tf / MAX_VEL_FWD + bend * nf / MAX_ACCEL_FWD + staticPower(v * tf, 1);
        double dF = tf / MAX_ACCEL_FWD;
        double cL = v * tl / MAX_VEL_STRAFE + bend * nl / MAX_ACCEL_STRAFE + staticPower(v * tl, 1);
        double dL = tl / MAX_ACCEL_STRAFE;
        double cZ = planHp[i] * v / turnRate + planHpp[i] * v * v / turnAccel + staticPower(Math.toDegrees(planHp[i] * v), 5);
        double dZ = planHp[i] / turnAccel;
        // same wheel mixing as Drive(): fl = Y + X - Z, fr = Y - X + Z, bl = Y - X - Z, br = Y + X + Z
        double[] cs = {cL + cF - cZ, cL - cF + cZ, cL - cF - cZ, cL + cF + cZ};
        double[] ds = {dL + dF - dZ, dL - dF + dZ, dL - dF - dZ, dL + dF + dZ};
        double pw = powerAt(i);                                   // wheel power this sample may use
        double lo = -1e9, hi = 1e9;
        for (int k = 0; k < 4; k++) {
            if (Math.abs(ds[k]) < 1e-12) {
                if (Math.abs(cs[k]) > pw) {
                    return new double[]{1, -1};
                }
            } else {
                double a1 = (pw - cs[k]) / ds[k], a2 = (-pw - cs[k]) / ds[k];
                lo = Math.max(lo, Math.min(a1, a2));
                hi = Math.min(hi, Math.max(a1, a2));
            }
        }
        return new double[]{lo, hi};
    }
    //endregion

    //region - NEW: helpers for following the plan
    // First plan sample at or past planned time t (searching forward from "from")
    int sampleAtTime(double t, int from) {
        int i = from;
        while (i < planN - 1 && planT[i] < t) {
            i++;
        }
        return i;
    }

    // Wanted motion in the field frame (in/s, rad/s, in/s^2, rad/s^2) -> wheel powers, using the measured model:
    // power = speed / top speed + acceleration / acceleration-from-rest (+ friction), on each robot axis,
    // plus VEL_GAIN times the speed error (wanted minus measured by GetVelo)
    public void setDriveFromField(double vx, double vy, double w, double ax, double ay, double alpha, double heading) {
        double c = Math.cos(heading), sn = Math.sin(heading);
        double vf = vx * c + vy * sn, vl = -vx * sn + vy * c;     // robot frame: forward, left
        double af = ax * c + ay * sn, al = -ax * sn + ay * c;
        double ef = (vx - velX) * c + (vy - velY) * sn, el = -(vx - velX) * sn + (vy - velY) * c;
        double x = (vf + VEL_GAIN * ef) / MAX_VEL_FWD + af / MAX_ACCEL_FWD + staticPower(vf, 1);
        double y = (vl + VEL_GAIN * el) / MAX_VEL_STRAFE + al / MAX_ACCEL_STRAFE + staticPower(vl, 1);
        double z = Math.toDegrees(w + VEL_GAIN * (w - velH)) / MAX_TURN_RATE + Math.toDegrees(alpha) / MAX_TURN_ACCEL
                + staticPower(Math.toDegrees(w), 5);
        setDrivePowers(x, y, z);
    }

    // x = forward, y = left, z = turn left. Same mixing as Drive(), but if any wheel would go past full
    // power, all four are scaled down together so the direction stays right (Drive clips each one).
    public void setDrivePowers(double x, double y, double z) {
        double fl = y + x - z, fr = y - x + z, bl = y - x - z, br = y + x + z;
        double m = Math.max(1, Math.max(Math.max(Math.abs(fl), Math.abs(fr)), Math.max(Math.abs(bl), Math.abs(br))));
        drivetrain.setFL(fl/m);
        drivetrain.setFr(fr/m);
        drivetrain.setBl(bl/m);
        drivetrain.setBr(br/m);
    }

    // Friction: K_STATIC in the direction of motion, faded in over the first "ramp" of speed so it doesn't buzz at rest
    double staticPower(double speed, double ramp) {
        return K_STATIC * Range.clip(speed / ramp, -1, 1);
    }

    // Moves "from" toward "to", but no faster than a full swing (-full to +full) in ACCEL_RAMP seconds
    double ramp(double from, double to, double full, double dt) {
        if (ACCEL_RAMP <= 0) {
            return to;
        }
        double step = 2 * full * dt / ACCEL_RAMP;
        return from + Range.clip(to - from, -step, step);
    }

    double wrapRad(double a) {
        while (a > Math.PI) a -= 2 * Math.PI;
        while (a < -Math.PI) a += 2 * Math.PI;
        return a;
    }

    boolean calibrated() {
        return MAX_VEL_FWD > 0 && MAX_ACCEL_FWD > 0 && MAX_VEL_STRAFE > 0 && MAX_ACCEL_STRAFE > 0
                && MAX_TURN_RATE > 0 && MAX_TURN_ACCEL > 0;
    }
    //endregion

    //Logs for position
    public void Pos(Pose2D poscheck) {
        Log.d("PositionCheck", "Target (x, y, z): " + poscheck.getX(DistanceUnit.INCH) + ", " +
                poscheck.getY(DistanceUnit.INCH) + ", " + poscheck.getHeading(AngleUnit.DEGREES));
        Log.d("PositionCheck", "Current (x, y, z): " + drivetrain.odoPose().getX(
                DistanceUnit.INCH) + ", " + drivetrain.odoPose().getY(DistanceUnit.INCH) + ", " +
                drivetrain.odoPose().getHeading(AngleUnit.DEGREES));
    }

    //FIX: the motors are already in BRAKE and the robot is already stopped, so no 500 ms wait
    public void DriveStop() {
        drivetrain.setBr(0);
        drivetrain.setFr(0);
        drivetrain.setBl(0);
        drivetrain.setFL(0);
    }

    //FIX: velocity per SECOND (field frame), measured over the last VEL_WINDOW seconds (one loop's movement
    // is too noisy: it made the wheel powers chatter). Returns the loop time in seconds.
    public double GetVelo(Pose2D currentPos, double currentTime) {
        double x = currentPos.getX(DistanceUnit.INCH), y = currentPos.getY(DistanceUnit.INCH);
        double h = Math.toRadians(currentPos.getHeading(AngleUnit.DEGREES));
        double deltaT = currentTime - lastTimeVelo;
        if (lastTimeVelo < 0) {
            histN = 0;
            deltaT = 0;
        }
        double hu = histN == 0 ? h : histH[histN - 1] + wrapRad(h - lastHeadingVelo);   // heading, not wrapped
        if (histN == histT.length) {
            System.arraycopy(histT, 1, histT, 0, histN - 1);
            System.arraycopy(histX, 1, histX, 0, histN - 1);
            System.arraycopy(histY, 1, histY, 0, histN - 1);
            System.arraycopy(histH, 1, histH, 0, histN - 1);
            histN--;
        }
        histT[histN] = currentTime;
        histX[histN] = x;
        histY[histN] = y;
        histH[histN] = hu;
        histN++;
        int k = histN - 1;
        while (k > 0 && currentTime - histT[k] < VEL_WINDOW) {
            k--;
        }
        double span = currentTime - histT[k];
        if (span > 1e-4) {
            velX = (x - histX[k]) / span;
            velY = (y - histY[k]) / span;
            velH = (hu - histH[k]) / span;
        } else {
            velX = 0;
            velY = 0;
            velH = 0;
        }
        lastPoseVelo = currentPos;
        lastHeadingVelo = h;
        lastTimeVelo = currentTime;
        velocity = Math.hypot(velX, velY);
        Log.d("Conversion","Velocity"+velocity);
        return deltaT;
    }

    //region - NEW: measureDrive - calibration run (needs about 3 ft clear on every side of the robot)
    // Short full-power bursts (and the braking after each) forward/back, left/right and spinning both ways.
    // Prints the numbers to paste into the CALIBRATION region at the top.
    public void measureDrive() {
        double[] fwd = measureAxis(1, 0, 0);
        double[] side = measureAxis(0, 1, 0);
        double[] turn = measureAxis(0, 0, 1);
        double ks = Math.max(0, (fwd[2] + side[2]) / 2);
        calResult = new double[]{fwd[0], fwd[1], side[0], side[1], turn[0], turn[1], ks};
        String[] lines = {
                String.format(Locale.US, "MAX_VEL_FWD = %.1f;", fwd[0]),
                String.format(Locale.US, "MAX_ACCEL_FWD = %.0f;", fwd[1]),
                String.format(Locale.US, "MAX_VEL_STRAFE = %.1f;", side[0]),
                String.format(Locale.US, "MAX_ACCEL_STRAFE = %.0f;", side[1]),
                String.format(Locale.US, "MAX_TURN_RATE = %.0f;", turn[0]),
                String.format(Locale.US, "MAX_TURN_ACCEL = %.0f;", turn[1]),
                String.format(Locale.US, "K_STATIC = %.3f;", ks)};
        for (String line : lines) {
            Log.d("SplineCal", line);
        }
        while (opMode.opModeIsActive()) {
            for (String line : lines) {
                Log.d("Calibration",line);
            }

            opMode.sleep(200);
        }
    }

    // One axis (x = forward, y = left, z = turn): a full-power burst, then zero power (BRAKE) until stopped;
    // out and then back so the robot ends near where it started. Returns {top speed, accel from rest}
    // (deg/s and deg/s^2 for turning) and the friction power. The robot never needs to reach top speed: see fitMotor.
    double[] measureAxis(double x, double y, double z) {
        double top = 0, acc = 0, fric = 0;
        for (int dir = 1; dir >= -1; dir -= 2) {
            double[] ts = new double[1500], xs = new double[1500], ps = new double[1500];
            int n = 0;
            drivetrain.odoUpdate();
            Pose2D p0 = drivetrain.odoPose();
            lastTimeVelo = -1;
            GetVelo(p0, runtime.seconds());
            double h0 = Math.toRadians(p0.getHeading(AngleUnit.DEGREES)), hPrev = h0, turnedSigned = 0;
            double t0 = runtime.seconds(), turned = 0, moved = 0, along, power = 1, tStop = 0, v;
            setDrivePowers(dir * x, dir * y, dir * z);
            while (opMode.opModeIsActive() && n < ts.length) {
                drivetrain.odoUpdate();
                Pose2D p =drivetrain.odoPose();
                double dt = GetVelo(p, runtime.seconds());
                double h = Math.toRadians(p.getHeading(AngleUnit.DEGREES));
                if (z != 0) {
                    v = Math.toDegrees(velH);
                } else if (x != 0) {
                    v = velX * Math.cos(h) + velY * Math.sin(h);
                } else {
                    v = -velX * Math.sin(h) + velY * Math.cos(h);
                }
                double ddx = p.getX(DistanceUnit.INCH) - p0.getX(DistanceUnit.INCH), ddy = p.getY(DistanceUnit.INCH) - p0.getY(DistanceUnit.INCH);
                turnedSigned += Math.toDegrees(wrapRad(h - hPrev));
                hPrev = h;
                if (z != 0) {
                    along = dir * turnedSigned;   // distance (or degrees) travelled along this axis
                } else if (x != 0) {
                    along = dir * (ddx * Math.cos(h0) + ddy * Math.sin(h0));
                } else {
                    along = dir * (-ddx * Math.sin(h0) + ddy * Math.cos(h0));
                }
                turned = Math.abs(turnedSigned);
                moved = Math.hypot(ddx, ddy);
                ts[n] = runtime.seconds() - t0;
                xs[n] = along;
                ps[n] = power;
                n++;
                if (power == 1 && (ts[n - 1] > CAL_BURST_S || (z != 0 ? turned > CAL_MAX_TURN : moved > CAL_MAX_TRAVEL))) {
                    power = 0;                     // burst over: zero power (BRAKE) and keep recording the slow-down
                    tStop = ts[n - 1];
                    setDrivePowers(0, 0, 0);
                } else if (power == 0 && (Math.abs(v) < 0.5 || ts[n - 1] - tStop > 1.5)) {
                    break;
                }
            }
            setDrivePowers(0, 0, 0);
            waitUntilStopped();
            double[] fit = fitMotor(ts, xs, ps, n);
            top += fit[0] / 2;
            acc += fit[1] / 2;
            fric += fit[2] / 2;
            Log.d("SplineCal", String.format(Locale.US, "axis %.0f %.0f %.0f dir %d: top %.1f accel %.0f friction %.3f (%d samples)", x, y, z, dir, fit[0], fit[1], fit[2], n));
        }
        return new double[]{top, acc, fric};
    }

    // Motor model: power = speed / top + acceleration / accel-from-rest + friction. Rearranged:
    // acceleration = A * power - (A / top) * speed - A * friction. Every sample (full power while speeding
    // up, zero power while braking) gives one equation; a least-squares fit gives {top, A, friction}.
    // Speed and acceleration come from the distance travelled over a fixed time window, so the result
    // doesn't depend on how fast the loop runs. If the fitted top speed is more than 10% above the fastest
    // speed actually seen (e.g. wheels slipping at the start), top is capped there and A and friction refitted.
    double[] fitMotor(double[] ts, double[] xs, double[] ps, int n) {
        double w = CAL_WINDOW;
        double[] vs = new double[n], as = new double[n];
        boolean[] use = new boolean[n];
        double seen = 0;
        for (int i = 0, lo = 0, hi = 0; i < n; i++) {
            while (ts[i] - ts[lo] > w) lo++;
            while (hi < n - 1 && ts[hi] - ts[i] < w) hi++;
            if (ts[i] - ts[lo] < 0.75 * w || ts[hi] - ts[i] < 0.75 * w || ps[lo] != ps[hi]) {
                continue;   // too close to an end, or the power changed inside the window
            }
            double tl = ts[i] - ts[lo], th = ts[hi] - ts[i];
            double vl = (xs[i] - xs[lo]) / tl, vh = (xs[hi] - xs[i]) / th;
            vs[i] = (vl * th + vh * tl) / (tl + th);
            as[i] = 2 * (vh - vl) / (tl + th);
            use[i] = true;
            if (ps[i] == 1) {
                seen = Math.max(seen, vs[i]);
            }
        }
        double[] c = fitAccel(vs, as, ps, use, 0);
        double top = -c[0] / c[1];
        if (!(top > 0 && top < 1.1 * seen)) {
            top = 1.1 * seen;
            c = fitAccel(vs, as, ps, use, top);
            return new double[]{top, c[0], -c[1] / c[0]};
        }
        return new double[]{top, c[0], -c[2] / c[0]};
    }

    // Least squares for the samples' acceleration a. top = 0: a = c0 * power + c1 * speed + c2.
    // top > 0 (known): a = c0 * (power - speed / top) + c1.
    double[] fitAccel(double[] vs, double[] as, double[] ps, boolean[] use, double top) {
        int k = top > 0 ? 2 : 3;
        double[][] m = new double[k][k + 1];
        for (int i = 0; i < vs.length; i++) {
            if (!use[i]) {
                continue;
            }
            double[] r = top > 0 ? new double[]{ps[i] - vs[i] / top, 1} : new double[]{ps[i], vs[i], 1};
            for (int j = 0; j < k; j++) {
                for (int q = 0; q < k; q++) {
                    m[j][q] += r[j] * r[q];
                }
                m[j][k] += r[j] * as[i];
            }
        }
        for (int col = 0; col < k; col++) {     // solve (Gaussian elimination)
            for (int row = col + 1; row < k; row++) {
                double f = m[row][col] / m[col][col];
                for (int q = col; q <= k; q++) {
                    m[row][q] -= f * m[col][q];
                }
            }
        }
        double[] c = new double[k];
        for (int row = k - 1; row >= 0; row--) {
            double sum = m[row][k];
            for (int q = row + 1; q < k; q++) {
                sum -= m[row][q] * c[q];
            }
            c[row] = sum / m[row][row];
        }
        return c;
    }

    void waitUntilStopped() {
        double t0 = runtime.seconds();
        lastTimeVelo = -1;
        while (opMode.opModeIsActive() && runtime.seconds() - t0 < 1.5) {
            drivetrain.odoUpdate();
            GetVelo(drivetrain.odoPose(), runtime.seconds());
            if (runtime.seconds() - t0 > 0.1 && velocity < 0.5 && Math.abs(Math.toDegrees(velH)) < 3) {
                break;
            }
        }
    }
    //endregion
}
