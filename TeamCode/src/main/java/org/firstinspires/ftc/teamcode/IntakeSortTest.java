/*
 * Copyright (c) 2025 DigitalChickenLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import android.util.Log;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/*
 * This OpMode helps calibrate the ticks/mm and directions for your deadwheels
 */

@Disabled
@TeleOp
public class IntakeSortTest extends OpMode {
    DecodeConstants dec = new DecodeConstants();

    private ElapsedTime runtime = new ElapsedTime();

    RevColorSensorV3 f2;
    NormalizedRGBA f2c;
    RevColorSensorV3 r2;
    NormalizedRGBA r2c;
    RevColorSensorV3 l2;
    NormalizedRGBA l2c;

    double x;
    double y;
    double z;

    boolean canscan = false;
    double scantime = 0;
    double lastdiff = 0;

    public void init() {
        dec.fl = hardwareMap.get(DcMotor.class, "fl");
        dec.fr = hardwareMap.get(DcMotor.class, "fr");
        dec.bl = hardwareMap.get(DcMotor.class, "bl");
        dec.br = hardwareMap.get(DcMotor.class, "br");

        dec.intake = hardwareMap.get(DcMotor.class, "intake");

        dec.susan = hardwareMap.get(Servo.class, "susan");
        dec.susanenc = hardwareMap.get(AnalogInput.class, "senc");

        dec.f0 = hardwareMap.digitalChannel.get("f0");
        dec.f1 = hardwareMap.digitalChannel.get("f1");
        dec.r0 = hardwareMap.digitalChannel.get("r0");
        dec.r1 = hardwareMap.digitalChannel.get("r1");
        dec.l0 = hardwareMap.digitalChannel.get("l0");
        dec.l1 = hardwareMap.digitalChannel.get("l1");

        f2 = hardwareMap.get(RevColorSensorV3.class, "f2");
        r2 = hardwareMap.get(RevColorSensorV3.class, "r2");
        l2 = hardwareMap.get(RevColorSensorV3.class, "l2");

        dec.fl.setDirection(DcMotor.Direction.REVERSE);
        dec.bl.setDirection(DcMotor.Direction.REVERSE);
        dec.intake.setDirection(DcMotor.Direction.REVERSE);

        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        dec.fl.setPower(0);
        dec.fr.setPower(0);
        dec.bl.setPower(0);
        dec.br.setPower(0);
        dec.intake.setPower(0);

        dec.susan.setPosition(dec.susanpos1);
        dec.susanV = dec.susanenc.getVoltage();

        runtime.reset();
    }

    public void loop() {
        x = gamepad1.right_stick_x;
        y = -gamepad1.left_stick_y;
        z = gamepad1.left_stick_x;

        dec.flpower = Range.clip(y + x + z, -1, 1);
        dec.frpower = Range.clip(y - x - z, -1, 1);
        dec.blpower = Range.clip(y + x - z, -1, 1);
        dec.brpower = Range.clip(y - x + z, -1, 1);

        dec.fl.setPower(dec.flpower);
        dec.fr.setPower(dec.frpower);
        dec.bl.setPower(dec.blpower);
        dec.br.setPower(dec.brpower);

        if (gamepad1.right_trigger > 0.2) {
            dec.intake.setPower(gamepad1.right_trigger);
        } else if (gamepad1.left_trigger > 0.2) {
            dec.intake.setPower(-gamepad1.left_trigger);
        } else {
            dec.intake.setPower(0);
        }

        f2c = f2.getNormalizedColors();
        r2c = r2.getNormalizedColors();
        l2c = l2.getNormalizedColors();

        dec.hsvF = new float[3];
        Color.RGBToHSV((int) (f2c.red * 255), (int) (f2c.green * 255), (int) (f2c.blue * 255), dec.hsvF);
        dec.hughf = dec.hsvF[0];
        dec.brianf = dec.hsvF[2];

        dec.hsvR = new float[3];
        Color.RGBToHSV((int) (r2c.red * 255), (int) (r2c.green * 255), (int) (r2c.blue * 255), dec.hsvR);
        dec.hughr = dec.hsvR[0];
        dec.brianr = dec.hsvR[2];

        dec.hsvL = new float[3];
        Color.RGBToHSV((int) (l2c.red * 255), (int) (l2c.green * 255), (int) (l2c.blue * 255), dec.hsvL);
        dec.hughl = dec.hsvL[0];
        dec.brianl = dec.hsvL[2];

        if ((f2.getDistance(DistanceUnit.CM) < 4)) {
            if ((f2c.green / f2c.red) > dec.gratio) {
                dec.revf = "Green";
            } else {
                dec.revf = "Purple";
            }
        } else {
            dec.revf = "None";
        }

        if ((dec.brianr > 0) && (dec.hughr > 125)) {
            if ((r2c.green / r2c.red) > dec.gratio) {
                dec.revr = "Green";
            } else {
                dec.revr = "Purple";
            }
        } else {
            dec.revr = "None";
        }

        if ((dec.brianl > 0) && (dec.hughl > 125)) {
            if ((l2c.green / l2c.red) > dec.gratio) {
                dec.revl = "Green";
            } else {
                dec.revl = "Purple";
            }
        } else {
            dec.revl = "None";
        }

        if (canscan) {
            if (dec.f0.getState() || dec.revf.matches("Purple")) {
                dec.FC = "Purple";
            } else if (dec.f1.getState() || dec.revf.matches("Green")) {
                dec.FC = "Green";
            } else {
                dec.FC = "None";
            }

            if (dec.r0.getState() || dec.revr.matches("Purple")) {
                dec.RC = "Purple";
            } else if (dec.r1.getState() || dec.revr.matches("Green")) {
                dec.RC = "Green";
            } else {
                dec.RC = "None";
            }

            if (dec.l0.getState() || dec.revl.matches("Purple")) {
                dec.LC = "Purple";
            } else if (dec.l1.getState() || dec.revl.matches("Green")) {
                dec.LC = "Green";
            } else {
                dec.LC = "None";
            }

            Log.d("Sorting", "F, R, L: " + dec.FC + ", " + dec.RC + ", " + dec.LC);
        }

        dec.susanV = dec.susanenc.getVoltage();
        dec.susandegreesat = (dec.susanV - dec.susanminV) / dec.susanVrange * dec.susanrange;

        if ((lastdiff >= dec.stol) && (Math.abs(dec.susandegreesat - dec.susandegreesto) < dec.stol)) {
            canscan = true;
            scantime = runtime.milliseconds() + 100;
        }

        if (Math.abs(dec.susandegreesat - dec.susandegreesto) < dec.stol) { //2?
            canscan = true;
            if (!dec.FC.matches("None")) {
                if ((!dec.RC.matches("None")) && (!dec.LC.matches("None"))) {
                    canscan = true;
                } else if (scantime < runtime.milliseconds()) {
                    if (dec.susan.getPosition() < 0.2) {
                        if (dec.LC.matches("None")) {
                            dec.susan.setPosition(dec.susanpos2);
                            canscan = false;
                        } else if (dec.RC.matches("None")) {
                            dec.susan.setPosition(dec.susanpos3);
                            canscan = false;
                        }
                    } else if (dec.susan.getPosition() < 0.6) {
                        if (dec.LC.matches("None")) {
                            dec.susan.setPosition(dec.susanpos3);
                            canscan = false;
                        } else if (dec.RC.matches("None")) {
                            dec.susan.setPosition(dec.susanpos1);
                            canscan = false;
                        }
                    } else {
                        if (dec.LC.matches("None")) {
                            dec.susan.setPosition(dec.susanpos1);
                            canscan = false;
                        } else if (dec.RC.matches("None")) {
                            dec.susan.setPosition(dec.susanpos2);
                            canscan = false;
                        }
                    }
                }
            }
        }

        dec.susandegreesto = dec.susan.getPosition() * dec.susanrange;
        lastdiff = Math.abs(dec.susandegreesat - dec.susandegreesto);
    }
}