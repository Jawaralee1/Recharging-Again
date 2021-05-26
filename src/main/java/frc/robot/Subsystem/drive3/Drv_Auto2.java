package frc.robot.Subsystem.drive3;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Subsystem.drive3.trajFunk.*;

public class Drv_Auto2 extends Drive {

    private static ATrajFunction[] traj;
    private static boolean allFinish = false;
    private static int autoStep = 0;
    private static int idx = 0;

    //Constructor.  Called with the path array
    public Drv_Auto2() {
    }

    /**Get the active Trajectories and initialize indexes.
     * <p>Reset Heading & Distance to 0.
     */
    public static void init() {
        traj = Trajectories.getTraj(1.0);
        autoStep = 0;
        idx = 0;
        allFinish = false;
        hdgRst();
        distRst();
        System.out.println("Auto2 - Init");
    } 

    //OK, WTF does this do? 
    public static void update() {
        sdbUpdate();
        switch (autoStep) {
            case 0:                 //Initialize the traj funk
                // System.out.println("Auto2 - 0");
                ATrajFunction.initTraj(); // Sets traj[x] state to 0
                autoStep++;
                break;
            case 1:                 //Run a leg of the path
                traj[idx].execute();
                if (ATrajFunction.finished()) autoStep++;
                break;
            case 2:                 //Closeout Leg
                ATrajFunction.done();
                idx++;
                autoStep = idx < traj.length ? 0 : autoStep++;
                break;
            case 3:                 //Done path
                setDone();          //Flag allFinished & Closeout all Legs (again?)
                cmdUpdate();
                break;
        }
    }

    private static void setDone() {
        allFinish = true;
        // for (ATrajFunction at : traj) {      //Why do it again?
        //     at.done();
        // }
    }

    public static boolean isAllFinish() {
        return allFinish;
    }

    public static void disable() {
        cmdUpdate();
    }

    public static void sdbInit() {
    }

    public static void sdbUpdate() {
        SmartDashboard.putNumber("Drv/Auto/DA3/Auto Step", autoStep);
        SmartDashboard.putNumber("Drv/Auto/DA3/Current Traj Idx", idx);
        SmartDashboard.putNumber("Drv/Auto/hdgFB", hdgFB());
        SmartDashboard.putNumber("Drv/Auto/hdgSP", steer.getHdgSP());
        SmartDashboard.putNumber("Drv/Auto/distFB", distFB());
        SmartDashboard.putNumber("Drv/Auto/distSP", steer.getDistSP());
    }

}
