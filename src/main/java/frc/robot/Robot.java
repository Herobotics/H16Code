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
import edu.wpi.first.wpilibj.XboxController;
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


  Solenoid shiftSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, 1);
  Solenoid clawSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, 2);
  Solenoid armSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, 0);

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto1 = "Auto1";
  private static final String kCustomAuto2 = "Auto2";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  Timer timer = new Timer();
 
  DigitalInput storedLimitSwitch = new DigitalInput(5);
  DigitalInput extendedLimitSwitch = new DigitalInput(4);

  DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);
  private final XboxController m_stick = new XboxController(0);
  public double driveScale = 0.75;
  public static double ARM_UP_SCALE_FACTOR = 0.40;
  public static double ARM_DOWN_SCALE_FACTOR = 0.10;
  public static double DRIVE_FORWARDS_SPEED = 0.5;
  public static double DRIVE_ROTATION_SPEED = 0.2;
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
        distanceInAuto = 40;
        
        break;
      
      case kCustomAuto1:
        // Put custom auto code here
        distanceInAuto = 60;
        break;
      
      
      case kDefaultAuto:
      default:
      distanceInAuto = 40;

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
    if(m_stick.getRawButtonPressed(GamePad.Button.X)){
      driveScale = driveScale * -1;
    }
    if(m_stick.getRawButtonPressed(GamePad.Button.B)){
      clawSolenoid.toggle();
    }
    if(m_stick.getRawButtonPressed(GamePad.Button.A)){
      armSolenoid.toggle();
    }

    // if (m_stick.getRawButtonPressed(GamePad.Button.START) || m_stick2.getRawButtonPressed(GamePad.Button.START)){
    //   armScale -= 0.02;
    // }
    // if (m_stick.getRawButtonPressed(GamePad.Button.BACK) || m_stick2.getRawButtonPressed(GamePad.Button.BACK)){
    //   armScale += 0.02;
    // }
    // System.out.println(armScale);
    double desiredArmValue = 0.0;
    if(m_stick.getRawButton(GamePad.Button.RB)){
      // GOES TO EXTENDED POSITION
      desiredArmValue = ARM_UP_SCALE_FACTOR; // POSTIVE 
    }
    else if(m_stick.getRawButton(GamePad.Button.LB)){
      // GOES TO STORED POSITION
      desiredArmValue = ARM_DOWN_SCALE_FACTOR*-1;
    }
    else {
      desiredArmValue = 0.0;
    }
    if(!storedLimitSwitch.get()){
      desiredArmValue = Math.max(0.0, desiredArmValue);
      System.out.println("Stored Limit switch pressed!");
      /* Update this when we understand what direction the values indicate */
    }
    if(!extendedLimitSwitch.get()){
      desiredArmValue = Math.max(0.0, desiredArmValue);
      System.out.println("Extended Limit switch pressed!");
      /* Update this when we understand what direction the values indicate */
    }
    System.out.println(desiredArmValue);
    armMotor.set(desiredArmValue);

    double stickYval = m_stick.getLeftY() * driveScale * DRIVE_FORWARDS_SPEED;
    double stickXval = -m_stick.getRightY() * driveScale * DRIVE_ROTATION_SPEED;
    m_drive.arcadeDrive(stickYval, stickXval);
    //System.out.format("This is get Y: %d This is get X:%d", -m_stick.getY(), m_stick.getX());
 if(m_stick.getRawButtonPressed(GamePad.Button.Y)) {
  shiftSolenoid.toggle();
    }

    //System.out.println(stickYval);
    System.out.println(encoderL.getDistance()+" L");
    System.out.println(encoderR.getDistance()+" R");
  }
}
