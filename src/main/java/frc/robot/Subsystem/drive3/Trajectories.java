package frc.robot.Subsystem.drive3;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.vision.RPI;
import frc.robot.Subsystem.drive3.trajFunk.*;


//TODO: still unsure on how to take the 2D array and use it as a trajectory object
public class Trajectories {
    private static double dfltPwr = 0.9;
    private static SendableChooser<String> chsr = new SendableChooser<String>();
    private static String[] chsrDesc = {
        "getEmpty", "getSlalom", "getBarrel", "getBounce", "getSquare",
        "getPathRedA", "getPathBluA", "getPathRedB", "getPathBluB", "getPAthBlue", "getPathGalaxtic",
    };

    /**Initialize Traj chooser */
    public static void chsrInit(){
        for(int i = 0; i < chsrDesc.length; i++){
            chsr.addOption(chsrDesc[i], chsrDesc[i]);
        }
        chsr.setDefaultOption(chsrDesc[0] + " (Default)", chsrDesc[0]);   //Default MUST have a different name
        SmartDashboard.putData("Traj/Choice", chsr);
    }

    /**Show on sdb traj chooser info.  Called from robotPeriodic  */
    public static void chsrUpdate(){
        SmartDashboard.putString("Traj/String", chsr.getSelected());
        SmartDashboard.putNumber("Traj/Gxtc Num", RPI.galacticShooter());
    }

    /**
     * Get the trajectory array that is selected in the chooser Traj/Choice.
     * @param pwr - default pwr to be usedin trajectories
     * @return The active, selected, Chooser Trajectory for use by AutoSelector
     */
    public static ATrajFunction[] getTraj(double pwr){
        switch(chsr.getSelected()){
            case "getEmpty":
            return getEmpty(pwr);
            case "getSlalom":
            return getSlalom(pwr);
            case "getBarrel":
            return getBarrel(pwr);
            case "getBounce":
            return getBounce(pwr);
            case "getSquare":
            return getSquare(pwr);
            case "getPathRedA":
            return getPathRedA(pwr);
            case "getPathBluA":
            return getPathBluA(pwr);
            case "getPathRedB":
            return getPathRedB(pwr);
            case "getPathBluB":
            return getPathBluB(pwr);
            case "getPathBlue":
            return getPathBlue(pwr);
            case "getPathGalaxtic":
            return getPathGalaxtic(pwr);
            case "getCurve1_1":
            return getCurve1_1(pwr);
            case "getCurve1_7":
            return getCurve1_7(pwr);
            case "getCurve1_5":
            return getCurve1_5(pwr);
            case "getCurve7_1":
            return getCurve7_1(pwr);
            case "getCurve7_7":
            return getCurve7_7(pwr);
            case "getCurve7_5":
            return getCurve7_5(pwr);
            case "getCurve5_1":
            return getCurve5_1(pwr);
            case "getCurve5_7":
            return getCurve5_7(pwr);
            case "getCurve5_5":
            return getCurve5_5(pwr);
            case "getCurveTry":
            return getCurveTry(pwr);
            default:
            System.out.println("Traj/Bad Traj Desc - " + chsr.getSelected());
            return getEmpty(0);
        }
    }

    /**
     * Get the trajectory array that is selected in the chooser Traj/Choice.
     * <p>Use a default power, 0.9.
     * 
     * @return The active, selected, Chooser Trajectory for use by AutoSelector
     */
    public static ATrajFunction[] getTraj(){
        return getTraj(dfltPwr);
    }


    public static String getChsrDesc(){
        return chsr.getSelected();
    }

    //------------------ Trajectories -------------------------------
    // each trajectory/path/automode is stored in each method
    // name each method by the path its doing

    public static ATrajFunction[] getEmpty(double pwr) {
        ATrajFunction[] traj = { new TurnNMove(0, pwr, 0) };
        return traj;
    }

    public static ATrajFunction[] getSlalom(double pwr) {
        ATrajFunction traj[] = {
            new TurnNMove(0, pwr, 2.5), 
            new TurnNMove(-55, pwr, 5.17), 
            new TurnNMove(0, pwr, 8.5),
            new TurnNMove(57, pwr, 3.2),
            new TankTurnHdg(135, 0, .80),
            new TurnNMove(132, pwr, 5),
            new TurnNMove(180, pwr, 8.5), 
            new TurnNMove(-125, pwr, 4.57),
            new TurnNMove(180, pwr, 2)
        };
        return traj;
    }

    public static ATrajFunction[] getBarrel(double pwr) {
        ATrajFunction[] traj = {
            new TurnNMove(0,100,5.7),
            new TankTurnHdg(-10, .75, 0.1),    //Turn to the right
            new TurnNMove(0,100, 5),
            new TankTurnHdg(40, .1, .85),
            new TurnNMove(44, 100, 4.4),
            new TankTurnHdg(-165, .1, .9),
            new TurnNMove(180, 100, 18)
        };
        return traj;
    }

    public static ATrajFunction[] getBounce(double pwr) {
        ATrajFunction[] traj = {
            new TurnNMove(0, pwr, 3), //2.8
            new TurnNMove(270, pwr, 3.4), //3.2
            new TurnNMove(270, pwr, -2.5),
            new TurnNMove(230, pwr, -5.5), //6.3
            new TurnNMove(315, pwr, 1.8), 
            new TurnNMove(270, pwr, 7.3), //6.8
            new TurnNMove(270, pwr, -6.2), //6.8
            new TurnNMove(0, pwr, 5.7), //6
            new TurnNMove(270, pwr, 8.2),
            new TurnNMove(270, pwr, -1.8), //2.4
            new TurnNMove(-10, pwr, 4)
         };
        return traj;
    }

    public static ATrajFunction[] getSquare(double pwr) {
        System.out.println("Made it here: Traj Sq");
        ATrajFunction traj[] = {
        new TurnNMove(0, pwr, 7),
        new TurnNMove(90, pwr, 7),
        new TurnNMove(180, pwr, 7),
        new TurnNMove(270, pwr, 7),
        new TurnNMove(350, pwr, 0)
        };
        return traj;
    }

    //-------------- Galaxtic Search ------------------------
    //------------- Path in name open Snorfler --------------
    public static ATrajFunction[] getPathRedA(double pwr) {
        ATrajFunction[] traj = {
            new TurnNMove(0, pwr, 3),
            new TurnNMove(25, pwr, 5.3),
            new TurnNMove(-85, pwr, 6.2),
            new TurnNMove(0, pwr, 10.3)
         };
        return traj;
    }

    public static ATrajFunction[] getPathBluA(double pwr) {
        ATrajFunction[] traj = { 
            new TurnNMove(22, pwr, 9.3),
            new TurnNMove(-70, pwr, 6.5),
            new TurnNMove(27, pwr, 4),
            new TurnNMove(10, pwr, 4)
        };
        return traj;
    }

    public static ATrajFunction[] getPathRedB(double pwr) {
        ATrajFunction[] traj = {
            new TurnNMove(-33, 65, 3.5),
            new TurnNMove(40, pwr, 7),
            new TurnNMove(-60, pwr, 7.1),
            new TurnNMove(-10, pwr, 6.5)
         };
        return traj;
    }

    public static ATrajFunction[] getPathBluB(double pwr) {
        ATrajFunction[] traj = {
            new TurnNMove(11, pwr, 11),
            new TurnNMove(-60, pwr, 5),
            new TurnNMove(40, pwr, 4),
            new TurnNMove(20, pwr, 4)
         };
        return traj;
    }

    public static ATrajFunction[] getPathBlue(double pwr) {
        ATrajFunction[] traj = {
            new TurnNMove(21, pwr, 9.5),  //Move to BlueA ball1
            new TurnNMove(-72, pwr, 8.7),  //Move to B6 thru BlueB ball1
            new TurnNMove(48, pwr, 10.0),   //Move to BlueAB ball3
         };
        return traj;
    }

    /**
     * Establishes the Trajectory array from the Raspberry Pi
     * @param pwr - applied default power to turns and runs
     * @return the Trajectoy array for the path assigned by the Raspberry Pi
     */
    public static ATrajFunction[] getPathGalaxtic(double pwr) {
        switch (RPI.galacticShooter()) {
            case 1:
                return getPathRedA(pwr);
            case 2:
            return getPathBlue(pwr);   //inside Blue
            // return getPathBluB(70);   //inside Blue
            case 3:
                return getPathRedB(pwr);
            case 4:
                return getPathBlue(pwr);   //outside Blue
                // return getPathBluA(70);   //outside Blue
            default:
                System.out.println("Bad Galaxtic path - " + RPI.galacticShooter());
                return getEmpty(0);
        }
    }

    public static ATrajFunction[] getCurveTry(double fwd) {
        ATrajFunction traj[] = {
            new CurveTurn(1.0, -0.9, 1.0),
            new CurveTurn(1.0, 0.9, 1.0),
            new TurnNMove(0.0, 1.0, 4.0),
            new CurveTurn(1.0, 1.0, 1.0),
        };
        return traj;
    }

    public static ATrajFunction[] getCurve1_1(double fwd) {
        ATrajFunction traj[] = {
            new CurveTurn(1.0, 1.0, 3.0)
        };
        return traj;
    }

    public static ATrajFunction[] getCurve1_7(double fwd) {
        ATrajFunction traj[] = {
            new CurveTurn(1.0, 0.75, 3.0)
        };
        return traj;
    }

    public static ATrajFunction[] getCurve1_5(double fwd) {
        ATrajFunction traj[] = {
            new CurveTurn(1.0, 0.5, 3.0)
        };
        return traj;
    }

    public static ATrajFunction[] getCurve7_1(double fwd) {
        ATrajFunction traj[] = {
            new CurveTurn(0.75, 1.0, 3.0)
        };
        return traj;
    }

    public static ATrajFunction[] getCurve7_7(double fwd) {
        ATrajFunction traj[] = {
            new CurveTurn(0.75, 0.75, 3.0)
        };
        return traj;
    }

    public static ATrajFunction[] getCurve7_5(double fwd) {
        ATrajFunction traj[] = {
            new CurveTurn(0.75, 0.5, 3.0)
        };
        return traj;
    }

    public static ATrajFunction[] getCurve5_1(double fwd) {
        ATrajFunction traj[] = {
            new CurveTurn(0.5, 1.0, 3.0)
        };
        return traj;
    }

    public static ATrajFunction[] getCurve5_7(double fwd) {
        ATrajFunction traj[] = {
            new CurveTurn(0.5, 0.75, 3.0)
        };
        return traj;
    }

    public static ATrajFunction[] getCurve5_5(double fwd) {
        ATrajFunction traj[] = {
            new CurveTurn(0.5, 0.5, 3.0)
        };
        return traj;
    }

}
