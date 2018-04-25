package me.potic.models.service

import groovy.util.logging.Slf4j
import me.potic.models.domain.Model
import org.springframework.stereotype.Service

@Service
@Slf4j
class ModelService {

    static final Model RANDOM = new Model(name: 'random', version: '1.0', description: 'random ranks', timestamp: '2017-12-13', isActive: true)
    static final Model LOGREG = new Model(name: 'logreg', version: '0.1', description: 'logistic regression (source, words count)', timestamp: '2018-03-01', isActive: true)
    static final Model NB_0_1 = new Model(name: 'nb', version: '0.1', description: 'naive bayes (source, words count)', timestamp: '2018-04-15', isActive: false)
    static final Model NB_0_2 = new Model(name: 'nb', version: '0.2', description: 'naive bayes (source, words count, showed count, skipped count)', timestamp: '2018-04-16', isActive: false)
    static final Model NB_0_3 = new Model(name: 'nb', version: '0.3', description: 'naive bayes with Bernoulli model', timestamp: '2018-04-25', isActive: true)
    static final Model SVM = new Model(name: 'svm', version: '0.1', description: 'svm', timestamp: '2018-04-25', isActive: true)

    static final List<Model> MODELS = [ RANDOM, LOGREG, NB_0_1, NB_0_2, NB_0_3, SVM ]

    Model getActualModel() {
        LOGREG
    }

    List<Model> getActiveModels() {
        getModels().findAll({ it.isActive })
    }

    List<Model> getModels() {
        MODELS
    }
}
