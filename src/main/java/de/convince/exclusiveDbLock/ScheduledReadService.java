package de.convince.exclusiveDbLock;

import java.sql.SQLException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledReadService {
    
    public final SteuerungInfoReadService steuerungInfoReadService;

    public ScheduledReadService(SteuerungInfoReadService steuerungInfoReadService) {
        this.steuerungInfoReadService = steuerungInfoReadService;        
    }

    @Scheduled(fixedRate = 3000)
    public void startReadingSteuerungsSatz() throws InterruptedException, SQLException, Exception {
        steuerungInfoReadService.readNextSteuerungInfo();
    }

}