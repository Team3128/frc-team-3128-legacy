package edu.wpi.first.wpilibj.templates.Controller;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.templates.DebugLog;
import edu.wpi.first.wpilibj.templates.EventManager.Event;
import edu.wpi.first.wpilibj.templates.ListenerManager.ListenerManager;

public class XControl extends Event {
    private Joystick xControl;
    private boolean controllerBound = false;
    public double x1, y1, x2, y2, triggers;
    private boolean[] buttonsPressed = {false, false, false, false, false, false, false, false, false, false};
    
    public XControl(int port) {
        super();
        xControl = new Joystick(port);
        this.registerIterable();
        DebugLog.log(4, referenceName, "Controller added self to event manager!");
    }
    
    public void execute() {
        if(!controllerBound) {
            DebugLog.log(2, this.toString(), "Controller checks called before controller bind! Ignoring controller checks.");
            return;
        }

        try {
            boolean updateJoy1 = false;
            boolean updateJoy2 = false;
            boolean updateTriggers = false;

            if(x1 != xControl.getRawAxis(XControlMap.x1Axis))       updateJoy1 = true;
            if(x2 != xControl.getRawAxis(XControlMap.x2Axis))       updateJoy2 = true;
            if(y1 != xControl.getRawAxis(XControlMap.y1Axis))       updateJoy1 = true;
            if(y2 != xControl.getRawAxis(XControlMap.y2Axis))       updateJoy2 = true;
            if(triggers != xControl.getRawAxis(XControlMap.trAxis)) updateTriggers = true;

            x1 = xControl.getRawAxis(XControlMap.x1Axis);
            x2 = xControl.getRawAxis(XControlMap.x2Axis);
            y1 = xControl.getRawAxis(XControlMap.y1Axis);
            y2 = xControl.getRawAxis(XControlMap.y2Axis);

            if(updateJoy1) ListenerManager.callListener("updateJoy1");
            if(updateJoy2) ListenerManager.callListener("updateJoy2");
            if(updateTriggers) ListenerManager.callListener("updateTriggers");
            if(updateJoy1 || updateJoy2 || updateTriggers) ListenerManager.callListener("updateDrive");

            for(int i = 1; i <= 10; i++) {
                if(buttonsPressed[i] != xControl.getRawButton(i))
                    ListenerManager.callListener("button" + XControlMap.getBtnString(i) + (xControl.getRawButton(i) ? "Down" : "Up"));
                buttonsPressed[i] = xControl.getRawButton(i);
            }
        } catch(NullPointerException e) {
            DebugLog.log(1, this.toString(), "Event was executed before controller was bound! Cancelling this event!");
            this.cancelEvent();
        }
    }

    //public void bindToController(int port) {
    //    xControl = new Joystick(port);
    //}
}
