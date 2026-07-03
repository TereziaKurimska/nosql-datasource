package nosql.mongo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nosql.mongo.entity.StudentDocument;

/**
 * Uloha 1: repozitar triedy Student postaveny na Spring Data CrudRepository.
 * Zakladne CRUD operacie (save, findById, findAll, deleteById, ...) ziskavame
 * uplne zadarmo len tym, ze rozsirujeme CrudRepository<StudentDocument, String>.
 * Nizsie je repozitar rozsireny o:
 *   - projekciu mena a priezviska podla akademickeho titulu,
 *   - dopyt "studenti daneho studijneho programu v danom roku" (uloha 2).
 */
public interface StudentMongoRepository extends CrudRepository<StudentDocument, String> {

    /**
     * Uloha 1 - rozsirenie: vrati LEN meno a priezvisko (projekcia) vsetkych
     * studentov s danym akademickym titulom (napr. "Bc.", "Mgr.", "Ing.").
     * Spring Data vygeneruje dopyt automaticky z nazvu metody (derived query)
     * a vdaka typu navratovej hodnoty (StudentNameProjection) posle na MongoDB
     * iba polia meno a priezvisko namiesto celeho dokumentu.
     */
    List<StudentNameProjection> findBySkratkaakadtitul(String skratkaakadtitul);

    /**
     * Uloha 2 - studenti, ktori v obdobi <yearStart, yearEnd> studuju studijny
     * program s danou skratkou. Student "studuje" v danom roku, ak jeho studium
     * zacalo najneskor na konci daneho obdobia A (studium este nekonci ALEBO
     * koncí az po zaciatku daneho obdobia).
     *
     * Pouzivame @Query (rucny MongoDB dopyt), lebo derived-query nazov metody
     * by pre tento typ rozsahoveho porovnania dvoch datumov bol necitatelny.
     */
    @Query("{ 'studium': { $elemMatch: { "
            + "'studijnyProgram.skratka': ?0, "
            + "'zaciatokStudia': { $lte: ?2 }, "
            + "$or: [ { 'koniecStudia': null }, { 'koniecStudia': { $gte: ?1 } } ] "
            + "} } }")
    List<StudentDocument> findByProgramAndYearRange(String programSkratka, LocalDate yearStart, LocalDate yearEnd);

    /**
     * Pohodlnejsia verzia predchadzajucej metody - stanovi len rok (napr. 2023)
     * a sama si z neho spocita 1.1.-31.12. daneho roka.
     */
    default List<StudentDocument> findByProgramAndYear(String programSkratka, int rok) {
        return findByProgramAndYearRange(programSkratka, LocalDate.of(rok, 1, 1), LocalDate.of(rok, 12, 31));
    }
}
