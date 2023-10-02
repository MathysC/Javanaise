package jvn.utils;

/**
 * LockState enum
 * This enum represents the state of the lock of a Javanaise object.
 * 
 * @author mathysc
 */
public enum LockState {
	NL, // No Lock
	R, // Read lock taken
	W, // Write lock taken
	RWC, // Write lock cached & Read taken
	// RC, // Read lock cached (not currently used)
	// WC, // Write lock cached (not currently used)
};
