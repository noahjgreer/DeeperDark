/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Keyable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Keyable;
import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.MultipartModelCombinedCondition;
import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.util.StringIdentifiable;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface MultipartModelCondition {
    public static final Codec<MultipartModelCondition> CODEC = Codec.recursive((String)"condition", group -> {
        Codec codec = Codec.simpleMap(MultipartModelCombinedCondition.LogicalOperator.CODEC, (Codec)group.listOf(), (Keyable)StringIdentifiable.toKeyable(MultipartModelCombinedCondition.LogicalOperator.values())).codec().comapFlatMap(map -> {
            if (map.size() != 1) {
                return DataResult.error(() -> "Invalid map size for combiner condition, expected exactly one element");
            }
            Map.Entry entry = map.entrySet().iterator().next();
            return DataResult.success((Object)new MultipartModelCombinedCondition((MultipartModelCombinedCondition.LogicalOperator)entry.getKey(), (List)entry.getValue()));
        }, condition -> Map.of(condition.operation(), condition.terms()));
        return Codec.either((Codec)codec, SimpleMultipartModelSelector.CODEC).flatComapMap(either -> (MultipartModelCondition)either.map(condition -> condition, selector -> selector), condition -> {
            MultipartModelCondition multipartModelCondition = condition;
            Objects.requireNonNull(multipartModelCondition);
            MultipartModelCondition multipartModelCondition2 = multipartModelCondition;
            int i = 0;
            DataResult dataResult = switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{MultipartModelCombinedCondition.class, SimpleMultipartModelSelector.class}, (Object)multipartModelCondition2, i)) {
                case 0 -> {
                    MultipartModelCombinedCondition multipartModelCombinedCondition = (MultipartModelCombinedCondition)multipartModelCondition2;
                    yield DataResult.success((Object)Either.left((Object)multipartModelCombinedCondition));
                }
                case 1 -> {
                    SimpleMultipartModelSelector simpleMultipartModelSelector = (SimpleMultipartModelSelector)multipartModelCondition2;
                    yield DataResult.success((Object)Either.right((Object)simpleMultipartModelSelector));
                }
                default -> DataResult.error(() -> "Unrecognized condition");
            };
            return dataResult;
        });
    });

    public <O, S extends State<O, S>> Predicate<S> instantiate(StateManager<O, S> var1);
}
