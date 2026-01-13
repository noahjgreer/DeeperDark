/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Stack
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.advancement.Advancement
 *  net.minecraft.advancement.AdvancementDisplay
 *  net.minecraft.advancement.AdvancementDisplays
 *  net.minecraft.advancement.AdvancementDisplays$ResultConsumer
 *  net.minecraft.advancement.AdvancementDisplays$Status
 *  net.minecraft.advancement.PlacedAdvancement
 */
package net.minecraft.advancement;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementDisplays;
import net.minecraft.advancement.PlacedAdvancement;

/*
 * Exception performing whole class analysis ignored.
 */
public class AdvancementDisplays {
    private static final int DISPLAY_DEPTH = 2;

    private static Status getStatus(Advancement advancement, boolean force) {
        Optional optional = advancement.display();
        if (optional.isEmpty()) {
            return Status.HIDE;
        }
        if (force) {
            return Status.SHOW;
        }
        if (((AdvancementDisplay)optional.get()).isHidden()) {
            return Status.HIDE;
        }
        return Status.NO_CHANGE;
    }

    private static boolean shouldDisplay(Stack<Status> statuses) {
        for (int i = 0; i <= 2; ++i) {
            Status status = (Status)statuses.peek(i);
            if (status == Status.SHOW) {
                return true;
            }
            if (status != Status.HIDE) continue;
            return false;
        }
        return false;
    }

    private static boolean shouldDisplay(PlacedAdvancement advancement, Stack<Status> statuses, Predicate<PlacedAdvancement> donePredicate, ResultConsumer consumer) {
        boolean bl = donePredicate.test(advancement);
        Status status = AdvancementDisplays.getStatus((Advancement)advancement.getAdvancement(), (boolean)bl);
        boolean bl2 = bl;
        statuses.push((Object)status);
        for (PlacedAdvancement placedAdvancement : advancement.getChildren()) {
            bl2 |= AdvancementDisplays.shouldDisplay((PlacedAdvancement)placedAdvancement, statuses, donePredicate, (ResultConsumer)consumer);
        }
        boolean bl3 = bl2 || AdvancementDisplays.shouldDisplay(statuses);
        statuses.pop();
        consumer.accept(advancement, bl3);
        return bl2;
    }

    public static void calculateDisplay(PlacedAdvancement advancement, Predicate<PlacedAdvancement> donePredicate, ResultConsumer consumer) {
        PlacedAdvancement placedAdvancement = advancement.getRoot();
        ObjectArrayList stack = new ObjectArrayList();
        for (int i = 0; i <= 2; ++i) {
            stack.push((Object)Status.NO_CHANGE);
        }
        AdvancementDisplays.shouldDisplay((PlacedAdvancement)placedAdvancement, (Stack)stack, donePredicate, (ResultConsumer)consumer);
    }
}

