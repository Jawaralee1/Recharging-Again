package frc.robot.Subsystem.drive.trajFunk;

import frc.robot.Subsystem.drive.Drive;
import frc.util.PIDXController;

/**
 * This AutoFunction turns to passed heading then moves passed distance.
 */
public class TurnNMove extends ATrajFunction {

    // private boolean finished = false;
    private double hdgSP = 0.0;
    private double distSP = 0.0;
    private double pwrMx = 0.0;

    
    public TurnNMove(double eHdg, double eDist, double ePwr) {
        hdgSP = eHdg;
        distSP = eDist;
        pwrMx = Math.abs(ePwr); // dont use negative power
    }

    public TurnNMove(double eHdg, double eDist) {
        hdgSP = eHdg;
        distSP = eDist;
        pwrMx = 1.0;
    }

    public void execute() {
        // update();
        switch (state) {
        case 0: // Init Trajectory, (1)turn to hdg then (2)moveto dist ...
            pidHdg = new PIDXController(1.0/70, 0.0, 0.0);
            pidHdg.enableContinuousInput(-180.0, 180.0);
            //Set extended values SP, DB, Mn, Mx, Exp, Cmp
            setExt(pidHdg, hdgSP, 2.0, 0.35, pwrMx, 2.0, true);

            pidDist = new PIDXController(-1.0/10, 0.0, 0.0);
            //Set extended values SP, DB, Mn, Mx, Exp, Cmp
            setExt(pidDist, distSP, 1.0, 0.2, pwrMx, 1.0, true);

            Drive.distRst();
            initSDB();
            state++;
            System.out.println("TNM2 - 0");
        case 1: // Turn to heading.  Do not move forward, yet.
            strCmd[0] = pidHdg.calculate(hdgFB());
            Drive.cmdUpdate(0.0, strCmd[0], false, 2);
            // Chk if hdg is done
            if (pidHdg.atSetpoint()) {
                state++;    // Chk hdg only
                Drive.distRst();
            }
            prtShtuff("TNM");
            break;
        case 2: // Move forward, steer Auto Heading and Dist
            strCmd[0] = pidHdg.calculate(hdgFB());
            strCmd[1] = pidDist.calculate(distFB());
            Drive.cmdUpdate(strCmd[1], strCmd[0], false, 2);
            // Chk if distance is done
            if (pidDist.atSetpoint()) state++; // Chk distance only
            prtShtuff("TNM");
            break;
        case 3:
            done();
            System.out.println("TNM2 - 3: ---------- Done -----------");
            break;
        }
        updSDB();
    }
}