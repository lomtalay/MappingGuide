package io.github.lomtalay.logger;

import java.io.PrintWriter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


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

	private LocalLogLevel level;
	private static PrintWriter pw;
	
	
	public static void setLogWriter(PrintWriter newPw) {
		pw = newPw;
	}
	
	private static Level forceLevel = null;
	private static final Logger internalLoger = Logger.getLogger("io.github.lomtalay.logger.LocalLogger");
	private static boolean initFlag = false;
	
	private Logger julLogger = null;
	
	public static void setForceLevel(String levelName) {
		forceLevel = LocalLogLevel.parseFromName(levelName);
	}
	
	private static void InternalLog(String msg, Throwable t) {
		
		if(internalLoger.getLevel() == null) {
			internalLoger.setLevel(LocalLogLevel.DEBUG);
			Handler hh[] = internalLoger.getHandlers();
			for(int i=0;i<hh.length;i++) {
				internalLoger.removeHandler(hh[i]);
			}
			
			ConsoleHandler consoleHandler = new ConsoleHandler();
			consoleHandler.setLevel(Level.ALL);
			SimpleFormatter internalFormatter = new SimpleFormatter();
			consoleHandler.setFormatter(internalFormatter);
			internalLoger.addHandler(consoleHandler);
		}
		
		
		LogRecord lr = new LogRecord(LocalLogLevel.INFO, msg);
        lr.setThrown(t);
        
        try {
        	internalLoger.log(lr);
        } catch(Throwable tt) {
        	System.err.println(msg);
        }
	}

	protected LocalLogger(String name) {
		
		
		julLogger = Logger.getLogger(name);
		
		if(julLogger.getLevel() == null) {
			julLogger.setLevel(julLogger.getParent().getLevel());
			Handler hh[] = internalLoger.getHandlers();
			boolean foundConsole = false;
			for(int i=0;i<hh.length;i++) {
				if(hh[i] instanceof ConsoleHandler) {
					((ConsoleHandler)hh[i]).setLevel(Level.ALL);;
					foundConsole = true;
				}
			}
			if(!foundConsole) {
				ConsoleHandler consoleHandler = new ConsoleHandler();
				consoleHandler.setLevel(Level.ALL);
				julLogger.addHandler(consoleHandler);
			}
			

			String forceLevelStr = System.getProperty("io.github.lomtalay.log.level");
			if(forceLevelStr != null && forceLevel == null) {
				System.out.println("io.github.lomtalay.log.level="+forceLevelStr);
				forceLevel = LocalLogLevel.parseFromName(forceLevelStr);
				System.out.println("sel default log level = "+forceLevel);
				julLogger.setLevel(forceLevel);
				
				if(System.getProperty("io.github.lomtalay.log.level.debug")!=null) {
					julLogger.log(createLogRecord(LocalLogLevel.TRACE, "check log trace level", null));
					julLogger.log(createLogRecord(LocalLogLevel.DEBUG, "check log debug level", null));
					julLogger.log(createLogRecord(LocalLogLevel.INFO, "check log info level", null));
					julLogger.log(createLogRecord(LocalLogLevel.WARN, "check log warn level", null));
					julLogger.log(createLogRecord(LocalLogLevel.ERROR, "check log error level", null));
				}
			}	
		} else 
		if(forceLevel != null) {
			julLogger.setLevel(forceLevel);
		}
	}
	

	public static LocalLogger getLogger(Class cls) {
		return new LocalLogger(cls.getName());
	}
	
	

	public boolean isErrorEnabled() {
		return julLogger.isLoggable(LocalLogLevel.ERROR);
	}
	public boolean isWarnEnabled() {
		return julLogger.isLoggable(LocalLogLevel.WARN);
	}
	public boolean isInfoEnabled() {
		return julLogger.isLoggable(LocalLogLevel.INFO);
	}
	public boolean isDebugEnabled() {
		return julLogger.isLoggable(LocalLogLevel.DEBUG);
	}
	public boolean isTraceEnabled() {
		return julLogger.isLoggable(LocalLogLevel.TRACE);
	}
	
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
			try {
				julLogger.log(createLogRecord(LocalLogLevel.ERROR, msg, t));
			} catch(Throwable e) {
				System.err.println(msg);
				if(t!=null) t.printStackTrace(System.err);
			}
		}
	}
	public void warn(String msg, Throwable t) {
		if(isWarnEnabled()) {
			try {
				julLogger.log(createLogRecord(LocalLogLevel.WARN, msg, t));
			} catch(Throwable e) {
				System.err.println(msg);
				if(t!=null) t.printStackTrace(System.err);
			}
		}
	}
	public void info(String msg, Throwable t) {
		if(isInfoEnabled()) {
			try {
				julLogger.log(createLogRecord(LocalLogLevel.INFO, msg, t));
			} catch(Throwable e) {
				System.err.println(msg);
				if(t!=null) t.printStackTrace(System.err);
			}
		}
	}
	public void debug(String msg, Throwable t) {
		if(isDebugEnabled()) {
			try {
				julLogger.log(createLogRecord(LocalLogLevel.DEBUG, msg, t));
			} catch(Throwable e) {
				System.err.println(msg);
				if(t!=null) t.printStackTrace(System.err);
			}
		}
	}
	public void trace(String msg, Throwable t) {
		if(isTraceEnabled()) {
			try {
				julLogger.log(createLogRecord(LocalLogLevel.TRACE, msg, t));
			} catch(Throwable e) {
				System.err.println(msg);
				if(t!=null) t.printStackTrace(System.err);
			}
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
		return String.valueOf(julLogger.getLevel());
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
		
		protected static Level parseFromName(String name) {
			if("trace".equalsIgnoreCase(name)) {
				return TRACE;
			} else
			if("debug".equalsIgnoreCase(name)) {
				return DEBUG;
			} else
			if("info".equalsIgnoreCase(name)) {
				return INFO;
			} else
			if("warn".equalsIgnoreCase(name)) {
				return WARN;
			} else
			if("error".equalsIgnoreCase(name)) {
				return ERROR;
			}
			
			return Level.OFF;
		}
		
	}
	
}
