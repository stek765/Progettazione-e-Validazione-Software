report
# SmartTracking - Sistema IoT per la Logistica Intelligente

**Corso:** Ingegneria del Software / Requirements Engineering  
**Anno Accademico:** 2025/2026  
**Docente:** Prof. Mariano Ceccato

## 👥 Team di Sviluppo
* **Bottega Marica** (mettere matricola)
* **Cicciarella Enrico** (mettere matricola)
* **Zanolli Stefano** (mettere matricola)

---

## 1. Introduzione e Contesto
**SmartTracking** è un sistema software progettato per la gestione e il monitoraggio di spedizioni sensibili. 
Il sistema si interfaccia con dispositivi IoT a basso costo dotati di sensori per raccogliere dati ambientali (temperatura, umidità, vibrazioni) durante il trasporto e lo stoccaggio di merci fragili.

L'obiettivo del progetto è garantire l'integrità della filiera logistica, permettendo agli attori coinvolti (Admin, Magazzinieri, Clienti) di visualizzare lo stato dei dispositivi e dei prodotti tramite una dashboard web accessibile anche via QR Code.

---

## 2. Requisiti e Scenari
Sulla base dell'analisi dei requisiti (Goal Diagram e Obstacle Analysis), abbiamo implementato i seguenti **9 scenari** principali, coprendo le funzionalità critiche del sistema per il modulo "Anagrafica e controllo accessi".

### Scenari Implementati (E2E) //TODO DA MODIFICARE
1.  **Registrazione Utente:** Creazione di un nuovo account (Cliente/Staff) con validazione dei dati (password strength, campi obbligatori).
2.  **Autenticazione e Login:** Accesso sicuro al portale con reindirizzamento in base al ruolo (Admin vs User).
3.  **Gestione Utenti (Admin):** L'amministratore visualizza la lista degli utenti registrati e può rimuoverli dal sistema.
4.  **Modifica Profilo:** L'utente autenticato aggiorna le proprie informazioni personali (nome, contatti, indirizzo).
5.  **Registrazione Dispositivo IoT:** Inserimento di un nuovo sensore/device nel sistema (Provisioning).
6.  **Assegnazione Dispositivo:** Un magazziniere o admin associa un dispositivo "libero" ad una specifica spedizione o utente (gestito tramite interfaccia Drag & Drop).
7.  **Dashboard di Monitoraggio:** Visualizzazione dello stato dei dispositivi e dei dati raccolti.
8.  **Logout:** Disconnessione sicura dalla sessione utente.
9.  **Gestione Errori/Validazione:** Feedback visivo in caso di tentativi di azioni non autorizzate o dati mancanti (es. form registrazione incompleto).

---

## 3. Metodologia di Sviluppo: TDD
Il progetto è stato sviluppato seguendo rigorosamente la metodologia **Test-Driven Development (TDD)**. Questo approccio ha guidato l'architettura del sistema, garantendo che ogni funzionalità fosse testabile e robusta fin dalla nascita.

### Il Ciclo Red-Green-Refactor applicato
Per ogni funzionalità, abbiamo seguito questi step:

1.  🔴 **RED (Scrittura del Test):**
    Abbiamo definito *Acceptance Tests* con Selenium prima di implementare la UI.
    * *Esempio:* Nel test `testDeleteUser`, abbiamo asserito che un utente sparisse dalla tabella HTML prima ancora di implementare il bottone "Elimina" nel frontend o la logica nel Controller.

2.  🟢 **GREEN (Implementazione):**
    Abbiamo scritto il codice minimo (Controller Spring MVC, Repository JPA, Template Thymeleaf) per far passare il test.
    * *Soluzione:* Implementazione di `GestioneUtentiPage` e correzione delle Entity per supportare la persistenza.

3.  🔵 **REFACTOR (Ottimizzazione):**
    Abbiamo migliorato la qualità del codice e dei test senza alterare il comportamento.
    * *Page Object Pattern:* Abbiamo estratto la logica di interazione con il DOM (selettori CSS, XPath) in classi dedicate (`ProfileAdminPage`, `RegistrationPage`) per rendere i test leggibili e manutenibili[cite: 738].
    * *Gestione Wait:* Abbiamo sostituito i `Thread.sleep` con `WebDriverWait` e condizioni esplicite (es. `waitForTextToAppear`, `invisibilityOfElementLocated`) per risolvere problemi di *flakiness* dovuti alle animazioni frontend.

---

## 4. Implementazione Tecnica

### Stack Tecnologico
* **Linguaggio:** Java 21
* [cite_start]**Build System:** Gradle [cite: 717]
* **Framework:** Spring Boot 3.2 (Web MVC, Security, Data JPA)
* **Database:** H2 In-Memory (per test rapidi e isolati)
* **Frontend:** Thymeleaf + HTML5/CSS3 + JavaScript (Anime.js per animazioni)
* **Testing:** JUnit 5, Selenium WebDriver, Mockito

### Architettura
Il sistema segue un'architettura a livelli (Layered Architecture):
1.  **Entity Layer:** Classi persistenti (`UserRegistered`, `Device`, `AbstractEntity`) che mappano il dominio.
2.  **Repository Layer:** Interfacce che estendono `CrudRepository` o `JpaRepository` per l'accesso ai dati.
3.  **Service Layer:** Logica di business e validazione (es. `CustomUserDetailsService`).
4.  **Controller Layer:** Gestione delle rotte HTTP e orchestrazione delle viste.

---

## 5. Quality Assurance

### Acceptance Testing (Selenium)
Abbiamo coperto gli scenari d'uso principali con test E2E (End-to-End).
I test simulano un utente reale che interagisce con il browser (Firefox/GeckoDriver).

**Strategie adottate per la stabilità dei test:**
* **Database Reset:** Uso di `@BeforeEach` per pulire e popolare il DB (H2) prima di ogni test, garantendo isolamento.
* **Page Object Model:** Ogni pagina HTML ha una classe Java corrispondente che espone metodi ad alto livello (es. `loginPage.performLogin(...)`, `gestionePage.deleteUser(...)`).
* **Gestione Asincrona:** Risoluzione di *Race Conditions* (es. test che fallivano perché controllavano la tabella prima che l'utente venisse cancellato) tramite `ExpectedConditions` personalizzate.

### Unit Testing
I test unitari coprono la logica interna dei servizi e delle entità, isolando le dipendenze esterne tramite Mockito.

---

## 6. Istruzioni per l'Esecuzione

### Prerequisiti
* JDK 21 installato.
* Browser Firefox installato (per i test Selenium).

### Comandi Gradle

Per eseguire l'intera suite di test (Unitari + Accettazione):
```bash
./gradlew test