/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package net.minecraft.entity.ai;

import com.google.common.annotations.VisibleForTesting;
import java.util.Comparator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.mob.Angriness;
import net.minecraft.entity.player.PlayerEntity;

@VisibleForTesting
protected record WardenAngerManager.SuspectComparator(WardenAngerManager angerManagement) implements Comparator<Entity>
{
    @Override
    public int compare(Entity entity, Entity entity2) {
        boolean bl2;
        if (entity.equals(entity2)) {
            return 0;
        }
        int i = this.angerManagement.suspectsToAngerLevel.getOrDefault((Object)entity, 0);
        int j = this.angerManagement.suspectsToAngerLevel.getOrDefault((Object)entity2, 0);
        this.angerManagement.primeAnger = Math.max(this.angerManagement.primeAnger, Math.max(i, j));
        boolean bl = Angriness.getForAnger(i).isAngry();
        if (bl != (bl2 = Angriness.getForAnger(j).isAngry())) {
            return bl ? -1 : 1;
        }
        boolean bl3 = entity instanceof PlayerEntity;
        boolean bl4 = entity2 instanceof PlayerEntity;
        if (bl3 != bl4) {
            return bl3 ? -1 : 1;
        }
        return Integer.compare(j, i);
    }

    @Override
    public /* synthetic */ int compare(Object first, Object second) {
        return this.compare((Entity)first, (Entity)second);
    }
}
