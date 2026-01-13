/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.entity.boss;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Uuids;

public static final class CommandBossBar.Serialized
extends Record {
    final Text name;
    final boolean visible;
    final int value;
    final int max;
    final BossBar.Color color;
    final BossBar.Style overlay;
    final boolean darkenScreen;
    final boolean playBossMusic;
    final boolean createWorldFog;
    final Set<UUID> players;
    public static final Codec<CommandBossBar.Serialized> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)TextCodecs.CODEC.fieldOf("Name").forGetter(CommandBossBar.Serialized::name), (App)Codec.BOOL.optionalFieldOf("Visible", (Object)false).forGetter(CommandBossBar.Serialized::visible), (App)Codec.INT.optionalFieldOf("Value", (Object)0).forGetter(CommandBossBar.Serialized::value), (App)Codec.INT.optionalFieldOf("Max", (Object)100).forGetter(CommandBossBar.Serialized::max), (App)BossBar.Color.CODEC.optionalFieldOf("Color", (Object)BossBar.Color.WHITE).forGetter(CommandBossBar.Serialized::color), (App)BossBar.Style.CODEC.optionalFieldOf("Overlay", (Object)BossBar.Style.PROGRESS).forGetter(CommandBossBar.Serialized::overlay), (App)Codec.BOOL.optionalFieldOf("DarkenScreen", (Object)false).forGetter(CommandBossBar.Serialized::darkenScreen), (App)Codec.BOOL.optionalFieldOf("PlayBossMusic", (Object)false).forGetter(CommandBossBar.Serialized::playBossMusic), (App)Codec.BOOL.optionalFieldOf("CreateWorldFog", (Object)false).forGetter(CommandBossBar.Serialized::createWorldFog), (App)Uuids.SET_CODEC.optionalFieldOf("Players", Set.of()).forGetter(CommandBossBar.Serialized::players)).apply((Applicative)instance, CommandBossBar.Serialized::new));

    public CommandBossBar.Serialized(Text name, boolean visible, int value, int max, BossBar.Color color, BossBar.Style overlay, boolean darkenScreen, boolean playBossMusic, boolean createWorldFog, Set<UUID> players) {
        this.name = name;
        this.visible = visible;
        this.value = value;
        this.max = max;
        this.color = color;
        this.overlay = overlay;
        this.darkenScreen = darkenScreen;
        this.playBossMusic = playBossMusic;
        this.createWorldFog = createWorldFog;
        this.players = players;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{CommandBossBar.Serialized.class, "name;visible;value;max;color;overlay;darkenScreen;playBossMusic;createWorldFog;players", "name", "visible", "value", "max", "color", "overlay", "darkenScreen", "playBossMusic", "createWorldFog", "players"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CommandBossBar.Serialized.class, "name;visible;value;max;color;overlay;darkenScreen;playBossMusic;createWorldFog;players", "name", "visible", "value", "max", "color", "overlay", "darkenScreen", "playBossMusic", "createWorldFog", "players"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CommandBossBar.Serialized.class, "name;visible;value;max;color;overlay;darkenScreen;playBossMusic;createWorldFog;players", "name", "visible", "value", "max", "color", "overlay", "darkenScreen", "playBossMusic", "createWorldFog", "players"}, this, object);
    }

    public Text name() {
        return this.name;
    }

    public boolean visible() {
        return this.visible;
    }

    public int value() {
        return this.value;
    }

    public int max() {
        return this.max;
    }

    public BossBar.Color color() {
        return this.color;
    }

    public BossBar.Style overlay() {
        return this.overlay;
    }

    public boolean darkenScreen() {
        return this.darkenScreen;
    }

    public boolean playBossMusic() {
        return this.playBossMusic;
    }

    public boolean createWorldFog() {
        return this.createWorldFog;
    }

    public Set<UUID> players() {
        return this.players;
    }
}
