package net.minecraft.client.gui.screen.world;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CustomizeFlatLevelScreen extends Screen {
   private static final Text TITLE = Text.translatable("createWorld.customize.flat.title");
   static final Identifier SLOT_TEXTURE = Identifier.ofVanilla("container/slot");
   private static final int ICON_SIZE = 18;
   private static final int BUTTON_HEIGHT = 20;
   private static final int ICON_BACKGROUND_OFFSET_X = 1;
   private static final int ICON_BACKGROUND_OFFSET_Y = 1;
   private static final int ICON_OFFSET_X = 2;
   private static final int ICON_OFFSET_Y = 2;
   private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 33, 64);
   protected final CreateWorldScreen parent;
   private final Consumer configConsumer;
   FlatChunkGeneratorConfig config;
   @Nullable
   private SuperflatLayersListWidget layers;
   @Nullable
   private ButtonWidget widgetButtonRemoveLayer;

   public CustomizeFlatLevelScreen(CreateWorldScreen parent, Consumer configConsumer, FlatChunkGeneratorConfig config) {
      super(TITLE);
      this.parent = parent;
      this.configConsumer = configConsumer;
      this.config = config;
   }

   public FlatChunkGeneratorConfig getConfig() {
      return this.config;
   }

   public void setConfig(FlatChunkGeneratorConfig config) {
      this.config = config;
      if (this.layers != null) {
         this.layers.updateLayers();
         this.updateRemoveLayerButton();
      }

   }

   protected void init() {
      this.layout.addHeader(this.title, this.textRenderer);
      this.layers = (SuperflatLayersListWidget)this.layout.addBody(new SuperflatLayersListWidget());
      DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addFooter(DirectionalLayoutWidget.vertical().spacing(4));
      directionalLayoutWidget.getMainPositioner().alignVerticalCenter();
      DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)directionalLayoutWidget.add(DirectionalLayoutWidget.horizontal().spacing(8));
      DirectionalLayoutWidget directionalLayoutWidget3 = (DirectionalLayoutWidget)directionalLayoutWidget.add(DirectionalLayoutWidget.horizontal().spacing(8));
      this.widgetButtonRemoveLayer = (ButtonWidget)directionalLayoutWidget2.add(ButtonWidget.builder(Text.translatable("createWorld.customize.flat.removeLayer"), (button) -> {
         if (this.hasLayerSelected()) {
            List list = this.config.getLayers();
            int i = this.layers.children().indexOf(this.layers.getSelectedOrNull());
            int j = list.size() - i - 1;
            list.remove(j);
            this.layers.setSelected(list.isEmpty() ? null : (SuperflatLayersListWidget.SuperflatLayerEntry)this.layers.children().get(Math.min(i, list.size() - 1)));
            this.config.updateLayerBlocks();
            this.layers.updateLayers();
            this.updateRemoveLayerButton();
         }
      }).build());
      directionalLayoutWidget2.add(ButtonWidget.builder(Text.translatable("createWorld.customize.presets"), (button) -> {
         this.client.setScreen(new PresetsScreen(this));
         this.config.updateLayerBlocks();
         this.updateRemoveLayerButton();
      }).build());
      directionalLayoutWidget3.add(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
         this.configConsumer.accept(this.config);
         this.close();
         this.config.updateLayerBlocks();
      }).build());
      directionalLayoutWidget3.add(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
         this.close();
         this.config.updateLayerBlocks();
      }).build());
      this.config.updateLayerBlocks();
      this.updateRemoveLayerButton();
      this.layout.forEachChild(this::addDrawableChild);
      this.refreshWidgetPositions();
   }

   protected void refreshWidgetPositions() {
      if (this.layers != null) {
         this.layers.position(this.width, this.layout);
      }

      this.layout.refreshPositions();
   }

   void updateRemoveLayerButton() {
      if (this.widgetButtonRemoveLayer != null) {
         this.widgetButtonRemoveLayer.active = this.hasLayerSelected();
      }

   }

   private boolean hasLayerSelected() {
      return this.layers != null && this.layers.getSelectedOrNull() != null;
   }

   public void close() {
      this.client.setScreen(this.parent);
   }

   @Environment(EnvType.CLIENT)
   class SuperflatLayersListWidget extends AlwaysSelectedEntryListWidget {
      private static final Text LAYER_MATERIAL_TEXT;
      private static final Text HEIGHT_TEXT;

      public SuperflatLayersListWidget() {
         MinecraftClient var10001 = CustomizeFlatLevelScreen.this.client;
         int var10002 = CustomizeFlatLevelScreen.this.width;
         int var10003 = CustomizeFlatLevelScreen.this.height - 103;
         Objects.requireNonNull(CustomizeFlatLevelScreen.this.textRenderer);
         super(var10001, var10002, var10003, 43, 24, (int)(9.0 * 1.5));

         for(int i = 0; i < CustomizeFlatLevelScreen.this.config.getLayers().size(); ++i) {
            this.addEntry(new SuperflatLayerEntry());
         }

      }

      public void setSelected(@Nullable SuperflatLayerEntry superflatLayerEntry) {
         super.setSelected(superflatLayerEntry);
         CustomizeFlatLevelScreen.this.updateRemoveLayerButton();
      }

      public void updateLayers() {
         int i = this.children().indexOf(this.getSelectedOrNull());
         this.clearEntries();

         for(int j = 0; j < CustomizeFlatLevelScreen.this.config.getLayers().size(); ++j) {
            this.addEntry(new SuperflatLayerEntry());
         }

         List list = this.children();
         if (i >= 0 && i < list.size()) {
            this.setSelected((SuperflatLayerEntry)list.get(i));
         }

      }

      protected void renderHeader(DrawContext context, int x, int y) {
         context.drawTextWithShadow(CustomizeFlatLevelScreen.this.textRenderer, (Text)LAYER_MATERIAL_TEXT, x, y, -1);
         context.drawTextWithShadow(CustomizeFlatLevelScreen.this.textRenderer, (Text)HEIGHT_TEXT, x + this.getRowWidth() - CustomizeFlatLevelScreen.this.textRenderer.getWidth((StringVisitable)HEIGHT_TEXT) - 8, y, -1);
      }

      static {
         LAYER_MATERIAL_TEXT = Text.translatable("createWorld.customize.flat.tile").formatted(Formatting.UNDERLINE);
         HEIGHT_TEXT = Text.translatable("createWorld.customize.flat.height").formatted(Formatting.UNDERLINE);
      }

      @Environment(EnvType.CLIENT)
      private class SuperflatLayerEntry extends AlwaysSelectedEntryListWidget.Entry {
         SuperflatLayerEntry() {
         }

         public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            FlatChunkGeneratorLayer flatChunkGeneratorLayer = (FlatChunkGeneratorLayer)CustomizeFlatLevelScreen.this.config.getLayers().get(CustomizeFlatLevelScreen.this.config.getLayers().size() - index - 1);
            BlockState blockState = flatChunkGeneratorLayer.getBlockState();
            ItemStack itemStack = this.createItemStackFor(blockState);
            this.renderIcon(context, x, y, itemStack);
            int var10000 = y + entryHeight / 2;
            Objects.requireNonNull(CustomizeFlatLevelScreen.this.textRenderer);
            int i = var10000 - 9 / 2;
            context.drawTextWithShadow(CustomizeFlatLevelScreen.this.textRenderer, (Text)itemStack.getName(), x + 18 + 5, i, -1);
            MutableText text;
            if (index == 0) {
               text = Text.translatable("createWorld.customize.flat.layer.top", flatChunkGeneratorLayer.getThickness());
            } else if (index == CustomizeFlatLevelScreen.this.config.getLayers().size() - 1) {
               text = Text.translatable("createWorld.customize.flat.layer.bottom", flatChunkGeneratorLayer.getThickness());
            } else {
               text = Text.translatable("createWorld.customize.flat.layer", flatChunkGeneratorLayer.getThickness());
            }

            context.drawTextWithShadow(CustomizeFlatLevelScreen.this.textRenderer, (Text)text, x + entryWidth - CustomizeFlatLevelScreen.this.textRenderer.getWidth((StringVisitable)text) - 8, i, -1);
         }

         private ItemStack createItemStackFor(BlockState state) {
            Item item = state.getBlock().asItem();
            if (item == Items.AIR) {
               if (state.isOf(Blocks.WATER)) {
                  item = Items.WATER_BUCKET;
               } else if (state.isOf(Blocks.LAVA)) {
                  item = Items.LAVA_BUCKET;
               }
            }

            return new ItemStack(item);
         }

         public Text getNarration() {
            FlatChunkGeneratorLayer flatChunkGeneratorLayer = (FlatChunkGeneratorLayer)CustomizeFlatLevelScreen.this.config.getLayers().get(CustomizeFlatLevelScreen.this.config.getLayers().size() - SuperflatLayersListWidget.this.children().indexOf(this) - 1);
            ItemStack itemStack = this.createItemStackFor(flatChunkGeneratorLayer.getBlockState());
            return (Text)(!itemStack.isEmpty() ? Text.translatable("narrator.select", itemStack.getName()) : ScreenTexts.EMPTY);
         }

         public boolean mouseClicked(double mouseX, double mouseY, int button) {
            SuperflatLayersListWidget.this.setSelected(this);
            return super.mouseClicked(mouseX, mouseY, button);
         }

         private void renderIcon(DrawContext context, int x, int y, ItemStack iconItem) {
            this.renderIconBackgroundTexture(context, x + 1, y + 1);
            if (!iconItem.isEmpty()) {
               context.drawItemWithoutEntity(iconItem, x + 2, y + 2);
            }

         }

         private void renderIconBackgroundTexture(DrawContext context, int x, int y) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, CustomizeFlatLevelScreen.SLOT_TEXTURE, x, y, 18, 18);
         }
      }
   }
}
