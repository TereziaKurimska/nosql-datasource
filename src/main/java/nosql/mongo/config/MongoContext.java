package nosql.mongo.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import nosql.mongo.repository.StudentMongoRepository;

/**
 * Jednoduchy Spring kontext pre Mongo cast projektu,
 * podobne ako DaoFactory pre MySQL cast,
 * rucne vytvorenie AnnotationConfigApplicationContext nad MongoConfig.
 *
 * Pouzivame:
 *   StudentMongoRepository repo = MongoContext.INSTANCE.getStudentRepository();
 *   MongoTemplate template = MongoContext.INSTANCE.getMongoTemplate();
 */
public enum MongoContext {
    INSTANCE;

    private AnnotationConfigApplicationContext context;

    private synchronized AnnotationConfigApplicationContext getContext() {
        if (context == null) {
            context = new AnnotationConfigApplicationContext(MongoConfig.class);
        }
        return context;
    }

    public StudentMongoRepository getStudentRepository() {
        return getContext().getBean(StudentMongoRepository.class);
    }

    public MongoTemplate getMongoTemplate() {
        return getContext().getBean(MongoTemplate.class);
    }
}
