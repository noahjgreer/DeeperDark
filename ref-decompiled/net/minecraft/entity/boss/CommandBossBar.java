package net.minecraft.entity.boss;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.MathHelper;

public class CommandBossBar extends ServerBossBar {
   private static final int DEFAULT_MAX_VALUE = 100;
   private final Identifier id;
   private final Set playerUuids = Sets.newHashSet();
   private int value;
   private int maxValue = 100;

   public CommandBossBar(Identifier id, Text displayName) {
      super(displayName, BossBar.Color.WHITE, BossBar.Style.PROGRESS);
      this.id = id;
      this.setPercent(0.0F);
   }

   public Identifier getId() {
      return this.id;
   }

   public void addPlayer(ServerPlayerEntity player) {
      super.addPlayer(player);
      this.playerUuids.add(player.getUuid());
   }

   public void addPlayer(UUID uuid) {
      this.playerUuids.add(uuid);
   }

   public void removePlayer(ServerPlayerEntity player) {
      super.removePlayer(player);
      this.playerUuids.remove(player.getUuid());
   }

   public void clearPlayers() {
      super.clearPlayers();
      this.playerUuids.clear();
   }

   public int getValue() {
      return this.value;
   }

   public int getMaxValue() {
      return this.maxValue;
   }

   public void setValue(int value) {
      this.value = value;
      this.setPercent(MathHelper.clamp((float)value / (float)this.maxValue, 0.0F, 1.0F));
   }

   public void setMaxValue(int maxValue) {
      this.maxValue = maxValue;
      this.setPercent(MathHelper.clamp((float)this.value / (float)maxValue, 0.0F, 1.0F));
   }

   public final Text toHoverableText() {
      return Texts.bracketed(this.getName()).styled((style) -> {
         return style.withColor(this.getColor().getTextFormat()).withHoverEvent(new HoverEvent.ShowText(Text.literal(this.getId().toString()))).withInsertion(this.getId().toString());
      });
   }

   public boolean addPlayers(Collection players) {
      Set set = Sets.newHashSet();
      Set set2 = Sets.newHashSet();
      Iterator var4 = this.playerUuids.iterator();

      UUID uUID;
      boolean bl;
      Iterator var7;
      while(var4.hasNext()) {
         uUID = (UUID)var4.next();
         bl = false;
         var7 = players.iterator();

         while(var7.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var7.next();
            if (serverPlayerEntity.getUuid().equals(uUID)) {
               bl = true;
               break;
            }
         }

         if (!bl) {
            set.add(uUID);
         }
      }

      var4 = players.iterator();

      ServerPlayerEntity serverPlayerEntity2;
      while(var4.hasNext()) {
         serverPlayerEntity2 = (ServerPlayerEntity)var4.next();
         bl = false;
         var7 = this.playerUuids.iterator();

         while(var7.hasNext()) {
            UUID uUID2 = (UUID)var7.next();
            if (serverPlayerEntity2.getUuid().equals(uUID2)) {
               bl = true;
               break;
            }
         }

         if (!bl) {
            set2.add(serverPlayerEntity2);
         }
      }

      for(var4 = set.iterator(); var4.hasNext(); this.playerUuids.remove(uUID)) {
         uUID = (UUID)var4.next();
         Iterator var11 = this.getPlayers().iterator();

         while(var11.hasNext()) {
            ServerPlayerEntity serverPlayerEntity3 = (ServerPlayerEntity)var11.next();
            if (serverPlayerEntity3.getUuid().equals(uUID)) {
               this.removePlayer(serverPlayerEntity3);
               break;
            }
         }
      }

      var4 = set2.iterator();

      while(var4.hasNext()) {
         serverPlayerEntity2 = (ServerPlayerEntity)var4.next();
         this.addPlayer(serverPlayerEntity2);
      }

      return !set.isEmpty() || !set2.isEmpty();
   }

   public static CommandBossBar fromSerialized(Identifier id, Serialized serialized) {
      CommandBossBar commandBossBar = new CommandBossBar(id, serialized.name);
      commandBossBar.setVisible(serialized.visible);
      commandBossBar.setValue(serialized.value);
      commandBossBar.setMaxValue(serialized.max);
      commandBossBar.setColor(serialized.color);
      commandBossBar.setStyle(serialized.overlay);
      commandBossBar.setDarkenSky(serialized.darkenScreen);
      commandBossBar.setDragonMusic(serialized.playBossMusic);
      commandBossBar.setThickenFog(serialized.createWorldFog);
      Set var10000 = serialized.players;
      Objects.requireNonNull(commandBossBar);
      var10000.forEach(commandBossBar::addPlayer);
      return commandBossBar;
   }

   public Serialized toSerialized() {
      return new Serialized(this.getName(), this.isVisible(), this.getValue(), this.getMaxValue(), this.getColor(), this.getStyle(), this.shouldDarkenSky(), this.hasDragonMusic(), this.shouldThickenFog(), Set.copyOf(this.playerUuids));
   }

   public void onPlayerConnect(ServerPlayerEntity player) {
      if (this.playerUuids.contains(player.getUuid())) {
         this.addPlayer(player);
      }

   }

   public void onPlayerDisconnect(ServerPlayerEntity player) {
      super.removePlayer(player);
   }

   public static record Serialized(Text name, boolean visible, int value, int max, BossBar.Color color, BossBar.Style overlay, boolean darkenScreen, boolean playBossMusic, boolean createWorldFog, Set players) {
      final Text name;
      final boolean visible;
      final int value;
      final int max;
      final BossBar.Color color;
      final BossBar.Style overlay;
      final boolean darkenScreen;
      final boolean playBossMusic;
      final boolean createWorldFog;
      final Set players;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(TextCodecs.CODEC.fieldOf("Name").forGetter(Serialized::name), Codec.BOOL.optionalFieldOf("Visible", false).forGetter(Serialized::visible), Codec.INT.optionalFieldOf("Value", 0).forGetter(Serialized::value), Codec.INT.optionalFieldOf("Max", 100).forGetter(Serialized::max), BossBar.Color.CODEC.optionalFieldOf("Color", BossBar.Color.WHITE).forGetter(Serialized::color), BossBar.Style.CODEC.optionalFieldOf("Overlay", BossBar.Style.PROGRESS).forGetter(Serialized::overlay), Codec.BOOL.optionalFieldOf("DarkenScreen", false).forGetter(Serialized::darkenScreen), Codec.BOOL.optionalFieldOf("PlayBossMusic", false).forGetter(Serialized::playBossMusic), Codec.BOOL.optionalFieldOf("CreateWorldFog", false).forGetter(Serialized::createWorldFog), Uuids.SET_CODEC.optionalFieldOf("Players", Set.of()).forGetter(Serialized::players)).apply(instance, Serialized::new);
      });

      public Serialized(Text text, boolean bl, int i, int j, BossBar.Color color, BossBar.Style style, boolean bl2, boolean bl3, boolean bl4, Set set) {
         this.name = text;
         this.visible = bl;
         this.value = i;
         this.max = j;
         this.color = color;
         this.overlay = style;
         this.darkenScreen = bl2;
         this.playBossMusic = bl3;
         this.createWorldFog = bl4;
         this.players = set;
      }

      public Text name() {
         return this.name;
      }

      public boolean visible() {
         return this.visible;
      }

      public int value() {
         return this.value;
      }

      public int max() {
         return this.max;
      }

      public BossBar.Color color() {
         return this.color;
      }

      public BossBar.Style overlay() {
         return this.overlay;
      }

      public boolean darkenScreen() {
         return this.darkenScreen;
      }

      public boolean playBossMusic() {
         return this.playBossMusic;
      }

      public boolean createWorldFog() {
         return this.createWorldFog;
      }

      public Set players() {
         return this.players;
      }
   }
}
