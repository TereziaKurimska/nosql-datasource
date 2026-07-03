package nosql.mongo.demo;

import java.util.List;

import nosql.mongo.config.MongoContext;
import nosql.mongo.entity.StudentDocument;
import nosql.mongo.repository.StudentMongoRepository;

/**
 * Uloha 2: zobrazenie vsetkych studentov, ktori v danom roku studuju
 * dany studijny program.
 */
public class ProgramYearQueryDemo {

    public static void main(String[] args) {
        String programSkratka = args.length > 0 ? args[0] : "MCH";
        int rok = args.length > 1 ? Integer.parseInt(args[1]) : 2000;

        StudentMongoRepository repository = MongoContext.INSTANCE.getStudentRepository();
        List<StudentDocument> studenti = repository.findByProgramAndYear(programSkratka, rok);

        System.out.printf("Studenti programu '%s' v roku %d: %d%n", programSkratka, rok, studenti.size());
        for (StudentDocument s : studenti) {
            System.out.printf(" - %s %s (%s), id=%s%n", s.getMeno(), s.getPriezvisko(), s.getSkratkaakadtitul(), s.getId());
        }
    }
}
