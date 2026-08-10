package com.gamja.mippify.mixin.sodium;

import com.gamja.mippify.compat.SodiumPipelinePatcher;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.caffeinemc.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShaderChunkRenderer.class)
public class MixinSodiumShaders {
    @Inject(method = "createShader", at = @At("HEAD"), cancellable = true)
    private void mippify$createShader(String path, TerrainRenderPass pass, CallbackInfoReturnable<RenderPipeline> cir) {
        cir.setReturnValue(SodiumPipelinePatcher.getOverride(this, path, pass));
    }
}
