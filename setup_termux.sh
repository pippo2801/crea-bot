#!/data/data/com.termux/files/usr/bin/bash
# =============================================================================
# 🚀 SETUP RAPIDO TERMUX PER 'CREA-BOT' ANDROID
# =============================================================================
# Esegui questo script all'interno di Termux dopo aver clonato il repository:
#   chmod +x setup_termux.sh
#   ./setup_termux.sh
# =============================================================================

set -e

echo "📦 [1/4] Aggiornamento dei repository di pacchetti Termux..."
pkg update -y && pkg upgrade -y

echo "🐍 [2/4] Installazione di Python, strumenti Android ADB e dipendenze..."
pkg install -y python android-tools git nano termux-api

echo "📚 [3/4] Installazione librerie Python necessarie (Pillow, NumPy)..."
# Su Termux pkg install python-pillow compila nativamente le estensioni C per ARM64
pkg install -y python-pillow python-numpy || pip install -r requirements.txt

echo "📱 [4/4] Configurazione permessi cartelle di sistema..."
termux-setup-storage

echo ""
echo "=========================================================="
echo "✅ Setup completato con successo!"
echo "=========================================================="
echo "Per testare la logica del solver:"
echo "   python bot/main.py --test"
echo ""
echo "Per avviare il bot sul gioco:"
echo "   python bot/main.py"
echo ""
echo "Per modificare le impostazioni e la risoluzione:"
echo "   nano bot/config.json"
echo "=========================================================="
