package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;

public class RotateCommand {
   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("rotate").requires(CommandManager.requirePermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("target", EntityArgumentType.entity()).then(CommandManager.argument("rotation", RotationArgumentType.rotation()).executes((context) -> {
         return rotateToPos((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), RotationArgumentType.getRotation(context, "rotation"));
      }))).then(((LiteralArgumentBuilder)CommandManager.literal("facing").then(CommandManager.literal("entity").then(((RequiredArgumentBuilder)CommandManager.argument("facingEntity", EntityArgumentType.entity()).executes((context) -> {
         return rotateFacingLookTarget((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), new LookTarget.LookAtEntity(EntityArgumentType.getEntity(context, "facingEntity"), EntityAnchorArgumentType.EntityAnchor.FEET));
      })).then(CommandManager.argument("facingAnchor", EntityAnchorArgumentType.entityAnchor()).executes((context) -> {
         return rotateFacingLookTarget((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), new LookTarget.LookAtEntity(EntityArgumentType.getEntity(context, "facingEntity"), EntityAnchorArgumentType.getEntityAnchor(context, "facingAnchor")));
      }))))).then(CommandManager.argument("facingLocation", Vec3ArgumentType.vec3()).executes((context) -> {
         return rotateFacingLookTarget((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), new LookTarget.LookAtPosition(Vec3ArgumentType.getVec3(context, "facingLocation")));
      })))));
   }

   private static int rotateToPos(ServerCommandSource source, Entity entity, PosArgument pos) {
      Vec2f vec2f = pos.getRotation(source);
      entity.rotate(vec2f.y, vec2f.x);
      source.sendFeedback(() -> {
         return Text.translatable("commands.rotate.success", entity.getDisplayName());
      }, true);
      return 1;
   }

   private static int rotateFacingLookTarget(ServerCommandSource source, Entity entity, LookTarget lookTarget) {
      lookTarget.look(source, entity);
      source.sendFeedback(() -> {
         return Text.translatable("commands.rotate.success", entity.getDisplayName());
      }, true);
      return 1;
   }
}
