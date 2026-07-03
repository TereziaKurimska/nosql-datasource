package nosql.mongo.entity;

import java.io.Serializable;

/**
 * Studijny program - vnorena (embedded) trieda vnutri Studium.
 * V Mongo teda NIE JE samostatna kolekcia, ale je ulozena priamo
 * v dokumente studenta.
 */
public class StudijnyProgram implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String skratka;
    private String popis;

    public StudijnyProgram() {
    }

    public StudijnyProgram(Long id, String skratka, String popis) {
        this.id = id;
        this.skratka = skratka;
        this.popis = popis;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkratka() {
        return skratka;
    }

    public void setSkratka(String skratka) {
        this.skratka = skratka;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    @Override
    public String toString() {
        return "StudijnyProgram{id=" + id + ", skratka='" + skratka + "', popis='" + popis + "'}";
    }
}
