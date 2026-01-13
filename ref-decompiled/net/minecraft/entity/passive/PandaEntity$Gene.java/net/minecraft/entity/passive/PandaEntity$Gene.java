/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.random.Random;

public static final class PandaEntity.Gene
extends Enum<PandaEntity.Gene>
implements StringIdentifiable {
    public static final /* enum */ PandaEntity.Gene NORMAL = new PandaEntity.Gene(0, "normal", false);
    public static final /* enum */ PandaEntity.Gene LAZY = new PandaEntity.Gene(1, "lazy", false);
    public static final /* enum */ PandaEntity.Gene WORRIED = new PandaEntity.Gene(2, "worried", false);
    public static final /* enum */ PandaEntity.Gene PLAYFUL = new PandaEntity.Gene(3, "playful", false);
    public static final /* enum */ PandaEntity.Gene BROWN = new PandaEntity.Gene(4, "brown", true);
    public static final /* enum */ PandaEntity.Gene WEAK = new PandaEntity.Gene(5, "weak", true);
    public static final /* enum */ PandaEntity.Gene AGGRESSIVE = new PandaEntity.Gene(6, "aggressive", false);
    public static final Codec<PandaEntity.Gene> CODEC;
    private static final IntFunction<PandaEntity.Gene> BY_ID;
    private static final int field_30350 = 6;
    private final int id;
    private final String name;
    private final boolean recessive;
    private static final /* synthetic */ PandaEntity.Gene[] field_6796;

    public static PandaEntity.Gene[] values() {
        return (PandaEntity.Gene[])field_6796.clone();
    }

    public static PandaEntity.Gene valueOf(String string) {
        return Enum.valueOf(PandaEntity.Gene.class, string);
    }

    private PandaEntity.Gene(int id, String name, boolean recessive) {
        this.id = id;
        this.name = name;
        this.recessive = recessive;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public boolean isRecessive() {
        return this.recessive;
    }

    static PandaEntity.Gene getProductGene(PandaEntity.Gene mainGene, PandaEntity.Gene hiddenGene) {
        if (mainGene.isRecessive()) {
            if (mainGene == hiddenGene) {
                return mainGene;
            }
            return NORMAL;
        }
        return mainGene;
    }

    public static PandaEntity.Gene byId(int id) {
        return BY_ID.apply(id);
    }

    public static PandaEntity.Gene createRandom(Random random) {
        int i = random.nextInt(16);
        if (i == 0) {
            return LAZY;
        }
        if (i == 1) {
            return WORRIED;
        }
        if (i == 2) {
            return PLAYFUL;
        }
        if (i == 4) {
            return AGGRESSIVE;
        }
        if (i < 9) {
            return WEAK;
        }
        if (i < 11) {
            return BROWN;
        }
        return NORMAL;
    }

    private static /* synthetic */ PandaEntity.Gene[] method_36642() {
        return new PandaEntity.Gene[]{NORMAL, LAZY, WORRIED, PLAYFUL, BROWN, WEAK, AGGRESSIVE};
    }

    static {
        field_6796 = PandaEntity.Gene.method_36642();
        CODEC = StringIdentifiable.createCodec(PandaEntity.Gene::values);
        BY_ID = ValueLists.createIndexToValueFunction(PandaEntity.Gene::getId, PandaEntity.Gene.values(), ValueLists.OutOfBoundsHandling.ZERO);
    }
}
