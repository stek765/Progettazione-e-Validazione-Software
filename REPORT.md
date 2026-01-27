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

#### 1. Scenario: Registrazione Nuovi Utenti
- **L'idea:** Un visitatore arriva sul sito, si registra e ottiene immediatamente l'accesso.
- **Il test:** Selenium apre la pagina di Sign Up, compila il form con dati validi, invia e verifica che l'utente atterri sulla Dashboard personale.

#### 2. Scenario: Gestione Profilo e Sicurezza
- **L'idea:** L'utente vuole cambiare la propria password per motivi di sicurezza.
- **Il test:** L'utente loggato va nel profilo, aggiorna la password. Il test esegue il logout e prova a rientrare con la vecchia password (fallendo) e poi con la nuova (riuscendo).

#### 3. Scenario: Assegnazione Dispositivi (Admin)
- **L'idea:** L'amministratore associa fisicamente un sensore a un utente specifico tramite interfaccia visuale.
- **Il test:** Usa il **Drag & Drop**: trascina un sensore dalla lista "Disponibili" alla card di un utente. Il test verifica che il sensore appaia ora sotto quel l'utente e che il backend abbia salvato l'associazione.

#### 4. Scenario: Provisioning e Configurazione Sensori
- **L'idea:** L'admin attiva un nuovo sensore vergine, generando le credenziali crittografiche necessarie.
- **Il test:** Nella pagina di dettaglio del dispositivo, l'admin attiva l'interruttore (toggle) "Abilita Provisioning". Il test verifica che appaiano a schermo il Token e la Chiave Privata generati dinamicamente.

#### 5. Scenario: Ruoli e Permessi (Security)
- **L'idea:** Un utente "base" non deve poter vedere o toccare le funzioni amministrative.
- **Il test:** Si effettua il login come utente standard. Si tenta di accedere forzatamente agli URL di amministrazione (ricevendo un errore 403 o redirect) e si verifica visivamente che i controlli di Drag & Drop e Provisioning siano nascosti o disabilitati.

---

###  La famiglia "Page Objects" (PO)

**Dove sono:** `track/acceptance/po/*` 

- **Cosa sono:** Non sono test, ma "traduttori". Dicono al test Selenium come muoversi per interagire con la pagina.

- **Perché:** **Page Object Pattern** serve a rendere i test facili da leggere e da modificare se cambia la grafica del sito.