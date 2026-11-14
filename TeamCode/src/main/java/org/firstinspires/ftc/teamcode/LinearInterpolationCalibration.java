package org.firstinspires.ftc.teamcode;

public class LinearInterpolationCalibration {
    // Known X and Y values
    //put sample distances and best velocities in these arrays, making sure indexes match
    //the more data sets, the better the result
    static final double[] distanceFromTarget = {0, 5, 10, 15};
    static final double[] optimalVelocity = {10, 20, 30, 40};
    static final double[] optimalHoodPosition = {0, 5, 10, 15};


    public static double interpolateVelo(double x) {

        // Find the two points surrounding x
        for (int i = 0; i < distanceFromTarget.length - 1; i++) {

            if (x >= distanceFromTarget[i] && x <= distanceFromTarget[i + 1]) {

                double x1 = distanceFromTarget[i];
                double x2 = distanceFromTarget[i + 1];

                double y1 = optimalVelocity[i];
                double y2 = optimalVelocity[i + 1];

                // Linear interpolation formula
                return y1 + (x - x1) *
                        (y2 - y1) / (x2 - x1);
            }
        }

       return optimalVelocity[optimalVelocity.length-1];
    }
    public static double interpolateHood(double x) {

        // Find the two points surrounding x
        for (int i = 0; i < distanceFromTarget.length - 1; i++) {

            if (x >= distanceFromTarget[i] && x <= distanceFromTarget[i + 1]) {

                double x1 = distanceFromTarget[i];
                double x2 = distanceFromTarget[i + 1];

                double y1 = optimalHoodPosition[i];
                double y2 = optimalHoodPosition[i + 1];

                // Linear interpolation formula
                return y1 + (x - x1) *
                        (y2 - y1) / (x2 - x1);
            }
        }

        return optimalVelocity[optimalVelocity.length-1];
    }
}
