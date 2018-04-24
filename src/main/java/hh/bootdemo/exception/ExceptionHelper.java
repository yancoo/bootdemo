/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hh.bootdemo.exception;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author yan
 */
public class ExceptionHelper {

	private final static Log log = LogFactory.getLog(ExceptionHelper.class);

	public static String generateException(Throwable e) {
		if (e == null) {
			return generateServiceException(new ServiceException(CommonExceptionType.nullException));
		}
		if (e instanceof ServletException) {
			e = ((ServletException) e).getRootCause();
		}

		if (e instanceof ServiceException) {
			return generateServiceException((ServiceException) e);
		}
		return generateServiceException(new ServiceException(CommonExceptionType.internalError, e, e.getMessage()));
	}

	private static String generateServiceException(ServiceException serviceException) {
		if (log.isDebugEnabled()) {
			log.debug("generate " + serviceException);
		}
		ObjectMapper mapper = new ObjectMapper();
		// jsonObject.put("exception", serviceException.getTypeName());
		// jsonObject.put("message", serviceException.getMessage() != null ?
		// serviceException.getMessage() : "");
		// if (serviceException.getArgs() != null) {
		// jsonObject.put("args", serviceException.getArgs());
		// }

		try {
			return mapper.writeValueAsString(serviceException);
		} catch (Exception e) {
			log.error("mapper.writeValueAsString exception " + serviceException.getMessage(), e);
			return null;
		}
	}
}
