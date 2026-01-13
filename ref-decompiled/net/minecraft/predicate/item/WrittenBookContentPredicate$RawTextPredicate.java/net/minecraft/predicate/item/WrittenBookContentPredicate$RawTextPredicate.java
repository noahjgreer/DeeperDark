/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record WrittenBookContentPredicate.RawTextPredicate(Text contents) implements Predicate<RawFilteredPair<Text>>
{
    public static final Codec<WrittenBookContentPredicate.RawTextPredicate> CODEC = TextCodecs.CODEC.xmap(WrittenBookContentPredicate.RawTextPredicate::new, WrittenBookContentPredicate.RawTextPredicate::contents);

    @Override
    public boolean test(RawFilteredPair<Text> rawFilteredPair) {
        return rawFilteredPair.raw().equals(this.contents);
    }

    @Override
    public /* synthetic */ boolean test(Object text) {
        return this.test((RawFilteredPair)text);
    }
}
