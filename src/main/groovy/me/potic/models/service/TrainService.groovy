package me.potic.models.service

import groovy.util.logging.Slf4j
import me.potic.models.domain.Article
import me.potic.models.domain.ArticleDataPoint
import me.potic.models.domain.Model
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.time.Duration
import java.time.LocalDate

@Service
@Slf4j
class TrainService {

    @Value(value = '${models.outdated.period}')
    int modelsOutdatedPeriod

    @Autowired
    ModelService modelService

    @Autowired
    RankerService rankerService

    @Autowired
    ArticlesService articlesService

    @Scheduled(fixedDelay = 30_000L)
    void trainOutdatedModels() {
        log.info 'training outdated models...'

        List<Model> activeModels = modelService.getActiveModels()
        List<Model> outdatedModels = activeModels.findAll({ model -> model.serializedModel == null || Duration.between(LocalDate.now().atStartOfDay(), LocalDate.parse(model.trainTimestamp).atStartOfDay()).toDays() >= modelsOutdatedPeriod })

        List<ArticleDataPoint> trainData = outdatedModels.empty ? [] : getEventsTrainDataset()

        outdatedModels.forEach({ Model model ->
            try {
                log.info("model ${model} is outdated")
                String serializedModel = rankerService.model(trainData, model)
                model.serializedModel = serializedModel
                modelService.upsertModel(model)
            } catch (e) {
                log.warn "training outdated model ${model} failed: $e.message", e
            }
        })
    }

    List<ArticleDataPoint> getEventsTrainDataset(Integer count = null) {
        log.info "preparing events train dataset with max size ${count}..."

        try {
            articlesService.getWithEvents(count).collect { Article article -> ArticleDataPoint.fromArticle(article) }
        } catch (e) {
            log.error "preparing events train dataset failed: $e.message", e
            throw new RuntimeException("preparing events train dataset failed: $e.message", e)
        }
    }
}
