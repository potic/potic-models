package me.potic.models.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.builder.Builder

@Builder
@EqualsAndHashCode
@ToString(includeNames = true)
class Article {

    String id

    String userId

    PocketArticle fromPocket

    Card card

    List<ArticleEvent> events

    List<Rank> ranks
}
