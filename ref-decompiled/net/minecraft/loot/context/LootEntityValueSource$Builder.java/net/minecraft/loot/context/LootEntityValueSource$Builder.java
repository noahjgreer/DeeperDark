/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.loot.context;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootEntityValueSource;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.dynamic.Codecs;

public static final class LootEntityValueSource.Builder<R> {
    private final Codecs.IdMapper<String, LootEntityValueSource<R>> ID_MAPPER = new Codecs.IdMapper();

    LootEntityValueSource.Builder() {
    }

    public <T> LootEntityValueSource.Builder<R> addAll(T[] values, Function<T, String> idGetter, Function<T, ? extends LootEntityValueSource<R>> sourceGetter) {
        for (T object : values) {
            this.ID_MAPPER.put(idGetter.apply(object), sourceGetter.apply(object));
        }
        return this;
    }

    public <T extends StringIdentifiable> LootEntityValueSource.Builder<R> addEnum(T[] values, Function<T, ? extends LootEntityValueSource<R>> sourceGetter) {
        return this.addAll(values, StringIdentifiable::asString, sourceGetter);
    }

    public <T extends StringIdentifiable & LootEntityValueSource<? extends R>> LootEntityValueSource.Builder<R> addEntityReferences(T[] values) {
        return this.addEnum((StringIdentifiable[])values, value -> LootEntityValueSource.cast((LootEntityValueSource)value));
    }

    public LootEntityValueSource.Builder<R> forEntities(Function<? super ContextParameter<? extends Entity>, ? extends LootEntityValueSource<R>> sourceFactory) {
        return this.addEnum(LootContext.EntityReference.values(), reference -> (LootEntityValueSource)sourceFactory.apply(reference.contextParam()));
    }

    public LootEntityValueSource.Builder<R> forBlockEntities(Function<? super ContextParameter<? extends BlockEntity>, ? extends LootEntityValueSource<R>> sourceFactory) {
        return this.addEnum(LootContext.BlockEntityReference.values(), reference -> (LootEntityValueSource)sourceFactory.apply(reference.contextParam()));
    }

    public LootEntityValueSource.Builder<R> forItemStacks(Function<? super ContextParameter<? extends ItemStack>, ? extends LootEntityValueSource<R>> sourceFactory) {
        return this.addEnum(LootContext.ItemStackReference.values(), reference -> (LootEntityValueSource)sourceFactory.apply(reference.contextParam()));
    }

    Codec<LootEntityValueSource<R>> getCodec() {
        return this.ID_MAPPER.getCodec((Codec<String>)Codec.STRING);
    }
}
