package me.potic.models.service

import groovy.util.logging.Slf4j
import me.potic.models.domain.Model
import org.springframework.stereotype.Service

@Service
@Slf4j
class ModelService {

    List<Model> getActiveModels() {
        [
                new Model(name: 'random', version: '1.0', description: 'random ranks', timestamp: '2017-12-13T01:12:00.000', isActive: true),
                new Model(name: 'logreg', version: '0.1', description: 'logistic regression based on source and words count', timestamp: '2018-03-01T10:29:00.000', isActive: true)
        ]
    }
}
