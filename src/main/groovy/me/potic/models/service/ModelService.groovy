package me.potic.models.service

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.gridfs.GridFSFile
import groovy.util.logging.Slf4j
import me.potic.models.domain.Model
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.nio.charset.Charset
import java.time.LocalDate

import static org.springframework.data.mongodb.core.query.Criteria.where

@Service
@Slf4j
class ModelService {

    @Autowired
    RankerService rankerService

    @Autowired
    MongoTemplate mongoTemplate

    @Autowired
    GridFsTemplate gridFsTemplate

    @Scheduled(fixedDelay = 3600_000L) // every 1 hour
    void checkActiveModels() {
        log.debug 'checking active models...'

        List<Model> existingModels = getAllModels()
        List<Model> activeModels = rankerService.models()

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
            throw new RuntimeException("upserting model ${model} failed", e)
        }
    }

    String storeSerializedModel(Model model, String serializedModel) {
        log.debug "storing serialized model for ${model}..."

        try {
            DBObject metadata = new BasicDBObject()
            metadata.put('model_name', model.name)
            metadata.put('model_version', model.version)
            metadata.put('model_trainTimestamp', model.trainTimestamp)

            GridFSFile file = gridFsTemplate.store(IOUtils.toInputStream(serializedModel, Charset.defaultCharset()), metadata)

            return file.id.toString()
        } catch (e) {
            log.error "storing serialized model for ${model} failed: $e.message", e
            throw new RuntimeException("storing serialized model for ${model} failed", e)
        }
    }

    void deleteSerializedModel(String serializedModelId) {
        log.debug "deleting serialized model #${serializedModelId}..."

        try {
            gridFsTemplate.delete(Query.query(where('_id').is(serializedModelId)))
        } catch (e) {
            log.error "deleting serialized model #${serializedModelId} failed: $e.message", e
            throw new RuntimeException("deleting serialized model #${serializedModelId} failed", e)
        }
    }
}
