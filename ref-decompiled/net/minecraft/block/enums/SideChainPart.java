/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.SideChainPart
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class SideChainPart
extends Enum<SideChainPart>
implements StringIdentifiable {
    public static final /* enum */ SideChainPart UNCONNECTED = new SideChainPart("UNCONNECTED", 0, "unconnected");
    public static final /* enum */ SideChainPart RIGHT = new SideChainPart("RIGHT", 1, "right");
    public static final /* enum */ SideChainPart CENTER = new SideChainPart("CENTER", 2, "center");
    public static final /* enum */ SideChainPart LEFT = new SideChainPart("LEFT", 3, "left");
    private final String id;
    private static final /* synthetic */ SideChainPart[] field_61451;

    public static SideChainPart[] values() {
        return (SideChainPart[])field_61451.clone();
    }

    public static SideChainPart valueOf(String string) {
        return Enum.valueOf(SideChainPart.class, string);
    }

    private SideChainPart(String id) {
        this.id = id;
    }

    public String toString() {
        return this.asString();
    }

    public String asString() {
        return this.id;
    }

    public boolean isConnected() {
        return this != UNCONNECTED;
    }

    public boolean isCenterOr(SideChainPart sideChainPart) {
        return this == CENTER || this == sideChainPart;
    }

    public boolean isNotCenter() {
        return this != CENTER;
    }

    public SideChainPart connectToRight() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0, 3 -> LEFT;
            case 1, 2 -> CENTER;
        };
    }

    public SideChainPart connectToLeft() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0, 1 -> RIGHT;
            case 2, 3 -> CENTER;
        };
    }

    public SideChainPart disconnectFromRight() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0, 3 -> UNCONNECTED;
            case 1, 2 -> RIGHT;
        };
    }

    public SideChainPart disconnectFromLeft() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0, 1 -> UNCONNECTED;
            case 2, 3 -> LEFT;
        };
    }

    private static /* synthetic */ SideChainPart[] method_72676() {
        return new SideChainPart[]{UNCONNECTED, RIGHT, CENTER, LEFT};
    }

    static {
        field_61451 = SideChainPart.method_72676();
    }
}

