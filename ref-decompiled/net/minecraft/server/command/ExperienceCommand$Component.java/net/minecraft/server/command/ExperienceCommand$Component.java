/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

static final class ExperienceCommand.Component
extends Enum<ExperienceCommand.Component> {
    public static final /* enum */ ExperienceCommand.Component POINTS = new ExperienceCommand.Component("points", PlayerEntity::addExperience, (player, experience) -> {
        if (experience >= player.getNextLevelExperience()) {
            return false;
        }
        player.setExperiencePoints((int)experience);
        return true;
    }, player -> MathHelper.floor(player.experienceProgress * (float)player.getNextLevelExperience()));
    public static final /* enum */ ExperienceCommand.Component LEVELS = new ExperienceCommand.Component("levels", ServerPlayerEntity::addExperienceLevels, (player, level) -> {
        player.setExperienceLevel((int)level);
        return true;
    }, player -> player.experienceLevel);
    public final BiConsumer<ServerPlayerEntity, Integer> adder;
    public final BiPredicate<ServerPlayerEntity, Integer> setter;
    public final String name;
    final ToIntFunction<ServerPlayerEntity> getter;
    private static final /* synthetic */ ExperienceCommand.Component[] field_13640;

    public static ExperienceCommand.Component[] values() {
        return (ExperienceCommand.Component[])field_13640.clone();
    }

    public static ExperienceCommand.Component valueOf(String string) {
        return Enum.valueOf(ExperienceCommand.Component.class, string);
    }

    private ExperienceCommand.Component(String name, BiConsumer<ServerPlayerEntity, Integer> adder, BiPredicate<ServerPlayerEntity, Integer> setter, ToIntFunction<ServerPlayerEntity> getter) {
        this.adder = adder;
        this.name = name;
        this.setter = setter;
        this.getter = getter;
    }

    private static /* synthetic */ ExperienceCommand.Component[] method_36967() {
        return new ExperienceCommand.Component[]{POINTS, LEVELS};
    }

    static {
        field_13640 = ExperienceCommand.Component.method_36967();
    }
}
