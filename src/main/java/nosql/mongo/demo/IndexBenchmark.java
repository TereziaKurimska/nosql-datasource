package nosql.mongo.demo;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.domain.Sort;

import com.mongodb.ExplainVerbosity;
import com.mongodb.client.MongoCollection;

import nosql.mongo.config.MongoContext;
import nosql.mongo.entity.StudentDocument;

/**
 * Uloha 3: vytvorenie indexu na studijne programy a porovnanie casu
 * vyhodnotenia dopytu z ulohy 2 PRED a PO vytvoreni indexu.
 *
 * Index vytvarame na "studium.studijnyProgram.skratka" (pole, podla ktoreho
 * sa v uloge 2 filtruje).
 *
 * Pre kazdy stav (bez indexu / s indexom) vypiseme:
 *   - vysledok z explain("executionStats"): totalDocsExamined, executionTimeMillis
 *   - priemerny "wall clock" cas z N opakovani dopytu
 */
public class IndexBenchmark {

    private static final String INDEXED_FIELD = "studium.studijnyProgram.skratka";
    private static final int OPAKOVANIA = 20;

    public static void main(String[] args) {
        String programSkratka = args.length > 0 ? args[0] : "MCH";
        int rok = args.length > 1 ? Integer.parseInt(args[1]) : 2000;

        MongoTemplate template = MongoContext.INSTANCE.getMongoTemplate();
        String filterJson = buildFilterJson(programSkratka, rok);

        System.out.println("Dopyt: " + filterJson);
        System.out.println();

        // 1) uistime sa, ze index NEexistuje - zmerame "studeny" stav
        dropIndexIfExists(template);
        System.out.println("=== BEZ INDEXU ===");
        meraj(template, filterJson);

        // 2) vytvorime index na studijny program
        template.indexOps(StudentDocument.class).createIndex(new Index().on(INDEXED_FIELD, Sort.Direction.ASC));
        System.out.println();
        System.out.println("=== S INDEXOM (" + INDEXED_FIELD + ") ===");
        meraj(template, filterJson);
    }

    private static String buildFilterJson(String programSkratka, int rok) {
        String yearStart = rok + "-01-01T00:00:00.000Z";
        String yearEnd = rok + "-12-31T00:00:00.000Z";
        return "{ 'studium': { $elemMatch: { "
                + "'studijnyProgram.skratka': '" + programSkratka + "', "
                + "'zaciatokStudia': { $lte: { $date: '" + yearEnd + "' } }, "
                + "$or: [ { 'koniecStudia': null }, { 'koniecStudia': { $gte: { $date: '" + yearStart + "' } } } ] "
                + "} } }";
    }

    private static void dropIndexIfExists(MongoTemplate template) {
        try {
            template.indexOps(StudentDocument.class).getIndexInfo().forEach(indexInfo -> {
                boolean naSpravnomPoli = indexInfo.getIndexFields().stream()
                        .anyMatch(f -> INDEXED_FIELD.equals(f.getKey()));
                if (naSpravnomPoli) {
                    template.indexOps(StudentDocument.class).dropIndex(indexInfo.getName());
                }
            });
        } catch (Exception e) {
            System.err.println("Index sa nepodarilo zrusit (mozno neexistoval): " + e.getMessage());
        }
    }

    private static void meraj(MongoTemplate template, String filterJson) {
        MongoCollection<Document> collection = template.getCollection("students");
        Document filter = Document.parse(filterJson);

        // a) explain - presny pocet prehladanych dokumentov a cas na strane MongoDB
        Document explain = collection.find(filter).explain(ExplainVerbosity.EXECUTION_STATS);
        Document executionStats = (Document) explain.get("executionStats");
        System.out.println("explain(): totalDocsExamined=" + executionStats.get("totalDocsExamined")
                + ", totalKeysExamined=" + executionStats.get("totalKeysExamined")
                + ", nReturned=" + executionStats.get("nReturned")
                + ", executionTimeMillis=" + executionStats.get("executionTimeMillis"));

        // b) jednoduche "wall clock" meranie z pohladu klienta (priemer z OPAKOVANIA behov)
        long start = System.nanoTime();
        for (int i = 0; i < OPAKOVANIA; i++) {
            collection.find(filter).into(new java.util.ArrayList<>());
        }
        long end = System.nanoTime();
        double priemerMs = (end - start) / 1_000_000.0 / OPAKOVANIA;
        System.out.printf("Priemerny cas dopytu (z %d opakovani): %.3f ms%n", OPAKOVANIA, priemerMs);
    }
}
