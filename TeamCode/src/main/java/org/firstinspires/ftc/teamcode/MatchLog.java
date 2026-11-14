package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * MatchLog: records what the robot did in a match, to calibrate the shots against a video of the field.
 * READ ONLY: it never sets a motor, a servo or anything else, and it never throws into your code.
 *
 * Use (iterative OpMode):  init(): log = new MatchLog(hardwareMap, gamepad1, gamepad2, "teleop");
 *                          end of loop(): log.put("name", value) ... then log.row();      stop(): log.close();
 *
 * At most every 40 ms it keeps one row: the time; your loops since the last row; the Pinpoint pose from YOUR code's last
 * update() (memory only); the hood and turret positions your code set (the SDK answers from memory); both gamepads; your
 * put() numbers; and the flywheel's speed: the ONLY hub read (one bulk read, about 2 ms, at most every 40 ms).
 * The rows stay in memory (room for 10 minutes, 6.6 MB, reserved at init, so nothing grows while you drive); close() writes one
 * CSV file on its own thread after the op mode: /sdcard/FIRST/matchlogs/<date-time>_<label>.csv on the Control Hub (never
 * overwriting a file: a name already there gets -2, -3; forced onto the storage, so switching off right after is safe).
 * The Pinpoint is the GoBildaPinpointDriver class in this package (the team's own copy of goBILDA's driver).
 */
public class MatchLog {
    static final double EVERY_S = 0.04;   // at most 25 rows a second
    static final int MAX_ROWS = 15000;    // 10 minutes, then it stops adding

    private final String label, started = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(new Date());
    private final ElapsedTime clock = new ElapsedTime();
    private final StringBuilder rows = new StringBuilder(MAX_ROWS * 220), row = new StringBuilder(512), extra = new StringBuilder(256);
    private String head = "";
    private GoBildaPinpointDriver pin;
    private DcMotorEx flywheel;
    private Servo hood, turret;
    private Gamepad g1, g2;
    private boolean on = true, closed;
    private int n, calls;
    private double last = -1;
    private String note = "";

    public MatchLog(HardwareMap hw, Gamepad gamepad1, Gamepad gamepad2, String label) {
        this.label = label;
        try {
            g1 = gamepad1;
            g2 = gamepad2;
            List<GoBildaPinpointDriver> pins = hw.getAll(GoBildaPinpointDriver.class);
            pin = pins.isEmpty() ? null : pins.get(0);
            flywheel = hw.tryGet(DcMotorEx.class, "flywheel");
            hood = hw.tryGet(Servo.class, "hood");
            turret = hw.tryGet(Servo.class, "turret");
            head = "t,dt_ms,loops,x_in,y_in,h_deg,pinpoint,flywheel_ticks_per_s,hood_pos,turret_pos,"
                    + "g1_lt,g1_rt,g1_buttons,g1_lx,g1_ly,g1_rx,g2_lt,g2_rt,g2_buttons,g2_lx,g2_ly,g2_rx,extra";
            note = (pin == null ? " no pinpoint" : "") + (flywheel == null ? " no flywheel" : "") + (hood == null ? " no hood" : "")
                    + (turret == null ? " no turret" : "");
        } catch (Throwable e) {
            off(e);
        }
    }

    /** A number your code has, for the next row (name=value in the last column). */
    public void put(String name, double value) {
        if (!on) return;
        try {
            extra.append(extra.length() > 0 ? ";" : "").append(name).append('=').append(value);
        } catch (Throwable e) {
            off(e);
        }
    }

    /** Call at the end of every loop; it keeps a row at most every 40 ms. */
    public void row() {
        if (!on) return;
        try {
            calls++;
            double t = clock.seconds();
            if ((last >= 0 && t - last < EVERY_S) || n >= MAX_ROWS) { extra.setLength(0); return; }
            StringBuilder r = row;
            r.setLength(0);
            num(r, t);
            r.append(',');
            num(r, last < 0 ? 0 : (t - last) * 1000);
            r.append(',').append(calls);
            calls = 0;
            last = t;
            if (pin != null) {
                Pose2D p = pin.getPosition();
                r.append(','); num(r, p.getX(DistanceUnit.INCH));
                r.append(','); num(r, p.getY(DistanceUnit.INCH));
                r.append(','); num(r, p.getHeading(AngleUnit.DEGREES));
                r.append(',').append(pin.getDeviceStatus());
            } else {
                r.append(",,,,");
            }
            r.append(','); if (flywheel != null) num(r, flywheel.getVelocity());
            r.append(','); if (hood != null) num(r, hood.getPosition());
            r.append(','); if (turret != null) num(r, turret.getPosition());
            pad(r, g1);
            pad(r, g2);
            r.append(',').append(extra).append('\n');
            extra.setLength(0);
            rows.append(r);
            n++;
        } catch (Throwable e) {
            off(e);
        }
    }

    /** Call once when the op mode ends: the file is written on its own thread. */
    public void close() {
        if (closed) return;
        closed = true;
        on = false;
        try {
            final String base = started + "_" + label;
            final String top = "# MatchLog " + label + " started " + started + ", " + n + " rows" + note + "\n" + head + "\n";
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        File dir = new File(AppUtil.FIRST_FOLDER, "matchlogs");
                        dir.mkdirs();
                        File f = new File(dir, base + ".csv");
                        for (int k = 2; !f.createNewFile() && k < 100; k++) f = new File(dir, base + "-" + k + ".csv");   // never overwrite a file
                        FileOutputStream fo = new FileOutputStream(f);
                        Writer out = new OutputStreamWriter(fo, "UTF-8");
                        out.write(top);
                        out.write(rows.toString());
                        out.flush();
                        fo.getFD().sync();   // on the storage now: safe if the robot is switched off right after the match
                        out.close();
                    } catch (Throwable ignored) {
                        // never disturb the robot
                    }
                }
            }, "MatchLog writer").start();
        } catch (Throwable ignored) {
            // never disturb the robot
        }
    }

    private void off(Throwable e) {
        on = false;
        note = note + ", off after an error: " + e;
    }

    private static void pad(StringBuilder r, Gamepad g) {
        if (g == null) { r.append(",,,,,,"); return; }
        int b = (g.a ? 1 : 0) | (g.b ? 2 : 0) | (g.x ? 4 : 0) | (g.y ? 8 : 0) | (g.left_bumper ? 16 : 0) | (g.right_bumper ? 32 : 0)
                | (g.dpad_up ? 64 : 0) | (g.dpad_down ? 128 : 0) | (g.dpad_left ? 256 : 0) | (g.dpad_right ? 512 : 0)
                | (g.back ? 1024 : 0) | (g.start ? 2048 : 0) | (g.left_stick_button ? 4096 : 0) | (g.right_stick_button ? 8192 : 0);
        r.append(','); num(r, g.left_trigger);
        r.append(','); num(r, g.right_trigger);
        r.append(',').append(b);
        r.append(','); num(r, g.left_stick_x);
        r.append(','); num(r, g.left_stick_y);
        r.append(','); num(r, g.right_stick_x);
    }

    private static void num(StringBuilder r, double v) {
        if (!Double.isNaN(v) && !Double.isInfinite(v)) r.append(Math.round(v * 10000) / 10000.0);
    }
}
