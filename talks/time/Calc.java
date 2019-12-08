import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class Calc {

    void simple() {
        // tag::plusMinus[]
        var now = LocalDateTime.now();
        var nextWeek = now.plusDays(7); // Immutable
        var yesterday = now.minusHours(24);
        // end::plusMinus[]
    }

    void advanced() {
        // tag::advanced[]
        var duration = Duration.ofDays(5).plus(Duration.ofHours(2));
        var sometimes = Instant.now().plus(duration);
        sometimes.isAfter(Instant.now());
        Duration.between(Instant.now(), sometimes);
        // PT121H59M59.9990012S

        Period.between(LocalDate.now(), LocalDate.of(2019, 12, 24));
        // P19D
        // end::advanced[]
    }

    void betweenOffset() {
        // tag::betweenOffset[]
        var sixMonth = Period.ofMonths(6);

        var odtNow = OffsetDateTime.now();
        // 2019-12-06T20:39:54.717348800+01:00

        odtNow.plus(sixMonth).toString();
        // 2020-06-06T20:39:54.717348800+01:00
        // end::betweenOffset[]
    }

    void zonedOffset() {
        // tag::zonedOffset[]
        var sixMonth = Period.ofMonths(6);

        var zdt = ZonedDateTime.now();
        zdt.toOffsetDateTime().toString();
        // 2019-12-06T20:50:36.461772200+01:00

        zdt.plus(sixMonth).toOffsetDateTime();
        // 2020-06-06T20:50:36.461772200+02:00
        // end::zonedOffset[]
    }

    void converting() {
        // tag::converting[]
        var instant = Instant.now();
        ZonedDateTime zdt = instant.atZone(ZoneId.of("Europe/Berlin"));
        LocalDateTime ldt = zdt.toLocalDateTime();
        OffsetDateTime odt = ldt.atOffset(ZoneOffset.ofHours(4));
        zdt = odt.atZoneSameInstant(ZoneId.systemDefault());
        zdt = odt.atZoneSimilarLocal(ZoneId.systemDefault());
        zdt.toInstant();
        // end::converting[]
    }

    public static void main(String... args) {
        var calc = new Calc();
        calc.simple();
        calc.advanced();
        calc.betweenOffset();
        calc.zonedOffset();
        calc.converting();
    }
}