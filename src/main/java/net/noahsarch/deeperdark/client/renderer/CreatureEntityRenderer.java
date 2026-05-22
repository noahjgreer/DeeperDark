package net.noahsarch.deeperdark.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.noahsarch.deeperdark.creature.CreatureEntity;
import net.noahsarch.deeperdark.creature.CreatureEntityRenderState;

/**
 * Renders {@link CreatureEntity} as a yaw-only billboard — always facing the camera
 * horizontally but never pitching up or down.
 *
 * Appearance:
 *   - 1.5 blocks wide × 3 blocks tall
 *   - One of four creature textures (deeperdark:textures/entity/creature0-3.png)
 *   - Both front and back faces rendered so it is visible from any angle
 *
 * Client-side improvements over the old ItemDisplay workaround:
 *   - Jitter is applied per-frame in the renderer using the synced intensity value,
 *     so the server never needs to spam position packets for visual shake.
 *   - Billboarding uses the actual camera yaw, not an approximation from the server.
 */
@Environment(EnvType.CLIENT)
public class CreatureEntityRenderer extends EntityRenderer<CreatureEntity, CreatureEntityRenderState> {

    private static final Identifier[] TEXTURES = new Identifier[4];
    private static final RenderType[] RENDER_TYPES = new RenderType[4];

    static {
        for (int i = 0; i < 4; i++) {
            TEXTURES[i] = Identifier.fromNamespaceAndPath("deeperdark", "textures/entity/creature" + i + ".png");
            RENDER_TYPES[i] = RenderTypes.entityCutout(TEXTURES[i]);
        }
    }

    // Creature visual dimensions in blocks
    private static final float WIDTH  = 1.5f;
    private static final float HEIGHT = 3.0f;

    public CreatureEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0f;
        this.shadowStrength = 0.0f;
    }

    @Override
    public CreatureEntityRenderState createRenderState() {
        return new CreatureEntityRenderState();
    }

    @Override
    public void extractRenderState(CreatureEntity entity, CreatureEntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.textureVariant   = entity.getTextureVariant();
        state.jitterIntensity  = entity.getJitterIntensity();
    }

    @Override
    public void submit(CreatureEntityRenderState state, PoseStack poseStack,
                       SubmitNodeCollector nodes, CameraRenderState camera) {

        int variant = Math.max(0, Math.min(3, state.textureVariant));
        int light = state.lightCoords;

        // Compute jitter once — reused for both normal and outline passes so they stay aligned.
        float jitter = state.jitterIntensity;
        float jx = jitter > 0.0f ? (float) ((Math.random() * 2.0 - 1.0) * jitter) : 0.0f;
        float jz = jitter > 0.0f ? (float) ((Math.random() * 2.0 - 1.0) * jitter) : 0.0f;

        poseStack.pushPose();
        if (jitter > 0.0f) poseStack.translate(jx, 0.0f, jz);
        // Yaw-only billboard: rotate around Y so the quad always faces the camera horizontally.
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - camera.yRot));

        float halfW = WIDTH / 2.0f;

        // Normal pass — front and back faces with full NEW_ENTITY vertex attributes.
        nodes.submitCustomGeometry(poseStack, RENDER_TYPES[variant], (pose, consumer) -> {
            // Front face
            consumer.addVertex(pose, -halfW, HEIGHT, 0f).setColor(255, 255, 255, 255).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 0f, 1f);
            consumer.addVertex(pose,  halfW, HEIGHT, 0f).setColor(255, 255, 255, 255).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 0f, 1f);
            consumer.addVertex(pose,  halfW,  0f,    0f).setColor(255, 255, 255, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 0f, 1f);
            consumer.addVertex(pose, -halfW,  0f,    0f).setColor(255, 255, 255, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 0f, 1f);
            // Back face (wound in reverse)
            consumer.addVertex(pose, -halfW,  0f,    0f).setColor(255, 255, 255, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 0f, -1f);
            consumer.addVertex(pose,  halfW,  0f,    0f).setColor(255, 255, 255, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 0f, -1f);
            consumer.addVertex(pose,  halfW, HEIGHT, 0f).setColor(255, 255, 255, 255).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 0f, -1f);
            consumer.addVertex(pose, -halfW, HEIGHT, 0f).setColor(255, 255, 255, 255).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 0f, -1f);
        });

        // Outline pass — same geometry submitted to the OUTLINE_TARGET when the entity is glowing.
        // outlineColor is 0 (no glow) or ARGB packed team/glow color (auto-populated by super.extractRenderState).
        // The outline render type uses a simpler vertex format: position + color + UV only.
        if (state.outlineColor != 0) {
            int oc = state.outlineColor;
            int outA = (oc >> 24) & 0xFF;
            int outR = (oc >> 16) & 0xFF;
            int outG = (oc >> 8)  & 0xFF;
            int outB =  oc        & 0xFF;
            RenderType outlineType = RenderTypes.outline(TEXTURES[variant]);
            nodes.submitCustomGeometry(poseStack, outlineType, (pose, consumer) -> {
                // Front face
                consumer.addVertex(pose, -halfW, HEIGHT, 0f).setColor(outR, outG, outB, outA).setUv(0f, 0f);
                consumer.addVertex(pose,  halfW, HEIGHT, 0f).setColor(outR, outG, outB, outA).setUv(1f, 0f);
                consumer.addVertex(pose,  halfW,  0f,    0f).setColor(outR, outG, outB, outA).setUv(1f, 1f);
                consumer.addVertex(pose, -halfW,  0f,    0f).setColor(outR, outG, outB, outA).setUv(0f, 1f);
                // Back face
                consumer.addVertex(pose, -halfW,  0f,    0f).setColor(outR, outG, outB, outA).setUv(0f, 1f);
                consumer.addVertex(pose,  halfW,  0f,    0f).setColor(outR, outG, outB, outA).setUv(1f, 1f);
                consumer.addVertex(pose,  halfW, HEIGHT, 0f).setColor(outR, outG, outB, outA).setUv(1f, 0f);
                consumer.addVertex(pose, -halfW, HEIGHT, 0f).setColor(outR, outG, outB, outA).setUv(0f, 0f);
            });
        }

        poseStack.popPose();
        super.submit(state, poseStack, nodes, camera);
    }
}
