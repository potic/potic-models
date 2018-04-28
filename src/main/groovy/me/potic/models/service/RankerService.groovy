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

    List<Model> ranks() {
        log.debug "requesting active ranks..."

        try {
            def response = rankerServiceRest.get {
                request.uri.path = "/ranks"
                request.contentType = 'application/json'
            }

            return response.collect({ new Model(it) })
        } catch (e) {
            log.error "requesting active ranks failed: $e.message", e
            throw new RuntimeException("requesting active ranks failed", e)
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
