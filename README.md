# Spacecraft & Mission Control Simulation System

A Java-based socket programming and object-oriented simulation system designed to control and monitor spacecraft 
(Rockets and Satellites) in real time from a centralized Mission Control server.


## Key Features

- **Client-Server Architecture:** Built on Java Sockets ('ServerSocket' & 'Socket') for real-time 
bidirectional communication between Mission Control and the Spacecraft Client.
- **Authentication System:** Secure login for Mission Control operators ('admin/1234').
- **Live Telemetry Monitoring:** Multithreaded telemetry streaming (Fuel, Altitude, Speed, and Status) with 
start/stop lifecycle control to optimize resource usage.
- **Session Persistence:** Saves and restores mission states (spacecraft status, fuel levels, altitude) across 
application restarts using 'mission_state.properties'.
- **Command Dispatch:** Supports remote execution of commands such as 'LAUNCH', 'BOOST', 'DEPLOY', 'SET_FUEL', and custom alerts.
- **Mission Event Logging:** Appends system events with timestamps into a local 'mission_log.txt' file.


##  OOP Design & Architecture

This project strictly adheres to **Object-Oriented Programming (OOP)** principles:

- **Abstraction & Polymorphism:** 'Spacecraft' serves as an abstract base class implemented by 
'Rocket' and 'Satellite'. 'Mission' is extended by 'SpaceMission'.
- **Interfaces:** 'CommandCenter' enforces unified execution contracts for both Client and Server.
- **Custom Exception Handling:** Utilizes domain-specific exceptions ('ConnectionException', 'InvalidCommandException').


## Repository Structure
.
├── Client.java                  # Socket communication logic for the client side
├── ClientMain.java              # Client entry point and local telemetry loop
├── Server.java                  # Socket server handling client connection & telemetry
├── ServerMain.java              # Mission Control CLI menu & authentication entry point
├── ClientHandler.java            # Background thread handler for live telemetry streaming
├── MissionControl.java          # Core logic connecting server, spacecraft, and loggers
├── CommandCenter.java           # Interface for socket command and telemetry interactions
├── Spacecraft.java               # Abstract base class for spacecrafts
├── Rocket.java                  # Subclass representing Rocket mechanics
├── Satellite.java               # Subclass representing Satellite mechanics
├── Mission.java                 # Abstract base class for missions
├── SpaceMission.java            # Concrete implementation of space missions
├── Telemetry.java               # Data model for fuel, altitude, speed, and status
├── MissionLog.java               # File writer utility for system events
├── User.java                    # User authentication helper
└── ConnectionException.java    # Custom exception classes

## Run the Project
1. First run 'ServerMain' class
2. Run 'ClientMain' class
3. Then run the code according the console..

## Team members who build this mini project for academic 
1.Isfak Mahmud Hridoy (Mission Control + Multi Threading)
2.Mahmudul Ferdous (Socket Programming)
3.Rizia Karim (Exception handling + File handling + Multi Threading)
4.Koushik Chakrobortty (Spacecraft + Rocket + Satellite)
5.Afsana Khatun ( Mission + Space Mission)
