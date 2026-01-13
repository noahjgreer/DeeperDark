/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Objects;
import net.minecraft.datafixer.fix.LeavesFix;
import org.jspecify.annotations.Nullable;

public static final class AddTrappedChestFix.ListFixer
extends LeavesFix.ListFixer {
    private @Nullable IntSet targets;

    public AddTrappedChestFix.ListFixer(Typed<?> typed, Schema schema) {
        super(typed, schema);
    }

    @Override
    protected boolean computeIsFixed() {
        this.targets = new IntOpenHashSet();
        for (int i = 0; i < this.properties.size(); ++i) {
            Dynamic dynamic = (Dynamic)this.properties.get(i);
            String string = dynamic.get("Name").asString("");
            if (!Objects.equals(string, "minecraft:trapped_chest")) continue;
            this.targets.add(i);
        }
        return this.targets.isEmpty();
    }

    public boolean isTarget(int index) {
        return this.targets.contains(index);
    }
}
