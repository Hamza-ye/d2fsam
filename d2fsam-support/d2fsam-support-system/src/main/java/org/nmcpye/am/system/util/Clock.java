package org.nmcpye.am.system.util;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing stopwatch-like functionality.
 */
public class Clock extends StopWatch {

    private static final String SEPARATOR = ": ";

    private static final Logger defaultLog = LoggerFactory.getLogger(Clock.class);

    private Logger log;

    /**
     * Create a new instance.
     */
    public Clock() {
        super();
    }

    /**
     * Create a new instance with a given logger.
     *
     * @param log the logger.
     */
    public Clock(Logger log) {
        super();
        this.log = log;
    }

    /**
     * Start the clock.
     *
     * @return the Clock.
     */
    public Clock startClock() {
        this.start();

        return this;
    }

    /**
     * Yields the elapsed time since the Clock was started as an HMS String.
     *
     * @return the elapsed time.
     */
    public String time() {
        super.split();

        return DurationFormatUtils.formatDurationHMS(super.getSplitTime());
    }

    /**
     * Timestamps the given message using the elapsed time of this Clock and
     * logs it using the logger.
     *
     * @param message the message to log.
     * @return this Clock.
     */
    public Clock logTime(String message) {
        super.split();

        String time = DurationFormatUtils.formatDurationHMS(super.getSplitTime());

        String msg = message + SEPARATOR + time;

        if (log != null) {
            log.info(msg);
        } else {
            defaultLog.info(msg);
        }

        return this;
    }
}
