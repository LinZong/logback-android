package org.slf4j

inline fun <reified T> getLogger(): Logger = LoggerFactory.getLogger(T::class.java)