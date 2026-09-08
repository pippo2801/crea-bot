import express from "express";
import path from "path";
import { createServer as createViteServer } from "vite";
import { GoogleGenAI } from "@google/genai";
import JSZip from "jszip";

const app = express();
const PORT = 3000;

app.use(express.json({ limit: "10mb" }));

// In-memory store for generated bots so they can be downloaded or fetched via raw curl
interface BotProject {
  id: string;
  title: string;
  gameName: string;
  targetResolution: string;
  orientation: "portrait" | "landscape";
  language: "python" | "bash";
  createdAt: string;
  files: {
    name: string;
    content: string;
    language: string;
  }[];
  instructions: string;
}

const botStore = new Map<string, BotProject>();

// Server-side Gemini initialization
let aiClient: GoogleGenAI | null = null;
function getAI(): GoogleGenAI {
  if (!aiClient) {
    aiClient = new GoogleGenAI({
      apiKey: process.env.GEMINI_API_KEY || "",
      httpOptions: {
        headers: {
          "User-Agent": "aistudio-build",
        },
      },
    });
  }
  return aiClient;
}

// Health check
app.get("/api/health", (req, res) => {
  res.json({ status: "ok", timestamp: new Date().toISOString() });
});

// API: Generate Bot using Gemini AI
app.post("/api/generate-bot", async (req, res) => {
  try {
    const {
      gameName = "Android Game",
      prompt,
      resolution = "1080x2400",
      orientation = "portrait",
      language = "python", // 'python' | 'bash'
      antiBanJitter = true,
      failSafeKillswitch = true,
      coordinateSequence = [],
    } = req.body;

    if (!prompt || typeof prompt !== "string") {
      return res.status(400).json({ error: "Prompt descrittivo del bot richiesto." });
    }

    const ai = getAI();

    const systemInstruction = `Sei un esperto sviluppatore di automazione e scripting per dispositivi Android e Termux.
Il tuo compito è generare bot leggeri, efficienti e sicuri per automatizzare compiti e giochi su smartphone Android tramite Termux (utilizzando comandi ADB locali come 'input tap', 'input swipe', o librerie Python compatibili con Termux).
I bot devono poter essere eseguiti direttamente sullo smartphone Android dell'utente tramite Termux e devono essere facilmente modificabili usando l'editor 'nano' o 'vim' di Termux.

Regole di programmazione fondamentali:
1. Usare comandi ADB nativi eseguibili in Termux (ad es. 'adb shell input tap X Y' o 'input tap X Y' se eseguito in root/shizuku/adb shell).
2. Fornire gestione ritardi casuali (human-like jitter) se richiesto (es. random.uniform(min, max) o sleep $((RANDOM % ...))) per non sembrare un robot ed evitare ban.
3. Se l'utente ha fornito coordinate registrate o risoluzione, scalare e includere commenti chiarissimi su ogni riga per spiegare all'utente quale coordinata fa cosa.
4. Includere un meccanismo di arresto sicuro (Kill Switch): gestione pulita di SIGINT (Ctrl+C) o controllo se esiste un file di stop (/sdcard/stop_bot).
5. Rispondi RIGOROSAMENTE in formato JSON valido che rispetti la seguente struttura:
{
  "title": "Nome descrittivo del bot",
  "summary": "Breve descrizione in italiano delle azioni che esegue",
  "files": [
    {
      "name": "bot.py" (oppure "bot.sh"),
      "language": "python" (o "bash"),
      "content": "Codice sorgente completo e pronto per l'esecuzione"
    },
    {
      "name": "setup.sh",
      "language": "bash",
      "content": "Script bash per installare su Termux i pacchetti necessari (es: pkg update -y && pkg install python android-tools -y...)"
    },
    {
      "name": "config.json",
      "language": "json",
      "content": "File di configurazione con parametri regolabili (ritardi, coordinate, numero di cicli) modificabili con nano"
    },
    {
      "name": "README.md",
      "language": "markdown",
      "content": "Guida rapida in italiano per Termux: come installare, avviare, fermare con Ctrl+C e modificare con nano"
    }
  ],
  "termuxQuickCommand": "Comando rapido da incollare in Termux per lanciare il bot",
  "notes": "Consigli utili per il gioco o Termux"
}`;

    const userPrompt = `Crea un bot per Android Termux per il gioco: "${gameName}".
Descrizione dell'automazione richiesta:
${prompt}

Specifiche tecniche del dispositivo:
- Risoluzione schermo: ${resolution}
- Orientamento: ${orientation}
- Linguaggio preferito: ${language}
- Anti-ban (Jitter casuale su ritardi e coordinate): ${antiBanJitter ? "Sì (attivo)" : "No"}
- Kill switch di sicurezza (Ctrl+C e verifica file stop): ${failSafeKillswitch ? "Sì (attivo)" : "No"}
${
  coordinateSequence && coordinateSequence.length > 0
    ? `- Coordinate specifiche fornite dall'utente: ${JSON.stringify(coordinateSequence)}`
    : ""
}

Genera codice pronto all'uso, senza placeholder non definiti, con commenti dettagliati in italiano così che l'utente possa aprirlo con 'nano bot.${language === "python" ? "py" : "sh"}' e modificarlo direttamente dal telefono.`;

    const response = await ai.models.generateContent({
      model: "gemini-3.8-flash",
      contents: userPrompt,
      config: {
        systemInstruction,
        responseMimeType: "application/json",
      },
    });

    const responseText = response.text || "{}";
    let parsed;
    try {
      parsed = JSON.parse(responseText);
    } catch (e) {
      // Clean possible markdown codeblocks if model wrapped in ```json
      const cleaned = responseText.replace(/```json\n?/g, "").replace(/```\n?/g, "").trim();
      parsed = JSON.parse(cleaned);
    }

    const botId = "bot_" + Date.now().toString(36) + "_" + Math.random().toString(36).substring(2, 6);
    const botProject: BotProject = {
      id: botId,
      title: parsed.title || `${gameName} AutoBot`,
      gameName,
      targetResolution: resolution,
      orientation: orientation as "portrait" | "landscape",
      language: language as "python" | "bash",
      createdAt: new Date().toISOString(),
      files: parsed.files || [],
      instructions: parsed.summary || "",
    };

    botStore.set(botId, botProject);

    res.json({
      success: true,
      botId,
      project: {
        ...botProject,
        termuxQuickCommand: parsed.termuxQuickCommand || `python bot.py`,
        notes: parsed.notes || "",
      },
    });
  } catch (error: any) {
    console.error("Errore durante la generazione del bot:", error);
    res.status(500).json({
      error: "Impossibile generare il bot con l'IA: " + (error.message || "Errore sconosciuto"),
    });
  }
});

// API: Refine existing bot
app.post("/api/refine-bot", async (req, res) => {
  try {
    const { currentFiles, modificationRequest, gameName = "Android Game" } = req.body;

    if (!modificationRequest || !currentFiles) {
      return res.status(400).json({ error: "File attuali e istruzioni di modifica richiesti." });
    }

    const ai = getAI();

    const systemInstruction = `Sei un esperto programmatore di script Termux Android per bot di giochi.
L'utente vuole modificare o perfezionare uno script bot esistente.
Riceverai i file attuali e la richiesta di modifica.
Restituisci i file aggiornati e completi rigorosamente in formato JSON:
{
  "summary": "Cosa è stato modificato",
  "files": [
    {
      "name": "nome_file",
      "language": "python" | "bash" | "json" | "markdown",
      "content": "Codice completo aggiornato"
    }
  ]
}`;

    const promptText = `File attuali:
${JSON.stringify(currentFiles, null, 2)}

Richiesta di modifica per il bot del gioco "${gameName}":
${modificationRequest}

Aggiorna i file implementando esattamente le modifiche richieste, mantenendo commenti in italiano e massima compatibilità con Termux.`;

    const response = await ai.models.generateContent({
      model: "gemini-3.8-flash",
      contents: promptText,
      config: {
        systemInstruction,
        responseMimeType: "application/json",
      },
    });

    const responseText = response.text || "{}";
    const cleaned = responseText.replace(/```json\n?/g, "").replace(/```\n?/g, "").trim();
    const parsed = JSON.parse(cleaned);

    res.json({
      success: true,
      summary: parsed.summary || "Bot aggiornato con successo.",
      files: parsed.files || currentFiles,
    });
  } catch (error: any) {
    console.error("Errore modifica bot:", error);
    res.status(500).json({
      error: "Impossibile aggiornare il bot: " + (error.message || "Errore sconosciuto"),
    });
  }
});

// API: Download all bot files as a ZIP package
app.post("/api/download-zip", async (req, res) => {
  try {
    const { botTitle = "crea-bot-android", files = [] } = req.body;

    const zip = new JSZip();
    const safeTitle = botTitle.toLowerCase().replace(/[^a-z0-9_-]/g, "_");
    const folder = zip.folder(safeTitle) || zip;

    for (const file of files) {
      folder.file(file.name, file.content);
    }

    const buffer = await zip.generateAsync({ type: "nodebuffer" });

    res.setHeader("Content-Type", "application/zip");
    res.setHeader("Content-Disposition", `attachment; filename="${safeTitle}.zip"`);
    res.send(buffer);
  } catch (error: any) {
    console.error("Errore creazione zip:", error);
    res.status(500).json({ error: "Errore durante la creazione dello zip." });
  }
});

// API: Raw file download/view (useful for direct curl in Termux!)
app.get("/api/raw/:botId/:fileName", (req, res) => {
  const { botId, fileName } = req.params;
  const project = botStore.get(botId);
  if (!project) {
    return res.status(404).send("# Bot non trovato o sessione scaduta");
  }

  const file = project.files.find((f) => f.name.toLowerCase() === fileName.toLowerCase());
  if (!file) {
    return res.status(404).send("# File non trovato nel bot specificato");
  }

  res.setHeader("Content-Type", "text/plain; charset=utf-8");
  res.send(file.content);
});

async function startServer() {
  // Vite middleware for development
  if (process.env.NODE_ENV !== "production") {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: "spa",
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), "dist");
    app.use(express.static(distPath));
    app.get("*", (req, res) => {
      res.sendFile(path.join(distPath, "index.html"));
    });
  }

  app.listen(PORT, "0.0.0.0", () => {
    console.log(`Server running on http://0.0.0.0:${PORT}`);
  });
}

startServer();
