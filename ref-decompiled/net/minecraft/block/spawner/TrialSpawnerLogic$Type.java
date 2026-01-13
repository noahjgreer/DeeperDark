/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.spawner;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;

public static final class TrialSpawnerLogic.Type
extends Enum<TrialSpawnerLogic.Type> {
    public static final /* enum */ TrialSpawnerLogic.Type NORMAL = new TrialSpawnerLogic.Type(ParticleTypes.FLAME);
    public static final /* enum */ TrialSpawnerLogic.Type OMINOUS = new TrialSpawnerLogic.Type(ParticleTypes.SOUL_FIRE_FLAME);
    public final SimpleParticleType particle;
    private static final /* synthetic */ TrialSpawnerLogic.Type[] field_50189;

    public static TrialSpawnerLogic.Type[] values() {
        return (TrialSpawnerLogic.Type[])field_50189.clone();
    }

    public static TrialSpawnerLogic.Type valueOf(String string) {
        return Enum.valueOf(TrialSpawnerLogic.Type.class, string);
    }

    private TrialSpawnerLogic.Type(SimpleParticleType particle) {
        this.particle = particle;
    }

    public static TrialSpawnerLogic.Type fromIndex(int index) {
        TrialSpawnerLogic.Type[] types = TrialSpawnerLogic.Type.values();
        if (index > types.length || index < 0) {
            return NORMAL;
        }
        return types[index];
    }

    public int getIndex() {
        return this.ordinal();
    }

    private static /* synthetic */ TrialSpawnerLogic.Type[] method_58711() {
        return new TrialSpawnerLogic.Type[]{NORMAL, OMINOUS};
    }

    static {
        field_50189 = TrialSpawnerLogic.Type.method_58711();
    }
}
