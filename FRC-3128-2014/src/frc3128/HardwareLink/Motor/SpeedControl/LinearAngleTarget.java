package frc3128.HardwareLink.Motor.SpeedControl;

import frc3128.HardwareLink.Motor.MotorControl;
import frc3128.Util.DebugLog;
import frc3128.Util.RobotMath;

/**
 *
 * @author Yousuf Soliman
 */

public class LinearAngleTarget extends MotorControl {
    private double minSpeed;
    private double targetAngle, threshold, kP;

    public LinearAngleTarget(double minSpeed, double threshold, double kP) {
        if (!RobotMath.isValidPower(minSpeed)) {
            throw new IllegalArgumentException("The minimum power is incorrect!");
        }
        this.minSpeed = Math.abs(minSpeed);
        this.threshold = threshold;
        this.kP = kP;
    }

    public void setControlTarget(double val) {
        this.targetAngle = val;
    }

    public double speedControlStep(double dt) {
        double error = RobotMath.angleDistance(this.getLinkedEncoderAngle(), this.targetAngle);
        double sgn = RobotMath.sgn(error); 
        double pGain = sgn*(Math.abs(error))*((1-this.minSpeed)/180.0)+this.minSpeed;
        pGain = (Math.abs(pGain) > this.minSpeed ? pGain : RobotMath.getMotorDirToTarget(this.getLinkedEncoderAngle(), this.targetAngle)*this.minSpeed);
        DebugLog.log(DebugLog.LVL_DEBUG, this, "Error: " + error);
        DebugLog.log(DebugLog.LVL_DEBUG, this, "Current Angle: " + this.getLinkedEncoderAngle());
        if(Math.abs(error) < threshold) { DebugLog.log(DebugLog.LVL_DEBUG, this, "SETTING ZERO"); return 0;}
        return pGain;
    }

    public void clearControlRun() {
    }

    public boolean isComplete() {
        double x =  Math.abs(RobotMath.angleDistance(this.getLinkedEncoderAngle(), this.targetAngle));
        return x < threshold;
    }
}
