/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2009, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.helpers;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;

import ch.qos.logback.classic.ClassicConstants;

/**
 * A servlet filter that inserts various values retrieved from the incoming http
 * request into the MDC.
 * 
 * <p>The values are removed after the request is processed.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class MDCInsertingServletFilter implements Filter {

  public void destroy() {
    // do nothing
  }

  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    insertIntoMDC(request);

    try {
      chain.doFilter(request, response);
    } finally {
      clearMDC();
    }
  }

  void insertIntoMDC(ServletRequest request) {

    MDC.put(ClassicConstants.REQUEST_REMOTE_HOST_MDC_KEY, request
        .getRemoteHost());
    
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      MDC.put(ClassicConstants.REQUEST_REQUST_URI, httpServletRequest
          .getRequestURI());
      MDC.put(ClassicConstants.REQUEST_USER_AGENT_MDC_KEY, httpServletRequest
          .getHeader("User-Agent"));
    }

  }

  void clearMDC() {
    MDC.remove(ClassicConstants.REQUEST_REMOTE_HOST_MDC_KEY);
    MDC.remove(ClassicConstants.REQUEST_REQUST_URI);
    MDC.remove(ClassicConstants.REQUEST_USER_AGENT_MDC_KEY);
  }

  public void init(FilterConfig arg0) throws ServletException {
    // do nothing
  }
}
