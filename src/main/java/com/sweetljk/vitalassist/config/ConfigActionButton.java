package com.sweetljk.vitalassist.config;

import fi.dy.masa.malilib.config.options.ConfigString;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * A config row that renders an action button instead of a normal editable value.
 * It exists only in the config GUI and is not part of the saved config list.
 */
public final class ConfigActionButton extends ConfigString {
    private final Supplier<String> buttonTextSupplier;
    private final BooleanSupplier enabledSupplier;
    private final Runnable action;
    private final String hoverText;

    public ConfigActionButton(String name, String displayName, String initialButtonText, String comment,
                              Supplier<String> buttonTextSupplier,
                              BooleanSupplier enabledSupplier,
                              Runnable action,
                              String hoverText) {
        super(name, initialButtonText, comment);
        this.setTranslatedName(displayName);
        this.buttonTextSupplier = buttonTextSupplier;
        this.enabledSupplier = enabledSupplier;
        this.action = action;
        this.hoverText = hoverText;
    }

    public String getButtonText() {
        return this.buttonTextSupplier.get();
    }

    public boolean isButtonEnabled() {
        return this.enabledSupplier.getAsBoolean();
    }

    public String getHoverText() {
        return this.hoverText;
    }

    public void runAction() {
        this.action.run();
    }
}
