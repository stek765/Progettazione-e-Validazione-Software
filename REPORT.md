# Report Progetto Smart-Tracking | MODULO 1

## 🚀 Panoramica del Progetto
**Smart-Tracking** è una piattaforma IoT per la gestione sicura di dispositivi di tracciamento.
Questo **Modulo 1** costituisce il "Core" amministrativo e di sicurezza: gestisce l'ecosistema di Utenti e Dispositivi prima che inizi il flusso dati vero e proprio.

### 📐 Architettura e Metodologia
Lo sviluppo ha seguito un processo **Agile**, focalizzandosi sull'implementazione iterativa di User Stories (i 5 scenari).
L'architettura segue il pattern **MVC (Model-View-Controller)** standard di Spring Boot:
- **Model (`entity`):** Rappresenta i dati persistenti (es. `UserRegistered`, `Device`, `ProvisioningToken`).
- **View (`templates`):** Interfaccia utente server-side rendering realizzata con **Thymeleaf**.
- **Controller (`controller`):** Smista le richieste HTTP, gestisce la sicurezza e orchestra la logica di business.

### 🧭 Navigazione Rapida nel Codice
Per orientarsi velocemente nella struttura del progetto:
- `src/main/java/.../controller`: Qui risiedono gli endpoint Web (HTML) e API (JSON).
- `src/main/java/.../repository`: Layer di accesso al Database (Spring Data JPA).
- `src/test/java/.../acceptance`: Dove vivono i test "End-to-End" (i 5 Scenari Selenium).
- `src/test/java/.../pageObjects`: Le classi che traducono la UI per i test, rendendoli leggibili.

---

## 🛠 Cheat Sheet Comandi

| Azione | Comando | Descrizione |
| :--- | :--- | :--- |
| **Avviare l'App** | `./gradlew bootRun` | Avvia il server su localhost:8080 |
| **Build del Progetto** | `./gradlew build` | Compila e pacchettizza l'applicazione |
| **Eseguire TUTTI i Test** | `./gradlew test` | Lancia Unit, Integration e Acceptance Tests |
| **Test Singolo** | `./gradlew test --tests "NomeClasse"` | Esegue solo una specifica classe di test |
| **Report Copertura** | `./gradlew jacocoTestReport` | Genera il report HTML (in `build/reports/jacoco`) |

---

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

### Altri Livelli di Testing (La Piramide)
Abbiamo completato la suite coprendo i livelli più bassi per garantire robustezza interna:

#### 🟢 Unit Tests (Velocità estrema)
- **Target:** `UserUnitTest.java`
- **L'idea:** Verificare che l'Entità Java funzioni isolata dal mondo.
- **Il test:** Istanzia la classe `UserRegistered` e controlla che Costruttori, Builder e valori di Default siano coerenti.

#### 🔵 Controller Tests (Sicurezza & API)
- **Target:** `AdminControllerTest.java`
- **L'idea:** Testare le rotte web e la sicurezza senza avviare il browser.
- **Il test:** Usa **MockMvc** per simulare chiamate HTTP. Verifica che le API rispondano 200 all'Admin e **403 Forbidden** a chi non è autorizzato.

#### 🟠 Integration Tests (Database Reale)
- **Target:** `DeviceAssignmentSystemTest.java`
- **L'idea:** Verificare che i dati atterrino correttamente sul Database.
- **Il test:** Scrive su un DB **H2** in memoria. Verifica che l'assegnazione (User <-> Device) sia persistita e controlla cosa succede ai dispositivi orfani.

---

###  La famiglia "Page Objects" (PO)

**Dove sono:** `track/acceptance/po/*` 

- **Cosa sono:** Non sono test, ma "traduttori". Dicono al test Selenium come muoversi per interagire con la pagina.

- **Perché:** **Page Object Pattern** serve a rendere i test facili da leggere e da modificare se cambia la grafica del sito.