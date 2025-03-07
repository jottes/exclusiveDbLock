package de.convince.exclusiveDbLock;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class LockAdvice {
    

    @ExceptionHandler
    public void handleException(SQLException e) {
        LoggerFactory.getLogger(LockAdvice.class).error("Error in LockController", e);
    }

    @ExceptionHandler
    public void handleException(NoSuchElementException e) {
        LoggerFactory.getLogger(LockAdvice.class).error("Keine neuen Steuerungseinträge mehr vorhanden!");
    }

}