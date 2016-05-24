package org.cohorte.utilities.rest.api;

import java.util.Set;

/**
 * A pluggable REST API service.
 *  
 * Sub-classes are components that can be plugged into a REST published by
 * simple instantiation.
 * 
 * @see {@code CRestApplication} 
 * 
 * @author Ahmad Shahwan
 *
 */
public interface IRestApplication {
	
	/**
	 * Return the path to the REST API application.
	 * 
	 * The path should not start or finish by "/".
	 * 
	 * @return a path to the application
	 */
	public String getPath();
	
	/**
	 * Return a set of web resources, as their types.
	 * 
	 * @return application's resources' types
	 */
	public Set<Class<?>> getClasses();
}
