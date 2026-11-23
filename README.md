<div align="center">

<img src="src/main/resources/assets/vitalassist/icon.png" width="128" alt="VitalAssist">

# VitalAssist

**适用于 Fabric 26.1.2 的客户端生存辅助 Mod**

自动工具 / 自动进食 / 自动图腾 / 自由视角 / 珍珠抛物线 / 聊天翻译 / 合乎周礼

[![Minecraft](https://img.shields.io/badge/Minecraft-26.1.2-62B47A.svg)](#)
[![Fabric Loader](https://img.shields.io/badge/Fabric%20Loader-%3E%3D0.19.1-DBB69C.svg)](#)
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

> 部分自动化功能可能在特定服务器被视为违规，请务必遵守所在服务器的规则。

## 下载

前往 [Releases](https://github.com/sweetljk/VitalAssist/releases) 下载最新版本：

- **最新版本**：`VitalAssist-1.2.jar`
- 直接下载：<https://github.com/sweetljk/VitalAssist/releases/download/v1.2/VitalAssist-1.2.jar>

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

## 安装

1. 安装 **Fabric Loader** `>= 0.19.1`（Minecraft `26.1.2`）
2. 将 `VitalAssist-1.2.jar` 放入 `.minecraft/mods`
3. 安装依赖 Mod（见下表）
4. 启动游戏，按 `Z + L` 打开配置界面

### 依赖

| Mod | 是否必需 | 说明 |
|---|---|---|
| [Fabric API](https://modrinth.com/mod/fabric-api) | 必需 | `0.153.0+26.1.2` 或更新 |
| [malilib](https://modrinth.com/mod/malilib) | 必需 | 提供配置界面 |
| [Mod Menu](https://modrinth.com/mod/modmenu) | 推荐 | 便于管理配置 |
| Quick Shulker | 可选 | 潜影盒补货功能需要 |

## 配置

配置入口：按 `Z + L`（可在「打开设置菜单」中修改），或通过 Mod Menu 进入。

界面顶部分为五个分类按钮：

- **全部** — 显示所有配置项
- **通用** — 打开设置菜单、静默烟花、豪气冲天
- **辅助** — 自动工具 / 进食 / 图腾、潜影盒补货、无减速
- **视觉** — 自由视角、珍珠抛物线、烟花 / 图腾预警、隐藏实体血量
- **聊天** — 更好的聊天、合乎周礼、语言翻译及其 API 配置与连接检测

## 从源码构建

```bash
# 需要 JDK 25
./gradlew build
```

产物位于 `build/libs/VitalAssist-1.2.jar`。

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
