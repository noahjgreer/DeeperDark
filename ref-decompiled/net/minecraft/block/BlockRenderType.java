/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockRenderType
 */
package net.minecraft.block;

/*
 * Exception performing whole class analysis ignored.
 */
public final class BlockRenderType
extends Enum<BlockRenderType> {
    public static final /* enum */ BlockRenderType INVISIBLE = new BlockRenderType("INVISIBLE", 0);
    public static final /* enum */ BlockRenderType MODEL = new BlockRenderType("MODEL", 1);
    private static final /* synthetic */ BlockRenderType[] field_11457;

    public static BlockRenderType[] values() {
        return (BlockRenderType[])field_11457.clone();
    }

    public static BlockRenderType valueOf(String string) {
        return Enum.valueOf(BlockRenderType.class, string);
    }

    private static /* synthetic */ BlockRenderType[] method_36708() {
        return new BlockRenderType[]{INVISIBLE, MODEL};
    }

    static {
        field_11457 = BlockRenderType.method_36708();
    }
}

