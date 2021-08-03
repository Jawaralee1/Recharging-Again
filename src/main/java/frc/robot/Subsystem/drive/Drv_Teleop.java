package frc.robot.Subsystem.drive;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.IO;
import frc.io.joysticks.JS_IO;
import frc.util.PIDXController;

/**
 * Extends the Drive class to manually control the robot in teleop mode.
 */
public class Drv_Teleop extends Drive {

    private static double tnkLeft() {return JS_IO.axLeftY.get();}       //Tank Left
    private static double tnkRight() {return JS_IO.axRightY.get();}     //Tank Right
    private static double arcMove() {return JS_IO.axLeftY.get();}       //Arcade move, fwd/bkwd
    private static double arcRot() {return JS_IO.axLeftX.get();}        //Arcade Rotation
    private static double curMove() {return JS_IO.axLeftY.get();}       //Curvature move, pwr applied
    private static double curRot() {return JS_IO.axRightX.get();}       //Curvature direction, left.right

    private static boolean tglFrontBtn() {return JS_IO.btnInvOrientation.onButtonPressed();}//Toggle orientation
    private static boolean tglScaleBtn() {return JS_IO.btnScaledDrive.onButtonPressed();}   //Toggle appling scaling
    private static boolean holdZeroBtn() {return JS_IO.btnHoldZero.isDown();}               //Hold zero hdg when held down
    private static boolean hold180Btn() {return JS_IO.btnHold180.isDown();}                 //Hold 180 hdg when held down

    //Defined (moved) to Drive.  Maybe common to telop & auto.
    // private static boolean frontSwapped;    // front of robot is swapped
    // private static boolean scaledOutput;    // scale the output signal
    // private static double scale = 0.5;      //Scale to apply to output is active
    // private static double scale() { return !scaledOutput ?  1.0 : scale; }

    // private static Steer steer = new Steer();       //Create steer instance for hdg & dist, use default parms
    // private static double strCmd[] = new double[2]; //Storage for steer return
    // private static double hdgFB() {return IO.navX.getAngle();}  //Only need hdg to Hold Angle 0 or 180

    private static int state = 1;   //Can be set by btn or sdb chooser
    private static String[] teleDrvType = {"Off", "Tank", "Arcade", "Curvature"};       //All drive type choices

    //Teleop Drive Chooser sdb chooser.  Note can also choose state by btn
    private static SendableChooser<Integer> teleDrvChsr = new SendableChooser<>();   //sdb Chooser
    private static int teleDrvChoice = state;   //Save teleDrvChooser for comparison cov then update state

    /**Initial items for teleop driving chooser.  Called from robotInit in Robot. */
    public static void chsrInit() {
        teleDrvChsr = new SendableChooser<Integer>();
        for(int i=0; i < teleDrvType.length; i++){
            teleDrvChsr.addOption(teleDrvType[i], i);
        }
        teleDrvChsr.setDefaultOption(teleDrvType[1] + " (Dflt)", 1);

        SmartDashboard.putData("Drv/Tele/Choice", teleDrvChsr);   //Put Chsr on sdb
        SmartDashboard.putString("Drv/Tele/Choosen", teleDrvType[teleDrvChsr.getSelected()]);   //Put selected on sdb
    }
 
    /**Initial items to teleop driving */
    public static void init() {
        Drive.init();

        sdbInit();
        teleDrvChoice = teleDrvChsr.getSelected();
        cmdUpdate(0, 0);
        IO.navX.reset();

        setSwapFront(false);
        setScaled(false);
        relAngleHold();
    }

    /**
     * Determine any state that needs to interupt the present state, usually by way of a JS button but
     * can be caused by other events.
     * <p>Added sdb chooser to select.  Can chg from btn or chooser.
     */
    public static void determ() {
        if(JS_IO.btnSelDrv.onButtonPressed()){          //On btn prsd Incr thru drive types with btn press
            state = ((++state) % teleDrvType.length);
        }

        if(teleDrvChoice != teleDrvChsr.getSelected()){  //If sdb chgs switch states to sdb choice
            state = teleDrvChsr.getSelected();
            teleDrvChoice = state;
        }

        if(tglFrontBtn()) setSwapFront(!isSwappedFront());    //Switch the direction of the front
        if(tglScaleBtn()) setScaled(!isScaled());
        if(holdZeroBtn()){
            setAngleHold(0.0);
        }else if(hold180Btn()){
            setAngleHold(180.0);
        }else{
            relAngleHold();
        }
    }

    /**
     * Called from Robot telopPerodic every 20mS to Update the drive sub system.
     */
    public static void update() {
        Drive.update();
        determ();
        sdbUpdate();
        switch (state) {
            case 0: // Stop Moving
            cmdUpdate(); // Stop moving
            break;
            case 1: // Tank mode.
            cmdUpdate(tnkLeft(), tnkRight(), false, 1); // Apply Hold, swap & scaling then send
            break;
            case 2: // Arcade mode.
            cmdUpdate(arcMove(), arcRot(), false, 2); // Apply Hold, swap & scaling then send
            break;
            case 3: // Curvature mode.
            cmdUpdate(curMove(), curRot(), false, 3); // Apply Hold, swap & scaling then send
            break;
            default:
            cmdUpdate();
            System.out.println("Invaid Drive State - " + state);
            break;
        }
    }

    /**Initialize sdb  */
    private static void sdbInit(){
        PIDXController.initSDBPid(pidHdgHold, "Tele/pidHdgHold");
        SmartDashboard.putNumber("Drv/Tele/Drive Scale", getScaledOut());                //push to NetworkTable, sdb
    }

    /**Update sdb stuff.  Called every 20mS from update. */
    public static void sdbUpdate() {
        PIDXController.updSDBPid(pidHdgHold, "Tele/pidHdgHold");

        SmartDashboard.putNumber("Drv/Tele/state", state);
        SmartDashboard.putString("Drv/Tele/Choosen", teleDrvType[state]);

        setScaledOut(SmartDashboard.getNumber("Drv/Tele/Drive Scale", getScaledOut()));
        SmartDashboard.putBoolean("Drv/Tele/scaled", isScaled());
        SmartDashboard.putBoolean("Drv/Tele/Front Swap", isSwappedFront());
        SmartDashboard.putBoolean("Drv/Tele/Hold 0", holdZeroBtn());
        SmartDashboard.putBoolean("Drv/Tele/Hold 180", hold180Btn());
        SmartDashboard.putNumber("Drv/Tele/HdgFB", hdgFB());
        SmartDashboard.putNumber("Drv/Tele/HdgHoldSP", pidHdgHold.getSetpoint());
        SmartDashboard.putNumber("Drv/Tele/HdgHoldOut", pidHdgHold.getAdj());
        SmartDashboard.putNumber("Drv/Tele/tankL", tnkLeft());
        SmartDashboard.putNumber("Drv/Tele/tankR", tnkRight());
    }

    /**
     * @return Active state of state machine.
     * <p> 0-Off, 1-Tank, 2-Arcade, 3-Curvature
     */
    public static int getState() {
        return state;
    }

    // /**
    //  * Condition JS input for Hold angle, front swap and/or scaling.
    //  * 
    //  * @param lspdOrMov - left tank or move arcade or curvature
    //  * @param rSpdOrRot - right tank or rotation arcade or curvature
    //  * @param diffType - 0=Off, 1=tank, 2=arcade, 3=curvature
    //  */
    // private static void cmdUpdate(double lspdOrMov, double rSpdOrRot, int diffType) {
    //     if(holdZeroBtn() || hold180Btn()){                  //If call for hold angle
    //         pidHdg.setSetpoint(holdZeroBtn() ? 0.0 : 180.0);    //Set hdgSP
    //         strCmd[0] = pidHdg.calculateX(hdgFB());             //Calc rotation
    //         rSpdOrRot = swapFront ? -strCmd[0] : strCmd[0];  //store in rot, neg if front swap
    //         if(diffType == 1) diffType = 2;                 //If type tank Chg to arcade
    //     }

    //     if(swapFront){
    //         lspdOrMov *= -1.0;  rSpdOrRot *= -1.0;  //Negate values
    //         if(diffType == 1){                      //If tank swap left and right also
    //             strCmd[0] = lspdOrMov;              //use strCmd as tmp storage
    //             lspdOrMov = rSpdOrRot;
    //             rSpdOrRot = strCmd[0];
    //         }
    //     }

    //     // lspdOrMov *= scale();  rSpdOrRot *= scale();        //scale it all (moved to diffDrv)
    //     cmdUpdate(lspdOrMov, rSpdOrRot, true, diffType);   //and send
    // }

}
