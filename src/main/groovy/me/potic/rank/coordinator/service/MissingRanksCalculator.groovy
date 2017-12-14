package me.potic.rank.coordinator.service

import groovy.util.logging.Slf4j
import me.potic.rank.coordinator.domain.Article
import me.potic.rank.coordinator.domain.Rank
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
    ArticlesService articlesService

    @Scheduled(fixedDelay = 30_000L)
    void calculateMissingRanks() {
        log.info 'calculating missing ranks...'

        String rankId = rankerService.getActualRankId()

        Collection<Article> articlesToRank = articlesService.findArticlesWithoutRank(rankId, articlesRequestSize)
        log.debug("got ${articlesToRank.size()} articles to calculate ranks...")

        articlesToRank.collect({ calculateRank(it, rankId) }).forEach({ article ->
            articlesService.updateArticle(article)
        })
    }

    Article calculateRank(Article article, String rankId) {
        double rankValue = rankerService.rank(article, rankId)

        article.ranks.removeAll { rank -> rank.id == rankId }
        article.ranks += new Rank(id: rankId, value: rankValue)

        return article
    }
}
