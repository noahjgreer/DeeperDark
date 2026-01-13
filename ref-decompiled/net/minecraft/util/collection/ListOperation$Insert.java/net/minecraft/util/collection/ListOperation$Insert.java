/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.slf4j.Logger
 */
package net.minecraft.util.collection;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.collection.ListOperation;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

public record ListOperation.Insert(int offset) implements ListOperation
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<ListOperation.Insert> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.NON_NEGATIVE_INT.optionalFieldOf("offset", (Object)0).forGetter(ListOperation.Insert::offset)).apply((Applicative)instance, ListOperation.Insert::new));

    @Override
    public ListOperation.Mode getMode() {
        return ListOperation.Mode.INSERT;
    }

    @Override
    public <T> List<T> apply(List<T> current, List<T> values, int maxSize) {
        int i = current.size();
        if (this.offset > i) {
            LOGGER.error("Cannot insert when offset is out of bounds");
            return current;
        }
        if (i + values.size() > maxSize) {
            LOGGER.error("Contents overflow in section insertion");
            return current;
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(current.subList(0, this.offset));
        builder.addAll(values);
        builder.addAll(current.subList(this.offset, i));
        return builder.build();
    }
}
