package me.potic.models.service

import groovy.util.logging.Slf4j
import me.potic.models.domain.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

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

    @Scheduled(fixedDelay = 30_000L)
    void calculateMissingRanks() {
        log.info 'calculating missing ranks...'

        List<Model> activeModels = modelService.getActiveModels()

        activeModels.forEach({ Model model ->
            Collection<Article> articlesToRank = articlesService.findArticlesWithoutRank("${model.name}:${model.version}", articlesRequestSize)
            log.debug("got ${articlesToRank.size()} articles to calculate rank for model ${model}...")

            articlesToRank.forEach({ article ->
                try {
                    ArticleDataPoint articleDataPoint = new ArticleDataPoint()
                    articleDataPoint.source = article.card?.source != null ? article.card?.source : ''
                    articleDataPoint.word_count = article.fromPocket?.word_count != null ? Integer.parseInt(article.fromPocket?.word_count) : 0
                    articleDataPoint.showed_count = article.events.count { event -> event.type == ArticleEventType.SHOWED }
                    articleDataPoint.skipped_count = article.events.count { event -> event.type == ArticleEventType.SKIPPED }

                    double rankValue = rankerService.rank(articleDataPoint, model)
                    articlesService.addRankToArticle(article.id, new Rank(id: "${model.name}:${model.version}", value: rankValue))
                } catch (e) {
                    log.warn "calculating rank rank from ${model} for ${article} failed: $e.message", e
                }
            })
        })
    }
}
