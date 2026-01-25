# 📘 SmartTracking - Flusso Logico del Sistema

Questo documento descrive il funzionamento logico dell'applicazione, dall'avvio del server fino alle operazioni specifiche come il provisioning dei dispositivi.

---

## 1. Avvio dell'Applicazione (Startup)
Quando si esegue il main in [SmartTrackApplication.java](src/main/java/it/univr/track/SmartTrackApplication.java):

1.  **Spring Boot Init**: Spring avvia il contesto, si connette al database (H2 o MySQL) e configura i bean.
2.  **Inizializzazione Mock (Statico)**:
    *   La classe [AdminWebController.java](src/main/java/it/univr/track/controller/web/AdminWebController.java) viene caricata in memoria.
    *   Il blocco `static { ... }` (riga ~27) esegue l'inizializzazione dei **Dispositivi Mock**.
    *   Vengono creati dispositivi non assegnati (es. "Sensore Umidità") e preparata la mappa `userDevicesMap` per conservare lo stato dei dispositivi durante la sessione.

---

## 2. Autenticazione e Ruoli
Il sistema utilizza **Spring Security** per gestire gli accessi.

*   **Login**: L'utente inserisce le credenziali. Il sistema verifica l'hash della password nel database comparandola con l'entity [UserRegistered.java](src/main/java/it/univr/track/entity/UserRegistered.java).
*   **Routing**:
    *   Se le credenziali sono valide, l'oggetto `Authentication` viene popolato.
    *   I controller usano questo oggetto per decidere cosa mostrare (es. `th:if="${isAdmin}"` nelle view Thymeleaf).

---

## 3. Gestione Utenti (Flusso Reale)
Questo modulo gestisce dati persistenti veri nel Database.

*   **Controller**: [AdminUserWebController.java](src/main/java/it/univr/track/controller/web/AdminUserWebController.java)
*   **Visualizzazione**: Quando l'Admin va su "Gestione Utenti" ([userListAdministration.html](src/main/resources/templates/userListAdministration.html)), il controller chiama [UserRepository.java](src/main/java/it/univr/track/repository/UserRepository.java) tramite `userRepository.findAll()`.
*   **Modifica**:
    1.  L'Admin clicca "Modifica".
    2.  Viene caricato il form [editUserAdmin.html](src/main/resources/templates/editUserAdmin.html) precompilato con i dati del DB.
    3.  Al salvataggio (`/admin/users/update`), se viene inserita una password, questa viene hashata e aggiornata. Se lasciata vuota, viene mantenuta la vecchia.

---

## 4. Gestione Globale / Mappa (Flusso Ibrido)
Questa è la parte più complessa perché unisce **Utenti Reali** con **Dispositivi Finti**.

*   **Controller**: [AdminWebController.java](src/main/java/it/univr/track/controller/web/AdminWebController.java) (Metodo `gestioneGlobale`)
*   **Il Processo di Costruzione della Vista**:
    1.  Il controller interroga il DB ([UserRepository.java](src/main/java/it/univr/track/repository/UserRepository.java)) per ottenere tutti gli **Utenti Reali**.
    2.  Cicla su ogni utente reale e crea un oggetto di visualizzazione `UserViewModel` (classe interna a `AdminWebController`).
    3.  Per ogni utente, controlla nella memoria statica (`userDevicesMap`) se ha dei dispositivi assegnati.
    4.  Il risultato è una lista ibrida: Utenti veri che possiedono Dispositivi simulati.
    5.  Questa lista viene passata alla vista [gestioneGlobale.html](src/main/resources/templates/gestioneGlobale.html) per disegnare la mappa/lista.

---

## 5. Dettaglio Dispositivo e Provisioning
Simulazione dell'attivazione di un sensore hardware.

*   **Pagina**: [dettaglioDeviceMock.html](src/main/resources/templates/dettaglioDeviceMock.html)
*   **Stato**: I dati del dispositivo (ID, Nome, Stato) provengono dalla memoria statica di `AdminWebController`.

### Il Flow del Provisioning (Click sullo Switch):
1.  **Trigger Frontend**: L'utente clicca lo switch "Provisioning" in [dettaglioDeviceMock.html](src/main/resources/templates/dettaglioDeviceMock.html).
2.  **Chiamata AJAX**: Javascript invia una richiesta `POST /device-mock/{id}/provision` al server.
3.  **Logica Server**:
    *   Trovata in [AdminWebController.java](src/main/java/it/univr/track/controller/web/AdminWebController.java) metodo `toggleProvision`.
    *   Riceve la richiesta, imposta il flag `provisioned = true` e assegna il MAC Address.
    *   **Crittografia**: Istanzia `KeyPairGenerator` (RSA 2048 bit) e genera una coppia di chiavi reale.
4.  **Risposta**:
    *   Il server restituisce un JSON contenente la **Chiave Privata**.
    *   La Chiave Pubblica viene loggata nella console del server.
5.  **Visualizzazione**: Javascript riceve il JSON e mostra il box viola con la Chiave Privata all'utente nel DOM.

---

## 6. Persistenza dei Dati
*   **Utenti**: Persistono per sempre (Database H2 su file o MySQL tramite [UserRepository.java](src/main/java/it/univr/track/repository/UserRepository.java)).
*   **Dispositivi Mock/Stato Provisioning**: Persistono nella RAM di [AdminWebController.java](src/main/java/it/univr/track/controller/web/AdminWebController.java) finché l'applicazione Java è accesa.

---

## Schema Riassuntivo File Chiave

| Funzionalità | File Controller | File HTML | Fonte Dati |
| :--- | :--- | :--- | :--- |
| **Lista Utenti (CRUD)** | [AdminUserWebController.java](src/main/java/it/univr/track/controller/web/AdminUserWebController.java) | [userListAdministration.html](src/main/resources/templates/userListAdministration.html) | Database Reale (JPA) |
| **Modifica Utente** | [AdminUserWebController.java](src/main/java/it/univr/track/controller/web/AdminUserWebController.java) | [editUserAdmin.html](src/main/resources/templates/editUserAdmin.html) | Database Reale (JPA) |
| **Mappa/Asset View** | [AdminWebController.java](src/main/java/it/univr/track/controller/web/AdminWebController.java) | [gestioneGlobale.html](src/main/resources/templates/gestioneGlobale.html) | Ibrido (DB + RAM Static) |
| **Dettaglio & Crypto** | [AdminWebController.java](src/main/java/it/univr/track/controller/web/AdminWebController.java) | [dettaglioDeviceMock.html](src/main/resources/templates/dettaglioDeviceMock.html) | RAM Static |
