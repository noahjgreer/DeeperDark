package net.noahsarch.deeperdark.creature;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * The Creature entity — a server-managed, client-rendered billboard horror entity.
 *
 * All movement and behavior is driven by {@link CreatureManager}.
 * Visual appearance (billboard rendering, per-frame jitter) is handled by
 * {@link net.noahsarch.deeperdark.client.renderer.CreatureEntityRenderer}.
 *
 * Two values are synced to the client:
 *   - textureVariant (0-3): which creature PNG to display
 *   - jitterIntensity (0.0+): how much the renderer should randomly offset the billboard per frame
 */
public class CreatureEntity extends Entity {

    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(CreatureEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> JITTER_INTENSITY =
            SynchedEntityData.defineId(CreatureEntity.class, EntityDataSerializers.FLOAT);

    public CreatureEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(VARIANT, 0);
        builder.define(JITTER_INTENSITY, 0.0f);
    }

    @Override
    public void tick() {
        // CreatureManager handles all movement/lifecycle. Suppress default entity ticking.
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        // The creature is immune to all damage — projectile rejection handles incoming projectiles separately.
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        // Transient entity — no persistence needed.
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        // Transient entity — no persistence needed.
    }

    // ===== Texture Variant =====

    public int getTextureVariant() {
        return entityData.get(VARIANT);
    }

    public void setTextureVariant(int variant) {
        entityData.set(VARIANT, Math.max(0, Math.min(3, variant)));
    }

    // ===== Jitter Intensity =====

    /** Intensity used by the client renderer to apply per-frame random billboard offset (in blocks). */
    public float getJitterIntensity() {
        return entityData.get(JITTER_INTENSITY);
    }

    public void setJitterIntensity(float intensity) {
        entityData.set(JITTER_INTENSITY, Math.max(0.0f, intensity));
    }
}
