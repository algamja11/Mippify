package com.gamja.mippify.render;

import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PolygonMode;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

/**
 * Edited from {@link com.mojang.renderpearl.api.pipeline.RenderPipeline}
 */

public class MutablePipeline extends RenderPipeline {
    private static final Identifier EMPTY_ID = Identifier.parse("");
    private Map<ShaderType, Identifier> shaders;
    private ShaderDefines shaderDefines;
    private List<BindGroupLayout> bindGroupLayouts;
    private @Nullable DepthStencilState depthStencilState;
    private PolygonMode polygonMode;
    private boolean cull;
    private List<@Nullable ColorTargetState> colorTargetStates;
    private List<@Nullable VertexFormat> vertexFormatPerBuffer;
    private PrimitiveTopology primitiveTopology;
    private int pushConstantSize;

    public MutablePipeline(Identifier location) {
        this(location, Map.of(ShaderType.VERTEX, EMPTY_ID), ShaderDefines.EMPTY, List.of(), new ColorTargetState[0], DepthStencilState.DEFAULT, PolygonMode.FILL, false, new VertexFormat[0], PrimitiveTopology.LINES, -1, -1);
    }

    public MutablePipeline(final Identifier location, final Map<ShaderType, Identifier> shaders, final ShaderDefines shaderDefines, final Collection<BindGroupLayout> bindGroupLayouts, final @Nullable ColorTargetState[] colorTargetStates, final @Nullable DepthStencilState depthStencilState, final PolygonMode polygonMode, final boolean cull, final @Nullable VertexFormat[] vertexFormatPerBuffer, final PrimitiveTopology primitiveTopology, final int pushConstantSize, final int sortKey) {
        super(location, shaders, shaderDefines, bindGroupLayouts, colorTargetStates, depthStencilState, polygonMode, cull, vertexFormatPerBuffer, primitiveTopology, pushConstantSize, sortKey);
    }

    public void set(RenderPipeline pipeline) {
        set(pipeline.getShaders(), pipeline.getShaderDefines(), pipeline.getBindGroupLayouts(), pipeline.getColorTargetStates().toArray(ColorTargetState[]::new), pipeline.getDepthStencilState(), pipeline.getPolygonMode(), pipeline.isCull(), pipeline.getVertexFormatBindings().toArray(VertexFormat[]::new), pipeline.getPrimitiveTopology(), pipeline.pushConstantSize());
    }

    public void set(final Map<ShaderType, Identifier> shaders, final ShaderDefines shaderDefines, final Collection<BindGroupLayout> bindGroupLayouts, final @Nullable ColorTargetState[] colorTargetStates, final @Nullable DepthStencilState depthStencilState, final PolygonMode polygonMode, final boolean cull, final @Nullable VertexFormat[] vertexFormatPerBuffer, final PrimitiveTopology primitiveTopology, final int pushConstantSize) {
        this.shaders = Collections.unmodifiableMap(new EnumMap(shaders));
        this.shaderDefines = shaderDefines;
        this.bindGroupLayouts = List.copyOf(bindGroupLayouts);
        this.depthStencilState = depthStencilState;
        this.polygonMode = polygonMode;
        this.cull = cull;
        this.colorTargetStates = ReferenceLists.unmodifiable(new ReferenceArrayList(colorTargetStates));
        this.vertexFormatPerBuffer = ReferenceLists.unmodifiable(new ReferenceArrayList(vertexFormatPerBuffer));
        this.primitiveTopology = primitiveTopology;
        this.pushConstantSize = pushConstantSize;
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
    public List<@Nullable ColorTargetState> getColorTargetStates() {
        return this.colorTargetStates;
    }

    @Override
    public @Nullable DepthStencilState getDepthStencilState() {
        return this.depthStencilState;
    }

    @Override
    public List<@Nullable VertexFormat> getVertexFormatBindings() {
        return this.vertexFormatPerBuffer;
    }

    @Override
    public @Nullable VertexFormat getVertexFormatBinding(final int bindingIndex) {
        return this.vertexFormatPerBuffer.get(bindingIndex);
    }

    @Override
    public PrimitiveTopology getPrimitiveTopology() {
        return this.primitiveTopology;
    }

    @Override
    public Map<ShaderType, Identifier> getShaders() {
        return this.shaders;
    }

    @Override
    public ShaderDefines getShaderDefines() {
        return this.shaderDefines;
    }

    @Override
    public List<BindGroupLayout> getBindGroupLayouts() {
        return this.bindGroupLayouts;
    }

    @Override
    public boolean wantsDepthTexture() {
        return this.depthStencilState != null;
    }

    @Override
    public int pushConstantSize() {
        return this.pushConstantSize;
    }
}
