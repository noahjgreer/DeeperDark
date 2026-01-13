/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerLikeEntity
 *  net.minecraft.client.network.ClientPlayerLikeState
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.client.util.DefaultSkinHelper
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.SkinTextures
 *  net.minecraft.item.Items
 *  net.minecraft.scoreboard.ReadableScoreboardScore
 *  net.minecraft.scoreboard.ScoreHolder
 *  net.minecraft.scoreboard.Scoreboard
 *  net.minecraft.scoreboard.ScoreboardDisplaySlot
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.scoreboard.number.NumberFormat
 *  net.minecraft.scoreboard.number.StyledNumberFormat
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.GameMode
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.network.ClientPlayerLikeState;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractClientPlayerEntity
extends PlayerEntity
implements ClientPlayerLikeEntity {
    private @Nullable PlayerListEntry playerListEntry;
    private final boolean deadmau5;
    private final ClientPlayerLikeState state = new ClientPlayerLikeState();

    public AbstractClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super((World)world, profile);
        this.deadmau5 = "deadmau5".equals(this.getGameProfile().name());
    }

    public @Nullable GameMode getGameMode() {
        PlayerListEntry playerListEntry = this.getPlayerListEntry();
        return playerListEntry != null ? playerListEntry.getGameMode() : null;
    }

    protected @Nullable PlayerListEntry getPlayerListEntry() {
        if (this.playerListEntry == null) {
            this.playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getUuid());
        }
        return this.playerListEntry;
    }

    public void tick() {
        this.state.tick(this.getEntityPos(), this.getVelocity());
        super.tick();
    }

    protected void addDistanceMoved(float distanceMoved) {
        this.state.addDistanceMoved(distanceMoved);
    }

    public ClientPlayerLikeState getState() {
        return this.state;
    }

    public @Nullable Text getMannequinName() {
        Scoreboard scoreboard = this.getEntityWorld().getScoreboard();
        ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
        if (scoreboardObjective != null) {
            ReadableScoreboardScore readableScoreboardScore = scoreboard.getScore((ScoreHolder)this, scoreboardObjective);
            MutableText text = ReadableScoreboardScore.getFormattedScore((ReadableScoreboardScore)readableScoreboardScore, (NumberFormat)scoreboardObjective.getNumberFormatOr((NumberFormat)StyledNumberFormat.EMPTY));
            return Text.empty().append((Text)text).append(ScreenTexts.SPACE).append(scoreboardObjective.getDisplayName());
        }
        return null;
    }

    public SkinTextures getSkin() {
        PlayerListEntry playerListEntry = this.getPlayerListEntry();
        return playerListEntry == null ? DefaultSkinHelper.getSkinTextures((UUID)this.getUuid()) : playerListEntry.getSkinTextures();
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ParrotEntity.Variant getShoulderParrotVariant(boolean leftShoulder) {
        return (leftShoulder ? this.getLeftShoulderParrotVariant() : this.getRightShoulderParrotVariant()).orElse(null);
    }

    public void tickRiding() {
        super.tickRiding();
        this.getState().tickRiding();
    }

    public void tickMovement() {
        this.tickPlayerMovement();
        super.tickMovement();
    }

    protected void tickPlayerMovement() {
        float f = !this.isOnGround() || this.isDead() || this.isSwimming() ? 0.0f : Math.min(0.1f, (float)this.getVelocity().horizontalLength());
        this.getState().tickMovement(f);
    }

    public float getFovMultiplier(boolean firstPerson, float fovEffectScale) {
        float h;
        float g;
        float f = 1.0f;
        if (this.getAbilities().flying) {
            f *= 1.1f;
        }
        if ((g = this.getAbilities().getWalkSpeed()) != 0.0f) {
            h = (float)this.getAttributeValue(EntityAttributes.MOVEMENT_SPEED) / g;
            f *= (h + 1.0f) / 2.0f;
        }
        if (this.isUsingItem()) {
            if (this.getActiveItem().isOf(Items.BOW)) {
                h = Math.min((float)this.getItemUseTime() / 20.0f, 1.0f);
                f *= 1.0f - MathHelper.square((float)h) * 0.15f;
            } else if (firstPerson && this.isUsingSpyglass()) {
                return 0.1f;
            }
        }
        return MathHelper.lerp((float)fovEffectScale, (float)1.0f, (float)f);
    }

    public boolean hasExtraEars() {
        return this.deadmau5;
    }
}

