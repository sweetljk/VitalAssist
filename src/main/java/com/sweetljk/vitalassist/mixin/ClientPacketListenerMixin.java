package com.sweetljk.vitalassist.mixin;

import com.sweetljk.vitalassist.feature.ChatTranslator;
import com.sweetljk.vitalassist.feature.ZhouLi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Unique
    private boolean vitalassist$llmProcessing;

    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    private void vitalassist$sendChat(String message, CallbackInfo ci) {
        if (vitalassist$llmProcessing) return;

        boolean translating = ChatTranslator.isActive();
        if (!translating && !ZhouLi.isActive()) return;
        if (ChatTranslator.shouldSkip(message)) return;

        ci.cancel();
        vitalassist$llmProcessing = true;
        String original = message;
        ClientPacketListener self = (ClientPacketListener) (Object) this;

        CompletableFuture<String> resultFuture = translating
                ? ChatTranslator.translateAsync(original)
                : ZhouLi.rewriteAsync(original);

        resultFuture.whenComplete((result, error) -> Minecraft.getInstance().execute(() -> {
            try {
                String outgoing = error == null && result != null && !result.isBlank() ? result : original;
                self.sendChat(outgoing);
            } finally {
                vitalassist$llmProcessing = false;
            }
        }));
    }
}
