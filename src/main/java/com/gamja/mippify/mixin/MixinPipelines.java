package com.gamja.mippify.mixin;

import com.gamja.mippify.render.PipelineOverrides;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderPipelines.class)
public class MixinPipelines {
    @ModifyVariable(method = "register(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;)Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;", at = @At("HEAD"))
    private static RenderPipeline mippify$register(RenderPipeline pipeline) {
        return mippify$registerInternal(pipeline);
    }

    @ModifyVariable(method = "registerOptional", at = @At("HEAD"))
    private static RenderPipeline mippify$registerOptional(RenderPipeline pipeline) {
        return mippify$registerInternal(pipeline);
    }

    @Unique
    private static RenderPipeline mippify$registerInternal(RenderPipeline pipeline) {
        RenderPipeline override = PipelineOverrides.get(pipeline.getLocation());
        return override == null ? pipeline : override;
    }
}
