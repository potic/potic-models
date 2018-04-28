package me.potic.models.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString(includeNames = true)
class ArticleDataPoint {

    String id

    String user_id

    String status

    Long time_read

    Long read_duration

    String has_image

    String has_video

    Integer word_count

    String source

    Integer showed_count

    Integer skipped_count

    Integer liked_count

    Integer disliked_count

    static ArticleDataPoint fromArticle(Article article) {
        Long read_duration = article.fromPocket != null && article.fromPocket.time_read != null && article.fromPocket.time_added != null ? Math.max(0, article.fromPocket.time_read - article.fromPocket.time_added) : null
        Integer word_count = article.fromPocket != null && article.fromPocket.word_count != null ? Long.parseLong(article.fromPocket.word_count) : null
        Integer showed_count = article.events != null ? article.events.count { event -> event.type == ArticleEventType.SHOWED } : null
        Integer skipped_count = article.events != null ? article.events.count { event -> event.type == ArticleEventType.SKIPPED } : null
        Integer liked_count = article.events != null ? article.events.count { event -> event.type == ArticleEventType.LIKED } : null
        Integer disliked_count = article.events != null ? article.events.count { event -> event.type == ArticleEventType.DISLIKED } : null

        ArticleDataPoint articleDataPoint = new ArticleDataPoint()
        articleDataPoint.id = article.id
        articleDataPoint.user_id = article.userId
        articleDataPoint.status = article.fromPocket?.status
        articleDataPoint.time_read = article.fromPocket?.time_read
        articleDataPoint.read_duration = read_duration
        articleDataPoint.has_image = article.fromPocket?.has_image
        articleDataPoint.has_video = article.fromPocket?.has_video
        articleDataPoint.word_count = word_count
        articleDataPoint.source = article.card?.source
        articleDataPoint.showed_count = showed_count
        articleDataPoint.skipped_count = skipped_count
        articleDataPoint.liked_count = liked_count
        articleDataPoint.disliked_count = disliked_count

        return articleDataPoint
    }
}
