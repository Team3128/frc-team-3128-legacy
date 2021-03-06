package org.team3128.hardware.misc;

import robotemulator.Gyro;

/**
*
* @author Noah Sutton-Smolin
*/
public class GyroLink {
   private final Gyro gyr;
  
   /**
    * Creates a new linked gyroscope.
    *
    * @param gyr the gyroscope to be created
    */
   public GyroLink(Gyro gyr) {this.gyr = gyr;}

   /**
    *
    * @return the current gyroscope angle
    */
   public double getAngle()
   {
	   return gyr.getAngle();
   }
   
   //I don't know what thisdoes, and it's not available in the emulator
//   public double getRate()
//   {
//	   return gyr.getRate();
//   }
  
   /**
    * Resets the stored angle
    */
   public void resetAngle()
   {
	   gyr.reset();
   }
}
