package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class HSVTelemetryDetector {

    // Mock telemetry class for demonstration
    public static class Telemetry {
        public void addData(String key, Object value) {
            System.out.println("[Telemetry] " + key + ": " + value);
        }
        public void update() {
            System.out.println("------------------------------------");
        }
    }

    /**
     * Detects HSV values at a specific point in a Mat and pushes to telemetry.
     * 
     * @param image      The source image Mat (in BGR or RGB format).
     * @param targetPoint The pixel coordinate to sample.
     * @param telemetry  The telemetry object to return the data.
     */
    public static void detectHSVTelemetry(Mat image, Point targetPoint, Telemetry telemetry) {
        // Ensure coordinates are within image bounds
        int x = (int) targetPoint.x;
        int y = (int) targetPoint.y;
        
        if (x < 0 || x >= image.cols() || y < 0 || y >= image.rows()) {
            telemetry.addData("Error", "Point out of bounds");
            return;
        }

        // 1. Convert BGR/RGB to HSV
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

        // 2. Get pixel data from the specific point
        // get() returns a double array containing [H, S, V]
        double[] hsvValues = hsvImage.get(y, x); 

        if (hsvValues != null) {
            double hue = hsvValues[0];
            double saturation = hsvValues[1];
            double value = hsvValues[2];

            // 3. Output telemetry data
            telemetry.addData("Coordinate", "(" + x + ", " + y + ")");
            telemetry.addData("Hue (H)", hue);
            telemetry.addData("Saturation (S)", saturation);
            telemetry.addData("Value (V)", value);
        } else {
            telemetry.addData("Status", "Failed to read pixel data");
        }
        telemetry.update();

        // Release Mat to prevent memory leaks
        hsvImage.release();
    }

    public static void main(String[] args) {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Load an image
        Mat srcImage = Imgcodecs.imread("sample.jpg");

        if (srcImage.empty()) {
            System.out.println("Failed to load image!");
            return;
        }

        // Define a point you want to detect (e.g., center of the image)
        Point pointToSample = new Point(srcImage.cols() / 2.0, srcImage.rows() / 2.0);
        
        // Instantiate Telemetry
        Telemetry telemetry = new Telemetry();

        // Run the detection and telemetry return
        detectHSVTelemetry(srcImage, pointToSample, telemetry);
        
        // Release the source Mat
        srcImage.release();
    }
}
