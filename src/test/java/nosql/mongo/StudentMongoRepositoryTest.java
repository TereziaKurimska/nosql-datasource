package nosql.mongo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import nosql.mongo.config.MongoContext;
import nosql.mongo.entity.StudentDocument;
import nosql.mongo.entity.StudijnyProgram;
import nosql.mongo.entity.Studium;
import nosql.mongo.repository.StudentMongoRepository;
import nosql.mongo.repository.StudentNameProjection;

/**
 * Uloha 1: zakladna funkcionalita repozitara.
 */
class StudentMongoRepositoryTest {

    private final StudentMongoRepository repository = MongoContext.INSTANCE.getStudentRepository();

    @Test
    void saveFindDelete() {
        StudentDocument student = sampleStudent("test-crud-1", "Janka", "Testovacia", "Bc.");

        repository.save(student);

        Optional<StudentDocument> found = repository.findById("test-crud-1");
        assertTrue(found.isPresent());
        assertEquals("Janka", found.get().getMeno());
        assertEquals("Testovacia", found.get().getPriezvisko());
        assertEquals(1, found.get().getStudium().size());
        assertEquals("INF", found.get().getStudium().get(0).getStudijnyProgram().getSkratka());

        repository.deleteById("test-crud-1");
        assertFalse(repository.findById("test-crud-1").isPresent());
    }

    @Test
    void projectionBySkratkaAkadTitul() {
        repository.save(sampleStudent("test-proj-1", "Peter", "Prvy", "Mgr."));
        repository.save(sampleStudent("test-proj-2", "Zuzana", "Druha", "Mgr."));

        try {
            List<StudentNameProjection> mgrStudenti = repository.findBySkratkaakadtitul("Mgr.");

            assertTrue(mgrStudenti.size() >= 2);
            boolean obsahujePetra = mgrStudenti.stream()
                    .anyMatch(p -> "Peter".equals(p.getMeno()) && "Prvy".equals(p.getPriezvisko()));
            boolean obsahujeZuzanu = mgrStudenti.stream()
                    .anyMatch(p -> "Zuzana".equals(p.getMeno()) && "Druha".equals(p.getPriezvisko()));
            assertTrue(obsahujePetra);
            assertTrue(obsahujeZuzanu);
        } finally {
            repository.deleteById("test-proj-1");
            repository.deleteById("test-proj-2");
        }
    }

    /**
     * Tento test predpoklada, ze uz predtym prebehla migracia
     * (nosql.mongo.migration.MysqlToMongoMigration.migrate()).
     * Ak kolekcia este nie je naplnena, test sa preskoci (assumeTrue).
     */
    @Test
    void findAllAfterMigration() {
        long count = repository.count();
        assumeTrue(count > 0, "Kolekcia 'students' je prazdna - najprv spusti MysqlToMongoMigration.");

        Iterable<StudentDocument> all = repository.findAll();
        assertNotNull(all);
        assertTrue(all.iterator().hasNext());
    }

    private StudentDocument sampleStudent(String id, String meno, String priezvisko, String titul) {
        StudentDocument student = new StudentDocument();
        student.setId(id);
        student.setMeno(meno);
        student.setPriezvisko(priezvisko);
        student.setKodpohlavie('Z');
        student.setSkratkaakadtitul(titul);

        StudijnyProgram program = new StudijnyProgram(1L, "INF", "Informatika");
        Studium studium = new Studium();
        studium.setId(1L);
        studium.setZaciatokStudia(LocalDate.of(2022, 9, 1));
        studium.setKoniecStudia(null);
        studium.setStudijnyProgram(program);

        student.setStudium(List.of(studium));
        return student;
    }
}
