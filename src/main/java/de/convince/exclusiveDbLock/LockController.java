package de.convince.exclusiveDbLock;

import java.sql.SQLException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
@RequestMapping("/api")
public class LockController {    

    private final SteuerungInfoReadService steuerungsInfoReadService;
    
    public LockController(SteuerungInfoReadService steuerungsInfoReadService) {
        this.steuerungsInfoReadService = steuerungsInfoReadService;        
    }

    @GetMapping(value = "/steuerung", produces = {"application/json"})  
    public ResponseEntity<SteuerungInfo> getSteuerungInfo() throws SQLException, InterruptedException {

       //var steuerung = steuerungsInfoReadService.readNextSteuerungInfo();

        return ResponseEntity.ok(new SteuerungInfo(null, null, null, null));
    }

}