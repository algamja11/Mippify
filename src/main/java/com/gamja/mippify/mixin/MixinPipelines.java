package com.gamja.mippify.mixin;

import com.gamja.mippify.Mippify;
import com.gamja.mippify.render.PipelineOverrides;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderPipelines.class)
public class MixinPipelines {
    @ModifyVariable(method = "register", at = @At("HEAD"))
    private static RenderPipeline mippify$register(RenderPipeline pipeline) {
        RenderPipeline override = PipelineOverrides.get(pipeline.getLocation());
// FIXME
//        if (Mippify.config().smoothing && override != null) {
        if (override != null) {
            return override;
        }
        return pipeline;
    }
}
