package me.potic.models.controller

import groovy.util.logging.Slf4j
import me.potic.models.domain.Model
import me.potic.models.service.ModelService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Slf4j
class ModelController {

    @Autowired
    ModelService modelService

    @CrossOrigin
    @GetMapping(path = '/actual')
    @ResponseBody ResponseEntity<Model> getActualModel() {
        log.debug "receive GET request for /actual"

        try {
            return new ResponseEntity<>(modelService.getActualModel(), HttpStatus.OK)
        } catch (e) {
            log.error "GET request for /actual failed: $e.message", e
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @CrossOrigin
    @GetMapping(path = '/active')
    @ResponseBody ResponseEntity<List<Model>> getActiveModels() {
        log.debug "receive GET request for /active"

        try {
            return new ResponseEntity<>(modelService.getActiveModels(), HttpStatus.OK)
        } catch (e) {
            log.error "GET request for /active failed: $e.message", e
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
