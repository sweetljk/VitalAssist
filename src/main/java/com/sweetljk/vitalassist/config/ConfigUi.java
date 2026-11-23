package com.sweetljk.vitalassist.config;

import com.google.common.collect.ImmutableList;
import com.sweetljk.vitalassist.client.VitalAssistClient;
import com.sweetljk.vitalassist.feature.ChatTranslator;
import com.sweetljk.vitalassist.feature.ZhouLi;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigUi extends GuiConfigsBase {
    private static Tab tab = Tab.ALL;

    private static boolean zhouLiTesting;
    private static String zhouLiTestStatus = "测试连接";
    private static boolean translationTesting;
    private static String translationTestStatus = "测试连接";

    private static final ConfigActionButton ZHOU_LI_TEST = new ConfigActionButton(
            "合乎周礼 Model 连接检测",
            "连接检测",
            "测试连接",
            "使用上方合乎周礼的 API BaseURL、Key 与 Model 发送最小测试请求",
            () -> zhouLiTestStatus,
            () -> !zhouLiTesting,
            () -> testConnection(ApiKind.ZHOU_LI),
            "点击测试当前合乎周礼 API 配置"
    );

    private static final ConfigActionButton TRANSLATION_TEST = new ConfigActionButton(
            "语言翻译 Model 连接检测",
            "连接检测",
            "测试连接",
            "使用上方语言翻译的 API BaseURL、Key 与 Model 发送最小测试请求",
            () -> translationTestStatus,
            () -> !translationTesting,
            () -> testConnection(ApiKind.TRANSLATION),
            "点击测试当前语言翻译 API 配置"
    );

    private static final ImmutableList<IConfigBase> GENERAL_CONFIGS = ImmutableList.of(
            Configs.OPEN_CONFIG,
            Configs.SILENT_FIREWORK,
            Configs.HAO_QI_CHONG_TIAN
    );

    private static final ImmutableList<IConfigBase> ASSIST_CONFIGS = ImmutableList.of(
            Configs.SHULKER_RESTOCK,
            Configs.NO_SLOW,
            Configs.AUTO_TOOL,
            Configs.AUTO_TOOL_INVENTORY,
            Configs.AUTO_TOOL_SWITCH_BACK,
            Configs.AUTO_EAT,
            Configs.AUTO_EAT_HUNGER,
            Configs.AUTO_EAT_HEALTH,
            Configs.AUTO_TOTEM,
            Configs.AUTO_TOTEM_INTERVAL,
            Configs.AUTO_TOTEM_SHULKER_RESTOCK,
            Configs.AUTO_TOTEM_MIN_TOTEMS
    );

    private static final ImmutableList<IConfigBase> VISUAL_CONFIGS = ImmutableList.of(
            Configs.FIREWORK_WARNING,
            Configs.FIREWORK_WARNING_THRESHOLD,
            Configs.TOTEM_WARNING,
            Configs.TOTEM_WARNING_THRESHOLD,
            Configs.PEARL_TRAJECTORY,
            Configs.PEARL_TRAJECTORY_COLOR,
            Configs.FREECAM,
            Configs.FREECAM_SPEED,
            Configs.FREECAM_RENDER_HANDS,
            Configs.NAMETAGS_HIDE_ENTITY_HEALTH,
            Configs.NAMETAGS_ENTITY_HEALTH_PATTERNS
    );

    private static final ImmutableList<IConfigBase> CHAT_CONFIGS = ImmutableList.of(
            Configs.BETTER_CHAT,
            Configs.CHAT_FOLD_REGEX,
            Configs.COMMAND_COMPLETION_FILTER,
            Configs.COMMAND_COMPLETION_FILTER_LIST,
            Configs.SPECTATOR_LIST_COMPLETION,
            Configs.ZHOU_LI,
            Configs.ZHOU_LI_API_BASE,
            Configs.ZHOU_LI_API_KEY,
            Configs.ZHOU_LI_MODEL,
            ZHOU_LI_TEST,
            Configs.CHAT_TRANSLATE,
            Configs.CHAT_TRANSLATE_TARGET,
            Configs.CHAT_TRANSLATE_API_BASE,
            Configs.CHAT_TRANSLATE_API_KEY,
            Configs.CHAT_TRANSLATE_MODEL,
            TRANSLATION_TEST
    );

    private static final ImmutableList<IConfigBase> CINE_CONFIGS = ImmutableList.of(
            Configs.CINE_ENABLED,
            Configs.CINE_IDLE_SECONDS,
            Configs.CINE_MANUAL,
            Configs.CINE_BEAT_SYNC,
            Configs.CINE_SHOT_BEATS,
            Configs.CINE_MIN_SHOT_SECONDS,
            Configs.CINE_SPEED,
            Configs.CINE_ENVIRONMENT_PERCENT,
            Configs.CINE_FOV_PUNCH,
            Configs.CINE_HANDHELD,
            Configs.CINE_HIDE_HUD,
            Configs.CINE_BARS,
            Configs.CINE_BAR_PERCENT,
            Configs.CINE_TRANSITION,
            Configs.CINE_TRANSITION_EFFECT,
            Configs.CINE_TRANSITION_SECONDS,
            Configs.CINE_MOB_FOCUS,
            Configs.CINE_MOB_RANGE,
            Configs.CINE_TRACKING_SOFTNESS,
            Configs.CINE_EXCLUDE_HOSTILE,
            Configs.CINE_EXCLUDE_BATS,
            Configs.CINE_LYRICS,
            Configs.CINE_STATUS_MESSAGES
    );

    private static final ImmutableList<IConfigBase> ALL_VIEW_CONFIGS = createAllViewConfigs();

    public ConfigUi() {
        super(10, 50, VitalAssistClient.MOD_NAME, null, "VitalAssist 配置界面");
    }

    @Override
    public void initGui() {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;
        for (Tab tab : Tab.values()) {
            x += this.createButton(x, y, -1, tab);
        }
    }

    @Override
    protected WidgetListConfigOptions createListWidget(int x, int y) {
        return new ActionButtonConfigList(x, y, this.getBrowserWidth(), this.getBrowserHeight(),
                this.getConfigWidth(), 0F, this.useKeybindSearch(), this);
    }

    private static ImmutableList<IConfigBase> createAllViewConfigs() {
        List<IConfigBase> configs = new ArrayList<>();
        for (IConfigBase config : Configs.ALL_CONFIGS) {
            configs.add(config);
            if (config == Configs.ZHOU_LI_MODEL) configs.add(ZHOU_LI_TEST);
            if (config == Configs.CHAT_TRANSLATE_MODEL) configs.add(TRANSLATION_TEST);
        }
        return ImmutableList.copyOf(configs);
    }

    private int createButton(int x, int y, int width, Tab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getTranslate());
        button.setEnabled(ConfigUi.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));
        return button.getWidth() + 2;
    }

    private static void testConnection(ApiKind kind) {
        Minecraft client = Minecraft.getInstance();

        if (kind == ApiKind.ZHOU_LI) {
            if (zhouLiTesting) return;
            zhouLiTesting = true;
            zhouLiTestStatus = "测试中...";

            ZhouLi.testConnection().thenAccept(result -> client.execute(() -> {
                zhouLiTesting = false;
                zhouLiTestStatus = result.success() ? "连接成功" : "连接失败";
                showTestResult(kind, result.success(), result.message());
            }));
            return;
        }

        if (translationTesting) return;
        translationTesting = true;
        translationTestStatus = "测试中...";

        ChatTranslator.testConnection().thenAccept(result -> client.execute(() -> {
            translationTesting = false;
            translationTestStatus = result.success() ? "连接成功" : "连接失败";
            showTestResult(kind, result.success(), result.message());
        }));
    }

    private static void showTestResult(ApiKind kind, boolean success, String message) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            String color = success ? "§a" : "§c";
            client.player.sendSystemMessage(Component.literal(
                    "§6[VitalAssist] " + kind.label + "：" + color + message));
        }
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        List<? extends IConfigBase> configs = switch (tab) {
            case ALL -> ALL_VIEW_CONFIGS;
            case GENERAL -> GENERAL_CONFIGS;
            case ASSIST -> ASSIST_CONFIGS;
            case VISUAL -> VISUAL_CONFIGS;
            case CHAT -> CHAT_CONFIGS;
            case CINE -> CINE_CONFIGS;
        };
        return ConfigOptionWrapper.createFor(configs);
    }

    private enum ApiKind {
        ZHOU_LI("合乎周礼"),
        TRANSLATION("语言翻译");

        private final String label;

        ApiKind(String label) {
            this.label = label;
        }
    }

    private static class ButtonListener implements IButtonActionListener {
        private final ConfigUi parent;
        private final Tab tab;

        private ButtonListener(Tab tab, ConfigUi parent) {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            ConfigUi.tab = this.tab;
            this.parent.reCreateListWidget();
            Objects.requireNonNull(this.parent.getListWidget()).resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    public enum Tab {
        ALL("全部"),
        GENERAL("通用"),
        ASSIST("辅助"),
        VISUAL("视觉"),
        CHAT("聊天"),
        CINE("电影视角");

        private final String translation;

        Tab(String translation) {
            this.translation = translation;
        }

        public String getTranslate() {
            return StringUtils.translate(translation);
        }
    }
}
