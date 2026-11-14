package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;

/**
 * Measures max translational (X/Y) and rotational acceleration + deceleration using the
 * Pinpoint odometry pods. Needs ~3 ft of clear space in every direction around the robot.
 *
 * Sequence (auto-runs on START, robot returns near its start spot between tests):
 *   1. X-axis burst (full power ~16 in) -> full reverse brake -> creep back to start
 *   2. Y-axis burst -> brake -> creep back
 *   3. Spin burst (full Z ~1.2 s) -> reverse brake -> creep back to heading 0
 *
 * Acceleration = steepest velocity slope over an 80 ms window (odo velocity, not motor
 * encoders, so wheel slip during the launch shows up as lower measured accel — that IS the
 * real usable acceleration). Results stay on telemetry until STOP, and are appended to
 * /sdcard/FIRST/maxAccelResults.txt and logcat tag "MaxAccel".
 */
@TeleOp(name = "Max Accel Test (odo)", group = "Test")
@Disabled
public class MaxAccelTest extends LinearOpMode {

    DecodeConstants dec = new DecodeConstants();

    // Space budget: 16 in powered burst + ~8 in braking keeps us well inside 3 ft.
    static final double BURST_TRAVEL_IN = 32;
    static final double BURST_TIMEOUT_S = 1.5;
    static final double BRAKE_TIMEOUT_S = 1.5;
    static final double SPEED_STOPPED_IN_S = 3;
    static final double ROT_BURST_S = 1.2;
    static final double ROT_STOPPED_DEG_S = 30;
    static final double SLOPE_WINDOW_S = 0.08;
    static final double RETURN_TIMEOUT_S = 6;
    static final double RETURN_MAX_POWER = 0.35;
    static final double G_IN_S2 = 386.09;

    /** time/value samples; acceleration = steepest secant slope over a sliding window */
    static class Trace {
        double[] t = new double[8000], v = new double[8000];
        int n = 0;
        void add(double time, double val) { if (n < t.length) { t[n] = time; v[n] = val; n++; } }
        double maxVal() { double m = 0; for (int i = 0; i < n; i++) m = Math.max(m, v[i]); return m; }
        double maxSlope(double window) { return bestSlope(window, +1); }
        double minSlope(double window) { return bestSlope(window, -1); }
        double bestSlope(double window, int sign) {
            double best = 0; int j = 0;
            for (int i = 1; i < n; i++) {
                while (t[i] - t[j] > window && j < i - 1) j++;
                double dt = t[i] - t[j];
                if (dt >= window * 0.5) {
                    double s = (v[i] - v[j]) / dt * sign;
                    if (s > best) best = s;
                }
            }
            return best * sign;
        }
    }

    @Override
    public void runOpMode() {
        dec.fl = hardwareMap.get(DcMotor.class, "fl");
        dec.fr = hardwareMap.get(DcMotor.class, "fr");
        dec.bl = hardwareMap.get(DcMotor.class, "bl");
        dec.br = hardwareMap.get(DcMotor.class, "br");
        dec.odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");

        dec.fl.setDirection(DcMotor.Direction.REVERSE);
        dec.bl.setDirection(DcMotor.Direction.REVERSE);
        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        dec.configureOdo(true);
        sleep(300); // Pinpoint IMU recalibration after resetPosAndIMU

        telemetry.addLine("Max accel test — needs 3 ft clear in EVERY direction.");
        telemetry.addLine("Runs: X burst, Y burst, spin. Robot returns to start between tests.");
        telemetry.addLine("Press START when the area is clear.");
        telemetry.update();
        waitForStart();
        if (isStopRequested()) return;

        double[] xRes = measureTranslation("X axis", 1, 0);
        returnToStart();
        double[] yRes = measureTranslation("Y axis", 0, 1);
        returnToStart();
        double[] rotRes = measureRotation();
        returnToStart();

        String summary =
                String.format("X axis:  top %.1f in/s | accel %.0f in/s2 (%.2f g) | brake %.0f in/s2 (%.2f g)%n",
                        xRes[0], xRes[1], xRes[1] / G_IN_S2, xRes[2], xRes[2] / G_IN_S2) +
                String.format("Y axis:  top %.1f in/s | accel %.0f in/s2 (%.2f g) | brake %.0f in/s2 (%.2f g)%n",
                        yRes[0], yRes[1], yRes[1] / G_IN_S2, yRes[2], yRes[2] / G_IN_S2) +
                String.format("Spin:    top %.0f deg/s | accel %.0f deg/s2 | brake %.0f deg/s2%n",
                        rotRes[0], rotRes[1], rotRes[2]);
        Log.d("MaxAccel", summary);
        try (java.io.FileWriter fw = new java.io.FileWriter("/sdcard/FIRST/maxAccelResults.txt", true)) {
            fw.write(summary + "----\n");
        } catch (Exception e) {
            Log.d("MaxAccel", "file write failed: " + e);
        }

        while (opModeIsActive()) {
            telemetry.addLine("RESULTS (also in /sdcard/FIRST/maxAccelResults.txt)");
            telemetry.addData("X top speed", "%.1f in/s", xRes[0]);
            telemetry.addData("X accel", "%.0f in/s2  (%.2f g)", xRes[1], xRes[1] / G_IN_S2);
            telemetry.addData("X brake", "%.0f in/s2  (%.2f g)", xRes[2], xRes[2] / G_IN_S2);
            telemetry.addData("Y top speed", "%.1f in/s", yRes[0]);
            telemetry.addData("Y accel", "%.0f in/s2  (%.2f g)", yRes[1], yRes[1] / G_IN_S2);
            telemetry.addData("Y brake", "%.0f in/s2  (%.2f g)", yRes[2], yRes[2] / G_IN_S2);
            telemetry.addData("Spin top speed", "%.0f deg/s", rotRes[0]);
            telemetry.addData("Spin accel", "%.0f deg/s2", rotRes[1]);
            telemetry.addData("Spin brake", "%.0f deg/s2", rotRes[2]);
            telemetry.update();
            sleep(100);
        }
    }

    /** Same mixing as DecodeAutoChamps: fl=Y+X-Z, fr=Y-X+Z, bl=Y-X-Z, br=Y+X+Z */
    void drive(double x, double y, double z) {
        dec.fl.setPower(Range.clip(y + x - z, -1, 1));
        dec.fr.setPower(Range.clip(y - x + z, -1, 1));
        dec.bl.setPower(Range.clip(y - x - z, -1, 1));
        dec.br.setPower(Range.clip(y + x + z, -1, 1));
    }

    double posX() { return dec.odo.getPosition().getX(DistanceUnit.INCH); }
    double posY() { return dec.odo.getPosition().getY(DistanceUnit.INCH); }
    double headingDeg() { return dec.odo.getPosition().getHeading(AngleUnit.DEGREES); }
    double speedInS() {
        return Math.hypot(dec.odo.getVelX(DistanceUnit.INCH), dec.odo.getVelY(DistanceUnit.INCH));
    }

    /** returns {top speed in/s, peak accel in/s2, peak braking decel in/s2} */
    double[] measureTranslation(String name, double px, double py) {
        dec.odo.update();
        double x0 = posX(), y0 = posY();

        Trace accel = new Trace();
        ElapsedTime timer = new ElapsedTime();
        drive(px, py, 0);
        while (opModeIsActive()) {
            dec.odo.update();
            double dist = Math.hypot(posX() - x0, posY() - y0);
            accel.add(timer.seconds(), speedInS());
            telemetry.addData("test", "%s burst  %.1f in  %.1f in/s", name, dist, speedInS());
            telemetry.update();
            if (dist >= BURST_TRAVEL_IN || timer.seconds() > BURST_TIMEOUT_S) break;
        }

        Trace brake = new Trace();
        ElapsedTime bTimer = new ElapsedTime();
        drive(-px, -py, 0);
        while (opModeIsActive()) {
            dec.odo.update();
            brake.add(bTimer.seconds(), speedInS());
            if (speedInS() < SPEED_STOPPED_IN_S || bTimer.seconds() > BRAKE_TIMEOUT_S) break;
        }
        drive(0, 0, 0);
        sleep(400);

        return new double[]{accel.maxVal(), accel.maxSlope(SLOPE_WINDOW_S), -brake.minSlope(SLOPE_WINDOW_S)};
    }

    /** returns {top speed deg/s, peak accel deg/s2, peak braking decel deg/s2} */
    double[] measureRotation() {
        dec.odo.update();

        Trace accel = new Trace();
        ElapsedTime timer = new ElapsedTime();
        drive(0, 0, 1);
        while (opModeIsActive()) {
            dec.odo.update();
            double omega = Math.abs(dec.odo.getHeadingVelocity(UnnormalizedAngleUnit.DEGREES));
            accel.add(timer.seconds(), omega);
            telemetry.addData("test", "spin burst  %.0f deg/s", omega);
            telemetry.update();
            if (timer.seconds() > ROT_BURST_S) break;
        }

        Trace brake = new Trace();
        ElapsedTime bTimer = new ElapsedTime();
        drive(0, 0, -1);
        while (opModeIsActive()) {
            dec.odo.update();
            double omega = Math.abs(dec.odo.getHeadingVelocity(UnnormalizedAngleUnit.DEGREES));
            brake.add(bTimer.seconds(), omega);
            if (omega < ROT_STOPPED_DEG_S || bTimer.seconds() > BRAKE_TIMEOUT_S) break;
        }
        drive(0, 0, 0);
        sleep(400);

        return new double[]{accel.maxVal(), accel.maxSlope(SLOPE_WINDOW_S), -brake.minSlope(SLOPE_WINDOW_S)};
    }

    /** Gentle P-drive back to (0,0,0) so every burst starts from the same spot. */
    void returnToStart() {
        ElapsedTime timer = new ElapsedTime();
        while (opModeIsActive() && timer.seconds() < RETURN_TIMEOUT_S) {
            dec.odo.update();
            double x = posX(), y = posY(), h = headingDeg();
            double dist = Math.hypot(x, y);
            double zE = -h;
            if (zE > 180) zE -= 360;
            if (zE < -180) zE += 360;
            if (dist < 2 && Math.abs(zE) < 6) break;

            double theta = Math.toDegrees(Math.atan2(-y, -x)); // toward origin, field frame
            double p = dist < 2 ? 0 : Range.clip(dist / 15.0, 0.12, RETURN_MAX_POWER);
            double xp = p * Math.cos(Math.toRadians(theta - h));
            double yp = p * Math.sin(Math.toRadians(theta - h));
            double zp = Range.clip(zE / 90.0, -0.3, 0.3);
            drive(xp, yp, zp);

            telemetry.addData("test", "returning  %.1f in  %.0f deg", dist, zE);
            telemetry.update();
        }
        drive(0, 0, 0);
        sleep(400);
    }
}
