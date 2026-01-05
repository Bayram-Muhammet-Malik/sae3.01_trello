package appSAE;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class TestDateIteration4 {

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Test
    void testFormatDateAujourdhui() {
        String aujourdHui = formatter.format(LocalDate.now());

        // Vérifie le format : jj-mm-aaaa
        assertEquals(10, aujourdHui.length());
        assertTrue(aujourdHui.matches("\\d{2}-\\d{2}-\\d{4}"));
    }

    @Test
    void testDateEstAujourdhui() {
        String aujourdHui = formatter.format(LocalDate.now());
        String dateTache = formatter.format(LocalDate.now());

        assertEquals(aujourdHui, dateTache,
                "La date de la tâche devrait être aujourd'hui");
    }

    @Test
    void testDateNestPasAujourdhui() {
        String aujourdHui = formatter.format(LocalDate.now());
        String dateTache = "12-12-2025";

        assertNotEquals(aujourdHui, dateTache,
                "La date de la tâche ne doit pas être aujourd'hui");
    }
}
