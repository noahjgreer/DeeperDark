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
import java.util.Optional;
import net.minecraft.util.collection.ListOperation;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

public record ListOperation.ReplaceSection(int offset, Optional<Integer> size) implements ListOperation
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<ListOperation.ReplaceSection> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.NON_NEGATIVE_INT.optionalFieldOf("offset", (Object)0).forGetter(ListOperation.ReplaceSection::offset), (App)Codecs.NON_NEGATIVE_INT.optionalFieldOf("size").forGetter(ListOperation.ReplaceSection::size)).apply((Applicative)instance, ListOperation.ReplaceSection::new));

    public ListOperation.ReplaceSection(int offset) {
        this(offset, Optional.empty());
    }

    @Override
    public ListOperation.Mode getMode() {
        return ListOperation.Mode.REPLACE_SECTION;
    }

    @Override
    public <T> List<T> apply(List<T> current, List<T> values, int maxSize) {
        ImmutableList list;
        int i = current.size();
        if (this.offset > i) {
            LOGGER.error("Cannot replace when offset is out of bounds");
            return current;
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(current.subList(0, this.offset));
        builder.addAll(values);
        int j = this.offset + this.size.orElse(values.size());
        if (j < i) {
            builder.addAll(current.subList(j, i));
        }
        if ((list = builder.build()).size() > maxSize) {
            LOGGER.error("Contents overflow in section replacement");
            return current;
        }
        return list;
    }
}
