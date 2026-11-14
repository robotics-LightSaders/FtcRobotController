package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class BiobuzzMecanumDrive {
    DcMotor fr;
    DcMotor fl;
    DcMotor br;
    DcMotor bl;
    GoBildaPinpointDriver odo;
    Pose2D odoorigin = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
    double xodooffset = 64; //-64
    double yodooffset = 95.5; //95.5

    double headingError;
    double maxAccelConstant = 400;
    double maxDeAccelConstant = 500;
    double PIDresponse = 7;
    double headingDerivative;
    double targetHeading = 0;
    double lastHeadingError;
    double headingPower;
    double lastHeadingTime;
    double kP = Math.pow(PIDresponse, 2) / maxAccelConstant;
    double kD = PIDresponse / maxDeAccelConstant;
    double headingDelta;
    double lastHeading;

    double odoVelo;
    double angAccel;
    double lastAngVelo;
    double deaccel;
    double angVelo;
    double maxVelo;
    double maxAccel;
    BiobuzzConstants bio = new BiobuzzConstants();



   public BiobuzzMecanumDrive(HardwareMap hardwareMap) {
        fl = hardwareMap.get(DcMotor.class, "fl");
        fr = hardwareMap.get(DcMotor.class, "fr");
        br = hardwareMap.get(DcMotor.class, "br");
        bl = hardwareMap.get(DcMotor.class, "bl");
        odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void navreset(boolean reset_opods, boolean recal_opods) {
        if (reset_opods) {
            odo.resetPosAndIMU(); //resets the position to 0 and recalibrates the IMU
        }

        if (recal_opods) {
            odo.recalibrateIMU(); //recalibrates the IMU without resetting position
        }
    }

    public void odoSetPos(Pose2D startPose) {
        odo.setPosition(startPose);
    }

    public Pose2D odoPose() {
        return odo.getPosition();

    }

    public void odoUpdate() {
        odo.update();
    }

    public void configureOdo(boolean reset) {
        odo.setOffsets(xodooffset, yodooffset, DistanceUnit.MM);
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        if (reset) {
            odo.resetPosAndIMU();
        }
    }

    public void setFr(double p) {
        fr.setPower(p);
    }

    public void setFL(double p) {
        fl.setPower(p);
    }

    public void setBr(double p) {
        br.setPower(p);
    }

    public void setBl(double p) {
        bl.setPower(p);
    }

    public void setPower(float x, float y, float z) {
        fl.setPower((Range.clip((y - x - z), -1, 1)));
        fr.setPower((Range.clip((y + x + z), -1, 1)));
        bl.setPower((Range.clip((y + x - z), -1, 1)));
        br.setPower((Range.clip((y - x + z), -1, 1)));
    }

    public double headingAlignment(double currentHeading, double headingTime) {
        currentHeading = -1 * currentHeading;
        if (currentHeading < 0) {
            currentHeading += 360;
        }
        Log.d("Heaidng", "Hedading" + currentHeading);
        headingDelta = currentHeading - lastHeading;
        headingError = targetHeading - currentHeading;
        if (headingError > 180) {
            headingError = headingError - 360;
        }
        if (headingError < -180) {
            headingError = headingError + 360;
        }
        // Derivative
        headingDerivative = (headingError - lastHeadingError) / (headingTime - lastHeadingTime);

        // PID calculation
        headingPower = (kP * headingError) + (kD * headingDerivative);
        lastHeadingTime = headingTime;
        lastHeadingError = headingError;
        return -headingPower / 7;
    }


    public void CalculateMetrics(double calcTime, double velocity) {

        odoVelo = velocity;
        angAccel = (odoVelo - lastAngVelo) / (calcTime - lastHeadingTime);

        if (angAccel < deaccel) {
            deaccel = angAccel;
            Log.d("CalcMetrics", "De accel " + deaccel);
        }
        if (angVelo > maxVelo) {
            maxVelo = angVelo;
            Log.d("CalcMetrics", "Max Velo" + maxVelo);

        }
        if (angAccel > maxAccel) {
            maxAccel = angAccel;
            Log.d("CalcMetrics", "Max Accel" + maxAccel);

        }
        lastHeadingTime = calcTime;
        lastAngVelo = odoVelo;
        //returns in order velocity, acceleration, de acceleration
    }

    public void setBrake() {
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setFloat() {
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }
}
