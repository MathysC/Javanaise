/***
 * Operation Interface : 
 * This interface defines the annotation link to the Operation.
 * Contact: 
 *
 * Authors: MathysC MatveiP
 */
package jvn.annotations;

import java.lang.annotation.*;

/*
 * The annotation is available at execution time. 
 */
@Retention(RetentionPolicy.RUNTIME)

/*
 * The annotation is associated with a type (Classe, interface)
 */
@Target(ElementType.METHOD)

public @interface Operation {
	String name();
}