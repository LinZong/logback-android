/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2011, QOS.ch. All rights reserved.
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
package ch.qos.logback.classic.net;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.dom4j.io.SAXReader;
import org.junit.*;
import org.slf4j.MDC;

import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.html.HTMLLayout;
import ch.qos.logback.classic.html.XHTMLEntityResolver;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.joran.spi.JoranException;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;

import static org.junit.Assert.*;

public class SMTPAppender_GreenTest {

  static final boolean SYNCHRONOUS = false;
  static final boolean ASYNCHRONOUS = true;
  static GreenMail GREEN_MAIL_SERVER;

  SMTPAppender smtpAppender;
  LoggerContext lc = new LoggerContext();
  Logger logger = lc.getLogger(this.getClass());

  static final String TEST_SUBJECT = "test subject";
  static final String HEADER = "HEADER\n";
  static final String FOOTER = "FOOTER\n";
  static final String DEFAULT_PATTERN = "%d{HH:mm:ss,SSS} %mdc [%thread] %-5level %class - %msg%n";

  @Before
  public void setUp() throws Exception {
    GREEN_MAIL_SERVER = new GreenMail();
    GREEN_MAIL_SERVER.start();
    MDC.clear();
  }

  @After
  public void tearDown() throws Exception {
    GREEN_MAIL_SERVER.stop();
  }

  void buildSMTPAppender(int port, boolean synchronicity) throws Exception {
    smtpAppender = new SMTPAppender();
    smtpAppender.setContext(lc);
    smtpAppender.setName("smtp");
    smtpAppender.setFrom("user@host.dom");
    smtpAppender.setSMTPHost("localhost");
    smtpAppender.setSMTPPort(port);
    smtpAppender.setSubject(TEST_SUBJECT);
    smtpAppender.addTo("nospam@qos.ch");
    smtpAppender.setAsynchronousSending(synchronicity);
    // smtpAppender.start();
  }

  private Layout<ILoggingEvent> buildPatternLayout(LoggerContext lc, String pattern) {
    PatternLayout layout = new PatternLayout();
    layout.setContext(lc);
    layout.setFileHeader(HEADER);
    layout.setOutputPatternAsHeader(false);
    layout.setPattern(pattern);
    layout.setFileFooter(FOOTER);
    layout.start();
    return layout;
  }

  private Layout<ILoggingEvent> buildHTMLLayout(LoggerContext lc) {
    HTMLLayout layout = new HTMLLayout();
    layout.setContext(lc);
    // layout.setFileHeader(HEADER);
    layout.setPattern("%level%class%msg");
    // layout.setFileFooter(FOOTER);
    layout.start();
    return layout;
  }

  private void printEmails(MimeMessage[] mma, boolean printBody) {
    StringBuffer sb = new StringBuffer();
    sb.append("### Emails:" + "\n");
    for (int i = 0; i < mma.length; i++) {
      MimeMessage mm = mma[i];
      try {
        if (printBody) {
          MimeMultipart mp = (MimeMultipart)mm.getContent();
          String body = GreenMailUtil.getBody(mp.getBodyPart(0));
          sb.append("[" + i + "]=" + mm.getSubject() + " : " + body + "\n");
        } else {
          sb.append("[" + i + "]=" + mm.getSubject() + "\n");
        }
      } catch (MessagingException e) {
      } catch (IOException e) {
      }
    }
    System.out.println(sb.toString());
  }

  private MimeMultipart verify(GreenMail testServer, String subject) throws MessagingException,
    IOException, InterruptedException {
    return verify(testServer, subject, false);
  }

  private MimeMultipart verify(GreenMail testServer, String subject, boolean verbose) throws MessagingException,
          IOException, InterruptedException {
    waitUntilEmailIsReceived(testServer, 1);
    MimeMessage[] mma = testServer.getReceivedMessages();
    assertNotNull(mma);
    assertEquals(1, mma.length);
    MimeMessage mm = mma[0];
    if (verbose) printEmails(mma, true);
    // http://jira.qos.ch/browse/LBCLASSIC-67
    assertEquals(subject, mm.getSubject());
    return (MimeMultipart) mm.getContent();
  }

  void waitUntilEmailIsSent() throws InterruptedException {
    lc.getExecutorService().shutdown();
    lc.getExecutorService().awaitTermination(1000, TimeUnit.MILLISECONDS);
  }

  void waitUntilEmailIsReceived(GreenMail testServer, int emailCount) throws InterruptedException {
    System.out.println("Waiting for message");
    testServer.waitForIncomingEmail(emailCount);
  }

  @Test
  public void syncronousSmoke() throws Exception {
    buildSMTPAppender(GREEN_MAIL_SERVER.getSmtp().getPort(), SYNCHRONOUS);

    smtpAppender.setLayout(buildPatternLayout(lc, DEFAULT_PATTERN));
    smtpAppender.start();
    logger.addAppender(smtpAppender);
    logger.debug("hello");
    logger.error("en error", new Exception("an exception"));

    MimeMultipart mp = verify(GREEN_MAIL_SERVER, TEST_SUBJECT);
    String body = GreenMailUtil.getBody(mp.getBodyPart(0));
    assertTrue(body.startsWith(HEADER.trim()));
    assertTrue(body.endsWith(FOOTER.trim()));
  }

  @Test
  public void asyncronousSmoke() throws Exception {
    buildSMTPAppender(GREEN_MAIL_SERVER.getSmtp().getPort(), ASYNCHRONOUS);
    smtpAppender.setLayout(buildPatternLayout(lc, DEFAULT_PATTERN));
    smtpAppender.start();
    logger.addAppender(smtpAppender);
    logger.debug("hello");
    logger.error("en error", new Exception("an exception"));

    waitUntilEmailIsSent();
    MimeMultipart mp = verify(GREEN_MAIL_SERVER, TEST_SUBJECT);
    String body = GreenMailUtil.getBody(mp.getBodyPart(0));
    assertTrue(body.startsWith(HEADER.trim()));
    assertTrue(body.endsWith(FOOTER.trim()));
  }

  // See also http://jira.qos.ch/browse/LOGBACK-734
  @Test
  public void callerDataShouldBeCorrectlySetWithAsyncronousSending() throws Exception {
    buildSMTPAppender(GREEN_MAIL_SERVER.getSmtp().getPort(), ASYNCHRONOUS);
    smtpAppender.setLayout(buildPatternLayout(lc,DEFAULT_PATTERN));
    smtpAppender.setIncludeCallerData(true);
    smtpAppender.start();
    logger.addAppender(smtpAppender);
    logger.debug("hello");
    logger.error("en error", new Exception("an exception"));

    waitUntilEmailIsSent();
    MimeMultipart mp = verify(GREEN_MAIL_SERVER, TEST_SUBJECT);
    String body = GreenMailUtil.getBody(mp.getBodyPart(0));
    assertTrue(body.contains("DEBUG "+this.getClass().getName()+" - hello"));
  }

  @Test
  public void LBCLASSIC_104() throws Exception {
    buildSMTPAppender(GREEN_MAIL_SERVER.getSmtp().getPort(), SYNCHRONOUS);
    smtpAppender.setAsynchronousSending(false);
    smtpAppender.setLayout(buildPatternLayout(lc, DEFAULT_PATTERN));
    smtpAppender.start();
    logger.addAppender(smtpAppender);
    MDC.put("key", "val");
    logger.debug("hello");
    MDC.clear();
    logger.error("en error", new Exception("an exception"));

    MimeMultipart mp = verify(GREEN_MAIL_SERVER, TEST_SUBJECT, true);
    String body = GreenMailUtil.getBody(mp.getBodyPart(0));
    assertTrue("missing HEADER in body", body.startsWith(HEADER.trim()));
    assertTrue("missing MDC in body", body.contains("key=val"));
    assertTrue("missing FOOTER in body", body.endsWith(FOOTER.trim()));
  }

  @Test
  public void html() throws Exception {
    buildSMTPAppender(GREEN_MAIL_SERVER.getSmtp().getPort(), SYNCHRONOUS);
    smtpAppender.setAsynchronousSending(false);
    smtpAppender.setLayout(buildHTMLLayout(lc));
    smtpAppender.start();
    logger.addAppender(smtpAppender);
    logger.debug("hello");
    logger.error("en error", new Exception("an exception"));

    MimeMultipart mp = verify(GREEN_MAIL_SERVER, TEST_SUBJECT);

    // verify strict adherence to xhtml1-strict.dtd
    SAXReader reader = new SAXReader();
    reader.setValidation(true);
    reader.setEntityResolver(new XHTMLEntityResolver());
    reader.read(mp.getBodyPart(0).getInputStream());

  }

  private void configure(int port, String file) throws JoranException {
    JoranConfigurator jc = new JoranConfigurator();
    jc.setContext(lc);
    //System.out.println("port=" + port);
    lc.putProperty("port", "" + port);
    jc.doConfigure(file);
  }

  @Test
  public void testCustomEvaluator() throws Exception {
    configure(GREEN_MAIL_SERVER.getSmtp().getPort(),
        ClassicTestConstants.JORAN_INPUT_PREFIX + "smtp/customEvaluator.xml");

    logger.debug("hello");
    String msg2 = "world";
    logger.debug(msg2);
    logger.debug("invisible");
    waitUntilEmailIsSent();
    MimeMultipart mp = verify(GREEN_MAIL_SERVER, this.getClass().getName() + " - " + msg2);
    String body = GreenMailUtil.getBody(mp.getBodyPart(0));
    assertEquals("helloworld", body);
  }

  @Test
  public void testCustomBufferSize() throws Exception {
    configure(GREEN_MAIL_SERVER.getSmtp().getPort(),
        ClassicTestConstants.JORAN_INPUT_PREFIX
            + "smtp/customBufferSize.xml");

    logger.debug("invisible1");
    logger.debug("invisible2");
    String msg = "hello";
    logger.error(msg);
    waitUntilEmailIsSent();
    MimeMultipart mp = verify(GREEN_MAIL_SERVER, this.getClass().getName() + " - " + msg);
    String body = GreenMailUtil.getBody(mp.getBodyPart(0));
    assertEquals(msg, body);
  }

  @Test
  public void testMultipleTo() throws Exception {
    buildSMTPAppender(GREEN_MAIL_SERVER.getSmtp().getPort(), SYNCHRONOUS);
    smtpAppender.setLayout(buildPatternLayout(lc, DEFAULT_PATTERN));
    smtpAppender.addTo("Test <test@example.com>, other-test@example.com");
    smtpAppender.start();
    logger.addAppender(smtpAppender);
    logger.debug("hello");
    logger.error("en error", new Exception("an exception"));

    waitUntilEmailIsReceived(GREEN_MAIL_SERVER, 3);
    MimeMessage[] mma = GREEN_MAIL_SERVER.getReceivedMessages();
    assertNotNull(mma);
    assertEquals(3, mma.length);
  }

  // http://jira.qos.ch/browse/LBCLASSIC-221
  @Test
  public void bufferShouldBeResetBetweenMessages() throws Exception {
    buildSMTPAppender(GREEN_MAIL_SERVER.getSmtp().getPort(), SYNCHRONOUS);
    smtpAppender.setLayout(buildPatternLayout(lc, DEFAULT_PATTERN));
    smtpAppender.start();
    logger.addAppender(smtpAppender);
    String msg0 = "hello zero";
    logger.debug(msg0);
    logger.error("error zero");

    String msg1 = "hello one";
    logger.debug(msg1);
    logger.error("error one");

    waitUntilEmailIsSent();
    waitUntilEmailIsReceived(GREEN_MAIL_SERVER, 2);

    MimeMessage[] mma = GREEN_MAIL_SERVER.getReceivedMessages();
    assertNotNull(mma);
    assertEquals(2, mma.length);

    MimeMessage mm0 = mma[0];
    MimeMultipart content0 = (MimeMultipart) mm0.getContent();
    String body0 = GreenMailUtil.getBody(content0.getBodyPart(0));
    assertTrue(body0.contains(msg0));

    MimeMessage mm1 = mma[1];
    MimeMultipart content1 = (MimeMultipart) mm1.getContent();
    String body1 = GreenMailUtil.getBody(content1.getBodyPart(0));
    // second body should not contain content from first message
    assertFalse(body1.contains(msg0));
  }
}
