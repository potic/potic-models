package me.potic.models.service

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.models.domain.ArticleDataPoint
import me.potic.models.domain.Model
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@Slf4j
class RankerService {

    HttpBuilder rankerServiceRest

    @Autowired
    HttpBuilder rankerServiceRest(@Value('${services.ranker.url}') String rankerServiceUrl) {
        rankerServiceRest = HttpBuilder.configure {
            request.uri = rankerServiceUrl
        }
    }

    List<Model> models() {
        log.debug "requesting active models..."

        try {
            def response = rankerServiceRest.get {
                request.uri.path = "/model"
                request.contentType = 'application/json'
            }

            return response.collect({ new Model(it) })
        } catch (e) {
            log.error "requesting active models failed: $e.message", e
            throw new RuntimeException("requesting active models failed", e)
        }
    }

    String model(List<ArticleDataPoint> trainData, Model model) {
        log.debug "request to train model ${model}..."

        try {
            def response = rankerServiceRest.post {
                request.uri.path = "/model/${model.name}:${model.version}"
                request.contentType = 'application/json'
                request.body = trainData
            }

            return response.serialized_model
        } catch (e) {
            log.error "request to train model ${model} failed: $e.message", e
            throw new RuntimeException("request to train model ${model} failed", e)
        }
    }

    double rank(ArticleDataPoint articleDataPoint, Model model) {
        log.debug "requesting rank from ${model} for ${articleDataPoint}..."

        try {
            BigDecimal response = rankerServiceRest.post(BigDecimal) {
                request.uri.path = "/rank/${model.name}:${model.version}"
                request.contentType = 'application/json'
                request.body = articleDataPoint
            }

            return response.toDouble()
        } catch (e) {
            log.error "requesting rank from ${model} for ${articleDataPoint} failed: $e.message", e
            throw new RuntimeException("requesting rank from ${model} for ${articleDataPoint} failed", e)
        }
    }
}
