/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.structure;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public static class RuinedPortalStructurePiece.Properties {
    public static final Codec<RuinedPortalStructurePiece.Properties> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.BOOL.fieldOf("cold").forGetter(properties -> properties.cold), (App)Codec.FLOAT.fieldOf("mossiness").forGetter(properties -> Float.valueOf(properties.mossiness)), (App)Codec.BOOL.fieldOf("air_pocket").forGetter(properties -> properties.airPocket), (App)Codec.BOOL.fieldOf("overgrown").forGetter(properties -> properties.overgrown), (App)Codec.BOOL.fieldOf("vines").forGetter(properties -> properties.vines), (App)Codec.BOOL.fieldOf("replace_with_blackstone").forGetter(properties -> properties.replaceWithBlackstone)).apply((Applicative)instance, RuinedPortalStructurePiece.Properties::new));
    public boolean cold;
    public float mossiness;
    public boolean airPocket;
    public boolean overgrown;
    public boolean vines;
    public boolean replaceWithBlackstone;

    public RuinedPortalStructurePiece.Properties() {
    }

    public RuinedPortalStructurePiece.Properties(boolean cold, float mossiness, boolean airPocket, boolean overgrown, boolean vines, boolean replaceWithBlackstone) {
        this.cold = cold;
        this.mossiness = mossiness;
        this.airPocket = airPocket;
        this.overgrown = overgrown;
        this.vines = vines;
        this.replaceWithBlackstone = replaceWithBlackstone;
    }
}
