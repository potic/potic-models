package me.potic.models.service

import groovy.util.logging.Slf4j
import me.potic.models.domain.Article
import me.potic.models.domain.Model
import me.potic.models.domain.Rank
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
                double rankValue = rankerService.rank(article, model)
                articlesService.addRankToArticle(article.id, new Rank(id: "${model.name}:${model.version}", value: rankValue))
            })
        })
    }
}
