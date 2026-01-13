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

public record WritableBookContentPredicate.RawStringPredicate(String contents) implements Predicate<RawFilteredPair<String>>
{
    public static final Codec<WritableBookContentPredicate.RawStringPredicate> CODEC = Codec.STRING.xmap(WritableBookContentPredicate.RawStringPredicate::new, WritableBookContentPredicate.RawStringPredicate::contents);

    @Override
    public boolean test(RawFilteredPair<String> rawFilteredPair) {
        return rawFilteredPair.raw().equals(this.contents);
    }

    @Override
    public /* synthetic */ boolean test(Object string) {
        return this.test((RawFilteredPair)string);
    }
}
