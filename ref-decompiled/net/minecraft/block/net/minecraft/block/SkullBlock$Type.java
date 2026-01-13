/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.SkullBlock;

public static final class SkullBlock.Type
extends Enum<SkullBlock.Type>
implements SkullBlock.SkullType {
    public static final /* enum */ SkullBlock.Type SKELETON = new SkullBlock.Type("skeleton");
    public static final /* enum */ SkullBlock.Type WITHER_SKELETON = new SkullBlock.Type("wither_skeleton");
    public static final /* enum */ SkullBlock.Type PLAYER = new SkullBlock.Type("player");
    public static final /* enum */ SkullBlock.Type ZOMBIE = new SkullBlock.Type("zombie");
    public static final /* enum */ SkullBlock.Type CREEPER = new SkullBlock.Type("creeper");
    public static final /* enum */ SkullBlock.Type PIGLIN = new SkullBlock.Type("piglin");
    public static final /* enum */ SkullBlock.Type DRAGON = new SkullBlock.Type("dragon");
    private final String id;
    private static final /* synthetic */ SkullBlock.Type[] field_11509;

    public static SkullBlock.Type[] values() {
        return (SkullBlock.Type[])field_11509.clone();
    }

    public static SkullBlock.Type valueOf(String string) {
        return Enum.valueOf(SkullBlock.Type.class, string);
    }

    private SkullBlock.Type(String id) {
        this.id = id;
        TYPES.put(id, this);
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ SkullBlock.Type[] method_36710() {
        return new SkullBlock.Type[]{SKELETON, WITHER_SKELETON, PLAYER, ZOMBIE, CREEPER, PIGLIN, DRAGON};
    }

    static {
        field_11509 = SkullBlock.Type.method_36710();
    }
}
