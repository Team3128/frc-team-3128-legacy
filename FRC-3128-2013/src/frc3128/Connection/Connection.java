package frc3128.Connection;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import frc3128.DebugLog;
import frc3128.EventManager.Event;

public class Connection extends Event {
    NetworkTable con;
    
    public Connection() {
        con = NetworkTable.getTable("camera");
    }
    
    public void execute() {
        con.beginTransaction();
        DebugLog.log(2, referenceName, "Data: " + con.getBoolean("verifyPacket", false));
        con.endTransaction();
    }
}