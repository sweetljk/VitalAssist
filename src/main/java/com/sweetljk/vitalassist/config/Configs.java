package com.sweetljk.vitalassist.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.data.json.JsonUtils;
import com.sweetljk.vitalassist.client.VitalAssistClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Configs implements IConfigHandler {
    public static final Configs INSTANCE = new Configs();
    private static final String FILE_PATH = "./config/" + VitalAssistClient.MOD_ID + ".json";
    private static final File CONFIG_DIR = new File("./config");

    public static final ConfigHotkey OPEN_CONFIG = new ConfigHotkey("打开设置菜单", "Z,L", "打开 VitalAssist 设置菜单");
    public static final ConfigHotkey SILENT_FIREWORK = new ConfigHotkey("静默烟花", "", "从背包静默切换并使用烟花火箭");
    public static final ConfigBoolean FIREWORK_WARNING = new ConfigBoolean("烟花预警", false, "烟花数量低于阈值时在物品栏上方提示");
    public static final ConfigInteger FIREWORK_WARNING_THRESHOLD = new ConfigInteger("烟花预警阈值", 16, 0, 512, "烟花数量小于或等于该值时显示预警");
    public static final ConfigBoolean TOTEM_WARNING = new ConfigBoolean("图腾预警", false, "背包和潜影盒内不死图腾总数低于阈值时在物品栏上方提示");
    public static final ConfigInteger TOTEM_WARNING_THRESHOLD = new ConfigInteger("图腾预警阈值", 2, 0, 512, "背包和潜影盒内不死图腾总数小于或等于该值时显示预警");
    public static final ConfigBoolean PEARL_TRAJECTORY = new ConfigBoolean("珍珠抛物线", false, "渲染末影珍珠预测抛物线");
    public static final ConfigColor PEARL_TRAJECTORY_COLOR = new ConfigColor("珍珠抛物线颜色", "#FF7333FF", "末影珍珠预测抛物线颜色");
    public static final ConfigBooleanHotkeyed FREECAM = new ConfigBooleanHotkeyed("自由视角", false, "", "让摄像机离开玩家本体自由移动");
    public static final ConfigDouble FREECAM_SPEED = new ConfigDouble("自由视角速度", 1.0, 0.0, 10.0, "自由视角摄像机移动速度");
    public static final ConfigBoolean FREECAM_RENDER_HANDS = new ConfigBoolean("自由视角渲染手", false, "自由视角时是否渲染手");
    public static final ConfigBooleanHotkeyed HAO_QI_CHONG_TIAN = new ConfigBooleanHotkeyed("豪气冲天", false, "", "在空中循环定身、播放 JH 片段并旋转第三人称视角");

    public static final ConfigBooleanHotkeyed NO_SLOW = new ConfigBooleanHotkeyed("无减速", false, "", "禁用粘液块移动减速");

    public static final ConfigBooleanHotkeyed AUTO_TOOL = new ConfigBooleanHotkeyed("自动工具", false, "", "挖掘方块时自动切换快捷栏内速度最快的工具");
    public static final ConfigBooleanHotkeyed AUTO_TOOL_INVENTORY = new ConfigBooleanHotkeyed("自动工具背包工具", false, "", "允许自动工具从背包临时换出合适工具");
    public static final ConfigBoolean AUTO_TOOL_SWITCH_BACK = new ConfigBoolean("自动工具切回", false, "停止挖掘后切回触发自动工具前的快捷栏槽位");
    public static final ConfigBooleanHotkeyed AUTO_EAT = new ConfigBooleanHotkeyed("自动吃", false, "", "饥饿值或血量过低时自动吃快捷栏内的食物");
    public static final ConfigInteger AUTO_EAT_HUNGER = new ConfigInteger("自动吃饥饿阈值", 14, 0, 20, "饥饿值低于或等于该值时自动吃");
    public static final ConfigDouble AUTO_EAT_HEALTH = new ConfigDouble("自动吃血量阈值", 10.0, 0.0, 20.0, "血量低于或等于该值时自动吃");
    public static final ConfigBooleanHotkeyed AUTO_TOTEM = new ConfigBooleanHotkeyed("自动图腾", false, "", "自动把背包里的不死图腾换到副手");
    public static final ConfigInteger AUTO_TOTEM_INTERVAL = new ConfigInteger("自动图腾间隔tick", 2, 1, 20, "自动图腾检查间隔");
    public static final ConfigBoolean AUTO_TOTEM_SHULKER_RESTOCK = new ConfigBoolean("自动图腾潜影盒补货", false, "自动图腾低于最小数量时从快捷潜影盒取出不死图腾");
    public static final ConfigInteger AUTO_TOTEM_MIN_TOTEMS = new ConfigInteger("自动图腾最小数量", 1, 0, 36, "背包和副手内不死图腾低于该数量时尝试从潜影盒补货");

    public static final ConfigBoolean SHULKER_RESTOCK = new ConfigBoolean("潜影盒补货", false, "从快捷潜影盒中自动补充珍珠和烟花（需要安装 Quick Shulker 否则没效果）");
    public static final ConfigBooleanHotkeyed BETTER_CHAT = new ConfigBooleanHotkeyed("更好的聊天", false, "", "启用聊天正则折叠/过滤功能");
    public static final ConfigStringList CHAT_FOLD_REGEX = new ConfigStringList("聊天折叠正则", ImmutableList.of(), "匹配到的聊天内容会在后续版本折叠显示");
    public static final ConfigBoolean COMMAND_COMPLETION_FILTER = new ConfigBoolean("命令补全屏蔽", false, "屏蔽指定命令在聊天 Tab 补全候选中显示和补全");
    public static final ConfigStringList COMMAND_COMPLETION_FILTER_LIST = new ConfigStringList("命令补全屏蔽列表", ImmutableList.of(), "要屏蔽补全的命令名，例如 help；不需要填写斜杠 哦不不不 spark指令导致我无法补全观察者我无疑是愤怒的");
    public static final ConfigBoolean SPECTATOR_LIST_COMPLETION = new ConfigBoolean("观察者列表补全", false, "旁观模式传送玩家列表中显示旁观者玩家");

    public static final ConfigBoolean NAMETAGS_HIDE_ENTITY_HEALTH = new ConfigBoolean("隐藏实体血量显示", false, "屏蔽 FZ 插件在实体名称标签下附加的血量显示");
    public static final ConfigStringList NAMETAGS_ENTITY_HEALTH_PATTERNS = new ConfigStringList("实体血量过滤模式", ImmutableList.of("§c\\d+血量", "\\n§c\\d+血量"), "用于匹配和移除血量文本的正则表达式模式，默认匹配红色数字+红色血量汉字格式");

    public static final ConfigBooleanHotkeyed ZHOU_LI = new ConfigBooleanHotkeyed("合乎周礼", false, "", "启用后聊天信息会经过 LLM 改写为周礼腔调再发送");
    public static final ConfigString ZHOU_LI_API_BASE = new ConfigString("合乎周礼 API BaseURL", "https://api.deepseek.com", "DeepSeek OpenAI 兼容 API 地址");
    public static final ConfigString ZHOU_LI_API_KEY = new ConfigString("合乎周礼 API Key", "", "DeepSeek API Key");
    public static final ConfigString ZHOU_LI_MODEL = new ConfigString("合乎周礼 Model", "deepseek-v4-pro", "DeepSeek 模型名称");

    public static final ConfigBooleanHotkeyed CHAT_TRANSLATE = new ConfigBooleanHotkeyed("语言翻译", false, "", "启用后发送的聊天会由 LLM 翻译成目标语言再发送；与合乎周礼同时开启时仅翻译生效");
    public static final ConfigString CHAT_TRANSLATE_TARGET = new ConfigString("语言翻译 目标语言", "英语", "翻译后的目标语言，例如：英语、汉语、日语。目标为英语时中文会译成英语，目标为汉语时英文会译成汉语");
    public static final ConfigString CHAT_TRANSLATE_API_BASE = new ConfigString("语言翻译 API BaseURL", "https://api.deepseek.com", "语言翻译使用的 OpenAI 兼容 API 地址，与合乎周礼相互独立");
    public static final ConfigString CHAT_TRANSLATE_API_KEY = new ConfigString("语言翻译 API Key", "", "语言翻译使用的 API Key，与合乎周礼相互独立");
    public static final ConfigString CHAT_TRANSLATE_MODEL = new ConfigString("语言翻译 Model", "deepseek-v4-pro", "语言翻译使用的模型名称");

    // ---------- 电影视角 ----------
    public static final ConfigBooleanHotkeyed CINE_ENABLED = new ConfigBooleanHotkeyed("电影视角", false, "", "音乐播放中挂机一段时间后自动接管相机，进行卡点运镜");
    public static final ConfigInteger CINE_IDLE_SECONDS = new ConfigInteger("电影视角 挂机秒数", 10, 5, 3600, "连续无输入多少秒后自动进入电影视角");
    public static final ConfigHotkey CINE_MANUAL = new ConfigHotkey("电影视角 手动进入", "", "按下立即进入/退出电影视角（需先开启主开关）");
    public static final ConfigBoolean CINE_BEAT_SYNC = new ConfigBoolean("电影视角 卡点运镜", true, "按音乐节拍对齐切镜点；关闭后按固定时长切镜");
    public static final ConfigInteger CINE_SHOT_BEATS = new ConfigInteger("电影视角 镜头拍数", 4, 1, 32, "每个镜头持续多少拍，副歌等段落会自动加快");
    public static final ConfigDouble CINE_MIN_SHOT_SECONDS = new ConfigDouble("电影视角 最短镜头秒数", 3.0, 1.0, 12.0, "单镜头最短时长，防止快歌闪切");
    public static final ConfigDouble CINE_SPEED = new ConfigDouble("电影视角 运镜速度", 0.7, 0.0, 4.0, "镜头运动速度倍数，0 为镜头内静止");
    public static final ConfigInteger CINE_ENVIRONMENT_PERCENT = new ConfigInteger("电影视角 风景镜头比例", 50, 0, 100, "选择风景构图而非人物构图的百分比");
    public static final ConfigDouble CINE_FOV_PUNCH = new ConfigDouble("电影视角 鼓点FOV脉冲", 8.0, 0.0, 20.0, "鼓点瞬间的 FOV 收放幅度（度），0 为关闭");
    public static final ConfigDouble CINE_HANDHELD = new ConfigDouble("电影视角 手持感", 0.4, 0.0, 1.0, "机位低频抖动幅度，0 为完全稳定");
    public static final ConfigBoolean CINE_HIDE_HUD = new ConfigBoolean("电影视角 隐藏HUD", true, "进入电影视角时隐藏原版 HUD，退出时恢复");
    public static final ConfigBoolean CINE_BARS = new ConfigBoolean("电影视角 上下黑边", true, "在画面上下绘制电影黑边");
    public static final ConfigInteger CINE_BAR_PERCENT = new ConfigInteger("电影视角 黑边高度", 13, 0, 25, "每侧黑边占屏幕高度的百分比，13% 约为 2.39:1");
    public static final ConfigBoolean CINE_TRANSITION = new ConfigBoolean("电影视角 转场", true, "切镜时加入遮罩转场");
    public static final ConfigInteger CINE_TRANSITION_EFFECT = new ConfigInteger("电影视角 转场特效", 0, 0, 5, "0=随机，1=黑淡，2=白闪，3=横擦，4=竖擦，5=光圈");
    public static final ConfigDouble CINE_TRANSITION_SECONDS = new ConfigDouble("电影视角 转场时长", 1.0, 0.1, 3.0, "转场每半程的时长（秒）");
    public static final ConfigBoolean CINE_MOB_FOCUS = new ConfigBoolean("电影视角 动物视角", true, "自动追踪附近的移动生物作为镜头主体");
    public static final ConfigDouble CINE_MOB_RANGE = new ConfigDouble("电影视角 动物视角搜索半径", 32.0, 6.0, 64.0, "搜索移动生物的半径（格）");
    public static final ConfigDouble CINE_TRACKING_SOFTNESS = new ConfigDouble("电影视角 追焦软度", 0.8, 0.1, 8.0, "朝向与位置追踪的软度（秒），越大越柔和");
    public static final ConfigBoolean CINE_EXCLUDE_HOSTILE = new ConfigBoolean("电影视角 排除敌对生物", true, "不把敌对生物作为追焦主体");
    public static final ConfigBoolean CINE_EXCLUDE_BATS = new ConfigBoolean("电影视角 排除蝙蝠", true, "不把蝙蝠作为追焦主体");
    public static final ConfigBoolean CINE_LYRICS = new ConfigBoolean("电影视角 歌词显示", true, "在下方黑边显示当前歌曲歌词（需安装 DangoMusic 且能取到歌词）");
    public static final ConfigBoolean CINE_STATUS_MESSAGES = new ConfigBoolean("电影视角 状态提示", true, "进出电影视角时在动作栏提示");

    public static final ImmutableList<IConfigBase> ALL_CONFIGS = createAllConfigs();
    public static final ImmutableList<IHotkeyTogglable> SWITCH_KEY = ImmutableList.of(
            AUTO_TOOL,
            AUTO_TOOL_INVENTORY,
            AUTO_EAT,
            AUTO_TOTEM,
            FREECAM,
            HAO_QI_CHONG_TIAN,
            NO_SLOW,
            BETTER_CHAT,
            ZHOU_LI,
            CHAT_TRANSLATE,
            CINE_ENABLED
    );
    public static final ImmutableList<ConfigHotkey> KEY_LIST = ImmutableList.of(OPEN_CONFIG, SILENT_FIREWORK, CINE_MANUAL);

    private static ImmutableList<IConfigBase> createAllConfigs() {
        List<IConfigBase> list = new ArrayList<>();
        list.add(OPEN_CONFIG);
        list.add(SILENT_FIREWORK);
        list.add(SHULKER_RESTOCK);
        list.add(FIREWORK_WARNING);
        list.add(FIREWORK_WARNING_THRESHOLD);
        list.add(TOTEM_WARNING);
        list.add(TOTEM_WARNING_THRESHOLD);
        list.add(PEARL_TRAJECTORY);
        list.add(PEARL_TRAJECTORY_COLOR);
        list.add(FREECAM);
        list.add(FREECAM_SPEED);
        list.add(FREECAM_RENDER_HANDS);
        list.add(HAO_QI_CHONG_TIAN);
        list.add(NO_SLOW);
        list.add(AUTO_TOOL);
        list.add(AUTO_TOOL_INVENTORY);
        list.add(AUTO_TOOL_SWITCH_BACK);
        list.add(AUTO_EAT);
        list.add(AUTO_EAT_HUNGER);
        list.add(AUTO_EAT_HEALTH);
        list.add(AUTO_TOTEM);
        list.add(AUTO_TOTEM_INTERVAL);
        list.add(AUTO_TOTEM_SHULKER_RESTOCK);
        list.add(AUTO_TOTEM_MIN_TOTEMS);
        list.add(BETTER_CHAT);
        list.add(CHAT_FOLD_REGEX);
        list.add(COMMAND_COMPLETION_FILTER);
        list.add(COMMAND_COMPLETION_FILTER_LIST);
        list.add(SPECTATOR_LIST_COMPLETION);
        list.add(NAMETAGS_HIDE_ENTITY_HEALTH);
        list.add(NAMETAGS_ENTITY_HEALTH_PATTERNS);
        list.add(ZHOU_LI);
        list.add(ZHOU_LI_API_BASE);
        list.add(ZHOU_LI_API_KEY);
        list.add(ZHOU_LI_MODEL);
        list.add(CHAT_TRANSLATE);
        list.add(CHAT_TRANSLATE_TARGET);
        list.add(CHAT_TRANSLATE_API_BASE);
        list.add(CHAT_TRANSLATE_API_KEY);
        list.add(CHAT_TRANSLATE_MODEL);
        list.add(CINE_ENABLED);
        list.add(CINE_IDLE_SECONDS);
        list.add(CINE_MANUAL);
        list.add(CINE_BEAT_SYNC);
        list.add(CINE_SHOT_BEATS);
        list.add(CINE_MIN_SHOT_SECONDS);
        list.add(CINE_SPEED);
        list.add(CINE_ENVIRONMENT_PERCENT);
        list.add(CINE_FOV_PUNCH);
        list.add(CINE_HANDHELD);
        list.add(CINE_HIDE_HUD);
        list.add(CINE_BARS);
        list.add(CINE_BAR_PERCENT);
        list.add(CINE_TRANSITION);
        list.add(CINE_TRANSITION_EFFECT);
        list.add(CINE_TRANSITION_SECONDS);
        list.add(CINE_MOB_FOCUS);
        list.add(CINE_MOB_RANGE);
        list.add(CINE_TRACKING_SOFTNESS);
        list.add(CINE_EXCLUDE_HOSTILE);
        list.add(CINE_EXCLUDE_BATS);
        list.add(CINE_LYRICS);
        list.add(CINE_STATUS_MESSAGES);
        return ImmutableList.copyOf(list);
    }

    @Override
    public void load() {
        File settingFile = new File(FILE_PATH);
        if (settingFile.isFile() && settingFile.exists()) {
            JsonElement jsonElement = JsonUtils.parseJsonFile(settingFile.toPath());
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject obj = jsonElement.getAsJsonObject();
                ConfigUtils.readConfigBase(obj, VitalAssistClient.MOD_ID, ALL_CONFIGS);
            }
        }
    }

    @Override
    public void save() {
        if ((CONFIG_DIR.exists() && CONFIG_DIR.isDirectory()) || CONFIG_DIR.mkdirs()) {
            JsonObject configRoot = new JsonObject();
            ConfigUtils.writeConfigBase(configRoot, VitalAssistClient.MOD_ID, ALL_CONFIGS);
            JsonUtils.writeJsonToFile(configRoot, new File(FILE_PATH).toPath());
        }
    }
}
