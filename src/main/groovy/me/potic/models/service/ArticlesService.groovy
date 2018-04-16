package me.potic.models.service

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.models.domain.Article
import me.potic.models.domain.ArticleEvent
import me.potic.models.domain.ArticleEventType
import me.potic.models.domain.Rank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@Slf4j
class ArticlesService {

    HttpBuilder articlesServiceRest

    @Autowired
    HttpBuilder articlesServiceRest(@Value('${services.articles.url}') String articlesServiceUrl) {
        articlesServiceRest = HttpBuilder.configure {
            request.uri = articlesServiceUrl
        }
    }

    Collection<Article> findArticlesWithoutRank(String rankId, int count) {
        log.debug "requesting ${count} articles without rank ${rankId}..."

        try {
            def response = articlesServiceRest.post {
                request.uri.path = '/graphql'
                request.contentType = 'application/json'
                request.body = [ query: """
                    {
                      withoutRank(rankId: "${rankId}", count: ${count}) {
                        id
                        
                        fromPocket {
                            word_count
                        }
                        
                        card {
                            source
                        }
                        
                        events {
                            userId
                            articleId
                            type
                            timestamp
                        }
                      }
                    }
                """ ]
            }

            List errors = response.errors
            if (errors != null && !errors.empty) {
                throw new RuntimeException("Request failed: $errors")
            }

            return response.data.withoutRank.collect({
                it['events'] = it['events'].collect({ event -> new ArticleEvent(userId: event['userId'], articleId: event['articleId'], type: ArticleEventType.valueOf(event['type']), timestamp: event['timestamp']) })

                new Article(it)
            })
        } catch (e) {
            log.error "requesting ${count} articles without rank ${rankId} failed: $e.message", e
            throw new RuntimeException("requesting ${count} articles without rank ${rankId} failed", e)
        }
    }

    void addRankToArticle(String articleId, Rank rank) {
        log.debug "adding rank ${rank} to article #${articleId}..."

        try {
            articlesServiceRest.post {
                request.uri.path = "/article/${articleId}/rank"
                request.contentType = 'application/json'
                request.body = rank
            }
        } catch (e) {
            log.error "adding rank ${rank} to article #${articleId} failed: $e.message", e
            throw new RuntimeException("adding rank ${rank} to article #${articleId} failed", e)
        }
    }
}
