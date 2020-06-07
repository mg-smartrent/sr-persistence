package com.mg.persistence.unit.service

import com.mg.persistence.unit.UnitTestSetup
import com.mg.persistence.service.Repository
import com.mg.persistence.service.nosql.MongoRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class MongoRepositoryTest extends UnitTestSetup {

    private static final Query query = new Query(Criteria.where("id").exists(true))

    def "test: if is mongo instance"() {
        given:
        Repository<Object> queryService = new MongoRepository<>()
        expect:
        (queryService instanceof MongoRepository<Object>)
    }


    def "test: is mongo instance"() {
        setup:
        MongoTemplate template = Stub(MongoTemplate.class)

        when:
        Repository<Object> queryService = new MongoRepository<>(template)
        template.find(_, _, _) >> Arrays.asList(new Object())
        template.count(_, _, _) >> 1
        template.save(_) >> new Object()

        then:
        queryService.findAll(query, Object.class).size() == 1
        queryService.findOneBy("id", "test", Object.class) != null
        queryService.findAllBy("id", "test", Object.class).size() == 1
        queryService.count(query, Object.class) == 1
        queryService.save(new Object()) != null
    }

}
