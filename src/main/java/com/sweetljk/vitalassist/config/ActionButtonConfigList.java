package com.sweetljk.vitalassist.config;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;

public class ActionButtonConfigList extends WidgetListConfigOptions {
    public ActionButtonConfigList(int x, int y, int width, int height, int configWidth,
                                  float scrollbarPosition, boolean useKeybindSearch,
                                  GuiConfigsBase parent) {
        super(x, y, width, height, configWidth, scrollbarPosition, useKeybindSearch, parent);
    }

    @Override
    protected WidgetConfigOption createListEntryWidget(int x, int y, int listIndex, boolean isOdd,
                                                       GuiConfigsBase.ConfigOptionWrapper wrapper) {
        if (wrapper.getConfig() instanceof ConfigActionButton) {
            return new WidgetConfigAction(x, y, this.browserEntryWidth, this.browserEntryHeight,
                    this.maxLabelWidth, this.configWidth, wrapper, listIndex, this.parent, this);
        }
        return super.createListEntryWidget(x, y, listIndex, isOdd, wrapper);
    }
}
