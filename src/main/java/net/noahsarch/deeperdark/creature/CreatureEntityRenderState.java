package net.noahsarch.deeperdark.creature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

/**
 * Client-side render state for {@link CreatureEntity}.
 * Extracted from the entity once per frame by {@link net.noahsarch.deeperdark.client.renderer.CreatureEntityRenderer}.
 */
@Environment(EnvType.CLIENT)
public class CreatureEntityRenderState extends EntityRenderState {

    /** Which of the four creature textures (0-3) to render. */
    public int textureVariant = 0;

    /** How far (in blocks) the renderer should randomly displace the billboard per frame. */
    public float jitterIntensity = 0.0f;
}
