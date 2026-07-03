package nosql.mongo.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Student ulozeny v MongoDB - "students" kolekcia.
 * Studium aj studijny program su ulozene priamo v dokumente (embedded),
 * ("student ma ulozene aj studium a studijny program").
 * Ako _id pouzivame rovnake id, ake ma osoba v MySQL (osoba.id), len ako String -
 * vdaka tomu vieme jednoducho parovat zaznamy medzi MySQL a Mongo.
 */
@Document(collection = "students")
public class StudentDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String meno;
    private String priezvisko;
    private Character kodpohlavie;

    // Podla akademickeho titulu sa bude robit projekcia (uloha 1) - hodi sa index.
    @Indexed
    private String skratkaakadtitul;

    private List<Studium> studium = new ArrayList<>();

    public StudentDocument() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeno() {
        return meno;
    }

    public void setMeno(String meno) {
        this.meno = meno;
    }

    public String getPriezvisko() {
        return priezvisko;
    }

    public void setPriezvisko(String priezvisko) {
        this.priezvisko = priezvisko;
    }

    public Character getKodpohlavie() {
        return kodpohlavie;
    }

    public void setKodpohlavie(Character kodpohlavie) {
        this.kodpohlavie = kodpohlavie;
    }

    public String getSkratkaakadtitul() {
        return skratkaakadtitul;
    }

    public void setSkratkaakadtitul(String skratkaakadtitul) {
        this.skratkaakadtitul = skratkaakadtitul;
    }

    public List<Studium> getStudium() {
        return studium;
    }

    public void setStudium(List<Studium> studium) {
        this.studium = studium;
    }

    @Override
    public String toString() {
        return "StudentDocument{id='" + id + "', meno='" + meno + "', priezvisko='" + priezvisko
                + "', kodpohlavie=" + kodpohlavie + ", skratkaakadtitul='" + skratkaakadtitul
                + "', studium=" + studium + "}";
    }
}
