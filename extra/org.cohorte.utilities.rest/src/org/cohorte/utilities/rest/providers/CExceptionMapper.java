package org.cohorte.utilities.rest.providers;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.ExceptionMapper;

import org.cohorte.utilities.rest.api.CRestResource;
import org.cohorte.utilities.rest.objects.CGedExceptionWrapper;

/**
 * Unchecked exceptions mapper.
 * 
 * @author Ahmad Shahwan
 *
 */
public class CExceptionMapper
	extends CRestResource
	implements ExceptionMapper<Throwable> {

	static boolean pDebug;
	
	static {
		pDebug = System.getProperty("DEBUG") != null;
	}

	@Context
	private HttpServletRequest pRequest;

	@Override
	public Response toResponse(Throwable ex) {
		getLog().logInfo(this, "", "Exception occured:\n%s", ex);
		if (pDebug) {
			ex.printStackTrace();
		}
		CGedExceptionWrapper wWrapper = new CGedExceptionWrapper(ex, pRequest);
		return Response
				.status(getStatus(wWrapper))
				.entity(wWrapper)
				.type(MediaType.APPLICATION_JSON).
				build();
	}
	
	private static StatusType getStatus(CGedExceptionWrapper aWrapper) {
		int wCode = aWrapper.getStatusCode();
		StatusType wStatus = Status.fromStatusCode(wCode);
		if (wStatus == null) {
			wStatus = new CHttpStatus(wCode);
		}
		return wStatus;
	}
	
	private static class CHttpStatus implements StatusType {
		
		private int pCode;
		private String pReason;
		private Family pFamily;
		
		public CHttpStatus(int aCode) {
			this.pCode = aCode;
			switch (aCode / 100) {
			case 1:
				this.pReason = "Information";
				this.pFamily = Family.INFORMATIONAL;
				break;
			case 2:
				this.pReason = "OK";
				this.pFamily = Family.SUCCESSFUL;
				break;
			case 3:
				this.pReason = "Redirection";
				this.pFamily = Family.REDIRECTION;
				break;
			case 4:
				this.pReason = "Unprocessable Request";
				this.pFamily = Family.CLIENT_ERROR;
				break;
			case 5:
				this.pReason = "Server Error";
				this.pFamily = Family.SERVER_ERROR;
				break;
			default:
				this.pReason = "Other";
				this.pFamily = Family.OTHER;
				break;
			}			
		}

		@Override
		public Family getFamily() {
			return this.pFamily;
		}

		@Override
		public String getReasonPhrase() {
			return this.pReason;
		}

		@Override
		public int getStatusCode() {
			return this.pCode;
		}
		
	}
}
