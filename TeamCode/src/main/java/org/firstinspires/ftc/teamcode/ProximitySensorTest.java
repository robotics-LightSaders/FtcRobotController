package org.firstinspires.ftc.teamcode;  // Replace with your team's package

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Disabled
@TeleOp(name = "ProximitySensorTest", group = "Sensor")
public class ProximitySensorTest extends LinearOpMode {

    RevColorSensorV3 colorSensor;  // Declare the sensor

    @Override
    public void runOpMode() {
        // Initialize the sensor from hardware map (name must match config)
        colorSensor = hardwareMap.get(RevColorSensorV3.class, "f2");

        // Optionally enable/disable LED (true for on)
        colorSensor.enableLed(true);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {


            // Display to driver station
            telemetry.addData("Distance (cm)", colorSensor.getDistance(DistanceUnit.CM));

            telemetry.update();


        }
    }
}
