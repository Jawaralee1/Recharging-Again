/*
Original Author: Jim & Anthony

History:
A - 1/20/20 - Original Release

Desc: Handles the shoooter subsystem
    0- everything off by pct
    1- shooter on by vel (rpm) up to speed, hood up
    default- everything off bby  pct.
    
    Buttons:

*/
package frc.robot.Subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.Encoder;
import frc.io.hdw_io.IO;
import frc.io.hdw_io.ISolenoid;
import frc.io.joysticks.JS_IO;

public class Shooter {
    private static WPI_TalonSRX shooter = IO.shooterTSRX;
    private static Encoder encSh = IO.shooter_Encoder;
    private static ISolenoid ballHood = IO.shooterHoodUp;

    private static int state;               //Shooter state machine.  0=Off by pct, 1=On by velocity, RPM
    private static Integer rpmWSP = 3000;   //Working RPM setpoint
    private static int rpmSPAdj = 3800;     //Adjustable RPM setpoint when choosen
    private static int atSpeedDeadband = 200;   // tbd, in rpm  ???
    private static double rpmToTpc = .07833333; // TBD rpm to ticks per cycle (100ms)  // 47 ticks per 1 rotation
    private static boolean shooterToggle = true;//?????

    private static double kF = 2.5;     //TalonSRX feedforward
    private static double kP = 100;     //TalonSRX Proportional band
    private static double kI = 0;       //TalonSRX Intgral term
    private static double kD = 0;       //TalonSRX Differential term

    //RPM Chooser.  Allows driver to select pre-select RPMs.  [0]is default [last] is adjustable
    private static SendableChooser<Integer> rpmChsr = new SendableChooser<Integer>();
    private static String[] rpmName  = {"RPM1", "RPM2", "RPM3", "RPM4", "RPM_Adj"}; //Names assigned (+ "-value")
    private static Integer[] rpmSP = {5500, 5000, 4500, 4000, -1};                  //Values to use (return)

    /**Initialize Shooter stuff.  Called from telopInit (maybe robotInit(?)) in Robot.java */
    public static void init() {
        sdbInit();

        ballHood.set(false);

        shooter.config_kF(0, kF);   //Send configuration parms to TalonSRX
        shooter.config_kP(0, kP);
        shooter.config_kI(0, kI);
        shooter.config_kD(0, kD);

        shooter.enableVoltageCompensation(true);
        shooter.configVoltageCompSaturation(12, 0);
        shooter.configVoltageMeasurementFilter(32, 0);
        encSh.reset();

        cmdUpdate(0.0, false);      //Turn motor off with pct
        state = 0;                  //Start at state 0
        rpmToTpc = .07833333;       //MAke sure this hasn't chgd????
        shooterToggle = true;       //????
    }

    /**
     * Determine any state that needs to interupt the present state, usually by way
     * of a JS button but can be caused by other events.
     */
    private static void determ() {
        // if (JS_IO.btnRampShooter.onButtonPressed()) {
        //     state = shooterToggle ? 1 : 0;
        //     shooterToggle = !shooterToggle;
        // }

        // 2nd option
        if (JS_IO.btnRampShooter.onButtonPressed()) {
            state = state != 1 ? 1 : 0;
        }

        if (JS_IO.allStop.onButtonPressed())
            state = 99;
    }

    /**Update Shooter.  Called from teleopPeriodic in robot.java */
    public static void update() {
        sdbUpdate();
        determ();

        switch (state) {
            case 0: // off - percentoutput (so that no negative power is sent to the motor)
                cmdUpdate(0, false);
                break;
            case 1: // on
                cmdUpdate(rpmWSP, true);

                break;
            default: // all off
                cmdUpdate(0, false);
                break;

        }
    }

    /**
     * Issue spd setting as rpmSP if isVelCmd true else as percent cmd.
     * @param spd - cmd to issue to Flywheel Talon motor controller as rpm or percentage
     * @param isVelCmd - spd should be issued as rpm setpoint else as a percenetage output.
     */
    public static void cmdUpdate(double spd, boolean isVelCmd) { // control through velocity or percent
        shooter.set(ControlMode.Disabled, 0);
        if (isVelCmd) { // Math.abs(spd) * rpmToTpc
            shooter.set(ControlMode.Velocity, Math.abs(spd) * rpmToTpc);    // control as velocity (RPM)
        } else {
            shooter.set(ControlMode.PercentOutput, Math.abs(spd));          // control as percentage output
        }

        if (shooter.getSelectedSensorVelocity() * 600 / 47 > 2000) { // if not running, keep hood down
            ballHood.set(true);
        } else {
            ballHood.set(false);
        }

        SmartDashboard.putNumber("Shooter/cmdUpd/spd", spd);
        SmartDashboard.putNumber("Shooter/cmdUpd/vel input", rpmToTpc);
    }

    /*-------------------------  SDB Stuff --------------------------------------
    /**Initialize sdb & chooser */
    public static void sdbInit() {
        SmartDashboard.putNumber("Shooter/RPM/kP", kP);     //Put kP on sdb
        SmartDashboard.putNumber("Shooter/RPM/kF", kF);     //Put kF on sdb

        //This initiates the RPm Chooser
        rpmChsr = new SendableChooser<Integer>();
        rpmChsr.setDefaultOption(rpmName[0] + "-" + rpmSP[0], rpmSP[0]);
        for(int i=1; i < rpmSP.length; i++){
            rpmChsr.addOption(rpmName[i] + "-" + rpmSP[i], rpmSP[i]);
        }
        SmartDashboard.putData("Shooter/RPM/Selection", rpmChsr);   //Put rpmChsr on sdb
        SmartDashboard.putNumber("Shooter/RPM/Adj SP", rpmSPAdj);   //Put rpmSPAdj on sdb
    }
 
    public static void sdbUpdate() {
        kF = SmartDashboard.getNumber("Shooter/RPM/kF", kF);    //Get kP from sdb
        kP = SmartDashboard.getNumber("Shooter/RPM/kP", kP);    //Get kF from sdb
        shooter.config_kF(0, kF);                               //Send kP new value to Talon
        shooter.config_kP(0, kP);                               //Send kF new value to Talon

        rpmSPAdj = (int) SmartDashboard.getNumber("Shooter/RPM/Adj SP", rpmSPAdj);  //Get the adjustable RPM SP from sdb
        rpmWSP = rpmChsr.getSelected();                         //Get selected RPM SP value from rpmChsr
        if(rpmWSP == null || rpmWSP < 0) rpmWSP = rpmSPAdj;     //If value is -1 (last choice) use adjustable SP
        SmartDashboard.putNumber("Shooter/RPM/Wkg SP", rpmWSP); //Put the working RPM SP,rpmWSP

        //Put general Shooter info on sdb
        SmartDashboard.putNumber("Shooter/State", state);
        SmartDashboard.putBoolean("Shooter/On", ((state == 1) ? true : false));
        SmartDashboard.putBoolean("Shooter/isAtSpeed", isAtSpeed());
        SmartDashboard.putBoolean("Shooter/shooterToggle", shooterToggle);

        //Put Flywheel info on sdb
        SmartDashboard.putNumber("Shooter/FlyWheel/Encoder", encSh.ticks());
        SmartDashboard.putNumber("Shooter/FlyWheel/Velocity", shooter.getSelectedSensorVelocity());
        SmartDashboard.putNumber("Shooter/Flywheel/RPM", shooter.getSelectedSensorVelocity() * 600 / 47);
        SmartDashboard.putNumber("Shooter/Flywheel/SRX curr", shooter.getStatorCurrent());
        SmartDashboard.putNumber("Shooter/Flywheel/pdp curr", IO.pdp.getCurrent(13));
    }

    //------------------------------ Shooter statuses and misc. -------------------------
    /**
     * Probably shouldn't use this bc the states can change.  Use statuses.
     * @return - present state of Shooter state machine.
     */
    public static int getState() {
        return state;
    }

    /**
     * 
     * @return - Is within 400 rpm of setpoint, rpmWSP
     */
    public static boolean closeToSpeed() {
        if (shooter.getSelectedSensorVelocity() * 600 / 47 >= (rpmWSP - 400)) {
            return true;
        }
        return false;
    }

    /**
     * 
     * @return - RPM FB is GTE setpoint & LTE SP + deadband.   ---???
     */
    public static boolean isAtSpeed() { // if it's within it's setpoint deadband
        if (shooter.getSelectedSensorVelocity() * 600 / 47 >= (rpmWSP) &&
            shooter.getSelectedSensorVelocity() * 600 / 47 <= (rpmWSP + atSpeedDeadband)) {
            return true;
        }
        return false;
    }

}
