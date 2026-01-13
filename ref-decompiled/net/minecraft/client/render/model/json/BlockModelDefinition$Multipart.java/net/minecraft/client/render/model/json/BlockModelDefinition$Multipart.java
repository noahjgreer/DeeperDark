/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.MultipartBlockStateModel;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.state.StateManager;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record BlockModelDefinition.Multipart(List<MultipartModelComponent> selectors) {
    public static final Codec<BlockModelDefinition.Multipart> CODEC = Codecs.nonEmptyList(MultipartModelComponent.CODEC.listOf()).xmap(BlockModelDefinition.Multipart::new, BlockModelDefinition.Multipart::selectors);

    public MultipartBlockStateModel.MultipartUnbaked toModel(StateManager<Block, BlockState> stateManager) {
        ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize((int)this.selectors.size());
        for (MultipartModelComponent multipartModelComponent : this.selectors) {
            builder.add(new MultipartBlockStateModel.Selector<BlockStateModel.Unbaked>(multipartModelComponent.init(stateManager), multipartModelComponent.model()));
        }
        return new MultipartBlockStateModel.MultipartUnbaked((List<MultipartBlockStateModel.Selector<BlockStateModel.Unbaked>>)builder.build());
    }
}
