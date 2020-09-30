package com.mg.persistence.unit.service

import com.mg.persistence.data.Change
import com.mg.persistence.service.nosql.MongoTrackingChangeService
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
        Repository<Change> queryService = new MongoRepository<>(null, null)
        expect:
        (queryService instanceof MongoRepository<Change>)
    }


    def "test: repository API"() {
        setup:
        MongoTemplate template = Stub(MongoTemplate.class)
        MongoTrackingChangeService changeService = Mock(MongoTrackingChangeService.class)

        when:
        Repository<Change> queryService = new MongoRepository<>(template, changeService)
        template.find(_, _, _) >> Arrays.asList(new Change())
        template.count(_, _, _) >> 1
        template.save(_, _) >> new Change()

        then:
        queryService.findAll(query, Change.class, "coll").size() == 1
        queryService.findOneBy("id", "test", Change.class, "coll") != null
        queryService.findAllBy("id", "test", Change.class, "coll").size() == 1
        queryService.count(query, Change.class, "coll") == 1
        queryService.save(new Change(), Change.class, "coll") != null
    }

}
