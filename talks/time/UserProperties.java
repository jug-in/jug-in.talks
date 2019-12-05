public class UserProperties {

    /* System.getProperties()
    // tag::sysprops[]
        user.variant:
        user.timezone:
        user.country: DE
        user.language: de
    // end::sysprops[]
    */

     /* System.getenv()
    // tag::sysenv[]
        TZ: Europe/Berlin
        LANG: de_DE.UTF-8
    // end::sysenv[]
    */

    public static void main(String... args) {
        System.getProperties().forEach((k, v) -> {
            if (k.toString().startsWith("user.")) {
                System.out.println(k + ": " + v);
            }
        });
        System.getenv().forEach((k, v) -> {
                System.out.println(k + ": " + v);
        });
    }

}