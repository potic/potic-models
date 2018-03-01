package me.potic.rank.coordinator.service

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.rank.coordinator.domain.Article
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
                      }
                    }
                """ ]
            }

            List errors = response.errors
            if (errors != null && !errors.empty) {
                throw new RuntimeException("Request failed: $errors")
            }

            return response.data.withoutRank.collect({ new Article(it) })
        } catch (e) {
            log.error "requesting ${count} articles without rank ${rankId} failed: $e.message", e
            throw new RuntimeException("requesting ${count} articles without rank ${rankId} failed", e)
        }
    }

    void updateArticle(Article article) {
        log.debug "updating article ${article}..."

        try {
            articlesServiceRest.put {
                request.uri.path = '/article'
                request.contentType = 'application/json'
                request.body = article
            }
        } catch (e) {
            log.error "updating article ${article} failed: $e.message", e
            throw new RuntimeException("updating article ${article} failed", e)
        }
    }
}
