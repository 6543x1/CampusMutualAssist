package com.jessie.campusmutualassist.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BannedUserException extends RuntimeException {
    private static Logger logger = LoggerFactory.getLogger(BannedUserException.class);

    public BannedUserException() {
        logger.info("A banned user was denied");
    }
}
