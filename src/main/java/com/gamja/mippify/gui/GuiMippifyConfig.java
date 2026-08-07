package com.gamja.mippify.gui;

import com.gamja.mippify.Lang;
import com.gamja.mippify.Mippify;
import com.gamja.mippify.MippifyConfig;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class GuiMippifyConfig extends Screen {
    private final Screen parentGui;
    private final HeaderAndFooterLayout layout;
    private final MippifyConfig config;

    private final ArrayList<AbstractWidget> configWidgets = new ArrayList<>();

    private boolean anyChanged = false;

    public GuiMippifyConfig(Screen parentGui) {
        super(Lang.getComponent("mippify.gui.title"));
        this.parentGui = parentGui;
        layout = new HeaderAndFooterLayout(this);
        config = Mippify.config();
    }

    @Override
    public void init() {
        configWidgets.clear();
        layout.removeChildren();
        clearWidgets();
        setupScreen();
        layout.visitWidgets(this::addRenderableWidget);
        layout.arrangeElements();
    }

    private int calcX(int i) {
        return width / 2 - 155 + i % 2 * 160;
    }

    private int calcY(int i) {
        return height / 6 + 21 * (i / 2) - 12;
    }

    private void setupScreen() {
        layout.addTitleHeader(getTitle(), getFont());
        layout.addToFooter(new Button.Builder(CommonComponents.GUI_DONE, (_) -> onClose()).size(200, 20).build());

        setupConfigs();
    }

    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        for (AbstractWidget widget : configWidgets) {
            if (!widget.isHovered()) continue;
            if (!(widget instanceof IConfigWidget configWidget)) continue;

            int boxX = width / 2 - 150;
            int boxY = height / 6 - 7;
            if (mouseY <= boxY + 98) {
                boxY += 105;
            }
            int boxW = 300;
            int boxH = 94;

            TooltipRenderUtil.extractTooltipBackground(graphics, boxX, boxY, boxW, boxH, null);

            Component tooltip = Lang.getComponent(configWidget.getAction().tooltip());
            int i = 0;
            for (FormattedCharSequence line : minecraft.font.split(tooltip, boxW - 6)) {
                graphics.text(minecraft.font, line, boxX + 3, boxY + 3 + i * 10, 0xFFDDDDDD);
                ++i;
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        minecraft.gui.setScreen(parentGui);
        config.saveAll();

        if (anyChanged) {
            minecraft.reloadResourcePacks();
        }
    }

    private void setupConfigs() {
        int i = 0;
        for (ConfigHandler.Action action : ConfigHandler.Action.values()) {
            int x = calcX(i);
            int y = calcY(i);
            int w = 150;
            int h = 20;

            Component message = ConfigHandler.getMessage(action);
            AbstractWidget widget = null;
            if (action.type() == 0) {
                widget = addRenderableWidget(new ConfigWidgetButton(action, x, y, w, h, message, (button) -> {
                    ConfigHandler.updateConfig(action);
                    button.setMessage(ConfigHandler.getMessage(action));

                    anyChanged = true;
                }));

            } else if (action.type() == 1) {
                widget = addRenderableWidget(new ConfigWidgetSlider(action, x, y, w, h, message, ConfigHandler.doubleConfig(action), (slider) -> {
                    ConfigHandler.updateConfig(action, slider.getValue());
                    slider.setMessage(ConfigHandler.getMessage(action));

                    anyChanged = true;
                }));

            }

            configWidgets.add(widget);

            ++i;
        }
    }
}
