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

### Inside Eclipse (or any other java IDE)

**Run the Application**:
 - Run the file named `JvnCoordImpl.java` located in `SOUCES/src/jvn/coord/`.
 - Every time you need to run a new Client, run the file `Irc.java` located in `SOUCES/src/irc/`.


### Dev Notes
When we want to send a Sentence (or serializable used by user, type doesn't matter) we send back a sharedObject (obj.jvnGetSharedObject() ) else, if we want to return a JvnObject, we return it. 