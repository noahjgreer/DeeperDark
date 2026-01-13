/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.WeatherRenderState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

@Environment(value=EnvType.CLIENT)
public class WeatherRendering {
    private static final float field_63581 = 0.225f;
    private static final int field_53148 = 10;
    private static final Identifier RAIN_TEXTURE = Identifier.ofVanilla("textures/environment/rain.png");
    private static final Identifier SNOW_TEXTURE = Identifier.ofVanilla("textures/environment/snow.png");
    private static final int field_53152 = 32;
    private static final int field_53153 = 16;
    private int soundChance;
    private final float[] NORMAL_LINE_DX = new float[1024];
    private final float[] NORMAL_LINE_DZ = new float[1024];

    public WeatherRendering() {
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 32; ++j) {
                float f = j - 16;
                float g = i - 16;
                float h = MathHelper.hypot(f, g);
                this.NORMAL_LINE_DX[i * 32 + j] = -g / h;
                this.NORMAL_LINE_DZ[i * 32 + j] = f / h;
            }
        }
    }

    public void buildPrecipitationPieces(World world, int ticks, float tickProgress, Vec3d cameraPos, WeatherRenderState state) {
        state.intensity = world.getRainGradient(tickProgress);
        if (state.intensity <= 0.0f) {
            return;
        }
        state.radius = MinecraftClient.getInstance().options.getWeatherRadius().getValue();
        int i = MathHelper.floor(cameraPos.x);
        int j = MathHelper.floor(cameraPos.y);
        int k = MathHelper.floor(cameraPos.z);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Random random = Random.create();
        for (int l = k - state.radius; l <= k + state.radius; ++l) {
            for (int m = i - state.radius; m <= i + state.radius; ++m) {
                Biome.Precipitation precipitation;
                int n = world.getTopY(Heightmap.Type.MOTION_BLOCKING, m, l);
                int o = Math.max(j - state.radius, n);
                int p = Math.max(j + state.radius, n);
                if (p - o == 0 || (precipitation = this.getPrecipitationAt(world, mutable.set(m, j, l))) == Biome.Precipitation.NONE) continue;
                int q = m * m * 3121 + m * 45238971 ^ l * l * 418711 + l * 13761;
                random.setSeed(q);
                int r = Math.max(j, n);
                int s = WorldRenderer.getLightmapCoordinates(world, mutable.set(m, r, l));
                if (precipitation == Biome.Precipitation.RAIN) {
                    state.rainPieces.add(this.createRainPiece(random, ticks, m, o, p, l, s, tickProgress));
                    continue;
                }
                if (precipitation != Biome.Precipitation.SNOW) continue;
                state.snowPieces.add(this.createSnowPiece(random, ticks, m, o, p, l, s, tickProgress));
            }
        }
    }

    public void renderPrecipitation(VertexConsumerProvider vertexConsumers, Vec3d pos, WeatherRenderState state) {
        RenderLayer renderLayer;
        if (!state.rainPieces.isEmpty()) {
            renderLayer = RenderLayers.weather(RAIN_TEXTURE, MinecraftClient.usesImprovedTransparency());
            this.renderPieces(vertexConsumers.getBuffer(renderLayer), state.rainPieces, pos, 1.0f, state.radius, state.intensity);
        }
        if (!state.snowPieces.isEmpty()) {
            renderLayer = RenderLayers.weather(SNOW_TEXTURE, MinecraftClient.usesImprovedTransparency());
            this.renderPieces(vertexConsumers.getBuffer(renderLayer), state.snowPieces, pos, 0.8f, state.radius, state.intensity);
        }
    }

    private Piece createRainPiece(Random random, int ticks, int x, int yMin, int yMax, int z, int light, float tickProgress) {
        int i = ticks & 0x1FFFF;
        int j = x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 & 0xFF;
        float f = 3.0f + random.nextFloat();
        float g = -((float)(i + j) + tickProgress) / 32.0f * f;
        float h = g % 32.0f;
        return new Piece(x, z, yMin, yMax, 0.0f, h, light);
    }

    private Piece createSnowPiece(Random random, int ticks, int x, int yMin, int yMax, int z, int light, float tickProgress) {
        float f = (float)ticks + tickProgress;
        float g = (float)(random.nextDouble() + (double)(f * 0.01f * (float)random.nextGaussian()));
        float h = (float)(random.nextDouble() + (double)(f * (float)random.nextGaussian() * 0.001f));
        float i = -((float)(ticks & 0x1FF) + tickProgress) / 512.0f;
        int j = LightmapTextureManager.pack((LightmapTextureManager.getBlockLightCoordinates(light) * 3 + 15) / 4, (LightmapTextureManager.getSkyLightCoordinates(light) * 3 + 15) / 4);
        return new Piece(x, z, yMin, yMax, g, i + h, j);
    }

    private void renderPieces(VertexConsumer vertexConsumer, List<Piece> pieces, Vec3d pos, float intensity, int range, float gradient) {
        float f = range * range;
        for (Piece piece : pieces) {
            float g = (float)((double)piece.x + 0.5 - pos.x);
            float h = (float)((double)piece.z + 0.5 - pos.z);
            float i = (float)MathHelper.squaredHypot(g, h);
            float j = MathHelper.lerp(Math.min(i / f, 1.0f), intensity, 0.5f) * gradient;
            int k = ColorHelper.getWhite(j);
            int l = (piece.z - MathHelper.floor(pos.z) + 16) * 32 + piece.x - MathHelper.floor(pos.x) + 16;
            float m = this.NORMAL_LINE_DX[l] / 2.0f;
            float n = this.NORMAL_LINE_DZ[l] / 2.0f;
            float o = g - m;
            float p = g + m;
            float q = (float)((double)piece.topY - pos.y);
            float r = (float)((double)piece.bottomY - pos.y);
            float s = h - n;
            float t = h + n;
            float u = piece.uOffset + 0.0f;
            float v = piece.uOffset + 1.0f;
            float w = (float)piece.bottomY * 0.25f + piece.vOffset;
            float x = (float)piece.topY * 0.25f + piece.vOffset;
            vertexConsumer.vertex(o, q, s).texture(u, w).color(k).light(piece.lightCoords);
            vertexConsumer.vertex(p, q, t).texture(v, w).color(k).light(piece.lightCoords);
            vertexConsumer.vertex(p, r, t).texture(v, x).color(k).light(piece.lightCoords);
            vertexConsumer.vertex(o, r, s).texture(u, x).color(k).light(piece.lightCoords);
        }
    }

    public void addParticlesAndSound(ClientWorld world, Camera camera, int ticks, ParticlesMode particlesMode, int weatherRadius) {
        float f = world.getRainGradient(1.0f);
        if (f <= 0.0f) {
            return;
        }
        Random random = Random.create((long)ticks * 312987231L);
        BlockPos blockPos = BlockPos.ofFloored(camera.getCameraPos());
        Vec3i blockPos2 = null;
        int i = 2 * weatherRadius + 1;
        int j = i * i;
        int k = (int)(0.225f * (float)j * f * f) / (particlesMode == ParticlesMode.DECREASED ? 2 : 1);
        for (int l = 0; l < k; ++l) {
            int n;
            int m = random.nextInt(i) - weatherRadius;
            BlockPos blockPos3 = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.add(m, 0, n = random.nextInt(i) - weatherRadius));
            if (blockPos3.getY() <= world.getBottomY() || blockPos3.getY() > blockPos.getY() + 10 || blockPos3.getY() < blockPos.getY() - 10 || this.getPrecipitationAt(world, blockPos3) != Biome.Precipitation.RAIN) continue;
            blockPos2 = blockPos3.down();
            if (particlesMode == ParticlesMode.MINIMAL) break;
            double d = random.nextDouble();
            double e = random.nextDouble();
            BlockState blockState = world.getBlockState((BlockPos)blockPos2);
            FluidState fluidState = world.getFluidState((BlockPos)blockPos2);
            VoxelShape voxelShape = blockState.getCollisionShape(world, (BlockPos)blockPos2);
            double g = voxelShape.getEndingCoord(Direction.Axis.Y, d, e);
            double h = fluidState.getHeight(world, (BlockPos)blockPos2);
            double o = Math.max(g, h);
            SimpleParticleType particleEffect = fluidState.isIn(FluidTags.LAVA) || blockState.isOf(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(blockState) ? ParticleTypes.SMOKE : ParticleTypes.RAIN;
            world.addParticleClient(particleEffect, (double)blockPos2.getX() + d, (double)blockPos2.getY() + o, (double)blockPos2.getZ() + e, 0.0, 0.0, 0.0);
        }
        if (blockPos2 != null && random.nextInt(3) < this.soundChance++) {
            this.soundChance = 0;
            if (blockPos2.getY() > blockPos.getY() + 1 && world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > MathHelper.floor(blockPos.getY())) {
                world.playSoundAtBlockCenterClient((BlockPos)blockPos2, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1f, 0.5f, false);
            } else {
                world.playSoundAtBlockCenterClient((BlockPos)blockPos2, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2f, 1.0f, false);
            }
        }
    }

    private Biome.Precipitation getPrecipitationAt(World world, BlockPos pos) {
        if (!world.getChunkManager().isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
            return Biome.Precipitation.NONE;
        }
        Biome biome = world.getBiome(pos).value();
        return biome.getPrecipitation(pos, world.getSeaLevel());
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Piece
    extends Record {
        final int x;
        final int z;
        final int bottomY;
        final int topY;
        final float uOffset;
        final float vOffset;
        final int lightCoords;

        public Piece(int x, int z, int bottomY, int topY, float uOffset, float vOffset, int lightCoords) {
            this.x = x;
            this.z = z;
            this.bottomY = bottomY;
            this.topY = topY;
            this.uOffset = uOffset;
            this.vOffset = vOffset;
            this.lightCoords = lightCoords;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Piece.class, "x;z;bottomY;topY;uOffset;vOffset;lightCoords", "x", "z", "bottomY", "topY", "uOffset", "vOffset", "lightCoords"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Piece.class, "x;z;bottomY;topY;uOffset;vOffset;lightCoords", "x", "z", "bottomY", "topY", "uOffset", "vOffset", "lightCoords"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Piece.class, "x;z;bottomY;topY;uOffset;vOffset;lightCoords", "x", "z", "bottomY", "topY", "uOffset", "vOffset", "lightCoords"}, this, object);
        }

        public int x() {
            return this.x;
        }

        public int z() {
            return this.z;
        }

        public int bottomY() {
            return this.bottomY;
        }

        public int topY() {
            return this.topY;
        }

        public float uOffset() {
            return this.uOffset;
        }

        public float vOffset() {
            return this.vOffset;
        }

        public int lightCoords() {
            return this.lightCoords;
        }
    }
}
