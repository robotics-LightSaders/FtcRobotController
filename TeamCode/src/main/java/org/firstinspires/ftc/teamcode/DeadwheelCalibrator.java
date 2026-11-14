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

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.OctoQuadFWv3;

/*
 * This OpMode helps calibrate the ticks/mm and directions for your deadwheels
 */

@Disabled
@TeleOp
public class DeadwheelCalibrator extends OpMode {

    // OctoQuadFWv3 oq = new OctoQuadFWv3();
    OctoQuadFWv3 oq;

    public void init() {
        oq = hardwareMap.get(OctoQuadFWv3.class, "octoquad");

        oq.setChannelBankConfig(OctoQuadFWv3.ChannelBankConfig.ALL_PULSE_WIDTH);
        oq.setSingleChannelPulseWidthParams(1, 0, 1024); //1024 or 9000?
        oq.setSingleEncoderDirection(1, OctoQuadFWv3.EncoderDirection.REVERSE);
        oq.saveParametersToFlash();

        //telemetry.addData("Firmware Version", oq.getFirmwareVersionString());
    }

    public void loop() {
        //oq.getFirmwareVersion();
        telemetry.addData("Encoder value", oq.readAllEncoderData().positions[1]);
        telemetry.update();
    }
}