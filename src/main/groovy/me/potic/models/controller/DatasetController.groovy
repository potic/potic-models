package me.potic.models.controller

import groovy.util.logging.Slf4j
import me.potic.models.domain.ArticleDataPoint
import me.potic.models.service.TrainService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse

@RestController
@Slf4j
class DatasetController {

    @Autowired
    TrainService trainService

    @CrossOrigin
    @GetMapping(path = '/dataset/train')
    void getEventsTrainDataset(
            @RequestParam(value = 'count', required = false) Integer count,
            HttpServletResponse response
    ) {
        log.debug "receive GET request for /dataset/train?count=${count}"

        try {
            List<ArticleDataPoint> eventsTrainDataset = trainService.getEventsTrainDataset(count)

            response.outputStream.withPrintWriter { writer ->
                writer.write('id,user_id,read,read_time,read_duration,has_image,has_video,word_count,source,showed_count,skipped_count,liked_count,disliked_count')
                eventsTrainDataset.forEach { ArticleDataPoint articleDataPoint ->
                    writer.write(
                            "${articleDataPoint.id}," +
                            "${articleDataPoint.user_id}," +
                            "${articleDataPoint.status}," +
                            "${articleDataPoint.time_read}," +
                            "${articleDataPoint.read_duration}," +
                            "${articleDataPoint.has_image}," +
                            "${articleDataPoint.has_video}," +
                            "${articleDataPoint.word_count}," +
                            "${articleDataPoint.source}," +
                            "${articleDataPoint.showed_count}," +
                            "${articleDataPoint.skipped_count}," +
                            "${articleDataPoint.liked_count}," +
                            "${articleDataPoint.disliked_count}\n"
                    )
                }
            }
        } catch (e) {
            log.error "GET request for /dataset/train?count=${count} failed: $e.message", e
            throw new RuntimeException("GET request for /dataset/train?count=${count} failed: $e.message", e)
        }
    }
}
