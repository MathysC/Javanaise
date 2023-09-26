# Javanaise
Projet de Système et application répartis

## Architecture
![image](https://github.com/MathysC/Javanaise/assets/32172257/f2ee5fa3-a2c4-4287-b651-8927088c31dc)

## Setup 

### Dans la console
`rmiregistry &`

### Côté Serveur
```java
public class Server implements Hello{

	public void funcFromStub() throws RemoteException {
		// Definir la(les) fonction(s) qui est(sont) dans le stub
	}

	public static void main(String[] args) {
		try {
			// Créer le serveur
			Server server = new Server();
			// Créer un stub (un portail) pour communiquer avec le serveur depuis
			// d'autres processus
			StubClass stub = (StubClass) UnicastRemoteObject.exportObject(server, 0);
			// Trouve le registre initialisé plus tôt dans la console
			Registry reg = LocateRegistry.getRegistry();
			// Lie le stub à un id unique en String pour le retrouver dans les autres
			// processus
			reg.bind("Hello", stub);
			
			System.out.println("Server ready");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

```
Un **stub** est un *portail* qui permet de communiquer. Le stub est créé par le serveur, puis inséré dans le regitre, afin que d'autres processus puissent le retrouver. Il fonctionne ensuite comme un parchemin de teleportation: tout endroit d'utilisation mennera au même point de départ, ici le serveur. 

### Côté Client
```java
public class Client {
	public static void main(String[] args) {
		// Au cas ou le hostname ne serait pas celui par défaut
		String host = (args.length < 1) ? null : args[0];
		try {
			// Trouve le registre lancé dans le terminal
			Registry reg = LocateRegistry.getRegistry(host);
			
			// Dans le registre, recupere le portail vers le serveur
			Hello stub = (Hello) reg.lookup("Hello");
			// Execute la fonction du serveur comme si elle etait locale
			String response = stub.helloWorld();
			// Process la reponse du serveur. 
			System.out.println("Response gotten from Client stub : "+response);
			
		} catch (Exception exp) {
			System.err.println("Error in client : \n"+exp);
		}
	}
}

```
Le client ne fait qu'utiliser ce qui a été déclaré par le serveur pour récuperer les informations, puis procede au traitement de l'information obtenu

### Interface stub
Interface contenant la liste des fonctions que le serveur doit implementer et que le client va appeler.  Les fonctions doivent throw une RemoteException !
```java
public interface Hello extends Remote{
	String helloWorld() throws RemoteException;
}
```