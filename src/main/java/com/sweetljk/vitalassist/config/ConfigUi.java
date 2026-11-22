package com.sweetljk.vitalassist.config;

import com.google.common.collect.ImmutableList;
import com.sweetljk.vitalassist.client.VitalAssistClient;
import com.sweetljk.vitalassist.feature.ZhouLi;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;

public class ConfigUi extends GuiConfigsBase {
    private static Tab tab = Tab.ALL;
    private static boolean connectionTesting;
    private static String connectionTestStatus = "测试连接";

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
            Configs.ZHOU_LI_MODEL
    );

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

        ButtonGeneric testButton = new ButtonGeneric(x + 4, y, 96, 20, connectionTestStatus);
        testButton.setEnabled(!connectionTesting);
        testButton.setHoverStrings("使用当前合乎周礼 API 地址、Key 和模型发送测试请求");
        this.addButton(testButton, (button, mouseButton) -> testConnection());
    }

    private int createButton(int x, int y, int width, Tab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getTranslate());
        button.setEnabled(ConfigUi.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));
        return button.getWidth() + 2;
    }

    private void testConnection() {
        if (connectionTesting) return;

        Minecraft client = Minecraft.getInstance();
        connectionTesting = true;
        connectionTestStatus = "测试中...";
        this.initGui();

        ZhouLi.testConnection().thenAccept(result -> client.execute(() -> {
            connectionTesting = false;
            connectionTestStatus = result.success() ? "连接成功" : "连接失败";

            if (client.player != null) {
                String color = result.success() ? "§a" : "§c";
                client.player.sendSystemMessage(Component.literal("§6[VitalAssist] " + color + result.message()));
            }

            if (client.screen == this) this.initGui();
        }));
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        List<? extends IConfigBase> configs = switch (tab) {
            case ALL -> Configs.ALL_CONFIGS;
            case GENERAL -> GENERAL_CONFIGS;
            case ASSIST -> ASSIST_CONFIGS;
            case VISUAL -> VISUAL_CONFIGS;
            case CHAT -> CHAT_CONFIGS;
        };
        return ConfigOptionWrapper.createFor(configs);
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
        CHAT("聊天");

        private final String translation;

        Tab(String translation) {
            this.translation = translation;
        }

        public String getTranslate() {
            return StringUtils.translate(translation);
        }
    }
}
