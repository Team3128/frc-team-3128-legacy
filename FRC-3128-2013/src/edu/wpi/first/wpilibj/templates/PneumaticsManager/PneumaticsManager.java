package edu.wpi.first.wpilibj.templates.PneumaticsManager;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.templates.DebugLog;
import edu.wpi.first.wpilibj.templates.EventManager.Event;
import edu.wpi.first.wpilibj.templates.ListenerManager.ListenerManager;
import java.util.Vector;

class LockAllPistons extends Event {
    public void execute() {
        for(int i = 0; i < PneumaticsManager.dualSolenoidList.size(); i++)
            ((DualSolenoid) PneumaticsManager.dualSolenoidList.elementAt(i)).unlockPiston();
    }
}

public class PneumaticsManager {
    private static Compressor c;
    private static boolean compressorSet = false;
    protected static Vector dualSolenoidList = new Vector();
    private final static String referenceName = "PneumaticsManager";

    public PneumaticsManager(Compressor c) {
        PneumaticsManager.c = c;
        PneumaticsManager.compressorSet = true;
        ListenerManager.addListener(new LockAllPistons(), "lockPistons");
    }

    public PneumaticsManager(int a, int b, int c, int d) {
        PneumaticsManager.c = new Compressor(a, b, c, d);
        PneumaticsManager.compressorSet = true;
        ListenerManager.addListener(new LockAllPistons(), "lockPistons");
    }

    public static PistonID addPiston(Solenoid solA, Solenoid solB) {
        dualSolenoidList.addElement(new DualSolenoid(solA, solB));
        return new PistonID(dualSolenoidList.size() - 1);
    }

    public static PistonID addPiston(int a, int b, int c, int d) {
        dualSolenoidList.addElement(new DualSolenoid(new Solenoid(a, b), new Solenoid(c, d)));
        return new PistonID(dualSolenoidList.size() - 1);
    }

    public static void setPistonStateLocked(PistonID id) {
        ((DualSolenoid) dualSolenoidList.elementAt(id.getID())).lockPiston();
    }

    public static void setPistonStateUnlocked(PistonID id) {
        ((DualSolenoid) dualSolenoidList.elementAt(id.getID())).unlockPiston();
    }

    public static void setPistonStateOn(PistonID id) {
        ((DualSolenoid) dualSolenoidList.elementAt(id.getID())).setPistonOn();
    }

    public static void setPistonStateOff(PistonID id) {
        ((DualSolenoid) dualSolenoidList.elementAt(id.getID())).setPistonOff();
    }

    public static void setCompressorStateOn() {
        if(PneumaticsManager.compressorSet)
            PneumaticsManager.c.start();
        if(!PneumaticsManager.compressorSet)
            DebugLog.log(2, referenceName, "Compressor is being started without first being instantiated!");
    }

    public static void setCompressorStateOff() {
        if(PneumaticsManager.compressorSet)
            PneumaticsManager.c.start();
        if(!PneumaticsManager.compressorSet)
            DebugLog.log(2, referenceName, "Compressor is being stopped without first being instantiated!");
    }

    public static void lockAllPistons() {
        for(int i = 0; i < dualSolenoidList.size(); i++)
            ((DualSolenoid) dualSolenoidList.elementAt(i)).lockPiston();
    }
}
