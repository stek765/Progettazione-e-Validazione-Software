### 1. Unit Tests (Test Unitari)

**Dove sono:** `src/test/java/it/univr/track/services/...` **Esempi:** `UserAuthenticationServiceTest.java`, `DeviceProvisioningServiceTest.java`

- **Cosa sono:** Sono i test più piccoli e veloci. Testano una singola "funzione" di calcolo o logica.

- **Su cosa:** Si concentrano sui **Services** (la logica di business), come il controllo delle password o l'attivazione di un sensore.

- **Perché:** Servono a garantire una **High Code Coverage** (copertura del codice elevata). Se cambi una riga di logica, questi test ti dicono subito se hai rotto qualcosa.

### 2. Acceptance Tests - API (REST Assured)

**Dove sono:** `src/test/java/it/univr/track/acceptance/AcceptanceApiTest.java`

- **Cosa sono:** Testano se il "dietro le quinte" del server risponde correttamente alle chiamate web.

- **Su cosa:** Sui **Controllers** (i file che gestiscono gli indirizzi URL).

- **Perché:** Il professore li richiede specificamente per verificare che il backend funzioni anche senza guardare la grafica.

### 3. Acceptance Tests - Web (Selenium)

**Dove sono:** `src/test/java/it/univr/track/acceptance/AcceptanceWebTest.java`

- **Cosa sono:** Test che aprono un browser "fantasma" e cliccano sui bottoni proprio come farebbe un utente.

- **Su cosa:** Sull'intera applicazione dall'inizio alla fine (Login → Dashboard → Profilo).

- **Perché:** Servono a coprire i **5 Scenari** di vita reale che abbiamo definito. Dimostrano che il sistema è "accettabile" per il cliente finale.

### 4. La famiglia "Page Objects" (PO)

**Dove sono:** `src/test/java/it/univr/track/acceptance/po/...` **Esempi:** `LoginPage.java`, `DashboardPage.java`

- **Cosa sono:** Non sono test, ma "traduttori". Dicono al test Selenium: "Per fare il login, scrivi qui e clicca lì".

- **Perché:** È un obbligo del progetto usare il **Page Object Pattern**. Serve a rendere i test facili da leggere e da modificare se cambia la grafica del sito.
