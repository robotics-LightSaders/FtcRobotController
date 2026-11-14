//package org.firstinspires.ftc.teamcode;
//
//import android.util.Size;
//
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.Range;
//
//import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
//import org.firstinspires.ftc.robotcore.external.navigation.Position;
//import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
//import org.firstinspires.ftc.vision.VisionPortal;
//import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
//import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
//
//import java.util.List;
//
//@Disabled
//@TeleOp(name = "ATL2", group = "TeleOp")
////@Disabled
//public class ATL2 extends OpMode {
//
//    private ElapsedTime runtime = new ElapsedTime();
//
//    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera
//
//    /**
//     * Variables to store the position and orientation of the camera on the robot. Setting these
//     * values requires a definition of the axes of the camera and robot:
//     * <p>
//     * Camera axes:
//     * Origin location: Center of the lens
//     * Axes orientation: +x right, +y down, +z forward (from camera's perspective)
//     * <p>
//     * Robot axes (this is typical, but you can define this however you want):
//     * Origin location: Center of the robot at field height
//     * Axes orientation: +x right, +y forward, +z upward
//     * <p>
//     * Position:
//     * If all values are zero (no translation), that implies the camera is at the center of the
//     * robot. Suppose your camera is positioned 5 inches to the left, 7 inches forward, and 12
//     * inches above the ground - you would need to set the position to (-5, 7, 12).
//     * <p>
//     * Orientation:
//     * If all values are zero (no rotation), that implies the camera is pointing straight up. In
//     * most cases, you'll need to set the pitch to -90 degrees (rotation about the x-axis), meaning
//     * the camera is horizontal. Use a yaw of 0 if the camera is pointing forwards, +90 degrees if
//     * it's pointing straight left, -90 degrees for straight right, etc. You can also set the roll
//     * to +/-90 degrees if it's vertical, or 180 degrees if it's upside-down.
//     */
//    private Position cameraPosition = new Position(DistanceUnit.INCH,
//            0, 0, 16.16, 0); //5.84, 7.94, 16.16
//    private YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES,
//            0, -109, 0, 0); //need to put yaw adjustment as servo rotates?
//
//    //The variable to store our instance of the AprilTag processor.
//    private AprilTagProcessor aprilTag;
//
//    //The variable to store our instance of the vision portal.
//    private VisionPortal visionPortal;
//
//    Position campos = new Position(DistanceUnit.INCH, 0, 0, 0, 0);
//    Position robopos = new Position(DistanceUnit.INCH, 0, 0, 0, 0);
//
//    DcMotor fr;
//    DcMotor fl;
//    DcMotor br;
//    DcMotor bl;
//
//    //Servo camtilt;
//    Servo camrot;
//
//    //GoBildaPinpointDriver odo;
//
//    double x;
//    double y;
//    double z;
//
//    double frpower = 0;
//    double flpower = 0;
//    double brpower = 0;
//    double blpower = 0;
//    boolean power = false;
//
//    //double tilt = 0;
//    double angle = 0;
//    double autoangle = 0;
//    double targetangle = 0;
//
//    double modetime = 0;
//    boolean manual = false;
//
//    //double xodooffset = 2;
//    //double yodooffset = 86;
//
//    Pose2D T11 = new Pose2D(DistanceUnit.INCH, -71, 47.5, AngleUnit.DEGREES, 55);
//    Pose2D T12 = new Pose2D(DistanceUnit.INCH, 0, 71, AngleUnit.DEGREES, 0);
//    Pose2D T13 = new Pose2D(DistanceUnit.INCH, 71, 47.5, AngleUnit.DEGREES, -55);
//    Pose2D T14 = new Pose2D(DistanceUnit.INCH, 71, -47.5, AngleUnit.DEGREES, -125);
//    Pose2D T15 = new Pose2D(DistanceUnit.INCH, 0, -71, AngleUnit.DEGREES, 0);
//    Pose2D T16 = new Pose2D(DistanceUnit.INCH, -71, -47.5, AngleUnit.DEGREES, 125);
//
//    @Override
//    public void init() {
//        initAprilTag();
//
//        // Wait for the DS start button to be touched.
//        telemetry.addData("Time to Init Camera: ", runtime.milliseconds());
//        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
//        telemetry.addData(">", "Touch START to start OpMode");
//        telemetry.update();
//
//        fl = hardwareMap.get(DcMotor.class, "fl");
//        fr = hardwareMap.get(DcMotor.class, "fr");
//        bl = hardwareMap.get(DcMotor.class, "bl");
//        br = hardwareMap.get(DcMotor.class, "br");
//
//        //camtilt = hardwareMap.get(Servo.class, "tilt");
//        camrot = hardwareMap.get(Servo.class, "rot");
//
//        //odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
//
//        fl.setDirection(DcMotor.Direction.REVERSE);
//        bl.setDirection(DcMotor.Direction.REVERSE);
//
//        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        fl.setPower(0);
//        fr.setPower(0);
//        bl.setPower(0);
//        br.setPower(0);
//
//        //camtilt.setPosition(0);
//        camrot.setPosition(0.91); //0.51 = middle
//
//        /*odo.setOffsets(xodooffset, yodooffset, DistanceUnit.MM); //these are tuned for 3110-0002-0001 Product Insight #1
//        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
//        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED,
//                GoBildaPinpointDriver.EncoderDirection.REVERSED);
//        odo.resetPosAndIMU();*/
//
//        while ((!gamepad1.a) && aprilTag.getDetections().isEmpty()) {
//            if (camrot.getPosition() > 0.1) {
//                camrot.setPosition(camrot.getPosition() - 0.0002);
//            } else {
//                camrot.setPosition(0.91);
//            }
//        }
//
//        telemetryAprilTag();
//
//        //lower servo position cw, higher is ccw
//        //180/0.395 = 455.696 --> 0.395 = servo travel for 180 degrees, so 455.696 converts servo position to angle
//        angle = (0.51 - camrot.getPosition()) * 455.696;
//
//        robopos.z = campos.z + angle;
//        //9.86 = hypotenuse of offsets, angle between robot and camera = 63 degrees
//        robopos.x = campos.x - (9.86 * Math.cos(Math.toRadians(robopos.z + 63))); //5.84 = x offset
//        robopos.y = campos.y - (9.86 * Math.sin(Math.toRadians(robopos.z + 63))); //7.94 = y offset
//        //may need to subtract 1.5 from y?
//
//        telemetry.addLine(String.format("RoboPos: %6.2f %6.2f %6.2f", robopos.x, robopos.y, robopos.z));
//        telemetry.update();
//
//        runtime.reset();
//    }
//
//    public void loop() {
//        telemetryAprilTag();
//
//        x = gamepad1.right_stick_x;
//        y = -gamepad1.left_stick_y;
//        z = gamepad1.left_stick_x;
//
//        flpower = Range.clip(y + x + z, -1, 1);
//        frpower = Range.clip(y - x - z, -1, 1);
//        blpower = Range.clip(y + x - z, -1, 1);
//        brpower = Range.clip(y - x + z, -1, 1);
//
//        if ((Math.abs(flpower) > 0.05) || (Math.abs(frpower) > 0.05) || (Math.abs(blpower) > 0.05) ||
//                (Math.abs(brpower) > 0.05)) {
//            power = true;
//        } else {
//            power = false;
//        }
//
//        fl.setPower(flpower);
//        fr.setPower(frpower);
//        bl.setPower(blpower);
//        br.setPower(brpower);
//
//        //lower servo position cw, higher is ccw
//        //180/0.395 = 455.696 --> 0.395 = servo travel for 180 degrees, so 455.696 converts servo position to angle
//        angle = (0.51 - camrot.getPosition()) * 455.696;
//
//        robopos.z = campos.z + angle;
//        //9.86 = hypotenuse of offsets, angle between robot and camera = 63 degrees
//        robopos.x = campos.x - (9.86 * Math.cos(Math.toRadians(robopos.z + 63))); //5.84 = x offset
//        robopos.y = campos.y - (9.86 * Math.sin(Math.toRadians(robopos.z + 63))); //7.94 = y offset
//        //may need to subtract 1.5 from y?
//
//        // Push telemetry to the Driver Station.
//        //telemetry.addData("Tilt Pos: ", camtilt.getPosition());
//        telemetry.addData("Manual: ", manual);
//        telemetry.addData("Rot Pos: ", camrot.getPosition());
//        telemetry.addData("Target: ", targetangle);
//        telemetry.addData("Camera Angle: ", angle);
//        telemetry.addLine(String.format("CamPos: %6.2f %6.2f %6.2f", campos.x, campos.y, campos.z));
//        telemetry.addLine(String.format("RoboPos: %6.2f %6.2f %6.2f", robopos.x, robopos.y, robopos.z));
//        telemetry.update();
//
//        if (campos.y > 0) {
//            if (campos.x > 24) {
//                targetangle = T13.getHeading(AngleUnit.DEGREES);
//            } else if (campos.x < -24) {
//                targetangle = T11.getHeading(AngleUnit.DEGREES);
//            } else {
//                targetangle = T12.getHeading(AngleUnit.DEGREES);
//            }
//        } else {
//            if (campos.x > 24) {
//                targetangle = T14.getHeading(AngleUnit.DEGREES);
//            } else if (campos.x < -24) {
//                targetangle = T16.getHeading(AngleUnit.DEGREES);
//            } else {
//                targetangle = T15.getHeading(AngleUnit.DEGREES);
//            }
//        }
//
//        if (gamepad1.y && (modetime < runtime.milliseconds())) {
//            modetime = runtime.milliseconds() + 200;
//
//            if (manual) {
//                manual = false;
//            } else {
//                manual = true;
//            }
//        }
//
//        if (manual) {
//            if (gamepad1.b) {
//                camrot.setPosition(camrot.getPosition() - 0.001);
//            } else if (gamepad1.x) {
//                camrot.setPosition(camrot.getPosition() + 0.001);
//            }
//        } else {
//            if (!power) {
//                if (!aprilTag.getDetections().isEmpty()) {
//                    if (TagHelper.getFirst(aprilTag).center.x > 400) {
//                        camrot.setPosition(camrot.getPosition() - (0.001 * ((TagHelper.getFirst(aprilTag).center.x - 400) / 192)));
//                    } else if (TagHelper.getFirst(aprilTag).center.x < 240) {
//                        camrot.setPosition(camrot.getPosition() + (0.001 * ((240 - TagHelper.getFirst(aprilTag).center.x) / 192)));
//                    }
//                } else {
//                    if (camrot.getPosition() > 0.1) {
//                        camrot.setPosition(camrot.getPosition() - 0.0002);
//                    } else {
//                        camrot.setPosition(0.95);
//                    }
//                }
//            } else {
//                autoangle = 0.51 - (targetangle / 455.696);
//                camrot.setPosition(autoangle);
//            }
//        }
//
//        // Save CPU resources; can resume streaming when needed.
//        if (gamepad1.dpad_down) {
//            visionPortal.stopStreaming();
//        } else if (gamepad1.dpad_up) {
//            visionPortal.resumeStreaming();
//        }
//
//        // Save more CPU resources when camera is no longer needed.
//        //visionPortal.close();
//
//    }   // end method runOpMode()
//
//    //Initialize the AprilTag processor.
//    private void initAprilTag() {
//
//        // Create the AprilTag processor.
//        aprilTag = new AprilTagProcessor.Builder()
//
//                // The following default settings are available to un-comment and edit as needed.
//                //.setDrawAxes(false)
//                //.setDrawCubeProjection(false)
//                //.setDrawTagOutline(true)
//                //.setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
//                //.setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
//                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
//                .setCameraPose(cameraPosition, cameraOrientation)
//
//                // == CAMERA CALIBRATION ==
//                // If you do not manually specify calibration parameters, the SDK will attempt
//                // to load a predefined calibration for your camera.
//                //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506)
//                .setLensIntrinsics(641.5, 641.5, 320, 240)
//                // ... these parameters are fx, fy, cx, cy.
//
//                .build();
//
//        // Adjust Image Decimation to trade-off detection-range for detection-rate.
//        // eg: Some typical detection data using a Logitech C920 WebCam
//        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
//        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
//        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second (default)
//        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second (default)
//        // Note: Decimation can be changed on-the-fly to adapt during a match.
//        //aprilTag.setDecimation(3);
//
//        // Create the vision portal by using a builder.
//        VisionPortal.Builder builder = new VisionPortal.Builder();
//
//        // Set the camera (webcam vs. built-in RC phone camera).
//        if (USE_WEBCAM) {
//            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
//        } else {
//            builder.setCamera(BuiltinCameraDirection.BACK);
//        }
//
//        // Choose a camera resolution. Not all cameras support all resolutions.
//        builder.setCameraResolution(new Size(640, 480));
//
//        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
//        //builder.enableLiveView(true);
//
//        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
//        //builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);
//
//        // Choose whether or not LiveView stops if no processors are enabled.
//        // If set "true", monitor shows solid orange screen if no processors enabled.
//        // If set "false", monitor shows camera view without annotations.
//        //builder.setAutoStopLiveView(false);
//
//        // Set and enable the processor.
//        builder.addProcessor(aprilTag);
//
//        // Build the Vision Portal, using the above settings.
//        visionPortal = builder.build();
//
//        // Disable or re-enable the aprilTag processor at any time.
//        //visionPortal.setProcessorEnabled(aprilTag, true);
//
//    }   // end method initAprilTag()
//
//    /**
//     * Add telemetry about AprilTag detections.
//     */
//    private void telemetryAprilTag() {
//
//        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//        telemetry.addData("# AprilTags Detected", currentDetections.size());
//
//        // Step through the list of detections and display info for each one.
//        for (AprilTagDetection detection : currentDetections) {
//            if (detection.metadata != null) {
//                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
//                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)",
//                        detection.robotPose.getPosition().x,
//                        detection.robotPose.getPosition().y,
//                        detection.robotPose.getPosition().z));
//                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)",
//                        detection.robotPose.getOrientation().getPitch(AngleUnit.DEGREES),
//                        detection.robotPose.getOrientation().getRoll(AngleUnit.DEGREES),
//                        detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES)));
//
//                campos.x = detection.robotPose.getPosition().x;
//                campos.y = detection.robotPose.getPosition().y;
//                campos.z = detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES);
//
//            } else {
//                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
//                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
//            }
//        }   // end for() loop
//
//        // Add "key" information to telemetry
//        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
//        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
//
//    }   // end method telemetryAprilTag()
//
//}   // end class
