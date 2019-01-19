package io.github.lomtalay.logger;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/*

FINEST  -> TRACE
FINER   -> DEBUG
FINE    -> DEBUG
CONFIG  -> INFO
INFO    -> INFO
WARNING -> WARN
SEVERE  -> ERROR

 */

public class LocalLogger {

	
	private Logger realLog;

	protected LocalLogger(String name) {
		
		
		realLog = Logger.getLogger(name);
		
		if(realLog.getLevel() == null) {
			//Logger rootLogger = Logger.getLogger("");
			realLog.setLevel(realLog.getParent().getLevel());
//			Handler hdl[] = rootLogger.getHandlers();
//			for(int i=0;i<hdl.length;i++) {
//				realLog.addHandler(hdl[i]);
//			}
		}
	}
	

	public static LocalLogger getLogger(Class cls) {
		
		return new LocalLogger(cls.getName());
	}
	

	public boolean isErrorEnabled() {
		return realLog.isLoggable(LocalLogLevel.ERROR);
	}
	public boolean isWarnEnabled() {
		return realLog.isLoggable(LocalLogLevel.WARN);
	}
	public boolean isInfoEnabled() {
		return realLog.isLoggable(LocalLogLevel.INFO);
	}
	public boolean isDebugEnabled() {
		return realLog.isLoggable(LocalLogLevel.DEBUG);
	}
	public boolean isTraceEnabled() {
		return realLog.isLoggable(LocalLogLevel.TRACE);
	}
//	
//	private String localFormatMsg(String msg) {
//		StackTraceElement steSet[] = Thread.currentThread().getStackTrace();
//		StackTraceElement ste = null;
//		
//		for(int i=0;i<steSet.length;i++) {
//			if(steSet[i].getClassName().equals(LocalLogger.class)) {
//				continue;
//			}
//			
//			ste = steSet[i];
//			break;
//		}
//		
//
//		
//		String fullClassName = ste.getClassName();
//	    String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
//	    String fileName = ste.getFileName();
//	    String methodName = ste.getMethodName();
//	    int lineNumber = ste.getLineNumber();
//	    StringBuilder sb = new StringBuilder();
//	    
//	    sb	.append("<").append(fullClassName).append("#").append(methodName).append(">")
//	    	.append("(").append(fileName).append(":").append(String.valueOf(lineNumber)).append(") - ")
//	    	.append(msg);
//	    
//	    return sb.toString();
//	}
	
	private LogRecord createLogRecord(Level level, String msg, Throwable t) {
		
		StackTraceElement steSet[] = Thread.currentThread().getStackTrace();
		StackTraceElement ste = null;
		
		for(int i=2;i<steSet.length;i++) {
			if(steSet[i].getClassName().equals(LocalLogger.class.getName())) {
				continue;
			}
			
			ste = steSet[i];
			break;
		}
		

		LogRecord lr = new LogRecord(level, msg);
        lr.setThrown(t);
        lr.setSourceClassName(ste.getMethodName()+"@"+ste.getFileName() + ":"+ste.getLineNumber());
        
        return lr;
	}
	
	public void error(String msg, Throwable t) {
		if(isErrorEnabled()) {
	        realLog.log(createLogRecord(LocalLogLevel.ERROR, msg, t));
		}
	}
	public void warn(String msg, Throwable t) {
		if(isWarnEnabled()) {
	        realLog.log(createLogRecord(LocalLogLevel.WARN, msg, t));
		}
	}
	public void info(String msg, Throwable t) {
		if(isInfoEnabled()) {
	        realLog.log(createLogRecord(LocalLogLevel.INFO, msg, t));
		}
	}
	public void debug(String msg, Throwable t) {
		if(isDebugEnabled()) {
	        realLog.log(createLogRecord(LocalLogLevel.DEBUG, msg, t));
		}
	}
	public void trace(String msg, Throwable t) {
		if(isTraceEnabled()) {
	        realLog.log(createLogRecord(LocalLogLevel.TRACE, msg, t));
		}
	}
	

	public void error(String msg) {
		error(msg, null);
	}
	public void warn(String msg) {
		warn(msg, null);
	}
	public void info(String msg) {
		info(msg, null);
	}
	public void debug(String msg) {
		debug(msg, null);
	}
	public void trace(String msg) {
		trace(msg, null);
	}


	public String getLevel() {
		return String.valueOf(realLog.getLevel());
	}
	

	public static class LocalLogLevel extends Level {
		
		private static final long serialVersionUID = 1L;
		
		public static final Level TRACE = new LocalLogLevel("TRACE", Level.FINEST.intValue());
		public static final Level DEBUG = new LocalLogLevel("DEBUG", Level.FINER.intValue()); 
		public static final Level INFO = new LocalLogLevel("INFO", Level.INFO.intValue());  
		public static final Level WARN = new LocalLogLevel("WARN", Level.WARNING.intValue());
		public static final Level ERROR = new LocalLogLevel("ERROR", Level.SEVERE.intValue()); 

		protected LocalLogLevel(String name, int value) {
			super(name, value);
		}
		
	}
	
}
