package appSAE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestDate {
    public static void main(String[] args) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy");

        String aujourdHui = formatter.format(LocalDate.now());

        System.out.println("Aujourd'hui : " + aujourdHui);

        String dateTache = "12-12-2025";

        if (dateTache.equals(aujourdHui)) {
            System.out.println("La tâche est aujourd'hui");
        } else {
            System.out.println("La tâche n'est pas aujourd'hui");
        }
    }
}
