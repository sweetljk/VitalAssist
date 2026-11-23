package com.sweetljk.vitalassist.client;

import com.sweetljk.vitalassist.config.Configs;
import com.sweetljk.vitalassist.config.HotkeysCallback;
import com.sweetljk.vitalassist.config.InputHandler;
import com.sweetljk.vitalassist.feature.*;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VitalAssistClient implements ClientModInitializer {
    public static final String MOD_ID = "vitalassist";
    public static final String MOD_NAME = "VitalAssist";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private int ticks;

    @Override
    public void onInitializeClient() {
        Configs.INSTANCE.load();
        HotkeysCallback.init();
        ConfigManager.getInstance().registerConfigHandler(MOD_NAME, Configs.INSTANCE);
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
        InputEventHandler.getInputManager().registerKeyboardInputHandler(InputHandler.getInstance());
        PearlTrajectoryRenderer.init();
        FireworkWarningHud.init();
        ShulkerRestock.init();
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> Configs.HAO_QI_CHONG_TIAN.setBooleanValue(false));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ticks++;
            ShulkerRestock.tick(client);
            SilentUseHotkeys.tick(client);
            Freecam.tick(client);
            HaoQiChongTian.tick(client);
            AutoEat.tick(client);
            AutoTool.tick(client);
            if (ticks % Math.max(1, Configs.AUTO_TOTEM_INTERVAL.getIntegerValue()) == 0) {
                AutoTotem.tick(client);
            }
            com.sweetljk.vitalassist.cine.CineDirector.onClientTick(client);
        });

        LOGGER.info("VitalAssist client loaded");
    }
}
