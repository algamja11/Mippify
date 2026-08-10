package com.gamja.mippify.compat;

import com.gamja.mippify.Mippify;
import com.gamja.mippify.access.Mappings;
import com.gamja.mippify.access.ReflectionUtils;
import com.gamja.mippify.render.MutablePipeline;
import com.gamja.mippify.render.PipelinePatcher;
import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.caffeinemc.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.minecraft.resources.Identifier;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class SodiumPipelinePatcher {
    private static final ArrayList<PipelinePatch> PIPELINE_PATCHES = new ArrayList<>();
    private static final ArrayList<String> SHADER_DEFINES = new ArrayList<>();

    private static Class<?> shadersClass;
    private static Method createShaderConstants;
    private static BindGroupLayout BIND_GROUP;

    private SodiumPipelinePatcher() {
    }

    static {
        if (Mippify.hasSodium()) {
            shadersClass = ShaderChunkRenderer.class;
            createShaderConstants = ReflectionUtils.tryGetMethod(shadersClass, Mappings.get("mod.sodium.method.ShaderChunkRenderer.createShaderConstants"), TerrainRenderPass.class);
            BIND_GROUP = (BindGroupLayout) ReflectionUtils.tryGet(ReflectionUtils.tryGetField(shadersClass, Mappings.get("mod.sodium.field.ShaderChunkRenderer.BIND_GROUP")), null);
        }
    }

    public static RenderPipeline getOverride(Object self, String path, TerrainRenderPass pass) {
        VertexFormat vertexFormat = (VertexFormat) ReflectionUtils.tryGet(ReflectionUtils.tryGetField(shadersClass, Mappings.get("mod.sodium.field.ShaderChunkRenderer.vertexFormat")), self);
        List<String> defines = createShaderConstants(pass);
        String shaderPath = pass.getPipeline().getLocation().getPath();

        int defIdx = SHADER_DEFINES.size();
        int defLen = defines.size();
        SHADER_DEFINES.addAll(defines);

        boolean isTranslucent = pass.isTranslucent();
        boolean isCutout = pass.supportsFragmentDiscard();

        if (isTranslucent || isCutout) {
            MutablePipeline pipeline = new MutablePipeline(Identifier.fromNamespaceAndPath(Mippify.MOD_ID, "sodium/" + shaderPath));
            PipelinePatch patch = new PipelinePatch(shaderPath, vertexFormat, defIdx, defLen, isTranslucent, isCutout, pipeline);
            PIPELINE_PATCHES.add(patch);
            patchPipelines();
            return pipeline;
        }

        return createShader(shaderPath, vertexFormat, defIdx, defLen, false, false);
    }

    private static List<String> createShaderConstants(TerrainRenderPass pass) {
        return (List<String>) ReflectionUtils.tryInvoke(createShaderConstants, null, pass);
    }

    public static void patchPipelines() {
        for (PipelinePatch patch : PIPELINE_PATCHES) {
            RenderPipeline shader = createShader(patch.path(), patch.format(), patch.defIdx(), patch.defLen(), patch.translucent(), patch.cutout());
            patch.pipeline().set(shader);
        }
    }

    private static RenderPipeline createShader(String path, VertexFormat format, int defIdx, int defLen, boolean translucent, boolean cutout) {
        RenderPipeline.Builder builder = RenderPipeline.builder()
                .withBindGroupLayout(BIND_GROUP)
                .withLocation(Identifier.fromNamespaceAndPath("sodium", path))
                .withCull(true)
                .withVertexShader(Identifier.fromNamespaceAndPath("sodium", "blocks/block_layer_opaque"))
                .withFragmentShader(Identifier.fromNamespaceAndPath("sodium", "blocks/block_layer_opaque"))
                .withDepthStencilState(DepthStencilState.DEFAULT)
                .withPrimitiveTopology(PrimitiveTopology.QUADS)
                .withVertexBinding(0, format);

        if (translucent) {
            builder.withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.TRANSLUCENT), GpuFormat.RGBA8_UNORM, -1));
        } else {
            builder.withColorTargetState(ColorTargetState.DEFAULT);
        }

        for (int i = defIdx; i < defIdx + defLen; i++) {
            builder.withShaderDefine(SHADER_DEFINES.get(i));
        }

        if (translucent) {
            builder.withShaderDefine("ALPHA_CUTOUT", PipelinePatcher.translucentShaderCutout());
        } else if (cutout) {
            builder.withShaderDefine("ALPHA_CUTOUT", PipelinePatcher.cutoutShaderCutout());
        }

        return builder.build();
    }

    private static record PipelinePatch(String path, VertexFormat format, int defIdx, int defLen, boolean translucent, boolean cutout, MutablePipeline pipeline) {
    }
}
