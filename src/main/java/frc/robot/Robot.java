// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;



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

  Solenoid shiftSolenoidR = new Solenoid(PneumaticsModuleType.CTREPCM,  0);
  Solenoid shiftSolenoidL = new Solenoid(PneumaticsModuleType.CTREPCM, 1);


  Solenoid clawSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, 2);
  Solenoid armSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, 3);

  Timer timer = new Timer();
 
  
  DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);
  private final Joystick m_stick = new Joystick(0);
  public double driveScale = 0.75;
  public static double armScale = 0.2;
  MotorController armMotor = new VictorSP(4);

  public Encoder encoderR = new Encoder(0,1,false,Encoder.EncodingType.k2X);
  public Encoder encoderL = new Encoder(2,3, true, Encoder.EncodingType.k2X);
  
  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_right.setInverted(true);
  }
  @Override
    public void autonomousInit() {
     timer.start();
     encoderL.setDistancePerPulse(1.0/40.0);
     encoderR.setDistancePerPulse(1.0/40.0);
     encoderR.reset();
     encoderL.reset();

    }

  
  /**
   * This function is called periodically during autonomous
   */
  @Override
  public void autonomousPeriodic() {
    if(timer.get() >= 5.0) {
    encoderL.reset();
    encoderR.reset();
    System.out.println(encoderL.getDistance()+" L");
    System.out.println(encoderR.getDistance()+" R");
    if (encoderL.getDistance()<=60 || encoderR.getDistance()<=60) {
      m_drive.arcadeDrive(0., 0);
      
    } else {
      m_drive.arcadeDrive(0, 0);
    }
   
    }

  
    
    //System.out.println(encoderR.getDistance());
    //m_drive.arcadeDrive(0.,) 0);

    
    }

  @Override
  public void teleopPeriodic() {
    // Drive with arcade drive.
    // That means that the Y axis drives forward
    // and backward, and the X turns left and right.
    if(m_stick.getRawButtonPressed(GamePad.Button.BACK)){
      driveScale = driveScale * -1;
    }
    if(m_stick.getRawButtonPressed(GamePad.Button.B)){
      clawSolenoid.toggle();
    }
    if(m_stick.getRawButtonPressed(GamePad.Button.A)){
      armSolenoid.toggle();
    }
    if(m_stick.getRawButtonPressed(GamePad.Button.RIGHT_PRESS)){
      armMotor.set(armScale);
    }
    if(m_stick.getRawButtonPressed(GamePad.Button.LEFT_PRESS)){
      armMotor.set(armScale*-1.0);
    }
    if(m_stick.getRawButtonPressed(GamePad.Button.LEFT_PRESS)&&m_stick.getRawButtonPressed(GamePad.Button.RIGHT_PRESS)){
      armMotor.set(0.0);
    }
    if(!m_stick.getRawButtonPressed(GamePad.Button.LEFT_PRESS)&&!m_stick.getRawButtonPressed(GamePad.Button.RIGHT_PRESS)){
      armMotor.set(0.0);
    }
    double stickYval = -m_stick.getY() * driveScale;
    double stickXval = m_stick.getX() * driveScale;
    m_drive.arcadeDrive(stickYval, stickXval);
    //System.out.format("This is get Y: %d This is get X:%d", -m_stick.getY(), m_stick.getX());
 if(m_stick.getRawButtonPressed(GamePad.Button.X)) {
  shiftSolenoidL.toggle();
  shiftSolenoidR.toggle(); 
    }
    //System.out.println(stickYval);
    System.out.println(encoderL.getDistance()+" L");
    System.out.println(encoderR.getDistance()+" R");
  }
}
