# Vergleichsbericht JSON-Differenzen

**Dokumentenanalyse: JSON N vs. JSON B**

---

## DOC 01

### Gefundene Unterschiede: 2

| Feld | JSON N | JSON B | Änderung |
|------|--------|--------|----------|
| **frage1.frage1_datumUhrzeit** | `"15 Sept. 2025 / 9 Uhr"` | `"15. Sept. 2025 / 9 Uhr"` | Punkt nach "15" hinzugefügt |
| **frage5.frage5_schilderungDesEreignisses** | `"Ich war mit dem eScooter unterwegs. Ich bin auf meiner Spur gefahren. Der Mann wollte links abbiegen und ist in mich ein gefahren"` | `"Ich war mit dem eBike Scooter unterwegs. Ich bin auf meiner Spur gefahren. Der Mann wollte links abbiegen und ist in mich ein gefahren."` | "eScooter" → "eBike Scooter", Punkt am Ende hinzugefügt |

---

## DOC 02

### Gefundene Unterschiede: 3

| Feld | JSON N | JSON B | Änderung |
|------|--------|--------|----------|
| **frage1.frage1_ortDesEreignisses** | `"Goethestrasse 57 4020 LINZ"` | `"Goethestrasse 57, 4020 LINZ"` | Komma nach "57" hinzugefügt |
| **frage3.frage3_vorfallsart_sportunfall** | `"true"` | `""` | Von "true" zu leerem String geändert |
| **frage5.frage5_schilderungDesEreignisses** | `"Bin aus der Haustür gegangen und dann am Gehsteig ausgerutscht, habe nicht gestolpert"` | `"Bin aus der Haustür gegangen und dann am Gehsteig ausgerutscht, habe nicht gesehen, dass er glatt war"` | "habe nicht gestolpert" → "habe nicht gesehen, dass er glatt war" |

---

## DOC 03

### Gefundene Unterschiede: 3

| Feld | JSON N | JSON B | Änderung |
|------|--------|--------|----------|
| **frage1.frage1_datumUhrzeit** | `"13.06.2015, 13:56"` | `"13.06.2025, 13:56"` | Jahr von 2015 auf 2025 geändert |
| **frage1.frage1_ortDesEreignisses** | `"Alfred-Hermann-Fried-Str. 10 9800 Althang-Puchheim"` | `"Alfred-Hermann-Fried-Str. 10, 9800 Affenburg-Puchheim"` | Komma nach "10" hinzugefügt, "Althang-Puchheim" → "Affenburg-Puchheim" |
| **frage5.frage5_schilderungDesEreignisses** | `"Bin über meine Kabe gestolpert, nachdem Nils sie hinter mir gekickt hat!"` | `"Bin über meine Kabe gestolpert, nachdem Nils sie hinter mir gelegt hat!"` | "gekickt" → "gelegt" |

---

## DOC 06

### Gefundene Unterschiede: 12

| Feld | JSON N | JSON B | Änderung |
|------|--------|--------|----------|
| **frage1.frage1_datumUhrzeit** | `"22. Juli 2025 17:30"` | `"22. Juli 2025 10:20"` | Uhrzeit von 17:30 auf 10:20 geändert |
| **frage1.frage1_ortDesEreignisses** | `"Bahnhof Schönfling"` | `"Bahnhof Schottengasse"` | Ort von "Schönfling" auf "Schottengasse" geändert |
| **frage4.frage4_schuldiger** | `"Max Hunde begleiten"` | `"Max Hundebegleiter"` | "Hunde begleiten" → "Hundebegleiter" |
| **frage5.frage5_schilderungDesEreignisses** | `"Als ich am Bahnhof aus dem Zug ausstieg, sprang mich ein schwerer Hund an, und biss mir beinahe die Hand ab."` | `"Als ich am Bahnhof aus dem Zug ausstieg, wurde ich von einem schweren Hund an der Wade gebissen."` | Vollständig unterschiedliche Beschreibung |
| **frage7.frage7_konsumVonAlkoholSuchtgiftMedikamenten** | `"true"` | `"false"` | Von "true" auf "false" geändert |
| **frage7.frage7_artUndMengeDesKonsums** | `"2 Bier"` | `"Schönfling 123456 + A2"` | Vollständig unterschiedlicher Inhalt |
| **frage7.frage7_zeitraumDesKonsums** | `"2 Stunden vorher"` | `"2 Stunden"` | "vorher" entfernt |
| **frage8.frage8_polizeidienststelle** | `"Schönfling"` | `"Bezirkspolizei ABCDEXAA"` | Von "Schönfling" auf "Bezirkspolizei ABCDEXAA" geändert |
| **frage8.frage8_aktenzahl** | `"A2245/7 + A2"` | `"123456"` | Von "A2245/7 + A2" auf "123456" geändert |
| **frage9.frage9_gerichtlichesVerfahrenAnhaengig** | `"true"` | `"false"` | Von "true" auf "false" geändert |
| **frage9.frage9_gerichtOderStaatsanwaltschaft** | `"Bezirksgericht"` | `""` | Von "Bezirksgericht" zu leerem String geändert |
| **frage9.frage9_aktenzahl** | `"ABC9EXA1"` | `""` | Von "ABC9EXA1" zu leerem String geändert |

---

## DOC 06 ROTATED

### Gefundene Unterschiede: 8

| Feld | JSON N | JSON B | Änderung |
|------|--------|--------|----------|
| **frage1.frage1_datumUhrzeit** | `"22. Juli 2025 17:30"` | `"22. Juli 2025 17.30"` | Doppelpunkt durch Punkt ersetzt (17:30 → 17.30) |
| **frage1.frage1_ortDesEreignisses** | `"Bahnhof Schönfling"` | `"Bahnhof Schöpfing"` | "Schönfling" → "Schöpfing" |
| **frage4.frage4_schuldiger** | `"Max Hunde begleiten"` | `"Max Hundebegleiter"` | "Hunde begleiten" → "Hundebegleiter" |
| **frage5.frage5_schilderungDesEreignisses** | `"Als ich am Bahnhof aus dem Zug ausstieg, sprang mich ein schwerer Hund an, und biss mir beinahe die Hand ab."` | `"Als ich am Bahnhof aus dem Zug ausstieg, sprang mich ein schwerer Hund an und biss mir beinahe die Hand ab."` | Komma vor "und" entfernt |
| **frage7.frage7_zeitraumDesKonsums** | `"2 Stunden vorher"` | `"1 Stunde zuvor"` | "2 Stunden vorher" → "1 Stunde zuvor" |
| **frage8.frage8_polizeidienststelle** | `"Schönfling"` | `"Schöpfing"` | "Schönfling" → "Schöpfing" |
| **frage8.frage8_aktenzahl** | `"A2245/7 + A2"` | `"12345/1+12"` | Von "A2245/7 + A2" auf "12345/1+12" geändert |
| **frage9.frage9_aktenzahl** | `"ABC9EXA1"` | `"ABCDEXAA"` | Von "ABC9EXA1" auf "ABCDEXAA" geändert |

---

## Zusammenfassung

**Gesamtanzahl analysierter Dokumente:** 5

**Gesamtanzahl gefundener Unterschiede:** 28

### Häufigste Änderungstypen:
- Formatierungsänderungen (Kommas, Punkte, Doppelpunkte)
- Textliche Abweichungen in Beschreibungen
- Ortsnamen-Variationen
- Datumsänderungen
- Aktenzeichen-Unterschiede
- Boolean-Werte (true/false) und deren Konsequenzen
