package org.cohorte.utilities.rest.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import org.cohorte.utilities.rest.providers.CExceptionMapper;
import org.cohorte.utilities.rest.providers.CRestLogFilter;
import org.cohorte.utilities.rest.serializers.CGedJAXBMessageBodyHandler;
import org.cohorte.utilities.rest.serializers.CGedJacksonMessageBodyHandler;

/**
 * Generic REST application abstract component.
 * Subclasses overrides {@code getClasses()}. They are likely to call\
 * {@code super.getClasses()} from within the override.
 * 
 * @author Ahmad Shahwan
 *
 */
public abstract class CRestApplication
	extends Application
	implements IRestApplication {

	/**
	 * No-args constructor.
	 */
	public CRestApplication() {
	}
	
	/**
	 * Denote if the resource support multipart.
	 * 
	 * @return {@code true} if the resource supports multipart requests,
	 * {@code false} otherwise (the default)
	 */
	abstract protected boolean supportsMultipart();
	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> result = new HashSet<Class<?>>();
		if (this.supportsMultipart()) {
			result.add(MultiPartFeature.class);;
		}
		result.add(CRestLogFilter.class);
		result.add(CGedJacksonMessageBodyHandler.class);
		result.add(CGedJAXBMessageBodyHandler.class);
		result.add(CExceptionMapper.class);
		return result;
	}
}
