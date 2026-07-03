package nosql.mongo.repository;

/**
 * Projekcia (uloha 1) - vrati iba meno a priezvisko, nie cely dokument.
 * Spring Data MongoDB si podla tohto rozhrania sam vytvori dopyt,
 * ktory na urovni MongoDB vyberie (projektuje) len polia "meno" a "priezvisko".
 */
public interface StudentNameProjection {
    String getMeno();
    String getPriezvisko();
}
