### POC für den ROW-Lock auf Steuerungstabelle
Über das FOR UPDATE im SQL-Statement wird der Datensatz, den der Worker ermittelt exklusiv blockiert.
Über den Controller kann mit 2 Tabs im Browser ein konkurrierender Zugriff simuliert werden.

Die Logik setzt alle Zugriffe auf eine bereits selektierte Row (durch das Statement im Controller) in eine Warteschlange.
Nach dem Commit der Transaktion, die den exklusiven Zugriff bekommen hat, ist die nächste Transaktion an der Reihe.
Wenn kein Timeout auftritt, so selektiert die nächste TA durch das Aktualisieren der Spalte verarb_zp den nächsten Satz, der das SELECT ... FOR UPDATE erfüllt.
Im Falle eines TA Timeout, muss die SQLException gefangen und behandelt werden (Logging würde ausreichen).
