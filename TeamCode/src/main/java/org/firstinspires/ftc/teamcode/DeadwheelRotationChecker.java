/*
 * Copyright (c) 2025 DigitalChickenLabs
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
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

package org.firstinspires.ftc.teamcode; // Adjust to your team's package

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
 * This OpMode illustrates how to use the DigitalChickenLabs OctoQuad to check the rotation in degrees of a deadwheel.
 * It assumes the deadwheel uses a REV Thru-Bore encoder with 8192 counts per revolution.
 * The OctoQuad must be configured in the robot hardware map as "octoquad" on an I2C port.
 * The deadwheel encoder is connected to channel 0 of the OctoQuad.
 * Press the X button on gamepad1 to reset the encoder to zero.
 * Note: Ensure the OctoQuad firmware is v3 or higher for SDK compatibility.
 */

@Disabled
@TeleOp(name = "Deadwheel Rotation Checker", group = "Sensor")
public class DeadwheelRotationChecker extends LinearOpMode {

    // Define the channel for the deadwheel encoder
    private final int DEADWHEEL_CHANNEL = 0;

    // Counts per revolution for REV Thru-Bore encoder (2048 cycles * 4 = 8192 counts)
    private final double COUNTS_PER_REV = 8192.0;

    // OctoQuad object
    private OctoQuad octoquad;

    // Variables for position and degrees
    private int position;
    private double degrees;

    @Override
    public void runOpMode() {

        // Get the OctoQuad from hardware map
        OctoQuadFWv3 oq = hardwareMap.get(OctoQuadFWv3.class, "octoquad");


        // Display firmware version
        telemetry.addData("OctoQuad Firmware Version", octoquad.getFirmwareVersion());

        // Set encoder direction (adjust if needed based on wiring/mounting)
        octoquad.setSingleEncoderDirection(DEADWHEEL_CHANNEL, OctoQuad.EncoderDirection.FORWARD);

        // Save parameters
        octoquad.saveParametersToFlash();

        telemetry.addLine("\nPress START to begin monitoring");
        telemetry.update();

        waitForStart();

        // Configure telemetry
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);
        telemetry.setMsTransmissionInterval(100);

        // Reset position to zero
        octoquad.resetSinglePosition(DEADWHEEL_CHANNEL);

        while (opModeIsActive()) {
            telemetry.addData(">", "Press X to Reset Encoder\n");

            // Check for reset button
            if (gamepad1.x) {
                octoquad.resetSinglePosition(DEADWHEEL_CHANNEL);
            }

            // Read the position using caching for efficiency
            position = octoquad.readSinglePosition_Caching(DEADWHEEL_CHANNEL);

            // Calculate degrees
            degrees = (position / COUNTS_PER_REV) * 360.0;

            // Display results
            telemetry.addData("Position (ticks)", "%7d", position);
            telemetry.addData("Rotation (degrees)", "%.2f", degrees);
            telemetry.update();
        }
    }
}