/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.util.math.random.Random;

static abstract class WoodlandMansionGenerator.RoomPool {
    WoodlandMansionGenerator.RoomPool() {
    }

    public abstract String getSmallRoom(Random var1);

    public abstract String getSmallSecretRoom(Random var1);

    public abstract String getMediumFunctionalRoom(Random var1, boolean var2);

    public abstract String getMediumGenericRoom(Random var1, boolean var2);

    public abstract String getMediumSecretRoom(Random var1);

    public abstract String getBigRoom(Random var1);

    public abstract String getBigSecretRoom(Random var1);
}
