package me.potic.models.service

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.models.domain.Article
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

    double rank(Article article, Model model) {
        log.debug "requesting rank from ${model} for ${article}..."

        try {
            BigDecimal response = rankerServiceRest.post(BigDecimal) {
                request.uri.path = "/rank/${model.name}:${model.version}"
                request.contentType = 'application/json'
                request.body = article
            }

            return response.toDouble()
        } catch (e) {
            log.error "requesting rank from ${model} for ${article} failed: $e.message", e
            throw new RuntimeException("requesting rank from ${model} for ${article} failed", e)
        }
    }
}
