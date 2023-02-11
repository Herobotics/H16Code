// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.nio.channels.Channel;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This is a demo program showing the use of the DifferentialDrive class. Runs the motors with
 * arcade steering.
 */
public class Robot extends TimedRobot {
  MotorController m_frontLeft = new VictorSP(0);
  MotorController m_rearLeft = new VictorSP(1);
  MotorControllerGroup m_left = new MotorControllerGroup(m_frontLeft, m_rearLeft);

  MotorController m_frontRight = new VictorSP(2);
  MotorController m_rearRight = new VictorSP(3);
  MotorControllerGroup m_right = new MotorControllerGroup(m_frontRight, m_rearRight);

  Solenoid shiftSolenoidR = new Solenoid(PneumaticsModuleType.CTREPCM, 0);
  Solenoid shiftSolenoidL = new Solenoid(PneumaticsModuleType.CTREPCM, 1);

  DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);
  private final Joystick m_stick = new Joystick(0);
 
 
  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_right.setInverted(true);
  }

  @Override
  public void teleopPeriodic() {
    // Drive with arcade drive.
    // That means that the Y axis drives forward
    // and backward, and the X turns left and right.
    double stickYval = -m_stick.getY();
    double stickXval = m_stick.getX();
    if(m_stick.getRawButtonPressed(GamePad.Button.B)){
      stickYval = stickYval * -1;
      stickXval = stickXval * -1; 
    }
    m_drive.arcadeDrive(stickYval, stickXval);
    //System.out.format("This is get Y: %d This is get X:%d", -m_stick.getY(), m_stick.getX());
 if(m_stick.getRawButtonPressed(GamePad.Button.A)) {
  shiftSolenoidL.toggle();
  shiftSolenoidR.toggle(); 
}
}
}
