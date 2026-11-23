<div align="center">

<img src="src/main/resources/assets/vitalassist/icon.png" width="128" alt="VitalAssist">

# VitalAssist

**适用于 Fabric 1.21.11 / 26.1.2 / 26.2 / 26.3 的客户端生存辅助 Mod**

自动工具 / 自动进食 / 自动图腾 / 自由视角 / 珍珠抛物线 / 电影视角 / 聊天翻译 / 合乎周礼

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11%20%7C%2026.1.2%20%7C%2026.2%20%7C%2026.3-62B47A.svg)](#)
[![Fabric Loader](https://img.shields.io/badge/Fabric%20Loader-0.19.1%2B-DBB69C.svg)](#)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](#)

[下载](#下载) · [功能](#功能) · [安装](#安装) · [配置](#配置) · [从源码构建](#从源码构建) · [来源](#来源与致谢)

</div>

---

## 简介

VitalAssist 是一个纯客户端的 Fabric 生存辅助 Mod，基于 **malilib** 提供统一的配置界面，所有功能均可独立开关，**默认全部关闭**，按需启用。

- 纯客户端，服务器无需安装
- 配置界面按 **全部 / 通用 / 辅助 / 视觉 / 聊天** 分类
- 支持快捷键绑定（默认 `Z + L` 打开设置）
- 集成 DeepSeek，可实现「合乎周礼」文言化改写与中英聊天翻译
- 音乐播放时挂机自动进入「电影视角」卡点运镜（歌词需 DangoMusic）

> 部分自动化功能可能在特定服务器被视为违规，请务必遵守所在服务器的规则。

## 下载

前往 [Releases](https://github.com/sweetljk/VitalAssist/releases) 下载与你游戏版本对应的 jar：

| 游戏版本 | 源码分支 | Release | 文件名 |
|---|---|---|---|
| **1.21.11** | [`1.21.11`](https://github.com/sweetljk/VitalAssist/tree/1.21.11) | [v1.3-1.21.11](https://github.com/sweetljk/VitalAssist/releases/tag/v1.3-1.21.11) | `VitalAssist-1.3+1.21.11.jar` |
| **26.1.2** | [`main`](https://github.com/sweetljk/VitalAssist/tree/main) | [v1.3](https://github.com/sweetljk/VitalAssist/releases/tag/v1.3) | `VitalAssist-1.3.jar` |
| **26.2** | [`26.2`](https://github.com/sweetljk/VitalAssist/tree/26.2) | [v1.3-26.2](https://github.com/sweetljk/VitalAssist/releases/tag/v1.3-26.2) | `VitalAssist-1.3+26.2.jar` |
| **26.3** | [`26.3`](https://github.com/sweetljk/VitalAssist/tree/26.3) | [v1.3-26.3](https://github.com/sweetljk/VitalAssist/releases/tag/v1.3-26.3) | `VitalAssist-1.3+26.3.jar` |

直链：

- 1.21.11：<https://github.com/sweetljk/VitalAssist/releases/download/v1.3-1.21.11/VitalAssist-1.3+1.21.11.jar>
- 26.1.2：<https://github.com/sweetljk/VitalAssist/releases/download/v1.3/VitalAssist-1.3.jar>
- 26.2：<https://github.com/sweetljk/VitalAssist/releases/download/v1.3-26.2/VitalAssist-1.3+26.2.jar>
- 26.3：<https://github.com/sweetljk/VitalAssist/releases/download/v1.3-26.3/VitalAssist-1.3+26.3.jar>

> jar 不能跨版本通用，请严格按游戏版本选择。

## 功能

### 通用

| 功能 | 说明 |
|---|---|
| 打开设置菜单 | 配置界面的打开快捷键（默认 `Z + L`） |
| 静默烟花 | 直接从背包静默切换并使用烟花火箭 |
| 豪气冲天 | 空中悬停，第三人称旋转视角并播放音效 |

### 辅助

| 功能 | 说明 |
|---|---|
| 自动工具 | 挖掘时自动切换到背包中最快的工具 |
| 自动进食 | 饥饿值或血量过低时自动进食 |
| 自动图腾 | 自动将图腾装备到副手，支持潜影盒补货 |
| 潜影盒补货 | 从潜影盒自动补充珍珠 / 烟花（需 Quick Shulker） |
| 无减速 | 关闭黏液块的移动减速效果 |

### 视觉

| 功能 | 说明 |
|---|---|
| 自由视角 | 摄像机脱离玩家自由移动，完整按键控制 |
| 珍珠抛物线 | 渲染末影珍珠的预测轨迹（颜色可调） |
| 烟花预警 | 烟花数量低于阈值时屏幕提示 |
| 图腾预警 | 图腾数量低于阈值时屏幕提示 |
| 隐藏实体血量 | 屏蔽实体名称标签下方的血量显示 |

### 聊天

| 功能 | 说明 |
|---|---|
| 更好的聊天 | 正则过滤与折叠聊天消息 |
| 合乎周礼 | 通过 DeepSeek 将发送的聊天改写为文言文风格 |
| 语言翻译 | 通过 DeepSeek 将发送的聊天翻译为目标语言（默认英语） |
| 命令补全屏蔽 | 在 Tab 补全中屏蔽指定命令 |

### 合乎周礼 / 语言翻译

两者都基于 DeepSeek 的 OpenAI 兼容接口，但**各用各的 API 配置**，互不影响：

| 配置项 | 合乎周礼 | 语言翻译 |
|---|---|---|
| BaseURL | `https://api.deepseek.com` | `https://api.deepseek.com` |
| API Key | 留空，需自行填写 | 留空，需自行填写 |
| Model | `deepseek-v4-pro` | `deepseek-v4-pro` |
| 目标语言 | — | `英语`（可改为 `汉语`、`日语` 等）|

- 每个 API 配置下方都有独立的 **「连接检测」** 按钮，用于验证当前地址、Key 与模型是否可用。
- 语言翻译：目标为英语时中文译成英语；目标为汉语时英文译成汉语。以 `/`、`!` 开头的命令会自动跳过。
- 翻译或改写失败时会自动发送原文，不会吞消息。
- 两者同时开启时，**语言翻译优先**，不会连续调用两次 API。

### 电影视角

播放音乐时挂机一段时间，自动接管相机做出卡点运镜；也可以按快捷键随时进入。

| 功能 | 说明 |
|---|---|
| 自动进入 | 主开关开启 + 音乐在播 + 连续无输入达设定秒数（默认 10 秒）后自动进入 |
| 手动进入 | 绑定快捷键后按下立即进入，再按一次退出 |
| 卡点运镜 | 切镜点对齐音乐小节第一拍；无节拍信号时按拍数用固定节拍换算 |
| 镜头库 | 约 90 组构图（环绕、推拉、摇臂、螺旋、跟拍、风景、洞穴等），按维度/群系/昼夜筛选不重复抽取 |
| 转场 | 黑淡、白闪、横擦、竖擦、光圈五种遮罩，可设时长 |
| 上下黑边 | 电影宽银幕黑边，高度可调（默认 13% ≈ 2.39:1） |
| 动物视角 | 自动追踪附近移动生物作为镜头主体，可排除敌对生物与蝙蝠 |
| 歌词 | 在下方黑边显示当前歌词与翻译 |
| 其它 | 鼓点 FOV 脉冲、手持感抖动、隐藏原版 HUD、进出状态提示 |

> **歌词与节拍依赖 [DangoMusic](https://modrinth.com/mod/dango-music)**。
> 歌词通过 DangoMusic 的网易云歌词接口获取，需要联网；未安装 DangoMusic 或该歌曲取不到歌词时，
> 歌词区留空但不影响运镜。自动进入同样要求 DangoMusic 正在播放音乐。
> 想显示歌词请安装 DangoMusic 并让它播放歌曲。

**退出**：任何键鼠输入立即交还视角；开界面、受伤、停歌或关闭开关则用弹簧平滑收敛回玩家眼位，
异常退出也会在下一 tick 对账恢复视角与 HUD。

> 电影视角与「自由视角」「豪气冲天」争夺同一台相机，进入时会自动关闭它们。

## 安装

1. 按上表选好与游戏版本对应的 jar，并安装该版本要求的 **Fabric Loader**：
   - `1.21.11` / `26.2` / `26.3`：`>= 0.20.1`
   - `26.1.2`：`>= 0.19.1`
2. 将对应版本的 jar 放入 `.minecraft/mods`
3. 安装依赖 Mod（见下表）
4. 启动游戏，按 `Z + L` 打开配置界面

### 依赖

| Mod | 是否必需 | 说明 |
|---|---|---|
| [Fabric API](https://modrinth.com/mod/fabric-api) | 必需 | 随版本：`1.21.11` → `0.141.6+1.21.11`，`26.1.2` → `0.153.0+26.1.2`，`26.2` → `0.161.0+26.2`，`26.3` → `0.167.0+26.3` |
| [malilib](https://modrinth.com/mod/malilib) | 必需 | 提供配置界面 |
| [Mod Menu](https://modrinth.com/mod/modmenu) | 推荐 | 便于管理配置（`1.21.11` → `17.0.1`，`26.1.2` → `18.0.0-beta.1`，`26.2` → `20.0.3`，`26.3` → `21.0.1`） |
| Quick Shulker | 可选 | 潜影盒补货功能需要 |
| [DangoMusic](https://modrinth.com/mod/dango-music) | 可选 | 电影视角的音乐节拍与歌词来源；未安装时手动进入仍可用，但不会自动触发 |

## 配置

配置入口：按 `Z + L`（可在「打开设置菜单」中修改），或通过 Mod Menu 进入。

界面顶部分为五个分类按钮：

- **全部** — 显示所有配置项
- **通用** — 打开设置菜单、静默烟花、豪气冲天
- **辅助** — 自动工具 / 进食 / 图腾、潜影盒补货、无减速
- **视觉** — 自由视角、珍珠抛物线、烟花 / 图腾预警、隐藏实体血量
- **聊天** — 更好的聊天、合乎周礼、语言翻译及其 API 配置与连接检测
- **电影视角** — 挂机自动/手动卡点运镜、黑边、转场、动物视角、歌词

## 从源码构建

每个游戏版本一个分支，先切到对应分支再构建：

| 游戏版本 | 分支 |
|---|---|
| 1.21.11 | `1.21.11` |
| 26.1.2 | `main` |
| 26.2 | `26.2` |
| 26.3 | `26.3` |

```bash
git checkout main        # 换成你要的版本分支
# 需要 JDK 25
./gradlew build
```

产物位于 `build/libs/VitalAssist-1.3.jar`（非 26.1.2 分支的产物名会带 `+游戏版本`）。

## 来源与致谢

本 Mod 的**部分功能移植自 [AS-PLUS](https://github.com/HelloMoon-1/AS-PLUS)**，在此向原作者表示感谢。

- 原项目地址：<https://github.com/HelloMoon-1/AS-PLUS>
- 本项目在其基础上做了功能裁剪、配置界面重构（malilib 分类 Tab）、默认值调整，并新增了语言翻译与独立的 DeepSeek API 连接检测等特性。

## 许可证

本项目基于 [MIT License](LICENSE) 开源，版权所有 (c) 2026 sweetljk。

---

<div align="center">

**VitalAssist** · Author: [sweetljk](https://github.com/sweetljk)

</div>
