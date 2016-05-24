package org.cohorte.utilities.rest.serializers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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
@Produces(MediaType.TEXT_XML)
@Consumes(MediaType.TEXT_XML)
public final class CGedJAXBMessageBodyHandler
	implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

	private static final String UTF_8 = "UTF-8";


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

			JAXBContext context = JAXBContext.newInstance(type);

			Unmarshaller unmarshaller = context.createUnmarshaller();
			return unmarshaller.unmarshal(streamReader);

		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
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
			JAXBContext context = JAXBContext.newInstance(type);
			Marshaller marshaller = context.createMarshaller();

			// output pretty printed
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshaller.marshal(object, writer);

		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		} finally {
			writer.close();
		}
	}
}
