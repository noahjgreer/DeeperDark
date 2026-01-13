/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.spawner.EntityDetector
 *  net.minecraft.block.spawner.EntityDetector$Selector
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.TypeFilter
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  net.minecraft.world.World
 */
package net.minecraft.block.spawner;

import java.util.List;
import java.util.UUID;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.spawner.EntityDetector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

/*
 * Exception performing whole class analysis ignored.
 */
public interface EntityDetector {
    public static final EntityDetector SURVIVAL_PLAYERS = (world, selector, center, radius, spawner) -> selector.getPlayers(world, player -> player.getBlockPos().isWithinDistance((Vec3i)center, radius) && !player.isCreative() && !player.isSpectator()).stream().filter(entity -> !spawner || EntityDetector.hasLineOfSight((World)world, (Vec3d)center.toCenterPos(), (Vec3d)entity.getEyePos())).map(Entity::getUuid).toList();
    public static final EntityDetector NON_SPECTATOR_PLAYERS = (world, selector, center, radius, spawner) -> selector.getPlayers(world, player -> player.getBlockPos().isWithinDistance((Vec3i)center, radius) && !player.isSpectator()).stream().filter(entity -> !spawner || EntityDetector.hasLineOfSight((World)world, (Vec3d)center.toCenterPos(), (Vec3d)entity.getEyePos())).map(Entity::getUuid).toList();
    public static final EntityDetector SHEEP = (world, selector, center, radius, spawner) -> {
        Box box = new Box(center).expand(radius);
        return selector.getEntities(world, (TypeFilter)EntityType.SHEEP, box, LivingEntity::isAlive).stream().filter(entity -> !spawner || EntityDetector.hasLineOfSight((World)world, (Vec3d)center.toCenterPos(), (Vec3d)entity.getEyePos())).map(Entity::getUuid).toList();
    };

    public List<UUID> detect(ServerWorld var1, Selector var2, BlockPos var3, double var4, boolean var6);

    private static boolean hasLineOfSight(World world, Vec3d pos, Vec3d entityEyePos) {
        BlockHitResult blockHitResult = world.raycast(new RaycastContext(entityEyePos, pos, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
        return blockHitResult.getBlockPos().equals((Object)BlockPos.ofFloored((Position)pos)) || blockHitResult.getType() == HitResult.Type.MISS;
    }
}

