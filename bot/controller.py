"""
=============================================================================
🎮 MODULO CONTROLLER INPUT ANDROID (bot/controller.py)
=============================================================================
Gestione dei tocchi e gesti di swipe per Termux / Android:
- Esegue swipe e tap nativi tramite comando 'input swipe' o 'input tap'
- Rileva automaticamente la modalità di esecuzione:
    1. ADB locale / Wireless (es. adb shell input ...)
    2. Shell root / Shizuku
    3. Shell diretta Termux
- Aggiunge variazione casuale (anti-bot human jitter)
=============================================================================
"""

import subprocess
import random
import time
import shutil

class AndroidController:
    def __init__(self, config=None):
        self.config = config or {
            "swipe_duration_ms": 180,
            "swipe_distance_px": 280,
            "jitter_range": 6,
            "delay_after_swipe_s": 0.45,
            "adb_prefix": None  # "adb shell", "su -c", o diretto
        }
        self.mode = self._detect_execution_mode()

    def _detect_execution_mode(self) -> str:
        """
        Rileva quale metodo usare per inviare comandi di input su Android:
        - "direct": se 'input' è disponibile direttamente nel PATH corrente (es. adb shell o root shell)
        - "adb": se 'adb' è installato e configurato (standard per Termux con Debug Wireless)
        - "su": se il device ha privilegi di root
        """
        # Test diretto
        if shutil.which("input"):
            return "direct"

        # Test adb
        if shutil.which("adb"):
            try:
                out = subprocess.run(["adb", "devices"], capture_output=True, text=True, timeout=2)
                if "device" in out.stdout:
                    return "adb"
            except Exception:
                pass

        # Test root su
        if shutil.which("su"):
            return "su"

        return "adb"  # default fallback

    def _run_cmd(self, cmd_args):
        """Esegue il comando Android adattandosi alla modalità rilevata."""
        try:
            if self.mode == "adb":
                full_cmd = ["adb", "shell"] + cmd_args
            elif self.mode == "su":
                full_cmd = ["su", "-c", " ".join(cmd_args)]
            else:
                full_cmd = cmd_args

            subprocess.run(full_cmd, check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            return True
        except Exception as e:
            print(f"[Controller] Errore esecuzione comando {cmd_args}: {e}")
            return False

    def tap(self, x: int, y: int):
        """Esegue un tap su coordinate (x, y) con jitter anti-ban casuale."""
        jitter_x = random.randint(-self.config.get("jitter_range", 4), self.config.get("jitter_range", 4))
        jitter_y = random.randint(-self.config.get("jitter_range", 4), self.config.get("jitter_range", 4))
        tx = max(0, x + jitter_x)
        ty = max(0, y + jitter_y)
        self._run_cmd(["input", "tap", str(tx), str(ty)])

    def swipe(self, x1: int, y1: int, x2: int, y2: int, duration_ms: int = None):
        """Esegue uno swipe fluido tra (x1, y1) e (x2, y2)."""
        dur = duration_ms or self.config.get("swipe_duration_ms", 180)
        # Aggiunta micro-variazione temporale realistica
        dur += random.randint(-15, 25)
        dur = max(60, dur)

        self._run_cmd(["input", "swipe", str(int(x1)), str(int(y1)), str(int(x2)), str(int(y2)), str(dur)])
        time.sleep(self.config.get("delay_after_swipe_s", 0.40))

    def swipe_direction(self, direction: str, center_x: int = 540, center_y: int = 1200, distance: int = None):
        """
        Esegue uno swipe nella direzione specificata ("UP", "DOWN", "LEFT", "RIGHT").
        Le coordinate di partenza possono essere il centro dello schermo o la posizione corrente della palla.
        """
        dist = distance or self.config.get("swipe_distance_px", 320)
        jitter = random.randint(-self.config.get("jitter_range", 5), self.config.get("jitter_range", 5))

        x1 = center_x + jitter
        y1 = center_y + jitter

        if direction == "UP":
            x2 = x1
            y2 = y1 - dist
        elif direction == "DOWN":
            x2 = x1
            y2 = y1 + dist
        elif direction == "LEFT":
            x2 = x1 - dist
            y2 = y1
        elif direction == "RIGHT":
            x2 = x1 + dist
            y2 = y1
        else:
            print(f"[Controller] Direzione sconosciuta: {direction}")
            return

        print(f"  👉 Swipe [{direction}]: ({x1}, {y1}) -> ({x2}, {y2})")
        self.swipe(x1, y1, x2, y2)
