/***
 * JAVANAISE API
 * State
 * Define enum represents the state of the lock of a Javanaise object.
 * Contact: 
 *
 * Authors: MathysC MatveiP
 */
package jvn.utils;

/**
 * LockState enum
 * This enum represents the state of the lock of a Javanaise object.
 * 
 * @author mathysc
 */
public enum State {
	NL, // No Lock
	R, // Read lock taken
	W, // Write lock taken
	RWC, // Write lock cached & Read taken
	RC, // Read lock cached (not currently used)
	WC, // Write lock cached (not currently used)
}
