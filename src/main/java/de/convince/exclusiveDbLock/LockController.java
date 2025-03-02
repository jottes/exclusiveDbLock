package de.convince.exclusiveDbLock;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;


@RestController
@RequestMapping("/api")
public class LockController {
    
    JdbcTemplate jdbcTemplate;

    public LockController(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);        
    }

    @GetMapping(value = "/steuerung", produces = {"application/json"})  
    @Transactional   
    public ResponseEntity<SteuerungInfo > getSteuerungInfo() {

        var sql = 
        """ 
        SELECT * FROM Steuerung
        WHERE 
        verarb_zp IS NULL AND 
        eingangs_zp IN (SELECT MIN(eingangs_zp) FROM Steuerung WHERE verarb_zp IS NULL) FOR UPDATE;
        """;

        var update = """
        UPDATE Steuerung SET verarb_zp = CURRENT_TIMESTAMP WHERE pkey = ?;
                """;

        // Datensatz abholen .... (konkurrierender Zugriff ist in Warteschlange!)
        var steuerung = jdbcTemplate.query(sql, new RowMapper<SteuerungInfo>() {
            @Override
            public SteuerungInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new SteuerungInfo(
                    rs.getInt("pkey"), 
                    rs.getTimestamp("verarb_zp"), 
                    rs.getInt("doktyp"), 
                    rs.getTimestamp("eingangs_zp"));
            }
        }).get(0);
        
        // Datensatz aktualisieren
        jdbcTemplate.update(update, steuerung.pkey());

        return ResponseEntity.ok(steuerung);
    }

}