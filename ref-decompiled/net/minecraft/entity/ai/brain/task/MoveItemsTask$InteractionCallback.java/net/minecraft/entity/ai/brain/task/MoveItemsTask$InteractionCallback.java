/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.function.TriConsumer
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.task.MoveItemsTask;
import net.minecraft.entity.mob.PathAwareEntity;
import org.apache.commons.lang3.function.TriConsumer;

@FunctionalInterface
public static interface MoveItemsTask.InteractionCallback
extends TriConsumer<PathAwareEntity, MoveItemsTask.Storage, Integer> {
}
