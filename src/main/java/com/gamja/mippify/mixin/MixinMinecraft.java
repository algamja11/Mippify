package com.gamja.mippify.mixin;

import com.gamja.mippify.gui.GuiMippifyConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "tick", at = @At("RETURN"))
    private void mippify$tick(CallbackInfo ci) {
        if (Minecraft.getInstance().gui.screen() == null){
            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_M)) {
                Minecraft.getInstance().gui.setScreen(new GuiMippifyConfig(null));
            }
        }
    }
}
