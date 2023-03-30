// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
// if reading this in the future dont use any of this unless you want a failure of a robot 
// anyways this was written in march of 2023 so hello future

package frc.robot;


import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
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
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



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


  Solenoid shiftSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, 0);


  Solenoid clawSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, 2);
  Solenoid armSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, 3);

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto1 = "Auto1";
  private static final String kCustomAuto2 = "Auto2";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  Timer timer = new Timer();
 
   DigitalInput gearSwitch = new DigitalInput(5);


  DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);
  private final Joystick m_stick = new Joystick(0);
  public double driveScale = 0.75;
  public static double armScale = 0.01;
  static double ENCODER_SCALE_FACTOR = 1.0/40.0;
  double distanceInAuto = 20;

  MotorController armMotor = new VictorSP(4);

  public Encoder encoderR = new Encoder(0,1,false,Encoder.EncodingType.k2X);
  public Encoder encoderL = new Encoder(2,3, true, Encoder.EncodingType.k2X);


  
  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_right.setInverted(true);
    CameraServer.startAutomaticCapture();
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("Auto1", kCustomAuto1);
    m_chooser.addOption("Auto2", kCustomAuto2);
    SmartDashboard.putData("Auto choices", m_chooser);
  }
  
  @Override
    public void autonomousInit() {
      m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);
     timer.start();
     encoderL.setDistancePerPulse(ENCODER_SCALE_FACTOR);
     encoderR.setDistancePerPulse(ENCODER_SCALE_FACTOR);
     encoderR.setReverseDirection(true);
     encoderR.reset();
     encoderL.reset();

    }

  
  /**
   * This function is called periodically during autonomous
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto2:
        // Put custom auto (number 2) code here
        distanceInAuto = 10;
        
        break;
      
      case kCustomAuto1:
        // Put custom auto code here
        distanceInAuto = 5;
        
        break;
      
      
      case kDefaultAuto:
      default:
      distanceInAuto = 20;

      break;
      
    }
    if(timer.get() >= 2.0) {
      System.out.println(encoderL.getDistance()+" L");
      System.out.println(encoderR.getDistance()+" R");
      if (encoderL.getDistance()<=distanceInAuto || encoderR.getDistance()<=distanceInAuto) {
        m_drive.arcadeDrive(0.5, 0);
        
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

    if (m_stick.getRawButtonPressed(GamePad.Button.LB)){
      armScale-= 0.01;
    }
    if (m_stick.getRawButtonPressed(GamePad.Button.RB)){
      armScale+= 0.01;
    }
    double desiredArmValue = 0.0;
    if(m_stick.getRawButtonPressed(GamePad.Button.RIGHT_PRESS)){
      desiredArmValue = armScale;
    }
    if(m_stick.getRawButtonPressed(GamePad.Button.LEFT_PRESS)){
      desiredArmValue = armScale*-1;
    }
    if(m_stick.getRawButtonPressed(GamePad.Button.LEFT_PRESS)&&m_stick.getRawButtonPressed(GamePad.Button.RIGHT_PRESS)){
      desiredArmValue = 0.0;
    }
    if(gearSwitch.get()){
      desiredArmValue = 0.0;
      /* Update this when we understand what direction the values indicate */
    }

    
  armMotor.set(desiredArmValue);
    double stickYval = -m_stick.getY() * driveScale;
    double stickXval = m_stick.getX() * driveScale;
    m_drive.arcadeDrive(stickYval, stickXval);
    //System.out.format("This is get Y: %d This is get X:%d", -m_stick.getY(), m_stick.getX());
 if(m_stick.getRawButtonPressed(GamePad.Button.X)) {
  shiftSolenoid.toggle();
    }

    //System.out.println(stickYval);
    System.out.println(encoderL.getDistance()+" L");
    System.out.println(encoderR.getDistance()+" R");
  }
}
