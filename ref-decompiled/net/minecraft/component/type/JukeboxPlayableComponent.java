package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.LazyRegistryEntryReference;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Texts;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public record JukeboxPlayableComponent(LazyRegistryEntryReference song) implements TooltipAppender {
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public JukeboxPlayableComponent(LazyRegistryEntryReference lazyRegistryEntryReference) {
      this.song = lazyRegistryEntryReference;
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      RegistryWrapper.WrapperLookup wrapperLookup = context.getRegistryLookup();
      if (wrapperLookup != null) {
         this.song.resolveEntry(wrapperLookup).ifPresent((entry) -> {
            MutableText mutableText = ((JukeboxSong)entry.value()).description().copy();
            Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.GRAY));
            textConsumer.accept(mutableText);
         });
      }

   }

   public static ActionResult tryPlayStack(World world, BlockPos pos, ItemStack stack, PlayerEntity player) {
      JukeboxPlayableComponent jukeboxPlayableComponent = (JukeboxPlayableComponent)stack.get(DataComponentTypes.JUKEBOX_PLAYABLE);
      if (jukeboxPlayableComponent == null) {
         return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
      } else {
         BlockState blockState = world.getBlockState(pos);
         if (blockState.isOf(Blocks.JUKEBOX) && !(Boolean)blockState.get(JukeboxBlock.HAS_RECORD)) {
            if (!world.isClient) {
               ItemStack itemStack = stack.splitUnlessCreative(1, player);
               BlockEntity var8 = world.getBlockEntity(pos);
               if (var8 instanceof JukeboxBlockEntity) {
                  JukeboxBlockEntity jukeboxBlockEntity = (JukeboxBlockEntity)var8;
                  jukeboxBlockEntity.setStack(itemStack);
                  world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockState));
               }

               player.incrementStat(Stats.PLAY_RECORD);
            }

            return ActionResult.SUCCESS;
         } else {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
         }
      }
   }

   public LazyRegistryEntryReference song() {
      return this.song;
   }

   static {
      CODEC = LazyRegistryEntryReference.createCodec(RegistryKeys.JUKEBOX_SONG, JukeboxSong.ENTRY_CODEC).xmap(JukeboxPlayableComponent::new, JukeboxPlayableComponent::song);
      PACKET_CODEC = PacketCodec.tuple(LazyRegistryEntryReference.createPacketCodec(RegistryKeys.JUKEBOX_SONG, JukeboxSong.ENTRY_PACKET_CODEC), JukeboxPlayableComponent::song, JukeboxPlayableComponent::new);
   }
}
