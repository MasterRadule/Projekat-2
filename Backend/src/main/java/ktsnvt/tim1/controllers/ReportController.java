package ktsnvt.tim1.controllers;

import ktsnvt.tim1.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping(value = "/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping()
    public ResponseEntity<Object> getReport(@RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
                                            @RequestParam("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
                                            @RequestParam("locationId") Long locationId, @RequestParam("eventId") Long eventId) {
        return new ResponseEntity<>(reportService.getReport(startDate, endDate, locationId, eventId), HttpStatus.OK);
    }
}
