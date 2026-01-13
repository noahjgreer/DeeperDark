/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.json.BlockPropertiesPredicate;
import net.minecraft.state.StateManager;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record BlockModelDefinition.Variants(Map<String, BlockStateModel.Unbaked> models) {
    public static final Codec<BlockModelDefinition.Variants> CODEC = Codecs.nonEmptyMap(Codec.unboundedMap((Codec)Codec.STRING, BlockStateModel.Unbaked.CODEC)).xmap(BlockModelDefinition.Variants::new, BlockModelDefinition.Variants::models);

    public void load(StateManager<Block, BlockState> stateManager, Supplier<String> idSupplier, BiConsumer<BlockState, BlockStateModel.UnbakedGrouped> callback) {
        this.models.forEach((predicate, model) -> {
            try {
                Predicate predicate2 = BlockPropertiesPredicate.parse(stateManager, predicate);
                BlockStateModel.UnbakedGrouped unbakedGrouped = model.cached();
                for (BlockState blockState : stateManager.getStates()) {
                    if (!predicate2.test(blockState)) continue;
                    callback.accept(blockState, unbakedGrouped);
                }
            }
            catch (Exception exception) {
                LOGGER.warn("Exception loading blockstate definition: '{}' for variant: '{}': {}", new Object[]{idSupplier.get(), predicate, exception.getMessage()});
            }
        });
    }
}
