import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import static java.time.temporal.ChronoField.*;

public class JavaTime {

    void instant() {
        // tag::instant[]
        var instant = Instant.now();
        instant.toString();
        // 2019-12-04T19:47:16.090017700Z

        // end::instant[]
    }

    void localDate() {
        // tag::localDate[]
        var localDate = LocalDate.now();
        localDate.toString(); // 2019-12-04

        // end::localDate[]
    }

    void localDateTime() {
        // tag::localDateTime[]
        var localDateTime = LocalDateTime.now();
        localDateTime.toString();
        // 2019-12-04T20:47:16.231363900

        // end::localDateTime[]
    }

    void localTime() {
        // tag::localTime[]
        var localTime = LocalTime.now();
        localTime.toString(); // 20:47:16.232363100

        // end::localTime[]
    }

    void offsetDateTime() {
        // tag::offsetDateTime[]
        var offsetDateTime = OffsetDateTime.now();
        offsetDateTime.toString();
        // 2019-12-04T20:47:16.233362900+01:00

        // end::offsetDateTime[]
    }

    void offsetTime() {
        // tag::offsetTime[]
        var offsetTime = OffsetTime.now();
        offsetTime.toString();
        // 20:47:16.234362400+01:00

        // end::offsetTime[]
    }

    void zonedDateTime() {
        // tag::zonedDateTime[]
        var zonedDateTime = ZonedDateTime.now();
        zonedDateTime.toString();
        // 2019-12-04T20:47:16.236364500+01:00[Europe/Berlin]

        // end::zonedDateTime[]
    }

    void isoFormat() {
        // tag::defaultFormat[]
        TemporalAccessor ta = Instant.now();
        var text = DateTimeFormatter.ISO_INSTANT.format(ta);
        // 2019-12-05T20:28:37.257047300Z
        ta = DateTimeFormatter.ISO_INSTANT.parse(text);

        // DateTimeFormatter.BASIC_ISO_DATE
        // DateTimeFormatter.ISO_WEEK_DATE
        // DateTimeFormatter.RFC_1123_DATE_TIME
        // end::defaultFormat[]
    }

    void customFormat() {
        // tag::customFormat[]
        // import static java.time.temporal.ChronoField.*;
        var dtf = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendValue(HOUR_OF_DAY, 2).appendLiteral(":")
                .appendValue(MINUTE_OF_HOUR, 2)
                .appendLiteral(" Uhr am ")
                .appendValue(DAY_OF_MONTH, 2).appendLiteral('.')
                .appendValue(MONTH_OF_YEAR, 2).appendLiteral('.')
                .appendValue(YEAR, 4).toFormatter();
        dtf.format(LocalDateTime.now());
        // 22:05 Uhr am 05.12.2019
        
        dtf.parse("18:30 uhr am 10.12.2019");
        // end::customFormat[]
    }

    public static void main(String... args) {
        var javaTime = new JavaTime();
        javaTime.instant();
        javaTime.localDate();
        javaTime.localDateTime();
        javaTime.localTime();
        javaTime.offsetDateTime();
        javaTime.offsetTime();
        javaTime.zonedDateTime();
        javaTime.isoFormat();
        javaTime.customFormat();
    }
}