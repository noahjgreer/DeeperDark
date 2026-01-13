/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.scoreboard;

import net.minecraft.util.StringIdentifiable;

public static final class ScoreboardCriterion.RenderType
extends Enum<ScoreboardCriterion.RenderType>
implements StringIdentifiable {
    public static final /* enum */ ScoreboardCriterion.RenderType INTEGER = new ScoreboardCriterion.RenderType("integer");
    public static final /* enum */ ScoreboardCriterion.RenderType HEARTS = new ScoreboardCriterion.RenderType("hearts");
    private final String name;
    public static final StringIdentifiable.EnumCodec<ScoreboardCriterion.RenderType> CODEC;
    private static final /* synthetic */ ScoreboardCriterion.RenderType[] field_1473;

    public static ScoreboardCriterion.RenderType[] values() {
        return (ScoreboardCriterion.RenderType[])field_1473.clone();
    }

    public static ScoreboardCriterion.RenderType valueOf(String string) {
        return Enum.valueOf(ScoreboardCriterion.RenderType.class, string);
    }

    private ScoreboardCriterion.RenderType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static ScoreboardCriterion.RenderType getType(String name) {
        return CODEC.byId(name, INTEGER);
    }

    private static /* synthetic */ ScoreboardCriterion.RenderType[] method_36799() {
        return new ScoreboardCriterion.RenderType[]{INTEGER, HEARTS};
    }

    static {
        field_1473 = ScoreboardCriterion.RenderType.method_36799();
        CODEC = StringIdentifiable.createCodec(ScoreboardCriterion.RenderType::values);
    }
}
