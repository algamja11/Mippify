package com.gamja.mippify.mixin;

import com.gamja.mippify.render.PipelineOverrides;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShaderManager.class)
public class MixinShaderManager {
    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/client/renderer/ShaderManager$Configs;", at = @At("RETURN"))
    private void mippify$prepare(ResourceManager manager, ProfilerFiller profiler, CallbackInfoReturnable<ShaderManager.Configs> cir) {
        PipelineOverrides.updatePipelines(false);
    }
}
