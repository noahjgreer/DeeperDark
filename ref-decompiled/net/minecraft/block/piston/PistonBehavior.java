/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.piston.PistonBehavior
 */
package net.minecraft.block.piston;

/*
 * Exception performing whole class analysis ignored.
 */
public final class PistonBehavior
extends Enum<PistonBehavior> {
    public static final /* enum */ PistonBehavior NORMAL = new PistonBehavior("NORMAL", 0);
    public static final /* enum */ PistonBehavior DESTROY = new PistonBehavior("DESTROY", 1);
    public static final /* enum */ PistonBehavior BLOCK = new PistonBehavior("BLOCK", 2);
    public static final /* enum */ PistonBehavior IGNORE = new PistonBehavior("IGNORE", 3);
    public static final /* enum */ PistonBehavior PUSH_ONLY = new PistonBehavior("PUSH_ONLY", 4);
    private static final /* synthetic */ PistonBehavior[] field_15973;

    public static PistonBehavior[] values() {
        return (PistonBehavior[])field_15973.clone();
    }

    public static PistonBehavior valueOf(String string) {
        return Enum.valueOf(PistonBehavior.class, string);
    }

    private static /* synthetic */ PistonBehavior[] method_36765() {
        return new PistonBehavior[]{NORMAL, DESTROY, BLOCK, IGNORE, PUSH_ONLY};
    }

    static {
        field_15973 = PistonBehavior.method_36765();
    }
}

