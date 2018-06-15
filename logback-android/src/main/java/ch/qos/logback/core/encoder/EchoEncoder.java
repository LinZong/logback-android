/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2013, QOS.ch. All rights reserved.
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
package ch.qos.logback.core.encoder;

import java.io.IOException;

import ch.qos.logback.core.CoreConstants;

public class EchoEncoder<E> extends EncoderBase<E> {
  String fileHeader;
  String fileFooter;

  public byte[] doEncode(E event) throws IOException {
    String val = event + CoreConstants.LINE_SEPARATOR;
    return val.getBytes();
  }

  public byte[] close() throws IOException {
    if (fileFooter == null) {
      return null;
    }
    return fileFooter.getBytes();
  }

  public byte[] init() {
    if (fileHeader == null) {
      return null;
    }
    return fileHeader.getBytes();
  }
}
