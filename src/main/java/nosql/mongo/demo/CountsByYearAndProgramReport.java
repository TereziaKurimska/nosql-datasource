package nosql.mongo.demo;

import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;

import nosql.mongo.config.MongoContext;

/**
 * Uloha 4: tabulka poctov studentov podla rokov a studijnych programov.
 *
 * "Rok" = rok zaciatku studia (studium.zaciatokStudia). Jeden student, ktory ma
 * viac studii, sa zaratava do kazdeho roku/programu, v ktorom nejake studium zacal
 * (preto najprv $unwind na pole studium).
 */
public class CountsByYearAndProgramReport {

    public static void main(String[] args) {
        MongoTemplate template = MongoContext.INSTANCE.getMongoTemplate();

        ProjectionOperation project = Aggregation.project()
                .and("studium.studijnyProgram.skratka").as("program")
                .and("studium.zaciatokStudia").extractYear().as("rok");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.unwind("studium"),
                project,
                Aggregation.group("rok", "program").count().as("pocet"),
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "_id.rok").and(Sort.by(Sort.Direction.ASC, "_id.program")))
        );

        AggregationResults<Document> results = template.aggregate(aggregation, "students", Document.class);
        List<Document> rows = results.getMappedResults();

        System.out.printf("%-8s %-15s %s%n", "Rok", "Program", "Pocet studentov");
        System.out.println("---------------------------------------------");
        for (Document row : rows) {
            Document id = (Document) row.get("_id");
            Object rok = id.get("rok");
            Object program = id.get("program");
            Object pocet = row.get("pocet");
            System.out.printf("%-8s %-15s %s%n", rok, program, pocet);
        }
        System.out.println("---------------------------------------------");
        System.out.println("Spolu riadkov: " + rows.size());
    }
}
