package de.convince.exclusiveDbLock;

import java.sql.Timestamp;

public record SteuerungInfo(Integer pkey, Timestamp verarb_zp, Integer doktyp, Timestamp eingangs_zp) {
    

}