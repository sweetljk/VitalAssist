# VitalAssist

A client-side survival utility mod for Fabric 26.2. Provides various quality-of-life features with a configurable GUI and hotkeys via malilib.

## Features

| Feature | Description |
|---------|-------------|
| **Auto Tool** | Automatically switches to the fastest tool in your hotbar when mining |
| **Auto Eat** | Automatically eats food when hunger or health is low |
| **Auto Totem** | Automatically equips totems to offhand; supports shulker restocking |
| **Freecam** | Detach camera from player for free movement; full key control |
| **HaoQiChongTian** | Freeze mid-air with third-person camera rotation and sound effect |
| **No Slow** | Disables slime block movement slowdown |
| **Silent Firework** | Switch and use fireworks directly from inventory |
| **Pearl Trajectory** | Renders predicted ender pearl trajectory |
| **Firework Warning** | On-screen warning when fireworks are below threshold |
| **Totem Warning** | On-screen warning when totems are below threshold |
| **Better Chat** | Chat regex filtering and folding |
| **ZhouLi Chat** | Rewrites outgoing chat through DeepSeek using a classical Chinese tone |
| **Chat Translation** | Translates outgoing chat through DeepSeek into a configurable target language (English by default) |
| **Cinematic Camera** | Idle- or hotkey-triggered beat-synced camera shots with letterbox bars, transitions, wildlife focus and lyrics while music plays (needs DangoMusic) |
| **Command Completion Filter** | Block specified commands from tab completion |
| **Hide Entity Health** | Blocks FZ datapack health display under entity nametags |
| **Shulker Restock** | Auto-restock pearls/fireworks from shulkers (requires Quick Shulker) |

## License

Released under the MIT License. See `LICENSE`.

## Dependencies

- **Fabric Loader** >= 0.20.1
- **Fabric API** (latest 0.161.0+26.2 recommended)
- **malilib** (required — provides config UI)
- **Mod Menu** (recommended for easy config management)
- **Quick Shulker** (optional — needed for shulker restock)
- **DangoMusic** (optional — supplies the music playback, beat grid and lyrics used by the Cinematic Camera; auto-entry needs it playing, and lyrics are fetched from its NetEase lyric API)

## Installation

1. Install Fabric Loader
2. Place the mod jar in `.minecraft/mods`
3. Download **malilib** and place it in the mods folder
4. (Optional) Install Mod Menu and/or Quick Shulker
5. Launch the game and press `Z + L` to open the config menu

## Configuration

All features are managed through the malilib config screen, grouped into tabs: All / General / Assist / Visual / Chat / Cinematic.
- Toggle switches and hotkey bindings
- Numeric sliders
- Color pickers
- String lists

Press `Z + L` (configurable) to open the settings.

### Cinematic Camera

- Enable the main switch, play music with DangoMusic, and stop touching the keyboard/mouse for the configured idle time (10 s by default) to enter automatically, or bind the manual hotkey to enter instantly.
- Requires **DangoMusic**: it provides the playback state, beat grid (`beatSnapshot`) and lyrics (`LyricsProvider.neteaseLyricById`). Without it, only manual entry works, beats fall back to a fixed tempo and the lyric area stays empty.
- Any keyboard/mouse input returns the camera immediately; opening a screen, taking damage, stopping the music or disabling the switch eases the camera back to the player.
- The Cinematic Camera and Freecam / HaoQiChongTian all drive the same camera, so entering disables those two.

## Notes

- Client-side only — no server installation required
- Some automation features may be disallowed on certain servers; obey server rules
- Shulker Restock requires the Quick Shulker mod to be installed
- Cinematic Camera lyrics and beat sync require the DangoMusic mod; lyrics also need network access
- All features are disabled by default and must be enabled in the config screen
