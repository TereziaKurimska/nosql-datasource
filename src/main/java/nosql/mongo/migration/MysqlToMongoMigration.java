package nosql.mongo.migration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import nosql.aislike.DaoFactory;
import nosql.mongo.config.MongoContext;
import nosql.mongo.repository.StudentMongoRepository;

/**
 * Nacita studentov z MySQL (cez existujuci nosql-datasource - DaoFactory/StudentDao)
 * a ulozi ich do MongoDB kolekcie "students" (embedded studium + studijny program).
 *
 * Pred spustenim overujem, ze:
 *   1) bezi lokalny MySQL (docker compose up -d) naplneny cez aislike.sql,
 *   2) bezi lokalny MongoDB (docker compose up -d),
 *   3) DaoFactory.getStudentDao() pouziva getJDBCTemplate2() (lokalny docker MySQL).
 */
public class MysqlToMongoMigration {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;
    // Realne data v tejto databaze su vo formate "d.M.yyyy", napr. "1.9.1998"
    private static final DateTimeFormatter SK_FORMAT = DateTimeFormatter.ofPattern("d.M.yyyy");

    public static void main(String[] args) {
        long count = migrate();
        System.out.println("Migrovanych studentov do MongoDB: " + count);
    }

    /**
     * Precita vsetkych studentov z MySQL a ulozi ich do Mongo.
     * Vracia pocet ulozenych studentov.
     */
    public static long migrate() {
        List<nosql.aislike.entity.Student> mysqlStudents = DaoFactory.INSTANCE.getStudentDao().getAll();

        List<nosql.mongo.entity.StudentDocument> documents = new ArrayList<>();
        for (nosql.aislike.entity.Student s : mysqlStudents) {
            documents.add(toDocument(s));
        }

        StudentMongoRepository repository = MongoContext.INSTANCE.getStudentRepository();
        repository.saveAll(documents);
        return documents.size();
    }

    private static nosql.mongo.entity.StudentDocument toDocument(nosql.aislike.entity.Student s) {
        nosql.mongo.entity.StudentDocument doc = new nosql.mongo.entity.StudentDocument();
        doc.setId(String.valueOf(s.getId()));
        doc.setMeno(s.getMeno());
        doc.setPriezvisko(s.getPriezvisko());
        doc.setKodpohlavie(s.getKodpohlavie());
        doc.setSkratkaakadtitul(s.getSkratkaakadtitul());

        List<nosql.mongo.entity.Studium> studiumList = new ArrayList<>();
        for (nosql.aislike.entity.Studium st : s.getStudium()) {
            nosql.mongo.entity.Studium mst = new nosql.mongo.entity.Studium();
            mst.setId(st.getId());
            mst.setZaciatokStudia(parseDate(st.getZaciatokStudia()));
            mst.setKoniecStudia(parseDate(st.getKoniecStudia()));

            nosql.aislike.entity.StudijnyProgram sp = st.getStudijnyProgram();
            if (sp != null) {
                mst.setStudijnyProgram(new nosql.mongo.entity.StudijnyProgram(sp.getId(), sp.getSkratka(), sp.getPopis()));
            }
            studiumList.add(mst);
        }
        doc.setStudium(studiumList);
        return doc;
    }

    /**
     * MySQL vracia datumy ako orezany String. V tejto databaze su vo formate
     * "d.M.yyyy" (napr. "1.9.1998"), niekedy s casom za medzerou (napr. "1.9.1998 0:00:00").
     * Skusime aj alternativny ISO format (yyyy-MM-dd), ak by sa format niekedy zmenil.
     * Prazdny/neplatny retazec => null (studium bez konca a pod.).
     */
    private static LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        // odstranime pripadny casovy suffix za medzerou ("1.9.1998 0:00:00" -> "1.9.1998")
        String candidate = raw.trim().split("\\s+")[0];

        try {
            return LocalDate.parse(candidate, SK_FORMAT);
        } catch (DateTimeParseException ignored) {
        }
        try {
            String isoCandidate = candidate.length() >= 10 ? candidate.substring(0, 10) : candidate;
            return LocalDate.parse(isoCandidate, ISO);
        } catch (DateTimeParseException e) {
            System.err.println("Neviem naparsovat datum '" + raw + "', ukladam ako null.");
            return null;
        }
    }
}
