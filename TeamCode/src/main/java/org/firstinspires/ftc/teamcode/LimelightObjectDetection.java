package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes.ColorResult; // Use DetectorResult if using ML Neural Networks

import java.util.List;

@Autonomous(name = "Limelight Object Detection Example", group = "Vision")
@Disabled
public class LimelightObjectDetection extends LinearOpMode {

    private Limelight3A limelight;

    @Override
    public void runOpMode() throws InterruptedException {
        // 1. Initialize the Limelight from the hardware map
        // Ensure "limelight" matches the name in your Robot Configuration
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        telemetry.addData("Status", "Limelight Initializing...");
        telemetry.update();

        // 2. Set your pipeline (e.g., Pipeline 0 for Color/Neural Net detection)
        int detectionPipeline = 0; 
        limelight.pipelineSwitch(detectionPipeline);

        // 3. Start the camera stream 
        limelight.start();

        telemetry.addData("Status", "Ready! Press Play");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // 4. Grab the latest result frame from the camera
            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {
                // Access general targeting metrics
                double tx = result.getTx();     // Horizontal Offset From Crosshair (-31.25 to 31.25 degrees)
                double ty = result.getTy();     // Vertical Offset From Crosshair (-24.85 to 24.85 degrees)
                double ta = result.getTa();     // Target Area (0% to 100% of image)

                double inx = 0.0000627425 * Math.pow(tx, 3) + 0.000258603 * Math.pow(tx, 2) + .187907 * tx - .968985;
                double iny = 0.0002044528 * Math.pow(ty, 3) + 0.00398129 * Math.pow(ty, 2) + .258848 * ty + 5.4361;

                telemetry.addData("Target Found", "Yes");
                /*
                telemetry.addData("TX (Deg)", "%.2f", tx);
                telemetry.addData("TY (Deg)", "%.2f", ty);
                telemetry.addData("X (In)", inx);
                telemetry.addData("Y (In)", iny);
                telemetry.addData("Target Area", "%.2f%%", ta);
                 */

                // 5. Advanced: Iterate through multiple detected color targets
                List<ColorResult> colorTargets = result.getColorResults();
                for (int i = 0; i < colorTargets.size(); i++) {
                    ColorResult target = colorTargets.get(i);

                    double txdeg = target.getTargetXDegrees();
                    double txdegin =  0.0000627425 * Math.pow(txdeg, 3) + 0.000258603 * Math.pow(txdeg, 2) + .187907 * txdeg - .968985;

                    double tydeg = target.getTargetYDegrees();
                    double tydegin =  0.0002044528 * Math.pow(tydeg, 3) + 0.00398129 * Math.pow(tydeg, 2) + .258848 * tydeg + 5.4361;

                    telemetry.addData("Target " + (i + 1) + " Area", "%.2f%%", target.getTargetArea());
                   // telemetry.addData("Target " + (i + 1) + " TX (Deg)", "%.2f%%", txdeg);
                   // telemetry.addData("Target " + (i + 1) + " TY (Deg)", "%.2f%%", tydeg);
                    telemetry.addData("Target " + (i + 1) + " X (In)", "%.2f%%", txdegin);
                    telemetry.addData("Target " + (i + 1) + " Y (In)", "%.2f%%", tydegin);


                    
                }
            } else {
                telemetry.addData("Target Found", "No");
            }

            telemetry.update();
            idle(); // Share processor clock with other threads
        }
        
        // 6. Stop pipeline on completion to conserve energy
        limelight.stop();
    }
}
