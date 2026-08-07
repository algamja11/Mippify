package com.gamja.mippify.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.LogicOp;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;

/**
 * Edited from {@link com.mojang.blaze3d.pipeline.RenderPipeline}
 */

public class MutablePipeline extends RenderPipeline {
    private static final Identifier EMPTY_ID = Identifier.parse("");
    private Identifier vertexShader;
    private Identifier fragmentShader;
    private ShaderDefines shaderDefines;
    private List<String> samplers;
    private List<UniformDescription> uniforms;
    private DepthTestFunction depthTestFunction;
    private PolygonMode polygonMode;
    private boolean cull;
    private LogicOp colorLogic;
    private Optional<BlendFunction> blendFunction;
    private boolean writeColor;
    private boolean writeAlpha;
    private boolean writeDepth;
    private VertexFormat vertexFormat;
    private VertexFormat.Mode vertexFormatMode;
    private float depthBiasScaleFactor;
    private float depthBiasConstant;

    public MutablePipeline(Identifier location) {
        this(location, EMPTY_ID, EMPTY_ID, ShaderDefines.EMPTY, List.of(), List.of(), Optional.empty(), DepthTestFunction.NO_DEPTH_TEST, PolygonMode.FILL, false, false, false, false, LogicOp.NONE, DefaultVertexFormat.EMPTY, VertexFormat.Mode.LINES, 0.0F, 0.0F, -1);
    }

    public MutablePipeline(Identifier location, Identifier vertexShader, Identifier fragmentShader, ShaderDefines shaderDefines, List<String> samplers, List<UniformDescription> uniforms, Optional<BlendFunction> blendFunction, DepthTestFunction depthTestFunction, PolygonMode polygonMode, boolean cull, boolean writeColor, boolean writeAlpha, boolean writeDepth, LogicOp colorLogic, VertexFormat vertexFormat, VertexFormat.Mode vertexFormatMode, float depthBiasScaleFactor, float depthBiasConstant, int sortKey) {
        super(location, vertexShader, fragmentShader, shaderDefines, samplers, uniforms, blendFunction, depthTestFunction, polygonMode, cull, writeColor, writeAlpha, writeDepth, colorLogic, vertexFormat, vertexFormatMode, depthBiasScaleFactor, depthBiasConstant, sortKey);
    }

    public void set(RenderPipeline pipeline) {
        set(pipeline.getVertexShader(), pipeline.getFragmentShader(), pipeline.getShaderDefines(), pipeline.getSamplers(), pipeline.getUniforms(), pipeline.getBlendFunction(), pipeline.getDepthTestFunction(), pipeline.getPolygonMode(), pipeline.isCull(), pipeline.isWriteColor(), pipeline.isWriteAlpha(), pipeline.isWriteDepth(), pipeline.getColorLogic(), pipeline.getVertexFormat(), pipeline.getVertexFormatMode(), pipeline.getDepthBiasScaleFactor(), pipeline.getDepthBiasConstant());
    }

    public void set(Identifier vertexShader, Identifier fragmentShader, ShaderDefines shaderDefines, List<String> samplers, List<UniformDescription> uniforms, Optional<BlendFunction> blendFunction, DepthTestFunction depthTestFunction, PolygonMode polygonMode, boolean cull, boolean writeColor, boolean writeAlpha, boolean writeDepth, LogicOp colorLogic, VertexFormat vertexFormat, VertexFormat.Mode vertexFormatMode, float depthBiasScaleFactor, float depthBiasConstant) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.shaderDefines = shaderDefines;
        this.samplers = samplers;
        this.uniforms = uniforms;
        this.depthTestFunction = depthTestFunction;
        this.polygonMode = polygonMode;
        this.cull = cull;
        this.blendFunction = blendFunction;
        this.writeColor = writeColor;
        this.writeAlpha = writeAlpha;
        this.writeDepth = writeDepth;
        this.colorLogic = colorLogic;
        this.vertexFormat = vertexFormat;
        this.vertexFormatMode = vertexFormatMode;
        this.depthBiasScaleFactor = depthBiasScaleFactor;
        this.depthBiasConstant = depthBiasConstant;
    }

    @Override
    public DepthTestFunction getDepthTestFunction() {
        return this.depthTestFunction;
    }

    @Override
    public PolygonMode getPolygonMode() {
        return this.polygonMode;
    }

    @Override
    public boolean isCull() {
        return this.cull;
    }

    @Override
    public LogicOp getColorLogic() {
        return this.colorLogic;
    }

    @Override
    public Optional<BlendFunction> getBlendFunction() {
        return this.blendFunction;
    }

    @Override
    public boolean isWriteColor() {
        return this.writeColor;
    }

    @Override
    public boolean isWriteAlpha() {
        return this.writeAlpha;
    }

    @Override
    public boolean isWriteDepth() {
        return this.writeDepth;
    }

    @Override
    public float getDepthBiasScaleFactor() {
        return this.depthBiasScaleFactor;
    }

    @Override
    public float getDepthBiasConstant() {
        return this.depthBiasConstant;
    }

    @Override
    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    @Override
    public VertexFormat.Mode getVertexFormatMode() {
        return this.vertexFormatMode;
    }

    @Override
    public Identifier getVertexShader() {
        return this.vertexShader;
    }

    @Override
    public Identifier getFragmentShader() {
        return this.fragmentShader;
    }

    @Override
    public ShaderDefines getShaderDefines() {
        return this.shaderDefines;
    }

    @Override
    public List<String> getSamplers() {
        return this.samplers;
    }

    @Override
    public List<UniformDescription> getUniforms() {
        return this.uniforms;
    }

    @Override
    public boolean wantsDepthTexture() {
        return this.depthTestFunction != DepthTestFunction.NO_DEPTH_TEST || this.depthBiasConstant != 0.0F || this.depthBiasScaleFactor != 0.0F || this.writeDepth;
    }
}
