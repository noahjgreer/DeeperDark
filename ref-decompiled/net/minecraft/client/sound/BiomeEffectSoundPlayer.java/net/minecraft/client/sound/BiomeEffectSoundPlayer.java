/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

    @Override
    public void tick() {
        this.soundLoops.values().removeIf(MovingSoundInstance::isDone);
        World world = this.player.getEntityWorld();
        WorldEnvironmentAttributeAccess worldEnvironmentAttributeAccess = world.getEnvironmentAttributes();
        AmbientSounds ambientSounds = worldEnvironmentAttributeAccess.getAttributeValue(EnvironmentAttributes.AMBIENT_SOUNDS_AUDIO, this.player.getEntityPos());
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
            this.soundManager.play(PositionedSoundInstance.ambient(biomeAdditionsSound.sound().value()));
        }
        ambientSounds.mood().ifPresent(biomeMoodSound -> {
            int i = biomeMoodSound.blockSearchExtent() * 2 + 1;
            BlockPos blockPos = BlockPos.ofFloored(this.player.getX() + (double)this.random.nextInt(i) - (double)biomeMoodSound.blockSearchExtent(), this.player.getEyeY() + (double)this.random.nextInt(i) - (double)biomeMoodSound.blockSearchExtent(), this.player.getZ() + (double)this.random.nextInt(i) - (double)biomeMoodSound.blockSearchExtent());
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
                PositionedSoundInstance positionedSoundInstance = PositionedSoundInstance.ambient(biomeMoodSound.sound().value(), this.random, this.player.getX() + g / l * m, this.player.getEyeY() + h / l * m, this.player.getZ() + k / l * m);
                this.soundManager.play(positionedSoundInstance);
                this.moodPercentage = 0.0f;
            } else {
                this.moodPercentage = Math.max(this.moodPercentage, 0.0f);
            }
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static class MusicLoop
    extends MovingSoundInstance {
        private int delta;
        private int strength;

        public MusicLoop(SoundEvent sound) {
            super(sound, SoundCategory.AMBIENT, SoundInstance.createRandom());
            this.repeat = true;
            this.repeatDelay = 0;
            this.volume = 1.0f;
            this.relative = true;
        }

        @Override
        public void tick() {
            if (this.strength < 0) {
                this.setDone();
            }
            this.strength += this.delta;
            this.volume = MathHelper.clamp((float)this.strength / 40.0f, 0.0f, 1.0f);
        }

        public void fadeOut() {
            this.strength = Math.min(this.strength, 40);
            this.delta = -1;
        }

        public void fadeIn() {
            this.strength = Math.max(0, this.strength);
            this.delta = 1;
        }
    }
}
