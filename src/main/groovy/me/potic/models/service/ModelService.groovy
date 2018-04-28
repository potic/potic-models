package me.potic.models.service

import groovy.util.logging.Slf4j
import me.potic.models.domain.Model
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.time.LocalDate

@Service
@Slf4j
class ModelService {

    @Autowired
    RankerService rankerService

    @Autowired
    MongoTemplate mongoTemplate

    @Scheduled(fixedDelay = 30_000L)
    void checkActiveModels() {
        log.info 'checking active models...'

        List<Model> existingModels = getAllModels()
        List<Model> activeModels = rankerService.ranks()

        List<Model> allModels = existingModels.collect({ model -> model.isActive = false; model })

        activeModels.forEach({ activeModel ->
            Model existingModel = allModels.find { model -> model.name == activeModel.name && model.version == activeModel.version }
            if (existingModel != null) {
                existingModel.description = activeModel.description
                existingModel.isActive = true
            } else {
                log.info "new model found ${activeModel}"
                activeModel.trainTimestamp = LocalDate.now().toString()
                activeModel.isActive = true
                allModels.add(activeModel)
            }
        })

        allModels.forEach({ upsertModel(it) })
    }

    Model getActualModel() {
        getAllModels().find { model -> model.name == 'nbayes' && model.version == '1.0' }
    }

    List<Model> getActiveModels() {
        getAllModels().findAll({ it.isActive })
    }

    List<Model> getAllModels() {
        log.debug 'finding all models...'

        try {
            return mongoTemplate.findAll(Model)
        } catch (e) {
            log.error "finding all models failed: $e.message", e
            throw new RuntimeException('finding all models failed', e)
        }
    }

    Model upsertModel(Model model) {
        log.debug "upserting model ${model}..."

        try {
            mongoTemplate.save(model)
            return model
        } catch (e) {
            log.error "upserting model ${model} failed: $e.message", e
            throw new RuntimeException('upserting model ${model} failed', e)
        }
    }
}
