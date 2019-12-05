import java.time.*;

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

    public static void main(String... args) {
        var calc = new Calc();
        calc.simple();
        calc.advanced();
    }
}