package com.gamja.mippify.mixin;

import com.gamja.mippify.gui.GuiMippifyConfig;
import com.gamja.mippify.render.PipelineOverrides;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "tick", at = @At("RETURN"))
    private void mippify$tick(CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gui.screen() == null && InputConstants.isKeyDown(minecraft.getWindow(), GLFW.GLFW_KEY_M)){
            minecraft.gui.setScreen(new GuiMippifyConfig(null));
        }
    }

    @Inject(method = "reloadResourcePacks()Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private void mippify$reloadResources(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        PipelineOverrides.updatePipelines(false);
    }
}
