package me.potic.rank.coordinator.service

import groovy.util.logging.Slf4j
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@Slf4j
class CalculateMissingRanks {

    @Scheduled(fixedDelay = 30_000L)
    void calculateMissingRanks() {
        log.info 'calculating missing ranks...'

        // request for actual rankId:modelId

        // request for articles without rank for rankId:modelId

        // calculate ranks

        // save articles with ranks
    }
}
