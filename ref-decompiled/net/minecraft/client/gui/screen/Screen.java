package net.minecraft.client.gui.screen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.net.URI;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.Navigable;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.narration.ScreenNarrator;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.sound.MusicSound;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class Screen extends AbstractParentElement implements Drawable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Text SCREEN_USAGE_TEXT = Text.translatable("narrator.screen.usage");
   public static final Identifier MENU_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/menu_background.png");
   public static final Identifier HEADER_SEPARATOR_TEXTURE = Identifier.ofVanilla("textures/gui/header_separator.png");
   public static final Identifier FOOTER_SEPARATOR_TEXTURE = Identifier.ofVanilla("textures/gui/footer_separator.png");
   private static final Identifier INWORLD_MENU_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/inworld_menu_background.png");
   public static final Identifier INWORLD_HEADER_SEPARATOR_TEXTURE = Identifier.ofVanilla("textures/gui/inworld_header_separator.png");
   public static final Identifier INWORLD_FOOTER_SEPARATOR_TEXTURE = Identifier.ofVanilla("textures/gui/inworld_footer_separator.png");
   protected static final float field_60460 = 2000.0F;
   protected final Text title;
   private final List children = Lists.newArrayList();
   private final List selectables = Lists.newArrayList();
   @Nullable
   protected MinecraftClient client;
   private boolean screenInitialized;
   public int width;
   public int height;
   private final List drawables = Lists.newArrayList();
   protected TextRenderer textRenderer;
   private static final long SCREEN_INIT_NARRATION_DELAY;
   private static final long NARRATOR_MODE_CHANGE_DELAY;
   private static final long MOUSE_MOVE_NARRATION_DELAY = 750L;
   private static final long MOUSE_PRESS_SCROLL_NARRATION_DELAY = 200L;
   private static final long KEY_PRESS_NARRATION_DELAY = 200L;
   private final ScreenNarrator narrator = new ScreenNarrator();
   private long elementNarrationStartTime = Long.MIN_VALUE;
   private long screenNarrationStartTime = Long.MAX_VALUE;
   @Nullable
   protected CyclingButtonWidget narratorToggleButton;
   @Nullable
   private Selectable selected;
   protected final Executor executor = (runnable) -> {
      this.client.execute(() -> {
         if (this.client.currentScreen == this) {
            runnable.run();
         }

      });
   };

   protected Screen(Text title) {
      this.title = title;
   }

   public Text getTitle() {
      return this.title;
   }

   public Text getNarratedTitle() {
      return this.getTitle();
   }

   public final void renderWithTooltip(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      context.createNewRootLayer();
      this.renderBackground(context, mouseX, mouseY, deltaTicks);
      context.createNewRootLayer();
      this.render(context, mouseX, mouseY, deltaTicks);
      context.renderTooltip();
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      Iterator var5 = this.drawables.iterator();

      while(var5.hasNext()) {
         Drawable drawable = (Drawable)var5.next();
         drawable.render(context, mouseX, mouseY, deltaTicks);
      }

   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256 && this.shouldCloseOnEsc()) {
         this.close();
         return true;
      } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else {
         Object var10000;
         switch (keyCode) {
            case 258:
               var10000 = this.getTabNavigation();
               break;
            case 259:
            case 260:
            case 261:
            default:
               var10000 = null;
               break;
            case 262:
               var10000 = this.getArrowNavigation(NavigationDirection.RIGHT);
               break;
            case 263:
               var10000 = this.getArrowNavigation(NavigationDirection.LEFT);
               break;
            case 264:
               var10000 = this.getArrowNavigation(NavigationDirection.DOWN);
               break;
            case 265:
               var10000 = this.getArrowNavigation(NavigationDirection.UP);
         }

         GuiNavigation guiNavigation = var10000;
         if (guiNavigation != null) {
            GuiNavigationPath guiNavigationPath = super.getNavigationPath((GuiNavigation)guiNavigation);
            if (guiNavigationPath == null && guiNavigation instanceof GuiNavigation.Tab) {
               this.blur();
               guiNavigationPath = super.getNavigationPath((GuiNavigation)guiNavigation);
            }

            if (guiNavigationPath != null) {
               this.switchFocus(guiNavigationPath);
            }
         }

         return false;
      }
   }

   private GuiNavigation.Tab getTabNavigation() {
      boolean bl = !hasShiftDown();
      return new GuiNavigation.Tab(bl);
   }

   private GuiNavigation.Arrow getArrowNavigation(NavigationDirection direction) {
      return new GuiNavigation.Arrow(direction);
   }

   protected void setInitialFocus() {
      if (this.client.getNavigationType().isKeyboard()) {
         GuiNavigation.Tab tab = new GuiNavigation.Tab(true);
         GuiNavigationPath guiNavigationPath = super.getNavigationPath(tab);
         if (guiNavigationPath != null) {
            this.switchFocus(guiNavigationPath);
         }
      }

   }

   protected void setInitialFocus(Element element) {
      GuiNavigationPath guiNavigationPath = GuiNavigationPath.of((ParentElement)this, (GuiNavigationPath)element.getNavigationPath(new GuiNavigation.Down()));
      if (guiNavigationPath != null) {
         this.switchFocus(guiNavigationPath);
      }

   }

   public void blur() {
      GuiNavigationPath guiNavigationPath = this.getFocusedPath();
      if (guiNavigationPath != null) {
         guiNavigationPath.setFocused(false);
      }

   }

   @VisibleForTesting
   protected void switchFocus(GuiNavigationPath path) {
      this.blur();
      path.setFocused(true);
   }

   public boolean shouldCloseOnEsc() {
      return true;
   }

   public void close() {
      this.client.setScreen((Screen)null);
   }

   protected Element addDrawableChild(Element drawableElement) {
      this.drawables.add((Drawable)drawableElement);
      return this.addSelectableChild(drawableElement);
   }

   protected Drawable addDrawable(Drawable drawable) {
      this.drawables.add(drawable);
      return drawable;
   }

   protected Element addSelectableChild(Element child) {
      this.children.add(child);
      this.selectables.add((Selectable)child);
      return child;
   }

   protected void remove(Element child) {
      if (child instanceof Drawable) {
         this.drawables.remove((Drawable)child);
      }

      if (child instanceof Selectable) {
         this.selectables.remove((Selectable)child);
      }

      this.children.remove(child);
   }

   protected void clearChildren() {
      this.drawables.clear();
      this.children.clear();
      this.selectables.clear();
   }

   public static List getTooltipFromItem(MinecraftClient client, ItemStack stack) {
      return stack.getTooltip(Item.TooltipContext.create((World)client.world), client.player, client.options.advancedItemTooltips ? TooltipType.Default.ADVANCED : TooltipType.Default.BASIC);
   }

   protected void insertText(String text, boolean override) {
   }

   public boolean handleTextClick(Style style) {
      ClickEvent clickEvent = style.getClickEvent();
      if (hasShiftDown()) {
         if (style.getInsertion() != null) {
            this.insertText(style.getInsertion(), false);
         }
      } else if (clickEvent != null) {
         this.handleClickEvent(this.client, clickEvent);
         return true;
      }

      return false;
   }

   protected void handleClickEvent(MinecraftClient client, ClickEvent clickEvent) {
      handleClickEvent(clickEvent, client, this);
   }

   protected static void handleClickEvent(ClickEvent clickEvent, MinecraftClient client, @Nullable Screen screenAfterRun) {
      ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)Objects.requireNonNull(client.player, "Player not available");
      Objects.requireNonNull(clickEvent);
      byte var5 = 0;
      switch (clickEvent.typeSwitch<invokedynamic>(clickEvent, var5)) {
         case 0:
            ClickEvent.RunCommand var6 = (ClickEvent.RunCommand)clickEvent;
            ClickEvent.RunCommand var10000 = var6;

            String var11;
            try {
               var11 = var10000.command();
            } catch (Throwable var10) {
               throw new MatchException(var10.toString(), var10);
            }

            String var12 = var11;
            handleRunCommand(clientPlayerEntity, var12, screenAfterRun);
            break;
         case 1:
            ClickEvent.ShowDialog showDialog = (ClickEvent.ShowDialog)clickEvent;
            clientPlayerEntity.networkHandler.showDialog(showDialog.dialog(), screenAfterRun);
            break;
         case 2:
            ClickEvent.Custom custom = (ClickEvent.Custom)clickEvent;
            clientPlayerEntity.networkHandler.sendPacket(new CustomClickActionC2SPacket(custom.id(), custom.payload()));
            if (client.currentScreen != screenAfterRun) {
               client.setScreen(screenAfterRun);
            }
            break;
         default:
            handleBasicClickEvent(clickEvent, client, screenAfterRun);
      }

   }

   protected static void handleBasicClickEvent(ClickEvent clickEvent, MinecraftClient client, @Nullable Screen screenAfterRun) {
      boolean var21;
      label49: {
         Objects.requireNonNull(clickEvent);
         byte var5 = 0;
         boolean var10001;
         Throwable var19;
         String var20;
         switch (clickEvent.typeSwitch<invokedynamic>(clickEvent, var5)) {
            case 0:
               ClickEvent.OpenUrl var6 = (ClickEvent.OpenUrl)clickEvent;
               ClickEvent.OpenUrl var23 = var6;

               URI var24;
               try {
                  var24 = var23.uri();
               } catch (Throwable var16) {
                  var19 = var16;
                  var10001 = false;
                  break;
               }

               URI var17 = var24;
               handleOpenUri(client, screenAfterRun, var17);
               var21 = false;
               break label49;
            case 1:
               ClickEvent.OpenFile openFile = (ClickEvent.OpenFile)clickEvent;
               Util.getOperatingSystem().open(openFile.file());
               var21 = true;
               break label49;
            case 2:
               ClickEvent.SuggestCommand var9 = (ClickEvent.SuggestCommand)clickEvent;
               ClickEvent.SuggestCommand var22 = var9;

               try {
                  var20 = var22.command();
               } catch (Throwable var15) {
                  var19 = var15;
                  var10001 = false;
                  break;
               }

               String var18 = var20;
               if (screenAfterRun != null) {
                  screenAfterRun.insertText(var18, true);
               }

               var21 = true;
               break label49;
            case 3:
               ClickEvent.CopyToClipboard var11 = (ClickEvent.CopyToClipboard)clickEvent;
               ClickEvent.CopyToClipboard var10000 = var11;

               try {
                  var20 = var10000.value();
               } catch (Throwable var14) {
                  var19 = var14;
                  var10001 = false;
                  break;
               }

               String var13 = var20;
               client.keyboard.setClipboard(var13);
               var21 = true;
               break label49;
            default:
               LOGGER.error("Don't know how to handle {}", clickEvent);
               var21 = true;
               break label49;
         }

         Throwable var4 = var19;
         throw new MatchException(var4.toString(), var4);
      }

      boolean bl = var21;
      if (bl && client.currentScreen != screenAfterRun) {
         client.setScreen(screenAfterRun);
      }

   }

   protected static boolean handleOpenUri(MinecraftClient client, @Nullable Screen screen, URI uri) {
      if (!(Boolean)client.options.getChatLinks().getValue()) {
         return false;
      } else {
         if ((Boolean)client.options.getChatLinksPrompt().getValue()) {
            client.setScreen(new ConfirmLinkScreen((confirmed) -> {
               if (confirmed) {
                  Util.getOperatingSystem().open(uri);
               }

               client.setScreen(screen);
            }, uri.toString(), false));
         } else {
            Util.getOperatingSystem().open(uri);
         }

         return true;
      }
   }

   protected static void handleRunCommand(ClientPlayerEntity player, String command, @Nullable Screen screenAfterRun) {
      player.networkHandler.runClickEventCommand(CommandManager.stripLeadingSlash(command), screenAfterRun);
   }

   public final void init(MinecraftClient client, int width, int height) {
      this.client = client;
      this.textRenderer = client.textRenderer;
      this.width = width;
      this.height = height;
      if (!this.screenInitialized) {
         this.init();
         this.setInitialFocus();
      } else {
         this.refreshWidgetPositions();
      }

      this.screenInitialized = true;
      this.narrateScreenIfNarrationEnabled(false);
      this.setElementNarrationDelay(SCREEN_INIT_NARRATION_DELAY);
   }

   protected void clearAndInit() {
      this.clearChildren();
      this.blur();
      this.init();
      this.setInitialFocus();
   }

   protected void setWidgetAlpha(float alpha) {
      Iterator var2 = this.children().iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         if (element instanceof ClickableWidget clickableWidget) {
            clickableWidget.setAlpha(alpha);
         }
      }

   }

   public List children() {
      return this.children;
   }

   protected void init() {
   }

   public void tick() {
   }

   public void removed() {
   }

   public void onDisplayed() {
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      if (this.client.world == null) {
         this.renderPanoramaBackground(context, deltaTicks);
      }

      this.applyBlur(context);
      this.renderDarkening(context);
   }

   protected void applyBlur(DrawContext context) {
      float f = (float)this.client.options.getMenuBackgroundBlurrinessValue();
      if (f >= 1.0F) {
         context.applyBlur();
      }

   }

   protected void renderPanoramaBackground(DrawContext context, float deltaTicks) {
      this.client.gameRenderer.getRotatingPanoramaRenderer().render(context, this.width, this.height, true);
   }

   protected void renderDarkening(DrawContext context) {
      this.renderDarkening(context, 0, 0, this.width, this.height);
   }

   protected void renderDarkening(DrawContext context, int x, int y, int width, int height) {
      renderBackgroundTexture(context, this.client.world == null ? MENU_BACKGROUND_TEXTURE : INWORLD_MENU_BACKGROUND_TEXTURE, x, y, 0.0F, 0.0F, width, height);
   }

   public static void renderBackgroundTexture(DrawContext context, Identifier texture, int x, int y, float u, float v, int width, int height) {
      int i = true;
      context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, 32, 32);
   }

   public void renderInGameBackground(DrawContext context) {
      context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
   }

   public boolean shouldPause() {
      return true;
   }

   public static boolean hasControlDown() {
      if (MinecraftClient.IS_SYSTEM_MAC) {
         return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 343) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 347);
      } else {
         return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 341) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 345);
      }
   }

   public static boolean hasShiftDown() {
      return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344);
   }

   public static boolean hasAltDown() {
      return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 342) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 346);
   }

   public static boolean isCut(int code) {
      return code == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isPaste(int code) {
      return code == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isCopy(int code) {
      return code == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isSelectAll(int code) {
      return code == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   protected void refreshWidgetPositions() {
      this.clearAndInit();
   }

   public void resize(MinecraftClient client, int width, int height) {
      this.width = width;
      this.height = height;
      this.refreshWidgetPositions();
   }

   public void addCrashReportSection(CrashReport report) {
      CrashReportSection crashReportSection = report.addElement("Affected screen", 1);
      crashReportSection.add("Screen name", () -> {
         return this.getClass().getCanonicalName();
      });
   }

   protected boolean isValidCharacterForName(String name, char character, int cursorPos) {
      int i = name.indexOf(58);
      int j = name.indexOf(47);
      if (character == ':') {
         return (j == -1 || cursorPos <= j) && i == -1;
      } else if (character == '/') {
         return cursorPos > i;
      } else {
         return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '.';
      }
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return true;
   }

   public void onFilesDropped(List paths) {
   }

   private void setScreenNarrationDelay(long delayMs, boolean restartElementNarration) {
      this.screenNarrationStartTime = Util.getMeasuringTimeMs() + delayMs;
      if (restartElementNarration) {
         this.elementNarrationStartTime = Long.MIN_VALUE;
      }

   }

   private void setElementNarrationDelay(long delayMs) {
      this.elementNarrationStartTime = Util.getMeasuringTimeMs() + delayMs;
   }

   public void applyMouseMoveNarratorDelay() {
      this.setScreenNarrationDelay(750L, false);
   }

   public void applyMousePressScrollNarratorDelay() {
      this.setScreenNarrationDelay(200L, true);
   }

   public void applyKeyPressNarratorDelay() {
      this.setScreenNarrationDelay(200L, true);
   }

   private boolean isNarratorActive() {
      return this.client.getNarratorManager().isActive();
   }

   public void updateNarrator() {
      if (this.isNarratorActive()) {
         long l = Util.getMeasuringTimeMs();
         if (l > this.screenNarrationStartTime && l > this.elementNarrationStartTime) {
            this.narrateScreen(true);
            this.screenNarrationStartTime = Long.MAX_VALUE;
         }
      }

   }

   public void narrateScreenIfNarrationEnabled(boolean onlyChangedNarrations) {
      if (this.isNarratorActive()) {
         this.narrateScreen(onlyChangedNarrations);
      }

   }

   private void narrateScreen(boolean onlyChangedNarrations) {
      this.narrator.buildNarrations(this::addScreenNarrations);
      String string = this.narrator.buildNarratorText(!onlyChangedNarrations);
      if (!string.isEmpty()) {
         this.client.getNarratorManager().narrateSystemImmediately(string);
      }

   }

   protected boolean hasUsageText() {
      return true;
   }

   protected void addScreenNarrations(NarrationMessageBuilder messageBuilder) {
      messageBuilder.put(NarrationPart.TITLE, this.getNarratedTitle());
      if (this.hasUsageText()) {
         messageBuilder.put(NarrationPart.USAGE, SCREEN_USAGE_TEXT);
      }

      this.addElementNarrations(messageBuilder);
   }

   protected void addElementNarrations(NarrationMessageBuilder builder) {
      List list = this.selectables.stream().flatMap((selectable) -> {
         return selectable.getNarratedParts().stream();
      }).filter(Selectable::isNarratable).sorted(Comparator.comparingInt(Navigable::getNavigationOrder)).toList();
      SelectedElementNarrationData selectedElementNarrationData = findSelectedElementData(list, this.selected);
      if (selectedElementNarrationData != null) {
         if (selectedElementNarrationData.selectType.isFocused()) {
            this.selected = selectedElementNarrationData.selectable;
         }

         if (list.size() > 1) {
            builder.put(NarrationPart.POSITION, (Text)Text.translatable("narrator.position.screen", selectedElementNarrationData.index + 1, list.size()));
            if (selectedElementNarrationData.selectType == Selectable.SelectionType.FOCUSED) {
               builder.put(NarrationPart.USAGE, this.getUsageNarrationText());
            }
         }

         selectedElementNarrationData.selectable.appendNarrations(builder.nextMessage());
      }

   }

   protected Text getUsageNarrationText() {
      return Text.translatable("narration.component_list.usage");
   }

   @Nullable
   public static SelectedElementNarrationData findSelectedElementData(List selectables, @Nullable Selectable selectable) {
      SelectedElementNarrationData selectedElementNarrationData = null;
      SelectedElementNarrationData selectedElementNarrationData2 = null;
      int i = 0;

      for(int j = selectables.size(); i < j; ++i) {
         Selectable selectable2 = (Selectable)selectables.get(i);
         Selectable.SelectionType selectionType = selectable2.getType();
         if (selectionType.isFocused()) {
            if (selectable2 != selectable) {
               return new SelectedElementNarrationData(selectable2, i, selectionType);
            }

            selectedElementNarrationData2 = new SelectedElementNarrationData(selectable2, i, selectionType);
         } else if (selectionType.compareTo(selectedElementNarrationData != null ? selectedElementNarrationData.selectType : Selectable.SelectionType.NONE) > 0) {
            selectedElementNarrationData = new SelectedElementNarrationData(selectable2, i, selectionType);
         }
      }

      return selectedElementNarrationData != null ? selectedElementNarrationData : selectedElementNarrationData2;
   }

   public void refreshNarrator(boolean previouslyDisabled) {
      if (previouslyDisabled) {
         this.setScreenNarrationDelay(NARRATOR_MODE_CHANGE_DELAY, false);
      }

      if (this.narratorToggleButton != null) {
         this.narratorToggleButton.setValue((NarratorMode)this.client.options.getNarrator().getValue());
      }

   }

   public TextRenderer getTextRenderer() {
      return this.textRenderer;
   }

   public boolean showsStatusEffects() {
      return false;
   }

   public ScreenRect getNavigationFocus() {
      return new ScreenRect(0, 0, this.width, this.height);
   }

   @Nullable
   public MusicSound getMusic() {
      return null;
   }

   static {
      SCREEN_INIT_NARRATION_DELAY = TimeUnit.SECONDS.toMillis(2L);
      NARRATOR_MODE_CHANGE_DELAY = SCREEN_INIT_NARRATION_DELAY;
   }

   @Environment(EnvType.CLIENT)
   public static class SelectedElementNarrationData {
      public final Selectable selectable;
      public final int index;
      public final Selectable.SelectionType selectType;

      public SelectedElementNarrationData(Selectable selectable, int index, Selectable.SelectionType selectType) {
         this.selectable = selectable;
         this.index = index;
         this.selectType = selectType;
      }
   }
}
