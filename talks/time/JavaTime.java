import java.time.*;

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

    public static void main(String... args) {
        var javaTime = new JavaTime();
        javaTime.instant();
        javaTime.localDate();
        javaTime.localDateTime();
        javaTime.localTime();
        javaTime.offsetDateTime();
        javaTime.offsetTime();
        javaTime.zonedDateTime();
    }
}