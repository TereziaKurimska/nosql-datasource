package nosql.mongo.entity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Jedno studium studenta - vnorene (embedded) v StudentDocument.studium.
 * Datumy su na rozdiel od povodnej MySQL DAO (kde su to orezane Stringy)
 * ulozene ako LocalDate, aby sa dali pouzit v dopytoch na rozsah rokov
 * (uloha c. 2) a v agregacii (uloha c. 4).
 */
public class Studium implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private LocalDate zaciatokStudia;
    private LocalDate koniecStudia;
    private StudijnyProgram studijnyProgram;

    public Studium() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getZaciatokStudia() {
        return zaciatokStudia;
    }

    public void setZaciatokStudia(LocalDate zaciatokStudia) {
        this.zaciatokStudia = zaciatokStudia;
    }

    public LocalDate getKoniecStudia() {
        return koniecStudia;
    }

    public void setKoniecStudia(LocalDate koniecStudia) {
        this.koniecStudia = koniecStudia;
    }

    public StudijnyProgram getStudijnyProgram() {
        return studijnyProgram;
    }

    public void setStudijnyProgram(StudijnyProgram studijnyProgram) {
        this.studijnyProgram = studijnyProgram;
    }

    @Override
    public String toString() {
        return "Studium{id=" + id + ", zaciatokStudia=" + zaciatokStudia + ", koniecStudia=" + koniecStudia
                + ", studijnyProgram=" + studijnyProgram + "}";
    }
}
