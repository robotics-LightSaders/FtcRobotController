package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes.ColorResult; // Use DetectorResult if using ML Neural Networks

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;


import java.util.List;

@Autonomous(name = "Limelight Distance", group = "Vision")
@Disabled
public class LimelightDistance extends LinearOpMode {


    private Limelight3A limelight;

    private double camraHeight = 37;

    private double camraAngle = 0;

    private double goalHeight = 74.95;

    private double distance = 0;


    @Override
    public void runOpMode() {


        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(1);
        limelight.start();//turns on limelight, takes about 100ms to turn on, so in init, but is power drain, not good to sit, so may move depends

        waitForStart();

        while (opModeIsActive()) {


            LLResult llResult = limelight.getLatestResult();

            if (llResult != null && llResult.isValid()) {
                // 5. Call MegaTag1 field localization (getBotpose)
                Pose3D botpose = llResult.getBotpose();

                if (botpose != null) {
                    // Extract X, Y, Z translations (in meters)
                    double xm = botpose.getPosition().x;
                    double ym = botpose.getPosition().y;
                    double zm = botpose.getPosition().z;

                    double xin = xm * 39.3701;
                    double yin = ym * 39.3701;
                    double zin = zm * 39.3701;

                    // Extract orientation (in degrees)
                    double yaw = botpose.getOrientation().getYaw();

                    // 6. Push to telemetry or supply to your odometry localizer
                    telemetry.addData("MegaTag1 Status", "Valid Lock");
                    telemetry.addData("Field X (inches)", "%.2f", xin);
                    telemetry.addData("Field Y (inches)", "%.2f", yin);
                    telemetry.addData("Field Z (inches)", "%.2f", zin);
                    telemetry.addData("Robot Heading (Yaw)", "%.2f°", yaw);
                } else {
                    telemetry.addData("MegaTag1 Status", "No Botpose calculated");
                }
            } else {
                telemetry.addData("MegaTag1 Status", "No AprilTags Visible / No Lock");
            }

            telemetry.update();

        }
    }


}


