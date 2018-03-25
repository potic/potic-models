package me.potic.models.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString(includeNames = true)
class Model {

    String name

    String version

    String description

    String timestamp

    boolean isActive
}
