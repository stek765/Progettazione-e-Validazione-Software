# Report Progetto Smart-Tracking | MODULO 1

### Acceptance Tests - API (REST Assured)
È stata creata una suite di test di accettazione (`AcceptanceApiTest.java`) utilizzando RestAssured. I test coprono con successo i flussi di:
- registrazione utente, 

- provisioning dispositivo,

- login dispositivo.

verificabile con: <u>./gradlew test --tests "it.univr.track.acceptance.AcceptanceApiTest"</u>

---

###  Acceptance Tests - Web (Selenium)
`AcceptanceWebTest.java`
Servono a coprire i **5 Scenari** di vita reale che abbiamo definito. Dimostrano che il sistema è "accettabile" per il cliente finale.

---

###  La famiglia "Page Objects" (PO)

**Dove sono:** `track/acceptance/po/*` 

- **Cosa sono:** Non sono test, ma "traduttori". Dicono al test Selenium come muoversi per interagire con la pagina.

- **Perché:** **Page Object Pattern** serve a rendere i test facili da leggere e da modificare se cambia la grafica del sito.