# VitalAssist

**A client-side survival utility mod for Fabric 26.3** — no server installation required.

**Short summary (for the CurseForge "Summary" field):**
> Client-side survival QoL for Fabric: auto tool/eat/totem, freecam, pearl trajectory, warnings, chat rewriting & translation via DeepSeek, and a beat-synced cinematic camera with lyrics.

---

## English

### What is VitalAssist?

VitalAssist is a configurable, purely client-side quality-of-life mod for Fabric. Every feature is an independent switch, and **all of them are off by default** — you enable only what you want. Settings live in a malilib config screen split into tabs: **All / General / Assist / Visual / Chat / Cinematic**.

Open the config with `Z + L` (rebindable), or through Mod Menu.

### Assist

- **Auto Tool** — switches to the fastest hotbar tool when you start mining; can also pull a suitable tool out of your inventory and switch back afterwards.
- **Auto Eat** — eats when hunger or health drops below configurable thresholds.
- **Auto Totem** — moves totems to your offhand, with optional shulker-box restocking when your count runs low.
- **Shulker Restock** — automatically refills pearls and fireworks from hotbar shulker boxes *(requires Quick Shulker)*.
- **No Slow** — removes the slime-block movement slowdown.

### Visual

- **Freecam** — detaches the camera from your body with full keyboard control.
- **HaoQiChongTian** — freeze mid-air, rotate in third person and play a sound clip.
- **Pearl Trajectory** — renders the predicted ender pearl arc, with a configurable color.
- **Firework / Totem Warning** — on-screen warning when your count drops below a threshold.
- **Hide Entity Health** — strips the health line servers add under entity nametags (regex-based).

### Chat

- **Better Chat** — regex-based filtering and folding of chat messages.
- **Command Completion Filter** — hides chosen commands from tab completion.
- **Spectator List Completion** — shows spectators in the teleport-to-player list.
- **ZhouLi (合乎周礼)** — rewrites your outgoing chat into a classical Chinese tone through DeepSeek.
- **Chat Translation** — translates your outgoing chat into a target language (English by default) through DeepSeek.

Both LLM features use an OpenAI-compatible endpoint and have **completely independent** BaseURL / API Key / Model settings, each with its own **connection test** button. Defaults are `https://api.deepseek.com` and `deepseek-v4-pro`; the API key field is empty and must be filled in by you. Commands starting with `/` or `!` are never touched, and on failure the original message is sent instead of being swallowed.

### Cinematic Camera（电影视角）

The headline feature. While music is playing, stop touching the keyboard and mouse and the mod gently takes over the camera.

- **Auto entry** — main switch on + music playing + no input for the configured idle time (10 s by default).
- **Manual entry** — bind a hotkey to enter/exit instantly.
- **Shot library** — about 90 composition presets (orbit, dolly in/out, crane, truck, spiral, tracking, landscape, cave, Nether, End), filtered by dimension, biome and day/night, drawn from a no-repeat deck.
- **Beat-synced cutting** — cuts land on the first beat of the bar; shot length is measured in beats and speeds up with the song's energy.
- **Cinematic look** — letterbox bars (height adjustable, 13% ≈ 2.39:1), five transition masks (black fade, white flash, horizontal wipe, vertical wipe, iris), FOV punch on beats, and a subtle handheld shake.
- **Wildlife focus** — tracks nearby moving mobs as the camera subject; hostile mobs and bats can be excluded.
- **Lyrics** — displayed inside the bottom letterbox bar, with translation when available.
- **Clean exit** — any keyboard or mouse input returns the camera immediately; opening a screen, taking damage, stopping the music or disabling the switch eases the camera back with a spring. Camera and HUD are reconciled every tick, so even abnormal exits restore your view.

> **The Cinematic Camera's beat sync, auto entry and lyrics require DangoMusic.**
> VitalAssist reads DangoMusic's playback state, beat grid and its NetEase lyric API. Without it, manual entry still works but cuts fall back to a fixed tempo and the lyric area stays empty. Displaying lyrics also needs network access; songs without available lyrics simply show nothing.

### Dependencies

| Mod | Required | Notes |
|---|---|---|
| Fabric Loader | Yes | 0.20.1 or newer |
| Fabric API | Yes | 0.167.0+26.3 recommended |
| malilib | Yes | Provides the config screen |
| Mod Menu | Recommended | Convenient access to settings |
| DangoMusic | Optional | Needed for Cinematic Camera music, beats and lyrics |
| Quick Shulker | Optional | Needed for shulker restocking |

### Notes

- Client-side only — nothing needs to be installed on the server.
- Some automation features may be considered cheating on certain servers. **Please follow your server's rules.**
- All features are disabled by default.
- Licensed under the MIT License.
- Source code and issue tracker: <https://github.com/sweetljk/VitalAssist>

---

## 中文

### 这是什么

VitalAssist 是一个纯客户端的 Fabric 生存辅助 Mod，所有功能都是独立开关，**默认全部关闭**，按需启用。配置界面基于 malilib，分为 **全部 / 通用 / 辅助 / 视觉 / 聊天 / 电影视角** 六个分类。

按 `Z + L`（可改键）或通过 Mod Menu 打开设置。

### 辅助

- **自动工具** — 挖掘时自动切到背包里最快的工具，也可以临时从背包换出，用完切回。
- **自动吃** — 饥饿值或血量低于设定阈值时自动进食。
- **自动图腾** — 自动把图腾换到副手，数量不足时可以从潜影盒补货。
- **潜影盒补货** — 从快捷栏潜影盒自动补充珍珠与烟花 *（需要 Quick Shulker）*。
- **无减速** — 取消黏液块的移动减速。

### 视觉

- **自由视角** — 相机脱离本体自由移动，完整按键控制。
- **豪气冲天** — 空中定身、第三人称旋转视角并播放音效。
- **珍珠抛物线** — 渲染末影珍珠的预测轨迹，颜色可调。
- **烟花 / 图腾预警** — 数量低于阈值时在物品栏上方提示。
- **隐藏实体血量** — 用正则屏蔽服务器在实体名称标签下附加的血量行。

### 聊天

- **更好的聊天** — 正则过滤与折叠聊天内容。
- **命令补全屏蔽** — 在 Tab 补全中隐藏指定命令。
- **观察者列表补全** — 旁观模式传送列表中显示旁观者。
- **合乎周礼** — 通过 DeepSeek 把发出的聊天改写成文言腔调。
- **语言翻译** — 通过 DeepSeek 把发出的聊天翻译成目标语言（默认英语）。

两个大模型功能都走 OpenAI 兼容接口，且 **BaseURL / API Key / 模型各自独立**，每个都有自己的**连接检测**按钮。默认地址 `https://api.deepseek.com`、模型 `deepseek-v4-pro`，Key 留空需自行填写。以 `/`、`!` 开头的命令不会处理；请求失败时照常发送原文，不会吞消息。

### 电影视角

本 Mod 的招牌功能：音乐播放中停下键鼠操作，相机会自动接管。

- **自动进入** — 主开关开启 + 音乐在播 + 连续无输入达到设定秒数（默认 10 秒）。
- **手动进入** — 绑一个快捷键，按下立即进入，再按退出。
- **镜头库** — 约 90 组构图（环绕、推拉、摇臂、横移、螺旋、跟拍、风景、洞穴、下界、末地），按维度、群系、昼夜筛选，不重复抽取。
- **卡点切镜** — 切点对齐小节第一拍，镜头时长按拍数计算，并随歌曲能量自动加快。
- **电影质感** — 上下黑边（高度可调，13% 约为 2.39:1）、五种转场遮罩（黑淡、白闪、横擦、竖擦、光圈）、鼓点 FOV 脉冲、轻微手持抖动。
- **动物视角** — 自动追踪附近移动的生物作为镜头主体，可排除敌对生物与蝙蝠。
- **歌词** — 在下方黑边显示当前歌词，有翻译时一并显示。
- **干净退出** — 任何键鼠输入立即交还视角；开界面、受伤、停歌或关闭开关时相机用弹簧平滑收敛回玩家眼位；每 tick 对账恢复，异常退出也能归还视角与 HUD。

> **电影视角的卡点、自动进入与歌词依赖 DangoMusic。**
> VitalAssist 读取 DangoMusic 的播放状态、节拍网格与网易云歌词接口。未安装时手动进入仍可用，但切镜会退化为固定节拍、歌词区留空。歌词显示还需要联网；取不到歌词的歌曲就只留空不显示。

### 依赖

| Mod | 是否必需 | 说明 |
|---|---|---|
| Fabric Loader | 必需 | 0.20.1 或更新 |
| Fabric API | 必需 | 建议 0.167.0+26.3 |
| malilib | 必需 | 提供配置界面 |
| Mod Menu | 推荐 | 方便进设置 |
| DangoMusic | 可选 | 电影视角的音乐、节拍与歌词来源 |
| Quick Shulker | 可选 | 潜影盒补货需要 |

### 说明

- 纯客户端，服务器不需要安装。
- 部分自动化功能在特定服务器可能被视为作弊，**请遵守所在服务器的规则**。
- 所有功能默认关闭。
- 基于 MIT License 开源。
- 源码与问题反馈：<https://github.com/sweetljk/VitalAssist>
