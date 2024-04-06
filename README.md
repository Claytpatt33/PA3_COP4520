# Mars Rover Temperature Module & Minotaur Gifts Simulation - Clayton Patterson COP4520 Assignment Three

## Mars Rover Temperature Module

### Proof of Correctness
This program uses concurrent data structures and thread-safe mechanisms to manage temperature readings from multiple sensors.

### Efficiency
The implementation is efficient due to the data structures (`ConcurrentLinkedQueue` and `CopyOnWriteArrayList`) which will help minimize synchronization overhead. 
### Experimental Evaluation
The program was tested to run and produce temperature reports every minute, with sensor readings simulated every 5 seconds. This was the most realistic option for testing, and will allow for validation. 

### Instructions
1. Save the Java code in a file named `MarsRoverTemperatureModule.java`.
2. Compile the code using `javac MarsRoverTemperatureModule.java`.
3. Run the compiled code with `java MarsRoverTemperatureModule`.

## Minotaur Gifts Simulation

### Proof of Correctness
The `MinotaurGiftsSimulation` utilizes a lock-based concurrent linked list (`GiftLinkedList`) to help manage gift operations. Each thread, of which there are four, represents a servant that can :add, remove, or check for gifts.

### Efficiency
The linked list's use of locking allows for multiple threads to utilize different parts of the list simultaneously. This helps with concurrency. 

### Experimental Evaluation
The program simulates a scenario where servants perform actions on gifts concurrently. It find the total the total number of presents added and thank you notes written. The simulation runs until all presents are processed and concludes by checking if each gift has a corresponding thank you note.

### Instructions
1. Save the Java code in a file named `MinotaurGiftsSimulation.java`.
2. Compile the code using `javac MinotaurGiftsSimulation.java`.
3. Run the compiled code with `java MinotaurGiftsSimulation`
