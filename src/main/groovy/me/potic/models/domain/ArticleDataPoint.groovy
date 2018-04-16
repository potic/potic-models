package me.potic.models.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString(includeNames = true)
class ArticleDataPoint {

    String source

    Integer word_count

    Integer skipped_count

    Integer showed_count
}
