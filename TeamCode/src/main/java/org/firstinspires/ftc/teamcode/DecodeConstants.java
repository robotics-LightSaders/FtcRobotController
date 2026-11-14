package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

public class DecodeConstants {

    //region - Hardware
    DcMotor fr;
    DcMotor fl;
    DcMotor br;
    DcMotor bl;

    DcMotorEx launch; //need Ex?
    DcMotor intake;
    DcMotor transfer;

    Servo hood;
    Servo camrot;
    Servo park;

    CRServo intakedos;

    Servo susan;
    AnalogInput susanenc;

    CRServo turret;
    AnalogInput turretenc;
    RTPAxon taxon;
    OctoQuadFWv3 oq;

    GoBildaPinpointDriver odo;

    DigitalChannel f0;
    DigitalChannel f1;
    DigitalChannel r0;
    DigitalChannel r1;
    DigitalChannel l0;
    DigitalChannel l1;

    NormalizedColorSensor f2;
    NormalizedColorSensor r2;
    NormalizedColorSensor l2;

    NormalizedRGBA f2c;
    NormalizedRGBA r2c;
    NormalizedRGBA l2c;
    //endregion

    //region - Colors
    String teamcolor = "Red";
    String FC = "None";
    String RC = "None";
    String LC = "None";
    String green = "Left";
    String purple = "Right";
    String purple2 = "None";
    String green2 = "None";
    String revf = "None";
    String revr = "None";
    String revl = "None";

    double brianf = 0;
    double brianr = 0;
    double brianl = 0;
    double hughf = 0;
    double hughr = 0;
    double hughl = 0;
    float[] hsvF;
    float[] hsvR;
    float[] hsvL;

    double preballs = 0;
    double balls = 0;
    boolean full = false;
    boolean firstfull = false;
    double fulltime = 0;

    double bratio = 1.6;
    double gratio = 2; //2.5
    double fbratio = 1.5;
    //endregion

    //region - Launch Limits
    double transferPower = 0;
    double launchvel = 0; //-12
    double launchdist = 0;
    boolean launchy = false;
    boolean canlaunch = false;
    boolean susanprep = false;
    boolean firstlaunch = false;
    boolean launched = false;
    boolean intaking = true;
    boolean fixing = false;
    boolean isSort = false;
    boolean greenLaunched = false;
    boolean purpleLaunched = false;
    boolean transferCycle = false;
    boolean rapidlaunch = false;
    boolean presort = false;
    boolean autolaunch = false;
    //endregion

    //region - Park
    double parkPOS = 0.35;
    boolean parkCheck = false;
    boolean parkON = false;
    double parkUp = 0.3; //0.35
    double parkDown = 0.6; //0.01, 0.9
    //endregion

    //region - Drive Variables
    double frpower;
    double flpower;
    double brpower;
    double blpower;

    double zto = 0;
    double yto = 0;
    double xto = 0;

    double yE = 0;
    double xE = 0;
    double zE = 0;

    double yP = 0;
    double xP = 0;
    double zP = 0;

    double YPOWER = 0;
    double XPOWER = 0;
    double ZPOWER = 0;

    double lastang = 0;
    double angvel = 0;
    double xvel = 0;
    double yvel = 0;

    double gatered = 30;
    double gateblue = 145;
    double gatetar = gatered;
    double gatetol = 1.5; //2, 1?

    double truz = 0;
    double zchange = 0;
    //endregion

    //region - Susan
    boolean canscan = false;
    double intakescan = 0;
    double lastdiff = 0;

    boolean donescan = false;

    double susanpre1 = 0; //0.14, 0.11
    double susanauto1 = 0.15;
    double susanpos1 = 0.17; //0.16, 0.19, 0.17, 0.14
    double susanpos2 = 0.55; //0.54, 0.62, 0.57, 0.54, 0.51
    double susanpos3 = 0.93; //0.92, 0.96 0.93, 0.9
    double susanauto3 = 0.94;
    double susanbtw12 = 0.35; //0.37
    double susanbtw23 = 0.73; //0.75
    double susanbtw31 = 0; // 1 //0.25 for btw 1 and 2, 0.62 fr btw 2 and 3
    double susanlaunch1 = 0.08; //0.1
    double susanlaunch2 = 0.47; //0.47, 0.44
    double susanlaunch3 = 0.85;
    double susanpast3 = 1;//1 CHANGED
    double susanpid3 = 0.95;

    double susanminV = 0.211; //0?
    double susanmaxV = 3.025; //3.3?
    double susanVrange = susanmaxV - susanminV;
    double susanV = 0;
    double susanmin = 23;
    double susanmax = 325;
    double susanrange = susanmax - susanmin;
    double stol = 10; //tune
    boolean rapido = false;
    boolean resetballs = false;

    double susanat = 0;

    double susandegreesat = 0;
    double susandegreesto = 0;
    double scantime = 0;

    double susprev = 0;
    boolean jam = false;
    //endregion

    //region - Hood
    double hooddown = 0;
    double hoodmid = 0.35;
    double hoodfar = 0.7;
    double hoodup = 0.85;
    double hoffset = 0;
    //endregion

    //region - Turret
    double tpos = 0;
    double ttar = 0;
    double terror = 0;

    double turtar = 0;
    double tx = 0;

    double tmaxp = 1; //0.5, 0.1, 0.16
    double tminp = 0.08; //0.09, 7
    double ttol = 2; //5, 4, 3
    double div = 40;
    double min = 1.2;
    double z3 = 10;

    double rev = 0;
    double turdegperrev = 30; //degrees/servo rev
    double turdegpos = 0;

    double fangle = 0;
    double turretpwrcoeff = 0;
    double turretpower = 0.05; //0.1, 0.16
    double toffset = 0;
    double turretmiddle = 588; //500 / 430
    double turret90 = 834; //850, 870 / 680
    double turret270 = 340; //130, 150 / 190

    double m = -0.365551; //-0.367267
    double mold = -0.247225; //0.250344 (251/990) = degree range / encoder range (-0.253535, 249982)
    double b = 394; //339
    double bold = 307; //57 (304)
    double ttmin = 247;
    double ttmax = 927; //995, 990 / 777
    double tdmin = (ttmax * m) + b;
    double tdmax = (ttmin * m) + b; //b, 303
    double turretRad = 7.9; //23/3

    double tlim = 0.12;
    double tv = 0; //max ~120 deg/sec
    double tvel = 0;
    double tlast = 0;
    //endregion

    //region - Cam
    double camlong = 0.43;
    double camclose = 0.36;
    double camlim = 53.7; //36 + 17.7
    double center = 320;
    double ctol = 10; //7
    double camtaroffset = 0;
    double cxoffset = 0;

    AprilTagDetection goalcode;
    AprilTagDetection goalcode0;
    AprilTagDetection goalcode1;
    AprilTagDetection ob;

    double goaltime = 0;
    Position usePos = new Position(DistanceUnit.INCH, 0, 0, 0, 0);
    Position lastCam = new Position(DistanceUnit.INCH, 0, 0, 0, 0);

    String order = "ppg";
    String firstorder = "gpp";
    //endregion

    //region - Launch Pos & PID
    Pose2D Rgoal = new Pose2D(DistanceUnit.INCH, -65, 65, AngleUnit.DEGREES, -135);
    Pose2D Bgoal = new Pose2D(DistanceUnit.INCH, -65, -65, AngleUnit.DEGREES, -45);
    Pose2D goal = Rgoal;
    Pose2D LAUNCH = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
    Pose2D notlaunch = new Pose2D(DistanceUnit.INCH, 200, 200, AngleUnit.DEGREES, 0);

    public static PIDFCoefficients PIDFc = new PIDFCoefficients(150, 0, 0, 14.13);
    public static PIDFCoefficients PIDFf = new PIDFCoefficients(160, 0, 0, 13.39); //147 0.75, 144 0.5 13.28
    //endregion

    //region - Tracking
    double newdist = 4; //0

    double goaldistanceT = 0;
    double goaldistanceX = 0;
    double goaldistanceY = 0;
    double goalangle = 0;
    double TFangle = 0;
    double GTangle = 0;
    double Gangle = 0;
    double Tangle = 0;
    double roboheading = 0;

    double velocityT = 0;
    double shootmin = 37; //39
    boolean tooclose = false;
    //endregion

    //region - Box Test Positions
    Pose2D linetest1 = new Pose2D(DistanceUnit.INCH, 25, 25, AngleUnit.DEGREES, -90);
    Pose2D linetest2 = new Pose2D(DistanceUnit.INCH, 25, -25, AngleUnit.DEGREES, 180);
    Pose2D linetest3 = new Pose2D(DistanceUnit.INCH, -25, 25, AngleUnit.DEGREES, 0);
    Pose2D linetest4 = new Pose2D(DistanceUnit.INCH, -25, -25, AngleUnit.DEGREES, -160);
    Pose2D linetest5 = new Pose2D(DistanceUnit.INCH, 25, 25, AngleUnit.DEGREES, 135);
    //endregion

    //region - Auto Positions
    Pose2D Rlaunch = new Pose2D(DistanceUnit.INCH, 62, 10, AngleUnit.DEGREES, -118);
    Pose2D Blaunch = new Pose2D(DistanceUnit.INCH, 62, -10, AngleUnit.DEGREES, -56); //-59
    Pose2D Rpark1 = new Pose2D(DistanceUnit.INCH, 55, 15, AngleUnit.DEGREES, -90);
    Pose2D Bpark1 = new Pose2D(DistanceUnit.INCH, 55, -15, AngleUnit.DEGREES, -90);
    Pose2D launchpos = new Pose2D(DistanceUnit.INCH, 18, -18, AngleUnit.DEGREES, 180);

    Pose2D Rstart = new Pose2D(DistanceUnit.INCH, 63, 8.25, AngleUnit.DEGREES, 0);
    Pose2D Bstart = new Pose2D(DistanceUnit.INCH, 63, -8.25, AngleUnit.DEGREES, 180);

    Pose2D Rpreintake1 = new Pose2D(DistanceUnit.INCH, 33.5, 26, AngleUnit.DEGREES, 0);  //36 29, 34
    Pose2D Rpreintake1x = new Pose2D(DistanceUnit.INCH, 33.5, 34, AngleUnit.DEGREES, 0);
    Pose2D Rintake1 = new Pose2D(DistanceUnit.INCH, 36, 65, AngleUnit.DEGREES, 0); //62, 65, 70, 72 //CHANGED
    Pose2D Bpreintake1 = new Pose2D(DistanceUnit.INCH, 39.5, -29, AngleUnit.DEGREES, 175); //38.5, 30 170
    Pose2D Bintake1 = new Pose2D(DistanceUnit.INCH, 40, -65, AngleUnit.DEGREES, 175); //39.5, 60, 62 170

    Pose2D Rlaunch1 = new Pose2D(DistanceUnit.INCH, 55, 15, AngleUnit.DEGREES, -8); //61, 10, 15 0, -4, -6
    Pose2D Rlaunch1q = new Pose2D(DistanceUnit.INCH, 55, 17, AngleUnit.DEGREES, -8); //61, 10, 15 0, -4, -6, -3
    Pose2D Rlaunch1b = new Pose2D(DistanceUnit.INCH, 60, 15, AngleUnit.DEGREES, -13); //61, 10, 15 0, -4, -6, -8 //62,8  61, 9   60, 10
    Pose2D Rlaunch1c = new Pose2D(DistanceUnit.INCH, 65, 17, AngleUnit.DEGREES, -12); //65, 68 -27, -23, -15, 67 0
    Pose2D Rlaunch1c2 = new Pose2D(DistanceUnit.INCH, 60, 19, AngleUnit.DEGREES, -12); //-20 //worked 18 but pierce nervous change y from 15
    Pose2D Rlaunch1f = new Pose2D(DistanceUnit.INCH, 62, 17, AngleUnit.DEGREES, 0);

    Pose2D Rlaunch1a = new Pose2D(DistanceUnit.INCH, 60, 15, AngleUnit.DEGREES, -4); //0
    Pose2D Rlaunch2 = new Pose2D(DistanceUnit.INCH, -2, 9, AngleUnit.DEGREES, -15); //10, 13
    Pose2D Rlaunch3 = new Pose2D(DistanceUnit.INCH, -17, 17, AngleUnit.DEGREES, -60); //14 24 0, -50
    Pose2D Rlaunch3i = new Pose2D(DistanceUnit.INCH, -17, 17, AngleUnit.DEGREES, 130);
    Pose2D Rlaunch3p = new Pose2D(DistanceUnit.INCH, -17, 17, AngleUnit.DEGREES, -135);

    Pose2D Blaunch1 = new Pose2D(DistanceUnit.INCH, 55, -15, AngleUnit.DEGREES, -170); //-170
    Pose2D Blaunch1a = new Pose2D(DistanceUnit.INCH, 60, -15, AngleUnit.DEGREES, 175); //-170, -175
    Pose2D Blaunch1b = new Pose2D(DistanceUnit.INCH, 55, -25, AngleUnit.DEGREES, -170); // y was -17//-170
    Pose2D Blaunch1bf = new Pose2D(DistanceUnit.INCH, 55, -20, AngleUnit.DEGREES, -170); // y was -17//-170
    //added -25 -20
    Pose2D Blaunch2 = new Pose2D(DistanceUnit.INCH, -2, -9, AngleUnit.DEGREES, -165); //2, 10, 3 13, 2, 5 14
    Pose2D Blaunch3 = new Pose2D(DistanceUnit.INCH, -17, -17, AngleUnit.DEGREES, -130);
    Pose2D Blaunch3s = new Pose2D(DistanceUnit.INCH, -14, -17, AngleUnit.DEGREES, -130);
    Pose2D Blaunch3a = new Pose2D(DistanceUnit.INCH, -17, -17, AngleUnit.DEGREES, -125);
    Pose2D Blaunch3i = new Pose2D(DistanceUnit.INCH, -17, -17, AngleUnit.DEGREES, 47); //50
    Pose2D Blaunch3p = new Pose2D(DistanceUnit.INCH, -17, -17, AngleUnit.DEGREES, -45);

    Pose2D Rpreintake2 = new Pose2D(DistanceUnit.INCH, 12, 25, AngleUnit.DEGREES, 0); //10.5 14 30, 11, 12 27, 11, 10.5, 11.25, 11
    Pose2D Rpreintake2g = new Pose2D(DistanceUnit.INCH, 12.5, 34, AngleUnit.DEGREES, 0); //12 36, 32
    Pose2D Rintake2 = new Pose2D(DistanceUnit.INCH, 12, 63, AngleUnit.DEGREES, 0); //10.5 14.5 60, 65, 11, 12 70, 10.5, 11.25, 11.5 62, 11
    Pose2D Rdodge = new Pose2D(DistanceUnit.INCH, 13, 23, AngleUnit.DEGREES, -45); //14 30, 11, 12 27, 11, 10.5

    Pose2D Bpreintake2 = new Pose2D(DistanceUnit.INCH, 17, -25, AngleUnit.DEGREES, 175); //17 170, 29, 18 26, 17, 16.75, 16
    Pose2D Bpreintake2g = new Pose2D(DistanceUnit.INCH, 17.5, -34, AngleUnit.DEGREES, 175);
    Pose2D Bintake2 = new Pose2D(DistanceUnit.INCH, 17, -72, AngleUnit.DEGREES, 175);//was 16 //was -67 //19, 60 170, 61, 63, 18, 17, 16.75, 16, 15, 13 -66, 15
    Pose2D Bdodge = new Pose2D(DistanceUnit.INCH, 13, -20, AngleUnit.DEGREES, -135); //17 170, 29, 18 26, 17, 16.75, 16 25

    Pose2D Bpreintake2c = new Pose2D(DistanceUnit.INCH, 16, -27, AngleUnit.DEGREES, 175); //17.5
    Pose2D Bintake2c = new Pose2D(DistanceUnit.INCH, 15, -71, AngleUnit.DEGREES, 175); //16.5
    Pose2D Bintake3c = new Pose2D(DistanceUnit.INCH, -11, -56, AngleUnit.DEGREES, 175); //-9 -54

    Pose2D Rpreintake3 = new Pose2D(DistanceUnit.INCH, -14, 30, AngleUnit.DEGREES, 0); //CHANGED x was -12
    Pose2D Rintake3 = new Pose2D(DistanceUnit.INCH, -14, 54, AngleUnit.DEGREES, 0); //CHANGED x was -12
    Pose2D Bpreintake3 = new Pose2D(DistanceUnit.INCH, -8, -30, AngleUnit.DEGREES, 175); //-9
    Pose2D Bintake3 = new Pose2D(DistanceUnit.INCH, -8, -54, AngleUnit.DEGREES, 175); //-9

    Pose2D Rpark2 = new Pose2D(DistanceUnit.INCH, 0, 40, AngleUnit.DEGREES, 0); //30
    Pose2D Bpark2 = new Pose2D(DistanceUnit.INCH, 0, -40, AngleUnit.DEGREES, -170);

    Pose2D autoend = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);

    //region - Exodus & Sabers
    Pose2D Rex = new Pose2D(DistanceUnit.INCH, -41, 55, AngleUnit.DEGREES, -90);
    Pose2D Bex = new Pose2D(DistanceUnit.INCH, -41, -55, AngleUnit.DEGREES, -90);

    Pose2D RexG = new Pose2D(DistanceUnit.INCH, 2, 56, AngleUnit.DEGREES, -100); //-4, 0
    Pose2D RexGi = new Pose2D(DistanceUnit.INCH, -2, 61, AngleUnit.DEGREES, 100); //-8 56, 60 90
    Pose2D BexG = new Pose2D(DistanceUnit.INCH, 4, -56, AngleUnit.DEGREES, -80); //was -4 //-4 for ST 100, -8
    Pose2D BexGi = new Pose2D(DistanceUnit.INCH, 1, -61, AngleUnit.DEGREES, 80); //WAS 1//-2
    Pose2D BexGe = new Pose2D(DistanceUnit.INCH, -4, -56, AngleUnit.DEGREES, -100); //8 100

    Pose2D RexST1 = new Pose2D(DistanceUnit.INCH, 14, 59, AngleUnit.DEGREES, -45);
    Pose2D RexST2 = new Pose2D(DistanceUnit.INCH, 54, 60, AngleUnit.DEGREES, -90); //36 59, 42 60
    Pose2D BexST1 = new Pose2D(DistanceUnit.INCH, 14, -59, AngleUnit.DEGREES, -135);
    Pose2D BexST2 = new Pose2D(DistanceUnit.INCH, 60, -60, AngleUnit.DEGREES, -90); //54

    Pose2D Rexe = new Pose2D(DistanceUnit.INCH, 0, 40, AngleUnit.DEGREES, 90); //-90 for ST
    Pose2D Bexe = new Pose2D(DistanceUnit.INCH, 0, -40, AngleUnit.DEGREES, 90); //-90 for ST

    Pose2D RsabG1 = new Pose2D(DistanceUnit.INCH, 54, 60, AngleUnit.DEGREES, 50); //36, 48, 58 61 70
    Pose2D RsabG2 = new Pose2D(DistanceUnit.INCH, 24, 65, AngleUnit.DEGREES, 50); //20 61 70, 18 64 10, 24 30, 22
    Pose2D BsabG1 = new Pose2D(DistanceUnit.INCH, 54, -60, AngleUnit.DEGREES, 130);
    Pose2D BsabG2 = new Pose2D(DistanceUnit.INCH, 24, -65, AngleUnit.DEGREES, 130); //22

    Pose2D RsabC1 = new Pose2D(DistanceUnit.INCH, 62.5, 54, AngleUnit.DEGREES, 0); //63, 64, 63, 62
    Pose2D RsabC1a = new Pose2D(DistanceUnit.INCH, 62.5, 64, AngleUnit.DEGREES, 0); //ADDED
    Pose2D RsabC2 = new Pose2D(DistanceUnit.INCH, 62, 62, AngleUnit.DEGREES, 0); //63
    Pose2D RsabC2x = new Pose2D(DistanceUnit.INCH, 62, 60, AngleUnit.DEGREES, 0); //63
    Pose2D RsabC3 = new Pose2D(DistanceUnit.INCH, 59, 62, AngleUnit.DEGREES, 0); //62
    Pose2D BsabC1 = new Pose2D(DistanceUnit.INCH, 62.5, -54, AngleUnit.DEGREES, -175); //62
    Pose2D BsabC2 = new Pose2D(DistanceUnit.INCH, 62, -62, AngleUnit.DEGREES, -175);
    Pose2D BsabC3 = new Pose2D(DistanceUnit.INCH, 59, -62, AngleUnit.DEGREES, -175);

    Pose2D RsabC15 = new Pose2D(DistanceUnit.INCH, 62.5, 48, AngleUnit.DEGREES, 0); //63, 64, 63, 62
    Pose2D RsabC25x = new Pose2D(DistanceUnit.INCH, 62, 54, AngleUnit.DEGREES, 0); //63

    Pose2D RexfCa = new Pose2D(DistanceUnit.INCH, 58, 54, AngleUnit.DEGREES, 0); //63, 64, 63, 62
    Pose2D RexfCb = new Pose2D(DistanceUnit.INCH, 55, 62, AngleUnit.DEGREES, 0); //63
    Pose2D BexfCa = new Pose2D(DistanceUnit.INCH, 58, -54, AngleUnit.DEGREES, -175); //62
    Pose2D BexfCb = new Pose2D(DistanceUnit.INCH, 55, -62, AngleUnit.DEGREES, -175);

    Pose2D RFXcp = new Pose2D(DistanceUnit.INCH, 52, 54, AngleUnit.DEGREES, 0);
    Pose2D RFXc = new Pose2D(DistanceUnit.INCH, 50, 62, AngleUnit.DEGREES, 0);
    Pose2D BFXcp = new Pose2D(DistanceUnit.INCH, 47, -54, AngleUnit.DEGREES, -175); //52
    Pose2D BFXc = new Pose2D(DistanceUnit.INCH, 45, -62, AngleUnit.DEGREES, -175); //50

    Pose2D RexGCp = new Pose2D(DistanceUnit.INCH, 6, 60, AngleUnit.DEGREES, 25); //12 50 0, 6
    Pose2D RexGC = new Pose2D(DistanceUnit.INCH, 15, 60, AngleUnit.DEGREES, 45); //65 30
    Pose2D BexGCp = new Pose2D(DistanceUnit.INCH, 7, -60, AngleUnit.DEGREES, 150); //12 50 175, 9
    Pose2D BexGC = new Pose2D(DistanceUnit.INCH, 15, -60, AngleUnit.DEGREES, 135); //8, 12
    //endregion
    //endregion

    //region - Odo
    Pose2D odoorigin = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
    Position campos = new Position(DistanceUnit.INCH, 0, 0, 0, 0);
    Position robopos = new Position(DistanceUnit.INCH, 0, 0, 0, 0);

    double camlenstorobocenter = 7.7;

    double xodooffset = 64; //-64
    double yodooffset = 95.5; //95.5

    public void configureOdo(boolean reset) {
        odo.setOffsets(xodooffset, yodooffset, DistanceUnit.MM);
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        if (reset) {
            odo.resetPosAndIMU();
        }
    }
    //endregion

    public double getTurretAngle(double turretpos) {
        return ((m * turretpos) + b);
    }

    public double setTurretAngle(Pose2D ODO, Pose2D GOAL, String color) {
        double coff = 0;

        double G = -goalA(ODO, GOAL, color);

        if (G < 20) { //15
            if (color.matches("Red")) {
                coff = 12; //7, 9
            } else {
                coff = -12; //7, 9
            }
        }

        double T = G - ODO.getHeading(AngleUnit.DEGREES);

        if (T > 360) {
            T = T - 360;
        } else if (T < 0) {
            T = T + 360;
        }

        if (T < tdmin) {
            T = tdmin;
        } else if (T > tdmax) {
            T = tdmax;
        }

        return (((T - b) / m) + coff);
    }

    public void initTaxon() {
        taxon = new RTPAxon(turret, turretenc, RTPAxon.Direction.REVERSE);
        taxon.setMaxPower(tmaxp);
        taxon.setRtp(false);
    }

    public double launchVelCalc(double dist, boolean lunch) {
        double velR;
        if (lunch) {
            if (dist < 16) { //18?
                velR = 2800;
            } else if (dist < 42) {
                velR = (0.00321502 * Math.pow(dist, 4)) - (0.424383 * Math.pow(dist, 3)) +
                        (20.71759 * Math.pow(dist, 2)) - (443.05556 * dist) + 6200;
            } else if (dist < 60) {
                velR = ((50 / 3) * dist) + 2000;
            } else if (dist < 84) {
                velR = ((-0.00482253) * Math.pow(dist, 4)) + (1.3696 * Math.pow(dist, 3)) -
                        (144.27083 * Math.pow(dist, 2)) + (6692.36111 * dist) - 112550; //112575.5
            } else if (dist < 108) {
                velR = (150 / (1 + Math.pow(Math.E, ((-1.25) * (dist - 93))))) + 3200; //3350
            } else if (dist < 132) {
                velR = (0.0000095797 * Math.pow(dist, 5)) - (0.00414031 * Math.pow(dist, 4)) +
                        (0.664019 * Math.pow(dist, 3)) - (46.91885 * Math.pow(dist, 2)) + (1266.82366 *
                        dist) - 150; //0
            } else { //144
                velR = Math.pow(Math.E, (0.115525 * (dist - 98.13686))) + 3600; //3800
            }
        } else {
            velR = 2000;
        }

        return velR;
    }

    public double launchHoodCalc(double dist, boolean lunch) {
        double hpos;
        if (lunch) {
            if (dist < 42) {
                hpos = hooddown;
            } else if (dist < 48) {
                hpos = (0.025 * dist) - 1.05;
            } else if (dist < 66) {
                hpos = 0.15;
            } else if (dist < 78) {
                hpos = (dist / 60) - 0.95;
            } else if (dist < 84) {
                hpos = 1 - (dist / 120) + 0.02; //0
            } else if (dist < 90) {
                hpos = 0.3;
            } else if (dist < 96) {
                hpos = 1.05 - (dist / 120);
            } else if (dist < 102) {
                hpos = (dist / 120) - 0.55;
            } else if (dist < 108) {
                hpos = 0.3;
            } else if (dist < 114) {
                hpos = 1.2 - (dist / 120);
            } else if (dist < 120) {
                hpos = 0.44 - (dist / 600);
            } else if (dist < 126) {
                hpos = (0.01 * dist) - 0.96;
            } else if (dist < 132) {
                hpos = (dist / 150) - 0.54;
            } else if (dist < 138) {
                hpos = 0.34;
            } else if (dist < 144) {
                hpos = (dist / 120) - 0.81;
            } else {
                hpos = 0.39;
            }
        } else {
            hpos = 0.2;
        }

        return hpos;
    }

    public double launchCalc(double dist, boolean fw) {
        if (fw) {
            if (dist > 112) {
                return ((2.5 * dist) + 3237.5) / 60 * 28;
            } else {
                return ((10.482 * dist) + 1799.3) / 60 * 28;
            }
        } else {
            if (dist > 112) {
                return hoodfar;
            } else {
                return Range.clip(((0.00723 * dist) - 0.311), 0, 0.6);
            }
        }
    }

    public double launchCal(double dist, double x, boolean fw, boolean pd, boolean d, boolean auto) {
        if (!pd) {
            if (fw) {
                if (x > 20) {
                    return (Math.pow(10.52885, ((0.0327551 * dist) - 2.35303)) + 3504.585) / 60 * 28;
                } else {
                    double v = ((0.00000329682 * Math.pow(dist, 5)) - (0.00140714 * Math.pow(dist, 4)) + (0.232815 * Math.
                            pow(dist, 3)) - (18.56979 * Math.pow(dist, 2)) + (721.88324 * dist) - 8742.1253) / 60 * 28;

                    if (!auto && (dist > 130) && (dist < 145)) {
                        return v + 20;
                    } else {
                        return v;
                    }
                }
            } else {
                if (x > 20) {
                    return 0.77;
                } else {
                    if (dist > 95) {
                        return ((0.000001757576 * Math.pow(dist, 4)) + ((-0.000762222) * Math.pow(dist, 3)) + (0.1234318 *
                                Math.pow(dist, 2)) + ((-8.842111) * dist) + 236.812);
                    } else if (dist > 90) {
                        return 0.43;
                    } else {
                        return ((0.00001331779 * Math.pow(dist, 3)) - (0.002665035 * Math.pow(dist, 2)) + (0.1806099 * dist) - 3.94);
                    }
                }
            }
        } else if (!d) {
            if (x > 20) {
                return (((-1164.98731) / (47.92894 + (0.00000000138912 * Math.pow(Math.E, (0.1813 * dist))))) + 170.21);
            } else {
                return 150;
            }
        } else {
            if (x > 20) {
                if (dist > 131.5) { //136
                    return 0.9;
                } else {
                    return 0.75;
                }
            } else {
                return 0;
            }
        }
    }

    public double LC(double distance, double x, boolean fw) {
        double dist = distance;

        if (fw) {
            if (x > 20) {
                dist = distance + turretRad;

                //return ((9 * dist) + 1990) / 60 * 28; //1975; new far?
                return ((8.3 * dist) + 2240) / 60 * 28; //2200
            } else {
                dist = distance;
                double v = ((0.00000329682 * Math.pow(dist, 5)) - (0.00140714 * Math.pow(dist, 4)) + (0.232815 * Math.
                        pow(dist, 3)) - (18.56979 * Math.pow(dist, 2)) + (721.88324 * dist) - 8640) / 60 * 28; //8742.1253, 8694, 8675

                if ((v / 28 * 60) < 2150) {
                    v = 2170 / 60 * 28; //2120
                }

                return v;
            }
        } else {
            if (x > 20) {
                return 0.4;
            } else {
                if (dist > 95) {
                    return ((0.000001757576 * Math.pow(dist, 4)) + ((-0.000762222) * Math.pow(dist, 3)) + (0.1234318
                            * Math.pow(dist, 2)) + ((-8.842111) * dist) + 236.812);
                } else if (dist > 90) {
                    return 0.43;
                } else {
                    return ((0.00001331779 * Math.pow(dist, 3)) - (0.002665035 * Math.pow(dist, 2)) + (0.1806099 *
                            dist) - 3.94);
                }
            }
        }
    }

    public double goalD(Pose2D odo, Pose2D g, boolean cam2robot) {
        double gdx = g.getX(DistanceUnit.INCH) - odo.getX(DistanceUnit.INCH);
        double gdy = g.getY(DistanceUnit.INCH) - odo.getY(DistanceUnit.INCH);
        double gdt = Math.sqrt(Math.pow(gdx, 2) + Math.pow(gdy, 2));

        if (cam2robot) {
            return gdt - 8;
        } else {
            return gdt;
        }
    }

    public double goalDC(Position pos, Pose2D g) {
        double gdx = g.getX(DistanceUnit.INCH) - pos.x;
        double gdy = g.getY(DistanceUnit.INCH) - pos.y;
        return Math.sqrt(Math.pow(gdx, 2) + Math.pow(gdy, 2));
    }

    public double GD(Pose2D odo, Pose2D goal) {
        double gdx = goal.getX(DistanceUnit.INCH) - odo.getX(DistanceUnit.INCH);
        double gdy = goal.getY(DistanceUnit.INCH) - odo.getY(DistanceUnit.INCH);
        return Math.sqrt(Math.pow(gdx, 2) + Math.pow(gdy, 2));
    }

    public double goalA(Pose2D odo, Pose2D g, String color) {
        double ga = Math.toDegrees(Math.atan((g.getX(DistanceUnit.INCH) - odo.getX(DistanceUnit.INCH)) /
                (g.getY(DistanceUnit.INCH) - odo.getY(DistanceUnit.INCH))));

        if (color.matches("Red")) {
            return ga;
        } else {
            return ga - 180;
        }
    }

    public double TurFangle(double taxrot, double tos, Pose2D odo) {
        double tfa = getTurretAngle(taxrot - tos) + odo.getHeading(AngleUnit.DEGREES) + 3;

        if (tfa > 180) {
            tfa = tfa - 360;
        } else if (tfa < -180) {
            tfa = tfa + 360;
        }

        return tfa;
    }

    public double FA2G(Pose2D o, Pose2D g) {
        return Math.toDegrees(Math.abs(Math.atan((g.getX(DistanceUnit.INCH) - o.getX(
                DistanceUnit.INCH)) / (g.getY(DistanceUnit.INCH) - o.getY(DistanceUnit.INCH)))));
    }

    public Position RP(Position CP, double TP, double TO, Pose2D OP) {
        Position rp = new Position();
        double RH = CP.z - getTurretAngle(TP - TO);

        if (RH > 180) {
            RH = RH - 360;
        } else if (RH < (-180)) {
            RH = RH + 360;
        }

        rp.x = CP.x + (turretRad * Math.sin(Math.toRadians(TurFangle(TP, TO, OP))));
        rp.y = (CP.y - (turretRad * Math.cos(Math.toRadians(TurFangle(TP, TO, OP))))) * 1.04; //multiplier
        rp.z = RH;

        return rp;
    }

    public double TuFiAn(double taxrot, Pose2D odo) {
        double tfa = getTurretAngle(taxrot) + odo.getHeading(AngleUnit.DEGREES) + 3;

        if (tfa > 180) {
            tfa = tfa - 360;
        } else if (tfa < -180) {
            tfa = tfa + 360;
        }

        return tfa;
    }

    public Position RoPo(Position CP, double TP, Pose2D OP) {
        Position rp = new Position();
        double RH = CP.z - getTurretAngle(TP);

        if (RH > 180) {
            RH = RH - 360;
        } else if (RH < (-180)) {
            RH = RH + 360;
        }

        rp.x = CP.x + (turretRad * Math.sin(Math.toRadians(TuFiAn(TP, OP))));
        rp.y = (CP.y - (turretRad * Math.cos(Math.toRadians(TuFiAn(TP, OP))))) * 1.04; //multiplier
        rp.z = RH;

        return rp;
    }

    public void initOQ() {
        oq.setChannelBankConfig(OctoQuadFWv3.ChannelBankConfig.ALL_PULSE_WIDTH);
        oq.setSingleChannelPulseWidthParams(1, 0, 1024); //1024 or 9000?
        oq.setSingleEncoderDirection(1, OctoQuadFWv3.EncoderDirection.REVERSE);
        oq.saveParametersToFlash();
    }
}
