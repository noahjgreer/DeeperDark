/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.sound.BiomeEffectSoundPlayer
 *  net.minecraft.client.sound.BiomeEffectSoundPlayer$MusicLoop
 *  net.minecraft.client.sound.MovingSoundInstance
 *  net.minecraft.client.sound.PositionedSoundInstance
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.client.util.ClientPlayerTickable
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.sound.BiomeAdditionsSound
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.LightType
 *  net.minecraft.world.World
 *  net.minecraft.world.attribute.AmbientSounds
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.attribute.WorldEnvironmentAttributeAccess
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.BiomeEffectSoundPlayer;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.WorldEnvironmentAttributeAccess;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BiomeEffectSoundPlayer
implements ClientPlayerTickable {
    private static final int MAX_STRENGTH = 40;
    private static final float field_32995 = 0.001f;
    private final ClientPlayerEntity player;
    private final SoundManager soundManager;
    private final Random random;
    private final Object2ObjectArrayMap<RegistryEntry<SoundEvent>, MusicLoop> soundLoops = new Object2ObjectArrayMap();
    private float moodPercentage;
    private @Nullable RegistryEntry<SoundEvent> field_63919;

    public BiomeEffectSoundPlayer(ClientPlayerEntity player, SoundManager soundManager) {
        this.random = player.getEntityWorld().getRandom();
        this.player = player;
        this.soundManager = soundManager;
    }

    public float getMoodPercentage() {
        return this.moodPercentage;
    }

    public void tick() {
        this.soundLoops.values().removeIf(MovingSoundInstance::isDone);
        World world = this.player.getEntityWorld();
        WorldEnvironmentAttributeAccess worldEnvironmentAttributeAccess = world.getEnvironmentAttributes();
        AmbientSounds ambientSounds = (AmbientSounds)worldEnvironmentAttributeAccess.getAttributeValue(EnvironmentAttributes.AMBIENT_SOUNDS_AUDIO, this.player.getEntityPos());
        RegistryEntry registryEntry = ambientSounds.loop().orElse(null);
        if (!Objects.equals(registryEntry, this.field_63919)) {
            this.field_63919 = registryEntry;
            this.soundLoops.values().forEach(MusicLoop::fadeOut);
            if (registryEntry != null) {
                this.soundLoops.compute((Object)registryEntry, (registryEntry2, loop) -> {
                    if (loop == null) {
                        loop = new MusicLoop((SoundEvent)registryEntry.value());
                        this.soundManager.play((SoundInstance)loop);
                    }
                    loop.fadeIn();
                    return loop;
                });
            }
        }
        for (BiomeAdditionsSound biomeAdditionsSound : ambientSounds.additions()) {
            if (!(this.random.nextDouble() < biomeAdditionsSound.tickChance())) continue;
            this.soundManager.play((SoundInstance)PositionedSoundInstance.ambient((SoundEvent)((SoundEvent)biomeAdditionsSound.sound().value())));
        }
        ambientSounds.mood().ifPresent(biomeMoodSound -> {
            int i = biomeMoodSound.blockSearchExtent() * 2 + 1;
            BlockPos blockPos = BlockPos.ofFloored((double)(this.player.getX() + (double)this.random.nextInt(i) - (double)biomeMoodSound.blockSearchExtent()), (double)(this.player.getEyeY() + (double)this.random.nextInt(i) - (double)biomeMoodSound.blockSearchExtent()), (double)(this.player.getZ() + (double)this.random.nextInt(i) - (double)biomeMoodSound.blockSearchExtent()));
            int j = world.getLightLevel(LightType.SKY, blockPos);
            this.moodPercentage = j > 0 ? (this.moodPercentage -= (float)j / 15.0f * 0.001f) : (this.moodPercentage -= (float)(world.getLightLevel(LightType.BLOCK, blockPos) - 1) / (float)biomeMoodSound.tickDelay());
            if (this.moodPercentage >= 1.0f) {
                double d = (double)blockPos.getX() + 0.5;
                double e = (double)blockPos.getY() + 0.5;
                double f = (double)blockPos.getZ() + 0.5;
                double g = d - this.player.getX();
                double h = e - this.player.getEyeY();
                double k = f - this.player.getZ();
                double l = Math.sqrt(g * g + h * h + k * k);
                double m = l + biomeMoodSound.offset();
                PositionedSoundInstance positionedSoundInstance = PositionedSoundInstance.ambient((SoundEvent)((SoundEvent)biomeMoodSound.sound().value()), (Random)this.random, (double)(this.player.getX() + g / l * m), (double)(this.player.getEyeY() + h / l * m), (double)(this.player.getZ() + k / l * m));
                this.soundManager.play((SoundInstance)positionedSoundInstance);
                this.moodPercentage = 0.0f;
            } else {
                this.moodPercentage = Math.max(this.moodPercentage, 0.0f);
            }
        });
    }
}

