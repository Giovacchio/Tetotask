# TetoTask come app Android 📱

La cartella `android/` è un progetto Android Studio completo, già pronto:
non serve installare Node o lanciare comandi.

## Primo avvio
1. **Android Studio → Open →** scegli la cartella `Tetotask/android`.
2. Aspetta la sincronizzazione di Gradle: la prima volta scarica i componenti Android (qualche minuto).
   ⚠️ Se compare l'**Upgrade Assistant** ("Upgrade Android Gradle Plugin…"), **non aggiornare**: chiudilo.
   Il progetto usa apposta la versione 8.13 (quella supportata da Capacitor). Serve Android Studio 2025 o successivo.
3. Collega il telefono con il debug USB attivo (oppure usa il Wi-Fi debugging) e premi **Run ▶**.

## Al primo uso sul telefono
- Consenti le **notifiche**.
- Se TetoTask te lo chiede, attiva **"Sveglie e promemoria"**, così i promemoria suonano all'ora esatta.
- Il **microfono** viene chiesto la prima volta che premi 🎤.
- Consigliato: Impostazioni Android → App → TetoTask → Batteria → **Senza restrizioni**.
  Su Xiaomi, Oppo e Huawei attiva anche l'**avvio automatico**.

## Cosa cambia rispetto alla versione web
- I promemoria, gli eventi e i timer vengono **programmati nel telefono**:
  suonano anche ad app chiusa, senza internet, e restano dopo un riavvio.
- Le notifiche hanno i pulsanti **✓ Fatto** e **⏰ +10 min**.
- Microfono e voce usano quelli di Android.
- Il tasto **indietro** chiude i pannelli.
- L'app parte anche offline (le librerie sono incluse).

## Quando aggiorni TetoTask
1. Aggiorna `index.html` come sempre (anche il sito web si aggiorna col push).
2. Fai **doppio clic su `aggiorna-app.bat`**.
3. In Android Studio premi **Run ▶**.

## Limiti di questa prima versione
- **Accesso Google**: nell'app arriva nel prossimo passo, col login nativo.
  Fino ad allora l'app usa un account anonimo suo, quindi non vede i dati della versione web.
- Il **"Ehy Teto"** sempre in ascolto non c'è nell'app. Il microfono funziona con il tasto 🎤.
- **Widget**: prossimo passo.
