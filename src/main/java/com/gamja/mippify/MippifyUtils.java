package com.gamja.mippify;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class MippifyUtils {
    private MippifyUtils() {
    }

    private static Minecraft mc() {
        return Minecraft.getInstance();
    }

    public static boolean getKeyDown(int scanCode) {
        return InputConstants.isKeyDown(mc().getWindow(), scanCode);
    }

    public static Screen getScreen() {
        return mc().gui.screen();
    }

    public static void setScreen(Screen screen) {
        mc().gui.setScreen(screen);
    }

    public static void reloadResources() {
        mc().updateMaxMipLevel(mc().options.mipmapLevels().get());
        mc().delayTextureReload();
    }

    public static void flushRenderer() {
        if (RenderSystem.isOnRenderThread()) {
            RenderSystem.getDevice().createCommandEncoder().submit();
            RenderSystem.getDevice().clearPipelineCache();
        } else {
            Mippify.LOGGER.warn("flushRenderer() was called outside of the render thread!");
        }
        mc().levelExtractor.allChanged();
        mc().levelRenderer.clearVisibleSections();
    }
}
