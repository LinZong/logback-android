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
package ch.qos.logback.core;

import ch.qos.logback.core.helpers.ConsoleOutputStreamWrapper;
import ch.qos.logback.core.joran.spi.ConsoleTarget;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.WarnStatus;

/**
 * ConsoleAppender appends log events to <code>System.out</code> or
 * <code>System.err</code> using a layout specified by the user. The default
 * target is <code>System.out</code>.
 * 
 * For more information about this appender, please refer to the online manual
 * at http://logback.qos.ch/manual/appenders.html#ConsoleAppender
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author Tom SH Liu
 */

public class ConsoleAppender<E> extends OutputStreamAppender<E> {

  public static final String SYSTEM_OUT = "System.out";
  public static final String SYSTEM_ERR = "System.err";
  protected ConsoleTarget target = ConsoleTarget.SystemOut;

  /**
   * As in most logback components, the default constructor does nothing.
   */
  public ConsoleAppender() {
  }

  /**
   * Sets the value of the <b>Target</b> option. Recognized values are
   * "System.out" and "System.err". Any other value will be ignored.
   */
  public void setTarget(String value) {
    String v = value.trim();

    if (SYSTEM_OUT.equalsIgnoreCase(v)) {
      target = ConsoleTarget.SystemOut;
    } else if (SYSTEM_ERR.equalsIgnoreCase(v)) {
      target = ConsoleTarget.SystemErr;
    } else {
      targetWarn(value);
    }
  }

  /**
   * Returns the current value of the <b>target</b> property. The default value
   * of the option is "System.out".
   * 
   * See also {@link #setTarget}.
   */
  public String getTarget() {
    switch (target) {
    case SystemOut:
      return SYSTEM_OUT;
    case SystemErr:
      return SYSTEM_ERR;
    }
    throw new IllegalStateException("Unexpected target value ["+target+"]");
  }

  void targetWarn(String val) {
    Status status = new WarnStatus("[" + val
        + " should be System.out or System.err.", this);
    status.add(new WarnStatus(
        "Using previously set target, System.out by default.", this));
    addStatus(status);
  }

  public void start() {
    setOutputStream(new ConsoleOutputStreamWrapper(target));
    super.start();
  }

}
