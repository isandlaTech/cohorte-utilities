package org.cohorte.utilities.rest.serializers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import org.cohorte.utilities.rest.objects.CCalendar;

/**
 * JSON message writer/reader.
 * 
 * TODO implement as component
 * 
 * @author Ahmad Shahwan
 * 
 * @see <a href="http://eclipsesource.com/blogs/2012/11/02/integrating-gson-into-a-jax-rs-based-application/">
 *      Original post</a>
 *
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class CGedJacksonMessageBodyHandler
	implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

	private static final String UTF_8 = "UTF-8";

	private ObjectMapper getMapper() {
		ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector introspector = 
				new JaxbAnnotationIntrospector(mapper.getTypeFactory());
		mapper.setAnnotationIntrospector(introspector);
		DateFormat df = new SimpleDateFormat(CCalendar.DATE_FORMAT);
		mapper.setDateFormat(df);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		return mapper;
	}
	
	@Override
	public boolean isReadable(
			Class<?> type,
			Type genericType,
			java.lang.annotation.Annotation[] annotations,
			MediaType mediaType) {
		return true;
	}

	@Override
	public Object readFrom(
			Class<Object> type,
			Type genericType,
			Annotation[] annotations,
			MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders,
			InputStream entityStream) throws IOException {
		InputStreamReader streamReader = new InputStreamReader(entityStream, UTF_8);
		try {
			return getMapper().readValue(streamReader, type);
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		} finally {
			streamReader.close();
		}
	}

	@Override
	public boolean isWriteable(
			Class<?> type,
			Type genericType,
			Annotation[] annotations,
			MediaType mediaType) {
		return true;
	}

	@Override
	public long getSize(
			Object object,
			Class<?> type,
			Type genericType,
			Annotation[] annotations,
			MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(
			Object object,
			Class<?> type,
			Type genericType,
			Annotation[] annotations,
			MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream
		) throws IOException, WebApplicationException {
		OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8);
		try {
			getMapper().writeValue(writer, object);
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		} finally {
			writer.close();
		}
	}
}
