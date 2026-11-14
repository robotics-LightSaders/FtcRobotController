package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.List;

public class TagHelper {
    public static AprilTagSingleDetection getFirst(AprilTagProcessor processor) {
        if (processor == null) return null;
        for (AprilTagDetection d : processor.getDetections()) {
            if (d instanceof AprilTagSingleDetection) {
                return (AprilTagSingleDetection) d;
            }
        }
        return null;
    }


}

