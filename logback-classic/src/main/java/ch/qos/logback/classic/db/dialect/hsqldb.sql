# This SQL script creates the required tables by ch.qos.logback.classic.db.DBAppender.
#
# It is intended for HSQL databases. It has been tested on HSQL 1.8.07.

DROP TABLE LOGGING_EVENT_EXCEPTION IF EXISTSu;
DROP TABLE LOGGING_EVENT_PROPERTY IF EXISTS;
DROP TABLE LOGGING_EVENT IF EXISTS;

CREATE TABLE LOGGING_EVENT (
  TIMESTMP BIGINT NOT NULL,
  FORMATTED_MESSAGE LONGVARCHAR NOT NULL,
  LOGGER_NAME VARCHAR(256) NOT NULL,
  LEVEL_STRING VARCHAR(256) NOT NULL,
  THREAD_NAME VARCHAR(256),
  REFERENCE_FLAG SMALLINT,
  CALLER_FILENAME VARCHAR(256), 
  CALLER_CLASS VARCHAR(256), 
  CALLER_METHOD VARCHAR(256), 
  CALLER_LINE CHAR(4),
  EVENT_ID BIGINT NOT NULL IDENTITY);


CREATE TABLE LOGGING_EVENT_PROPERTY (
  EVENT_ID BIGINT NOT NULL,
  MAPPED_KEY  VARCHAR(254) NOT NULL,
  MAPPED_VALUE LONGVARCHAR,
  PRIMARY KEY(EVENT_ID, MAPPED_KEY),
  FOREIGN KEY (EVENT_ID) REFERENCES LOGGING_EVENT(EVENT_ID));

CREATE TABLE LOGGING_EVENT_EXCEPTION (
  EVENT_ID BIGINT NOT NULL,
  I SMALLINT NOT NULL,
  TRACE_LINE VARCHAR(256) NOT NULL,
  PRIMARY KEY(EVENT_ID, I),
  FOREIGN KEY (EVENT_ID) REFERENCES LOGGING_EVENT(EVENT_ID));