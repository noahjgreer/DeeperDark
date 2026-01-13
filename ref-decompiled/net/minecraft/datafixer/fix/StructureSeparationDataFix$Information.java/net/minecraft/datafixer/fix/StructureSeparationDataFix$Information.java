/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

static final class StructureSeparationDataFix.Information {
    public static final Codec<StructureSeparationDataFix.Information> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("spacing").forGetter(information -> information.spacing), (App)Codec.INT.fieldOf("separation").forGetter(information -> information.separation), (App)Codec.INT.fieldOf("salt").forGetter(information -> information.salt)).apply((Applicative)instance, StructureSeparationDataFix.Information::new));
    final int spacing;
    final int separation;
    final int salt;

    public StructureSeparationDataFix.Information(int spacing, int separation, int salt) {
        this.spacing = spacing;
        this.separation = separation;
        this.salt = salt;
    }

    public <T> Dynamic<T> method_28288(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, CODEC.encodeStart(dynamicOps, (Object)this).result().orElse(dynamicOps.emptyMap()));
    }
}
