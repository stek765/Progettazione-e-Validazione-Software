# Report Progetto Smart-Tracking | MODULO 1

Membri gruppo:
Stefano Zanolli (VR521385)

## Panoramica del Progetto
**Smart-Tracking** è una piattaforma IoT per la gestione sicura di dispositivi di tracciamento.
Questo **Modulo 1** si occupa di gestisce l'ecosistema di Utenti e Dispositivi prima che inizi il flusso dati vero e proprio, quindi: 
- Login
- Registrazione
- Creazione utenti
- Gestione info utenti e proprio profilo
- Associazione Dispositivi a Utenti
- Provisioning Dispositivi

<p align="center">
  <img src="https://github.com/user-attachments/assets/ff650e34-64c4-483e-af1f-4b2b6542bc0f" width="400" />
</p>




### Architettura e Organizzazione 
Lo sviluppo ha seguito un processo **Agile**, focalizzandosi sull'implementazione iterativa di User Stories (i 5 scenari di accettazione).

L'architettura segue il pattern **MVC (Model-View-Controller)** standard di Spring Boot, strutturato per garantire una chiara separazione delle responsabilità (*Separation of Concerns*):

- **Model (`entity` & `repository`)**: Rappresenta il cuore dello stato del sistema. Include le **Entity**, che mappano i dati persistenti sul database, e i **Repository** (Spring Data JPA), che isolano la logica di accesso ai dati dal resto dell'applicazione.
- **View (`templates` & `dto`)**: Gestisce il rendering dell'interfaccia utente. Utilizza **Thymeleaf** per la generazione dinamica delle pagine lato server e i **DTO (Data Transfer Objects)** per il passaggio sicuro dei dati, evitando l'esposizione diretta delle entità del database verso l'esterno.
- **Controller (`api & webcontrollers`)**: La figura del controller agisce come coordinatore del sistema. Riceve le richieste HTTP, valida i dati in ingresso e orchestra la logica di business delegandola ai servizi, garantendo che ogni operazione rispetti i permessi di accesso (**RBAC**) prima di interagire con il Model.

### Navigazione Rapida nel Codice
Per orientarsi velocemente nella struttura del progetto:
- `main/.../controller`: Qui risiedono gli endpoint Web (HTML) e API (JSON).
- `main/.../repository`: Layer di accesso al Database (Spring Data JPA).
- `test/.../acceptance`: Dove vivono i test "End-to-End" (i 5 Scenari Selenium + API Rest Assured).
- `test/.../pageObjects`: Le classi che traducono la UI per i test, rendendoli leggibili.

---

## Comandi utili

| Azione | Comando | Descrizione |
| :--- | :--- | :--- |
| **Avviare l'App** | `./gradlew bootRun` | Avvia il server su localhost:8080 |
| **Build del Progetto** | `./gradlew build` | Compila e pacchettizza l'applicazione |
| **Eseguire TUTTI i Test** | `./gradlew test` | Lancia Unit, Integration e Acceptance Tests |
| **Test Singolo** | `./gradlew test --tests "NomeClasse"` | Esegue solo una specifica classe di test |
| **Report Copertura** | `./gradlew jacocoTestReport` | Genera il report HTML (in `build/reports/jacoco`) |

---
___
 ---  --- --- 
### Acceptance Tests - API (REST Assured)
È stata creata una suite di test di accettazione (`AcceptanceApiTest.java`) utilizzando RestAssured. I test coprono con successo i flussi di:
- registrazione utente, 

- provisioning dispositivo,

- login dispositivo.

verificabile con: `./gradlew test --tests "it.univr.track.acceptance.AcceptanceApiTest"`

---

###  Acceptance Tests - Web (Selenium) + PageObject
`AcceptanceWebTest.java`
Servono a coprire i **5 Scenari**, ovvero scenari di vita reale su come verrà usato il software. Dimostrano che il sistema è "accettabile" per il cliente finale.

#### 1. Scenario: Registrazione Nuovi Utenti
- **L'idea:** Un nuovo dipendente arriva sul sito, si registra e ottiene immediatamente l'accesso.
- **Il test:** Selenium apre la pagina di Sign Up, compila il form con dati validi, invia e verifica che l'utente atterri sulla Dashboard personale.

#### 2. Scenario: Gestione Profilo e Sicurezza
- **L'idea:** L'utente vuole cambiare la propria password per motivi di sicurezza.
- **Il test:** L'utente loggato va nel profilo, aggiorna la password. Il test esegue il logout e prova a rientrare con la vecchia password (fallendo) e poi con la nuova (riuscendo).

#### 3. Scenario: Assegnazione Dispositivi (Admin)
- **L'idea:** L'amministratore associa fisicamente un sensore a un utente specifico tramite interfaccia visuale.
- **Il test:** L'automazione esegue il **login come Admin**, naviga alla pagina dei dispositivi e usa il **Drag & Drop**: trascina un sensore dalla lista "Disponibili" alla card di un utente. Il test verifica che il sensore appaia ora sotto quell'utente e che il backend abbia salvato l'associazione.

#### 4. Scenario: Provisioning e Configurazione Sensori
- **L'idea:** L'admin attiva un nuovo sensore vergine, generando le credenziali crittografiche necessarie.
- **Il test:** Dopo il **login come Admin**, si naviga nel dettaglio di un dispositivo e si attiva l'interruttore (toggle) "Abilita Provisioning". Il test verifica che appaiano a schermo il MAC del dispositivo e la Chiave Privata da salvare, generati dinamicamente.

#### 5. Scenario: Ruoli e Permessi (Security)
- **L'idea:** Un utente "base" non deve poter vedere o toccare le funzioni amministrative.
- **Il test:** Si effettua il login come utente standard. Si tenta di accedere forzatamente agli URL di amministrazione (ricevendo un errore 403 o redirect) e si verifica visivamente che i controlli di Drag & Drop e Provisioning siano nascosti o disabilitati.

---

### Ulteriori Test per aumentare la Coverage:

#### Unit Tests
- **Target:** `UserUnitTest.java`
- **L'idea:** Verificare che l'Entità Java funzioni isolata dal mondo.
- **Il test:** Istanzia la classe `UserRegistered` e controlla che Costruttori, Builder e valori di Default siano coerenti.

#### Controller Tests 
- **Target:** `AdminUserWebControllerTest` e `UserWebControllerTest`.
- **Tecnologia:** **MockMvc** per simulare richieste HTTP complete (GET/POST) senza avviare un server reale.

##### 1. Test sul Flusso Utente Completo (`UserWebController`)
- **L'idea:** Garantire che ogni interazione dell'utente finale (registrazione, login, gestione profilo) sia sicura e priva di errori.
- **Il test:** Vengono testati i meccanismi di validazione (password deboli, email errate), la protezione delle rotte (redirect al login se non autenticati) e l'integrità dei dati durante l'aggiornamento del profilo.

##### 2. Test sull'Amministrazione Utenti (`AdminUserWebController`)
- **L'idea:** Assicurare che l'amministratore possa gestire il ciclo di vita degli utenti senza causare incongruenze nel sistema.
- **Il test:** Copre tutte le operazioni CRUD (creazione, modifica, eliminazione), verificando in particolare i casi limite, come il tentativo di un admin di cancellare il proprio account (che deve fallire) o l'invio di dati incompleti.

---

Tutti questi test sommati hanno portato ad una coverage del 91% 

<img width="1382" height="266" alt="coverage" src="https://github.com/user-attachments/assets/65d9a525-d0d1-4163-8bf3-de7f491ac896" />


