package com.gamja.mippify.mixin;

import com.gamja.mippify.Mippify;
import com.gamja.mippify.render.MipGenerator;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Transparency;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.MipmapStrategy;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MipmapGenerator.class)
public class MixinMipGenerator {
    @Inject(method = "generateMipLevels", at = @At("HEAD"), cancellable = true)
    private static void mippify$generateMipLevels(Identifier name, NativeImage[] currentMips, int newMipLevel, MipmapStrategy mipmapStrategy, float alphaCutoffBias, Transparency transparency, CallbackInfoReturnable<NativeImage[]> cir) {
        if (Mippify.config().enableMod) {
            cir.setReturnValue(MipGenerator.generateMipLevels(name, currentMips, newMipLevel, mipmapStrategy, alphaCutoffBias, transparency));
        }
    }
}