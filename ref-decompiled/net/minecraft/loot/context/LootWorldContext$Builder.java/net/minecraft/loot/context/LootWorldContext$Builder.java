/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.loot.context;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import org.jspecify.annotations.Nullable;

public static class LootWorldContext.Builder {
    private final ServerWorld world;
    private final ContextParameterMap.Builder parameters = new ContextParameterMap.Builder();
    private final Map<Identifier, LootWorldContext.DynamicDrop> dynamicDrops = Maps.newHashMap();
    private float luck;

    public LootWorldContext.Builder(ServerWorld world) {
        this.world = world;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    public <T> LootWorldContext.Builder add(ContextParameter<T> parameter, T value) {
        this.parameters.add(parameter, value);
        return this;
    }

    public <T> LootWorldContext.Builder addOptional(ContextParameter<T> parameter, @Nullable T value) {
        this.parameters.addNullable(parameter, value);
        return this;
    }

    public <T> T get(ContextParameter<T> parameter) {
        return this.parameters.getOrThrow(parameter);
    }

    public <T> @Nullable T getOptional(ContextParameter<T> parameter) {
        return this.parameters.getNullable(parameter);
    }

    public LootWorldContext.Builder addDynamicDrop(Identifier id, LootWorldContext.DynamicDrop dynamicDrop) {
        LootWorldContext.DynamicDrop dynamicDrop2 = this.dynamicDrops.put(id, dynamicDrop);
        if (dynamicDrop2 != null) {
            throw new IllegalStateException("Duplicated dynamic drop '" + String.valueOf(this.dynamicDrops) + "'");
        }
        return this;
    }

    public LootWorldContext.Builder luck(float luck) {
        this.luck = luck;
        return this;
    }

    public LootWorldContext build(ContextType contextType) {
        ContextParameterMap contextParameterMap = this.parameters.build(contextType);
        return new LootWorldContext(this.world, contextParameterMap, this.dynamicDrops, this.luck);
    }
}
