package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.sweetljk.vitalassist.feature.BetterChat;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class ChatHudMixin {
    @Shadow @Final private List<GuiMessage> allMessages;
    @Shadow @Final private Minecraft minecraft;
    @Shadow private void refreshTrimmedMessages() {}

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"), cancellable = true)
    private void vitalassist$addMessage(Component message, MessageSignature signature, GuiMessageTag indicator, CallbackInfo ci) {
        if (BetterChat.handleMessage(allMessages, this::refreshTrimmedMessages, message, signature, indicator, minecraft.gui.getGuiTicks())) ci.cancel();
    }

    @Inject(method = "clearMessages", at = @At("HEAD"))
    private void vitalassist$clearMessages(boolean clearHistory, CallbackInfo ci) {
        BetterChat.clear();
    }
}
