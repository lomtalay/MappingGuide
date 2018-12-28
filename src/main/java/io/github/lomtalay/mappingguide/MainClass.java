package io.github.lomtalay.mappingguide;


import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.slf4j.LoggerFactory;

public class MainClass {
	
	org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	void doAction() {
		log.trace("TRACE");
		log.debug("DEBUG");
		log.info("INFO");
		log.warn("WARN");
		log.error("ERROR");
	}
	
	public static void main(String[] args) {
		final LogManager logManager = LogManager.getLogManager();

        // Programmatic configuration
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-7s [%3$s] (%2$s) %5$s %6$s%n");

        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new SimpleFormatter());
        
        final Logger root = Logger.getLogger("");
        root.setLevel(Level.ALL);
        root.addHandler(consoleHandler);

        final Logger app = Logger.getLogger("io.github.lomtalay");
        app.setLevel(Level.FINEST);
        app.addHandler(consoleHandler);
        
        MainClass mainClass = new MainClass();
        mainClass.doAction();
	}
}
