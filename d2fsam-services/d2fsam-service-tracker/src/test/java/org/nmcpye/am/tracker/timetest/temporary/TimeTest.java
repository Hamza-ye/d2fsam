package org.nmcpye.am.tracker.timetest.temporary;

import org.junit.jupiter.api.Test;
import org.nmcpye.am.util.DateUtils;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.zone.ZoneOffsetTransition;
import java.util.Calendar;
import java.util.Date;

import static java.time.ZoneId.systemDefault;
import static java.time.ZonedDateTime.now;

public class TimeTest {
    @Test
    void testAllEmpty() {
        // Our System's current time is = Tue Jan 10 21:11:28 AST 2023
        // Now time in system's local time = Tue Jan 10 21:11:28 AST 2023
        Date nowDate = new Date();

        // Now time in UTC/Greenwich time = 2023-01-10T18:11:28.329739100Z
        Instant nowInstant = Instant.now();

        Calendar calendar = Calendar.getInstance();

        // Now time in system's local time = Tue Jan 10 21:11:28 AST 2023
        Date dateInCalender = Calendar.getInstance().getTime();
        calendar.setTime(nowDate);


        // the passed time in calender/system's
        // local time = Tue Jan 10 21:11:28 AST 2023
        Date nowDateInCalender = calendar.getTime();

        calendar.add(Calendar.HOUR_OF_DAY, 5);

        // Wed Jan 11 02:11:28 AST 2023
        Date nowCalenderPlus5H = calendar.getTime();
        Date dateOfNowInstant = java.util.Date.from(nowInstant);

        Date nowDateLast = new Date();
        // Now time in localTime to Instant time
        // in UTC/Greenwich = 2023-01-10T18:11:28.329Z
        Instant nowDateToInstant = nowDate.toInstant();

        // Now time in localTime to Instant time in
        // UTC/Greenwich same as previous one = 2023-01-10T18:11:28.329Z
        Instant instantFromNowDateEpoc = Instant.ofEpochMilli(nowDate.getTime());

        // 2023-01-10T23:11:28.329739100Z
        Instant nowInstantPlus5H = nowInstant
            .plus(5, ChronoUnit.HOURS);

        // 2023-01-10T21:11:28.460908300+03:00[Asia/Baghdad]
        ZonedDateTime nowZonedDateTime = ZonedDateTime.now();

        // 2023-01-10T21:11:28.329+03:00[Asia/Baghdad]
        ZonedDateTime nowDateAsZonedDateTime = Instant
            .ofEpochMilli(nowDate.getTime())
            .atZone(ZoneId.systemDefault());


        LocalDateTime localDateTime = nowInstant
            .atZone(ZoneId.systemDefault()).toLocalDateTime();
        Instant i;
        ZoneOffsetTransition transition = ZoneId.of("UTC").getRules().getTransition(localDateTime);
        if (transition == null) {
            i = localDateTime.toInstant(ZoneId.of("UTC").getRules().getOffset(localDateTime));
        } else {
            i = localDateTime.toInstant(transition.getOffsetAfter());
        }
        Instant newInstant = localDateTime
            .toInstant(ZoneId.of("UTC")
                .getRules().getStandardOffset(i));


//        Instant newInstant = nowlocalDateTimeAsZonedDateTime.toInstant();


        // Will get the instant time (UTC/Greenwich) in current System Zone time
        // 2023-01-10T21:11:28.884+03:00[Asia/Baghdad]
        ZonedDateTime nowInstantAsZonedDateTime = Instant
            .ofEpochMilli(nowInstant.toEpochMilli())
            .atZone(ZoneId.systemDefault());

        String enrollmentDate = LocalDate.now().plus( 2, ChronoUnit.DAYS ).toString();

        ///////////////////////////////
        ZonedDateTime now1 = ZonedDateTime.now();
        Date twoMonthsAgo1 = Date.from( now1.minusMonths( 2 ).toInstant() );
        Date threeMonthAgo1 = Date.from( now1.minusMonths( 3 ).toInstant() );
        Date fourMonthAgo1 = Date.from( now1.minusMonths( 4 ).toInstant() );
        Date twentyTwoDaysAgo1 = Date.from( now1.minusDays( 22 ).toInstant() );

        ZonedDateTime now = ZonedDateTime.now();
        LocalDateTime twoMonthsAgo = now.minusMonths(2).toLocalDateTime();
        LocalDateTime threeMonthAgo = now.minusMonths(3).toLocalDateTime();
        LocalDateTime fourMonthAgo = now.minusMonths(4).toLocalDateTime();
        LocalDateTime twentyTwoDaysAgo = now.minusDays(22).toLocalDateTime();

        LocalDateTime localDateTime2 = nowInstant
            .atZone(ZoneId.systemDefault()).toLocalDateTime();

        var time1 = now();
        var time2 = ZonedDateTime.ofInstant(
            DateUtils.instantFromLocalDateTime(localDateTime2), systemDefault()).plusMonths(1);

        var time3 = now();
        var time4 = ZonedDateTime.of(localDateTime2, systemDefault()).plusMonths(1);

        var time5 = ZonedDateTime.of(twoMonthsAgo, systemDefault()).plusMonths(1).isAfter(now());
    }
}
