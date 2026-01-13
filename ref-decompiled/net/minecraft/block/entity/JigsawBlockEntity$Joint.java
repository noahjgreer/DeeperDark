/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public static final class JigsawBlockEntity.Joint
extends Enum<JigsawBlockEntity.Joint>
implements StringIdentifiable {
    public static final /* enum */ JigsawBlockEntity.Joint ROLLABLE = new JigsawBlockEntity.Joint("rollable");
    public static final /* enum */ JigsawBlockEntity.Joint ALIGNED = new JigsawBlockEntity.Joint("aligned");
    public static final StringIdentifiable.EnumCodec<JigsawBlockEntity.Joint> CODEC;
    private final String name;
    private static final /* synthetic */ JigsawBlockEntity.Joint[] field_23332;

    public static JigsawBlockEntity.Joint[] values() {
        return (JigsawBlockEntity.Joint[])field_23332.clone();
    }

    public static JigsawBlockEntity.Joint valueOf(String string) {
        return Enum.valueOf(JigsawBlockEntity.Joint.class, string);
    }

    private JigsawBlockEntity.Joint(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public Text asText() {
        return Text.translatable("jigsaw_block.joint." + this.name);
    }

    private static /* synthetic */ JigsawBlockEntity.Joint[] method_36716() {
        return new JigsawBlockEntity.Joint[]{ROLLABLE, ALIGNED};
    }

    static {
        field_23332 = JigsawBlockEntity.Joint.method_36716();
        CODEC = StringIdentifiable.createCodec(JigsawBlockEntity.Joint::values);
    }
}
