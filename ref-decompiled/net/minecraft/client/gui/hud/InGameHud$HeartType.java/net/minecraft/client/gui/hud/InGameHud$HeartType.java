/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static final class InGameHud.HeartType
extends Enum<InGameHud.HeartType> {
    public static final /* enum */ InGameHud.HeartType CONTAINER = new InGameHud.HeartType(Identifier.ofVanilla("hud/heart/container"), Identifier.ofVanilla("hud/heart/container_blinking"), Identifier.ofVanilla("hud/heart/container"), Identifier.ofVanilla("hud/heart/container_blinking"), Identifier.ofVanilla("hud/heart/container_hardcore"), Identifier.ofVanilla("hud/heart/container_hardcore_blinking"), Identifier.ofVanilla("hud/heart/container_hardcore"), Identifier.ofVanilla("hud/heart/container_hardcore_blinking"));
    public static final /* enum */ InGameHud.HeartType NORMAL = new InGameHud.HeartType(Identifier.ofVanilla("hud/heart/full"), Identifier.ofVanilla("hud/heart/full_blinking"), Identifier.ofVanilla("hud/heart/half"), Identifier.ofVanilla("hud/heart/half_blinking"), Identifier.ofVanilla("hud/heart/hardcore_full"), Identifier.ofVanilla("hud/heart/hardcore_full_blinking"), Identifier.ofVanilla("hud/heart/hardcore_half"), Identifier.ofVanilla("hud/heart/hardcore_half_blinking"));
    public static final /* enum */ InGameHud.HeartType POISONED = new InGameHud.HeartType(Identifier.ofVanilla("hud/heart/poisoned_full"), Identifier.ofVanilla("hud/heart/poisoned_full_blinking"), Identifier.ofVanilla("hud/heart/poisoned_half"), Identifier.ofVanilla("hud/heart/poisoned_half_blinking"), Identifier.ofVanilla("hud/heart/poisoned_hardcore_full"), Identifier.ofVanilla("hud/heart/poisoned_hardcore_full_blinking"), Identifier.ofVanilla("hud/heart/poisoned_hardcore_half"), Identifier.ofVanilla("hud/heart/poisoned_hardcore_half_blinking"));
    public static final /* enum */ InGameHud.HeartType WITHERED = new InGameHud.HeartType(Identifier.ofVanilla("hud/heart/withered_full"), Identifier.ofVanilla("hud/heart/withered_full_blinking"), Identifier.ofVanilla("hud/heart/withered_half"), Identifier.ofVanilla("hud/heart/withered_half_blinking"), Identifier.ofVanilla("hud/heart/withered_hardcore_full"), Identifier.ofVanilla("hud/heart/withered_hardcore_full_blinking"), Identifier.ofVanilla("hud/heart/withered_hardcore_half"), Identifier.ofVanilla("hud/heart/withered_hardcore_half_blinking"));
    public static final /* enum */ InGameHud.HeartType ABSORBING = new InGameHud.HeartType(Identifier.ofVanilla("hud/heart/absorbing_full"), Identifier.ofVanilla("hud/heart/absorbing_full_blinking"), Identifier.ofVanilla("hud/heart/absorbing_half"), Identifier.ofVanilla("hud/heart/absorbing_half_blinking"), Identifier.ofVanilla("hud/heart/absorbing_hardcore_full"), Identifier.ofVanilla("hud/heart/absorbing_hardcore_full_blinking"), Identifier.ofVanilla("hud/heart/absorbing_hardcore_half"), Identifier.ofVanilla("hud/heart/absorbing_hardcore_half_blinking"));
    public static final /* enum */ InGameHud.HeartType FROZEN = new InGameHud.HeartType(Identifier.ofVanilla("hud/heart/frozen_full"), Identifier.ofVanilla("hud/heart/frozen_full_blinking"), Identifier.ofVanilla("hud/heart/frozen_half"), Identifier.ofVanilla("hud/heart/frozen_half_blinking"), Identifier.ofVanilla("hud/heart/frozen_hardcore_full"), Identifier.ofVanilla("hud/heart/frozen_hardcore_full_blinking"), Identifier.ofVanilla("hud/heart/frozen_hardcore_half"), Identifier.ofVanilla("hud/heart/frozen_hardcore_half_blinking"));
    private final Identifier fullTexture;
    private final Identifier fullBlinkingTexture;
    private final Identifier halfTexture;
    private final Identifier halfBlinkingTexture;
    private final Identifier hardcoreFullTexture;
    private final Identifier hardcoreFullBlinkingTexture;
    private final Identifier hardcoreHalfTexture;
    private final Identifier hardcoreHalfBlinkingTexture;
    private static final /* synthetic */ InGameHud.HeartType[] field_33952;

    public static InGameHud.HeartType[] values() {
        return (InGameHud.HeartType[])field_33952.clone();
    }

    public static InGameHud.HeartType valueOf(String string) {
        return Enum.valueOf(InGameHud.HeartType.class, string);
    }

    private InGameHud.HeartType(Identifier fullTexture, Identifier fullBlinkingTexture, Identifier halfTexture, Identifier halfBlinkingTexture, Identifier hardcoreFullTexture, Identifier hardcoreFullBlinkingTexture, Identifier hardcoreHalfTexture, Identifier hardcoreHalfBlinkingTexture) {
        this.fullTexture = fullTexture;
        this.fullBlinkingTexture = fullBlinkingTexture;
        this.halfTexture = halfTexture;
        this.halfBlinkingTexture = halfBlinkingTexture;
        this.hardcoreFullTexture = hardcoreFullTexture;
        this.hardcoreFullBlinkingTexture = hardcoreFullBlinkingTexture;
        this.hardcoreHalfTexture = hardcoreHalfTexture;
        this.hardcoreHalfBlinkingTexture = hardcoreHalfBlinkingTexture;
    }

    public Identifier getTexture(boolean hardcore, boolean half, boolean blinking) {
        if (!hardcore) {
            if (half) {
                return blinking ? this.halfBlinkingTexture : this.halfTexture;
            }
            return blinking ? this.fullBlinkingTexture : this.fullTexture;
        }
        if (half) {
            return blinking ? this.hardcoreHalfBlinkingTexture : this.hardcoreHalfTexture;
        }
        return blinking ? this.hardcoreFullBlinkingTexture : this.hardcoreFullTexture;
    }

    static InGameHud.HeartType fromPlayerState(PlayerEntity player) {
        InGameHud.HeartType heartType = player.hasStatusEffect(StatusEffects.POISON) ? POISONED : (player.hasStatusEffect(StatusEffects.WITHER) ? WITHERED : (player.isFrozen() ? FROZEN : NORMAL));
        return heartType;
    }

    private static /* synthetic */ InGameHud.HeartType[] method_37300() {
        return new InGameHud.HeartType[]{CONTAINER, NORMAL, POISONED, WITHERED, ABSORBING, FROZEN};
    }

    static {
        field_33952 = InGameHud.HeartType.method_37300();
    }
}
