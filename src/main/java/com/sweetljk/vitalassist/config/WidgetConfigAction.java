package com.sweetljk.vitalassist.config;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import fi.dy.masa.malilib.render.GuiContext;

public class WidgetConfigAction extends WidgetConfigOption {
    private ConfigActionButton actionConfig;
    private ButtonGeneric actionButton;

    public WidgetConfigAction(int x, int y, int width, int height, int labelWidth, int configWidth,
                              GuiConfigsBase.ConfigOptionWrapper wrapper, int listIndex,
                              IKeybindConfigGui host, WidgetListConfigOptionsBase<?, ?> parent) {
        super(x, y, width, height, labelWidth, configWidth, wrapper, listIndex, host, parent);
    }

    @Override
    protected void addConfigOption(int x, int y, int labelWidth, int configWidth, IConfigBase config) {
        if (!(config instanceof ConfigActionButton action)) {
            super.addConfigOption(x, y, labelWidth, configWidth, config);
            return;
        }

        this.addLabel(x, y + 7, labelWidth, 8, -1, new String[]{config.getConfigGuiDisplayName()});
        if (config.getComment() != null && !config.getComment().isBlank()) {
            this.addConfigComment(x, y + 5, labelWidth, 12, config.getComment());
        }

        ButtonGeneric button = new ButtonGeneric(x + labelWidth + 10, y, configWidth, 20, action.getButtonText());
        button.setHoverStrings(action.getHoverText());
        button.setEnabled(action.isButtonEnabled());
        this.addButton(button, (clickedButton, mouseButton) -> {
            if (mouseButton == 0 && action.isButtonEnabled()) {
                action.runAction();
            }
        });

        this.actionConfig = action;
        this.actionButton = button;
    }

    @Override
    public void render(GuiContext guiContext, int mouseX, int mouseY, boolean selected) {
        if (this.actionConfig != null && this.actionButton != null) {
            this.actionButton.setDisplayString(this.actionConfig.getButtonText());
            this.actionButton.setEnabled(this.actionConfig.isButtonEnabled());
        }
        super.render(guiContext, mouseX, mouseY, selected);
    }

    @Override
    public boolean wasConfigModified() {
        return false;
    }

    @Override
    public void applyNewValueToConfig() {
    }
}
