package org.firstinspires.ftc.teamcode.op1;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.CalibrateTargetColor;
import org.firstinspires.ftc.teamcode.util.Configuration;
import org.firstinspires.ftc.teamcode.vision.HSVColor;
import org.firstinspires.ftc.teamcode.vision.TeamElementDetector;
import org.firstinspires.ftc.teamcode.vision.Webcam;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

import kotlin.math.UMathKt;

@Autonomous
public class OpenCVAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        // Get the webcam from the hardware map
        Webcam webcam = new Webcam("Webcam 1", hardwareMap);
        Configuration config = new Configuration();

        HSVColor hsv = null;

        if (config.get("target") != null) {
            hsv = (HSVColor) config.get("target");
        } else {
            telemetry.log().add("WARNING WARNING WARNING:");
            telemetry.log().add("TARGET COLOR WAS NOT CALIBRATED!!!");
            telemetry.log().add("Please run the Calibrate Target Color OpMode!");
            android.util.Log.w("TeamElementDemo", "Target Color was NOT CALIBRATED! Falling back to default");
            hsv = new HSVColor(0.0,0.0,0.0);
        }

        // Setup the detector pipeline
        TeamElementDetector detector = new TeamElementDetector(hsv.toScalar());
        webcam.setPipeline(detector);

        // Open the camera
        webcam.open();

        int votes = 0;
        int rightvotes = 0;
        int leftvotes = 0;
        int centervotes = 0;

        // Wait for the OpMode to start
        waitForStart();
        //Whie Op Mode is Active

        Double time = getRuntime();
        // While the less there is less than 1000 votes
        while (votes < 500 && opModeIsActive()) {
            // Log the result of our analysis
            telemetry.addData("Analysis", detector.getAnalysis());

            if (detector.getAnalysis() == TeamElementDetector.TeamElementPosition.RIGHT) {
                rightvotes++;
            } else if (detector.getAnalysis() == TeamElementDetector.TeamElementPosition.CENTER) {
                centervotes++;
            } else {
                leftvotes++;
            }
            // Don't burn CPU cycles busy-looping
            // Normally, more processing would go here

            votes++;
            Double elapsed = getRuntime() - time;
            telemetry.addData("Right Votes", rightvotes);
            telemetry.addData("Left Votes", leftvotes);
            telemetry.addData("Center Votes", centervotes);
            telemetry.addData("Total Votes", votes);
            telemetry.addData("Time", elapsed);
            telemetry.update();
            sleep(10);
        }

        if (leftvotes >= centervotes && leftvotes >= rightvotes) {
            telemetry.log().add("Decided left");
        } else if (centervotes >= leftvotes && centervotes >= rightvotes) {
            telemetry.log().add("Decided right");
        } else {
            telemetry.log().add("Decided center");
        }

        telemetry.update();

        sleep(10000);
    }
}