package me.potic.models.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.annotation.Id

@EqualsAndHashCode
@ToString(includeNames = true)
class Model {

    @Id
    String id

    String name

    String version

    String description

    String trainTimestamp

    String serializedModel

    boolean isActive
}
