# Universal Game Bot

## Versione
1.0 — Skeleton

## Obiettivo

Trasformare il progetto originale `crea-bot` in una piattaforma Android
locale e modulare capace di osservare, comprendere, apprendere, testare
e successivamente automatizzare giochi Android reali senza modificare
gli APK dei giochi e senza richiedere root.

Il sistema deve essere progettato per giochi semplici e complessi,
inclusi giochi a griglia, puzzle, giochi dinamici e futuri giochi
real-time.

## Principi fondamentali

- Local-first.
- Offline-first quando tecnicamente possibile.
- Nessuna API a pagamento obbligatoria.
- Nessun provider AI obbligatorio.
- Nessun falso completamento.
- Nessun valore di apprendimento inventato.
- 100% significa autonomia realmente verificata.
- Il punteggio può diminuire quando vengono rilevate situazioni nuove.
- L'apprendimento continua anche dopo il 100%.
- Arresto sicuro in caso di stato sconosciuto o bassa confidenza.
- Tutto deve rimanere modificabile e compilabile da Termux.
- Il PC/ADB/USB serve per sviluppo, debug e installazione, non per
  l'esecuzione normale del bot.

## Architettura principale

### Core

- ScreenCaptureManager
- VisionEngine
- GameDetector
- ActionEngine
- TimingEngine
- SafetyManager
- BotLoop

### Game Model

- GameState
- GameObject
- GameRule
- GameProfile
- GameAdapter
- GameEvent

### Learning

- LearningEngine
- ObservationEngine
- MemoryManager
- ErrorMemory
- StrategyMemory
- LearningScore
- ConfidenceManager

### Planning

- Planner
- StrategyEngine
- PredictionEngine
- GameSolver

### Verification

- ActionVerifier
- VictoryDetector
- FailureDetector
- AutonomousTestEngine
- RegressionDetector
- AutonomyCertificate

### AI

- AIRouter
- LocalAIProvider
- GeminiProvider
- OpenAIProvider
- CopilotProvider
- CloudProvider
- ZeroCostGuard

I provider esterni sono opzionali e non devono essere necessari
per il normale funzionamento offline.

### Storage

- Game profiles locali
- Learning state
- Memory
- Strategies
- Test results
- Error history
- Profile versions
- Import/export

## Ciclo operativo

Observe
→ Understand
→ Plan
→ Act
→ Verify
→ Learn
→ Observe

In caso di bassa confidenza:

Observe
→ Unknown state
→ Safety stop
→ Learning / user assistance

## Modalità

### AUTO

Il bot opera autonomamente utilizzando esclusivamente capacità
verificate.

### ASSISTITO

Il bot propone azioni e richiede conferma quando necessario.

### APPRENDIMENTO

L'utente gioca e il sistema osserva:
- schermate
- azioni
- conseguenze
- regole
- oggetti
- controlli
- obiettivi
- vittorie
- sconfitte
- transizioni
- livelli

L'apprendimento non deve limitarsi alla registrazione di coordinate
o macro.

### ALLENAMENTO

Il bot esegue prove controllate per migliorare strategie e verificare
la propria conoscenza.

## Learning Score

Il punteggio deve essere basato su evidenze reali.

Componenti previste:

- screen recognition
- object recognition
- controls
- rules
- objective
- state recognition
- strategy
- reactions
- error handling
- level progression
- victory detection
- failure detection
- autonomous tests
- unknown situation handling

Ogni componente deve poter conservare:
- score
- confidence
- evidence count
- successful tests
- failed tests
- last verification

### Stati indicativi

0–39   = Osservazione
40–69  = Apprendimento
70–89  = Test autonomi
90–99  = Quasi pronto
100    = Autonomia verificata

Il 100% non può essere assegnato semplicemente perché sono state
raccolte molte osservazioni.

## Autonomia verificata

Prima del 100% il sistema deve eseguire test autonomi senza intervento
dell'utente.

I test devono includere, quando possibile:

- situazioni già conosciute
- situazioni non identiche a quelle osservate
- livelli differenti
- errori
- transizioni
- sessioni prolungate
- vittoria
- sconfitta
- recupero
- comportamento sicuro davanti a situazioni sconosciute

Se il bot fallisce, il punteggio deve poter diminuire e il gioco deve
tornare nello stato di apprendimento/test.

## Categorie UI

### Home

- gioco rilevato
- stato bot
- Start
- Stop
- modalità
- autonomia

### Giochi

Elenco dei giochi conosciuti.

### Apprendimento

Elenco dei giochi ancora in apprendimento.

Ogni gioco mostra:
- barra reale
- percentuale
- stato
- confidence
- ultima attività
- componenti
- test eseguiti
- problemi

### Completati

Mostra esclusivamente giochi con autonomia realmente verificata.

### Laboratorio

Mostra:
- osservazioni
- eventi
- decisioni
- azioni
- verifiche
- errori
- apprendimento
- test autonomi

### AI e Risorse

Mostra:
- provider disponibili
- provider attivo
- stato locale
- eventuali quote
- operazioni esterne
- Zero-Cost Guard

### Impostazioni

- modalità
- sicurezza
- apprendimento
- privacy
- memoria
- import/export
- diagnostica

## Sicurezza

Il sistema deve sempre poter essere fermato rapidamente.

Se:
- lo stato non è riconosciuto
- la confidenza è troppo bassa
- il gioco cambia in modo inatteso
- un'azione produce un risultato non previsto

il bot deve preferire fermarsi o entrare in modalità assistita
anziché continuare alla cieca.

## Giochi

La piattaforma non deve essere costruita esclusivamente intorno
ad Amaze o Sudoku.

Primi adapter previsti:

- Amaze
- Sudoku
- 2048
- Chess

Adapter futuri:

- FIFA / EA SPORTS FC
- altri giochi real-time

## Giochi complessi

Per giochi dinamici come FIFA il sistema dovrà supportare:

- stato temporale
- tracking
- movimento
- previsione
- decisioni real-time
- reazioni
- correzione delle azioni
- strategie multiple

Un gioco real-time non deve essere trattato come una semplice
griglia statica.

## Versioning previsto

1.0  Skeleton
1.1  Core
1.2  Screen Capture + Vision
1.3  Game Detection
1.4  Learning Engine
1.5  Profiles + Memory
1.6  Autonomous Verification
1.7  AI Router
1.8  Zero-Cost + Offline
1.9  Amaze Adapter
2.0  Primo Universal Game Bot operativo

Ogni milestone deve essere verificata prima di iniziare la successiva.

## Compatibilità

Il progetto deve rimanere modificabile da Termux.

Ogni milestone deve fornire:

- file creati
- file modificati
- file spostati
- file eliminati
- dipendenze aggiunte
- versioni
- comando di build
- risultato build
- percorso APK quando disponibile
- problemi
- prossimo milestone

## Regola di sviluppo

Non implementare una funzione come "completata" se è solo uno
stub o una simulazione.

Gli elementi non ancora implementati devono essere chiaramente
identificati come:

- PLANNED
- SKELETON
- NOT IMPLEMENTED

Mai simulare un'autonomia del bot.
