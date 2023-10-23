# Javanaise
**A distributed object cache in Java**

## Setup 

### Prerequisites:
- **Java**: Ensure you have Java installed. You can verify this by running the following command:
  `java -version`
  
### Installation Guide:

**Clone the Repository**:
- Open your terminal or command prompt.
- Navigate to the directory where you want to clone the project.
- Run the following command to clone the repository:

`git clone https://github.com/m2gi-clergetm/Javanaise.git`

- Change your current directory to the project's root directory:
`cd Javanaise`

---

## Run

### Inside Eclipse (or any other java IDE)

**Run the Application**:
 - Run the file named `JvnCoordImpl.java` located in `SOUCES/src/jvn/coord/`.
 - Every time you need to run a new Client, run the file `Irc.java` located in `SOUCES/src/irc/`.

---

### Tests

#### Headless Class  
A headless version of IRC is available to test a big number of operations. To use it, first launch the `JvnCoordImpl.java`. Then run `IrcHeadless.java`. It is possible to modify the number of operations by changing the value of `NActions` line 30 of `IrcHeadless.java`.

#### Burst Test
A burst test is available as a bash file, in the root of the project. To run it, use   
`bash burst.sh <number of simultaneous clients>`. Then, kill the running processes by killing the execution, with `Ctrl+C`.

Example :

`bash burst.sh 4`
```bash
[ ] Start Server
[ ] Launch 4 tests
Run 1
Run 2
Run 3
Run 4
Server ready
Irc headless safely ended after 24986 reads and 25014 writes
Irc headless safely ended after 24986 reads and 25014 writes
Irc headless safely ended after 25142 reads and 24858 writes
Irc headless safely ended after 24752 reads and 25248 writes
^Ckill 72548
kill 72550
kill 72551
kill 72552
kill 72553
Processes killed.
```