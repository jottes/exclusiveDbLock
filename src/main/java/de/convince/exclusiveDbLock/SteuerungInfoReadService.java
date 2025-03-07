package de.convince.exclusiveDbLock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SteuerungInfoReadService {
    
     JdbcTemplate jdbcTemplate;
     
     public SteuerungInfoReadService(JdbcTemplate jdbcTemplate) {
         this.jdbcTemplate = jdbcTemplate;        
     }
  
    @Transactional(rollbackFor = Exception.class) 
    public void readNextSteuerungInfo() throws SQLException, InterruptedException, NoSuchElementException {

        try {  

            LoggerFactory.getLogger(SteuerungInfoReadService.class).info("verarbeite nächsten Steuerungssatz ...");

            var sql = 
            """ 
            SELECT * FROM Steuerung
            WHERE 
            verarb_zp IS NULL AND 
            eingangs_zp IN (SELECT MIN(eingangs_zp) FROM Steuerung WHERE verarb_zp IS NULL) FOR UPDATE;
            """;  

            // Datensatz abholen .... (konkurrierender Zugriff ist in Warteschlange!)
            var steuerung = jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> new SteuerungInfo(
                    rs.getInt("pkey"),
                    rs.getTimestamp("verarb_zp"),
                    rs.getInt("doktyp"),
                    rs.getTimestamp("eingangs_zp"))).stream().findFirst().orElseThrow();
            LoggerFactory.getLogger(SteuerungInfoReadService.class).info("Steuerungssatz gelesen: {} - DokTyp [{}]", steuerung.pkey(), steuerung.doktyp());
            
            // WICHTIG!!! Datensatz aktualisieren (Bedingung für den nächsten Zugriff ist verändert und liefert nicht mehr den selben Satz)
            var updateVerarbeitungZeitpunktQuery = "UPDATE Steuerung SET verarb_zp = CURRENT_TIMESTAMP WHERE pkey = ?";                                 
            jdbcTemplate.update(updateVerarbeitungZeitpunktQuery, steuerung.pkey());
                    
            // künstliches Delay 
            var delay = ThreadLocalRandom.current().nextInt(1000, 5001);
            LoggerFactory.getLogger(SteuerungInfoReadService.class).info("Steuerungssatz [{}] Millisekunden in Verarbeitung: [{}] - Verarb-ZP: ", delay, steuerung.pkey());        
            Thread.sleep(delay);
            LoggerFactory.getLogger(SteuerungInfoReadService.class).info("Steuerungssatz Verarbeitung beendet: [{}]", steuerung.pkey());        
                
    } 
    catch (NoSuchElementException e) {
        LoggerFactory.getLogger(SteuerungInfoReadService.class).error("Keine neuen Steuerungseinträge mehr vorhanden!");         
    }

    }


}