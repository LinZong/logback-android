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
package ch.qos.logback.classic;

import ch.qos.logback.core.util.CoreTestConstants;

public class ClassicTestConstants {
  final static public String ISO_REGEX = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}";
  //pool-1-thread-47
  final static public String NAKED_MAIN_REGEX = ".+";
  final static public String MAIN_REGEX = "\\[" + NAKED_MAIN_REGEX + "\\]";
  final static public String TEST_PREFIX = CoreTestConstants.BASE_DIR + "src/test/";
  final static public String INPUT_PREFIX = TEST_PREFIX + "input/";
  final static public String RESOURCES_PREFIX = TEST_PREFIX + "resources/";
  final static public String JORAN_INPUT_PREFIX = INPUT_PREFIX + "joran/";
  final static public String ISSUES_PREFIX =   ClassicTestConstants.JORAN_INPUT_PREFIX+"issues/";
  final static public String OUTPUT_DIR_PREFIX= CoreTestConstants.OUTPUT_DIR_PREFIX;
}
