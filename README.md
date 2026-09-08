# 🤖 crea-bot: Bot Dinamico per Giochi a Griglia su Android (Termux)

Sistema modulare di automazione intelligente per giochi basati su griglia e labirinto a scivolamento (come **Amaze GO**, **Roller Splat**, **Paint the Maze**) eseguibile e modificabile direttamente su smartphone Android tramite **Termux**.

A differenza dei bot tradizionali a coordinate fisse (che falliscono quando le dimensioni del labirinto o gli ostacoli cambiano), questo bot utilizza un modulo di **visione dinamica** basato sull'analisi dello schermo e un **risolutore algoritmico BFS** per identificare automaticamente la palla, i muri e le celle da colorare ad ogni livello.

---

## 📁 Struttura del Progetto

```
crea-bot/
├── bot/
│   ├── main.py          # Entry point del bot: loop di cattura, calcolo ed esecuzione
│   ├── vision.py        # Visione dinamica: analisi schermo, sampling colore, griglia
│   ├── solver.py        # Risolutore BFS/A*: calcola la sequenza ideale di swipe
│   ├── controller.py    # Interfaccia input Android (ADB Wireless, Shizuku, Root)
│   └── config.json      # File di configurazione (sensibilità, ritardi, margini)
├── src/                 # Web UI / Console di gestione in React + Tailwind
├── server.ts            # Server Node.js / Express con API Gemini per la personalizzazione
├── requirements.txt     # Dipendenze Python (Pillow, NumPy)
├── package.json         # Dipendenze Web / Servizi
├── setup_termux.sh      # Script di installazione automatica con un comando
├── metadata.json        # Metadati dell'applicazione
└── README.md            # Questa guida
```

---

## 📲 Installazione Rapida su Termux (Android)

### 1. Prerequisiti
1. Scarica e installa **Termux** da [F-Droid](https://f-droid.org/en/packages/com.termux/) *(non usare la versione obsoleta del Play Store)*.
2. Su Android 11, 12, 13 o 14 attiva **Opzioni Sviluppatore > Debug Wireless**.

### 2. Clona il Repository in Termux
Apri Termux e digita:

```bash
pkg install -y git
git clone https://github.com/pippo2801/crea-bot.git
cd crea-bot
```

### 3. Esegui il Setup Automatico
```bash
chmod +x setup_termux.sh
./setup_termux.sh
```

---

## ⚡ Connessione ADB Wireless senza PC (Android 11+)

Puoi usare la funzione schermo diviso (Split Screen) o finestra fluttuante del telefono:

1. Vai in **Impostazioni > Opzioni Sviluppatore > Debug Wireless**.
2. Tocca *"Associa dispositivo con codice di accoppiamento"* (mostrerà una porta e un codice a 6 cifre).
3. In Termux inserisci:
   ```bash
   adb pair localhost:PORTA_ACCOPPIAMENTO
   ```
   *(Inserisci il codice di 6 cifre quando richiesto)*.
4. Connettiti alla porta principale di Debug Wireless:
   ```bash
   adb connect localhost:PORTA_PRINCIPALE
   ```

---

## 🎮 Avvio e Controllo del Bot

### Test Simulazione (Senza bisogno del gioco aperto)
Verifica l'algoritmo di risoluzione direttamente dal terminale:
```bash
python bot/main.py --test
```

### Avvio sul Gioco (Amaze GO / Giochi a Griglia)
Apri il gioco sul telefono, passa a Termux (in modalità split-screen o finestra mobile) e avvia:
```bash
python bot/main.py
```

### Risolvi solo il Livello Corrente
```bash
python bot/main.py --once
```

---

## ✏️ Come Modificare i Parametri Direttamente dal Telefono

Non serve un computer: puoi modificare ogni aspetto del bot direttamente con l'editor da terminale `nano`:

```bash
nano bot/config.json
```

- Modifica `swipe_distance_px` se gli swipe non coprono tutta la lunghezza.
- Modifica `default_rows` o `default_cols` in base al livello.
- **Salva**: premi `Ctrl + O` e poi `Invio`.
- **Esci**: premi `Ctrl + X`.

---

## 🛑 Arresto di Emergenza (Kill Switch)

- Premi `Ctrl + C` nella finestra di Termux.
- Oppure da qualsiasi shell digita `touch /sdcard/stop_bot`: il bot interromperà immediatamente l'esecuzione al ciclo successivo.
