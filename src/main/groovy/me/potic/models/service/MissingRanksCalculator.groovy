package me.potic.models.service

import groovy.util.logging.Slf4j
import me.potic.models.domain.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.time.LocalDateTime

@Service
@Slf4j
class MissingRanksCalculator {

    @Value(value = '${articles.request.size}')
    int articlesRequestSize

    @Autowired
    RankerService rankerService

    @Autowired
    ModelService modelService

    @Autowired
    ArticlesService articlesService

    @Scheduled(fixedDelay = 10_000L) // every 10 sec
    void calculateMissingRanks() {
        log.debug 'calculating missing ranks...'

        List<Model> activeModels = modelService.getActiveModels()

        activeModels.forEach({ Model model ->
            Collection<Article> articlesToRank = articlesService.findArticlesWithOldestRank("${model.name}:${model.version}", articlesRequestSize)
            log.debug("got ${articlesToRank.size()} articles to calculate rank for model ${model}...")

            articlesToRank.forEach({ article ->
                try {
                    ArticleDataPoint articleDataPoint = ArticleDataPoint.fromArticle(article)
                    double rankValue = rankerService.rank(articleDataPoint, model)
                    articlesService.addRankToArticle(article.id, new Rank(id: "${model.name}:${model.version}", value: rankValue, timestamp: LocalDateTime.now()))
                } catch (e) {
                    log.warn "calculating rank rank from ${model} for ${article} failed: $e.message", e
                }
            })
        })
    }
}
