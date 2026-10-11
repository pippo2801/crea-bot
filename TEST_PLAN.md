# Universal Game Bot — Piano di collaudo

## Regole prima dei test

- Non generare o installare APK senza autorizzazione esplicita dell'utente.
- Non dichiarare un gioco autonomo sulla sola base del punteggio di apprendimento.
- Iniziare con permessi, osservazione e azioni innocue; niente tocchi automatici in acquisti, pagamenti, chat o schermate account.
- Conservare log di esito, dispositivo/Android, versione app e gioco testato.
- Interrompere il test se lo schermo rilevato non corrisponde allo stato reale, se il bot ripete azioni senza progresso o se la sicurezza non è chiara.

## Fase A — CI senza APK

1. Controllare che Java 17, Android SDK 34 e Gradle siano disponibili.
2. Eseguire `gradle --no-daemon testDebugUnitTest`.
3. Confermare i test unitari per:
   - soglie del punteggio di apprendimento;
   - punteggio limitato a 0–100;
   - nessun certificato se non ci sono controlli reali;
   - certificato soltanto quando tutti i controlli configurati passano;
   - eccezioni dei controlli conteggiate come fallimenti.
4. Registrare il commit e l'URL della run. Nessuna APK build in questa fase.

## Fase B — Installazione e permessi (solo dopo autorizzazione alla build)

1. Installare manualmente la build autorizzata sul dispositivo di prova.
2. Aprire l'app e verificare avvio, navigazione, schermate principali e assenza di crash.
3. Negare e concedere separatamente i permessi richiesti; verificare che l'app spieghi come recuperare da un permesso negato.
4. Verificare MediaProjection: consenso esplicito del sistema, avvio/arresto cattura, nessuna cattura dopo l'arresto.
5. Verificare che l'utente possa interrompere il bot immediatamente.

## Fase C — Osservazione senza azioni

1. Aprire un gioco supportato e avviare solo l'osservazione.
2. Controllare screenshot, dimensioni, orientamento e rilevamento del gioco.
3. Confrontare lo stato riconosciuto con quello realmente visibile.
4. Ripetere con caricamento, pausa, overlay, cambio orientamento e schermata non riconosciuta.
5. Nessuna azione automatica finché il riconoscimento non è affidabile.

## Fase D — Azioni controllate

1. Usare prima un gioco semplice e reversibile (Amaze GO o Sudoku).
2. Verificare che ogni azione abbia coordinate valide e limiti di frequenza.
3. Verificare stop immediato, timeout, gestione di schermate inattese e assenza di loop incontrollati.
4. Confrontare piano, azione eseguita e risultato osservato.
5. Ripetere almeno 10 sessioni brevi, annotando successi, falsi positivi e blocchi.

## Fase E — Verifica per singolo gioco

Per ogni gioco creare una scheda separata con:
- nome e versione del gioco;
- dispositivi/risoluzioni testati;
- schermate riconosciute;
- azioni consentite e azioni vietate;
- condizioni vittoria/sconfitta e loro evidenza;
- test passati/falliti e log;
- limiti noti.

EA SPORTS FC Mobile richiede un piano dedicato e test iniziali in modalità non competitiva. Non dichiarare supporto completo o autonomia finché non siano dimostrati riconoscimento, azioni appropriate, gestione delle transizioni e stop sicuro.

## Criteri di uscita

Un test passa solo con risultato osservabile e ripetibile. Un certificato di autonomia richiede almeno un controllo reale e il successo di tutti i controlli configurati. Le prove su un gioco non certificano automaticamente gli altri giochi.
