/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.function.IntFunction;
import net.minecraft.util.function.ValueLists;

protected static final class SpellcastingIllagerEntity.Spell
extends Enum<SpellcastingIllagerEntity.Spell> {
    public static final /* enum */ SpellcastingIllagerEntity.Spell NONE = new SpellcastingIllagerEntity.Spell(0, 0.0, 0.0, 0.0);
    public static final /* enum */ SpellcastingIllagerEntity.Spell SUMMON_VEX = new SpellcastingIllagerEntity.Spell(1, 0.7, 0.7, 0.8);
    public static final /* enum */ SpellcastingIllagerEntity.Spell FANGS = new SpellcastingIllagerEntity.Spell(2, 0.4, 0.3, 0.35);
    public static final /* enum */ SpellcastingIllagerEntity.Spell WOLOLO = new SpellcastingIllagerEntity.Spell(3, 0.7, 0.5, 0.2);
    public static final /* enum */ SpellcastingIllagerEntity.Spell DISAPPEAR = new SpellcastingIllagerEntity.Spell(4, 0.3, 0.3, 0.8);
    public static final /* enum */ SpellcastingIllagerEntity.Spell BLINDNESS = new SpellcastingIllagerEntity.Spell(5, 0.1, 0.1, 0.2);
    private static final IntFunction<SpellcastingIllagerEntity.Spell> BY_ID;
    final int id;
    final double[] particleVelocity;
    private static final /* synthetic */ SpellcastingIllagerEntity.Spell[] field_7376;

    public static SpellcastingIllagerEntity.Spell[] values() {
        return (SpellcastingIllagerEntity.Spell[])field_7376.clone();
    }

    public static SpellcastingIllagerEntity.Spell valueOf(String string) {
        return Enum.valueOf(SpellcastingIllagerEntity.Spell.class, string);
    }

    private SpellcastingIllagerEntity.Spell(int id, double particleVelocityX, double particleVelocityY, double particleVelocityZ) {
        this.id = id;
        this.particleVelocity = new double[]{particleVelocityX, particleVelocityY, particleVelocityZ};
    }

    public static SpellcastingIllagerEntity.Spell byId(int id) {
        return BY_ID.apply(id);
    }

    private static /* synthetic */ SpellcastingIllagerEntity.Spell[] method_36658() {
        return new SpellcastingIllagerEntity.Spell[]{NONE, SUMMON_VEX, FANGS, WOLOLO, DISAPPEAR, BLINDNESS};
    }

    static {
        field_7376 = SpellcastingIllagerEntity.Spell.method_36658();
        BY_ID = ValueLists.createIndexToValueFunction(spell -> spell.id, SpellcastingIllagerEntity.Spell.values(), ValueLists.OutOfBoundsHandling.ZERO);
    }
}
