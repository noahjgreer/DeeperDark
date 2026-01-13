/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.hash.Hashing
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.ConfirmScreen
 *  net.minecraft.client.gui.screen.NoticeScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.pack.PackListWidget
 *  net.minecraft.client.gui.screen.pack.PackScreen
 *  net.minecraft.client.gui.screen.pack.PackScreen$1
 *  net.minecraft.client.gui.screen.pack.PackScreen$DirectoryWatcher
 *  net.minecraft.client.gui.screen.pack.ResourcePackOrganizer
 *  net.minecraft.client.gui.screen.pack.ResourcePackOrganizer$AbstractPack
 *  net.minecraft.client.gui.screen.pack.ResourcePackOrganizer$Pack
 *  net.minecraft.client.gui.screen.world.SymlinkWarningScreen
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.client.texture.NativeImageBackedTexture
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.toast.SystemToast
 *  net.minecraft.resource.InputSupplier
 *  net.minecraft.resource.ResourcePack
 *  net.minecraft.resource.ResourcePackManager
 *  net.minecraft.resource.ResourcePackProfile
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.gui.screen.world.SymlinkWarningScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class PackScreen
extends Screen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Text AVAILABLE_TITLE = Text.translatable((String)"pack.available.title");
    private static final Text SELECTED_TITLE = Text.translatable((String)"pack.selected.title");
    private static final Text OPEN_FOLDER = Text.translatable((String)"pack.openFolder");
    private static final Text SEARCH_BOX_PLACEHOLDER = Text.translatable((String)"gui.packSelection.search").fillStyle(TextFieldWidget.SEARCH_STYLE);
    private static final int field_32395 = 200;
    private static final int field_62930 = 4;
    private static final int field_62931 = 15;
    private static final Text DROP_INFO = Text.translatable((String)"pack.dropInfo").formatted(Formatting.GRAY);
    private static final Text FOLDER_INFO = Text.translatable((String)"pack.folderInfo");
    private static final int field_32396 = 20;
    private static final Identifier UNKNOWN_PACK = Identifier.ofVanilla((String)"textures/misc/unknown_pack.png");
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);
    private final ResourcePackOrganizer organizer;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable PackScreen.DirectoryWatcher directoryWatcher;
    private long refreshTimeout;
    private @Nullable PackListWidget availablePackList;
    private @Nullable PackListWidget selectedPackList;
    private @Nullable TextFieldWidget searchBox;
    private final Path file;
    private @Nullable ButtonWidget doneButton;
    private final Map<String, Identifier> iconTextures = Maps.newHashMap();

    public PackScreen(ResourcePackManager resourcePackManager, Consumer<ResourcePackManager> applier, Path file, Text title) {
        super(title);
        this.organizer = new ResourcePackOrganizer(arg_0 -> this.updatePackLists(arg_0), arg_0 -> this.getPackIconTexture(arg_0), resourcePackManager, applier);
        this.file = file;
        this.directoryWatcher = DirectoryWatcher.create((Path)file);
    }

    public void close() {
        this.organizer.apply();
        this.closeDirectoryWatcher();
    }

    private void closeDirectoryWatcher() {
        if (this.directoryWatcher != null) {
            try {
                this.directoryWatcher.close();
                this.directoryWatcher = null;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    protected void init() {
        Objects.requireNonNull(this.textRenderer);
        Objects.requireNonNull(this.textRenderer);
        this.layout.setHeaderHeight(4 + 9 + 4 + 9 + 4 + 15 + 4);
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addHeader((Widget)DirectionalLayoutWidget.vertical().spacing(4));
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add((Widget)new TextWidget(this.getTitle(), this.textRenderer));
        directionalLayoutWidget.add((Widget)new TextWidget(DROP_INFO, this.textRenderer));
        this.searchBox = (TextFieldWidget)directionalLayoutWidget.add((Widget)new TextFieldWidget(this.textRenderer, 0, 0, 200, 15, (Text)Text.empty()));
        this.searchBox.setPlaceholder(SEARCH_BOX_PLACEHOLDER);
        this.searchBox.setChangedListener(arg_0 -> this.setSearch(arg_0));
        this.availablePackList = (PackListWidget)this.layout.addBody((Widget)new PackListWidget(this.client, this, 200, this.height - 66, AVAILABLE_TITLE));
        this.selectedPackList = (PackListWidget)this.layout.addBody((Widget)new PackListWidget(this.client, this, 200, this.height - 66, SELECTED_TITLE));
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)OPEN_FOLDER, button -> Util.getOperatingSystem().open(this.file)).tooltip(Tooltip.of((Text)FOLDER_INFO)).build());
        this.doneButton = (ButtonWidget)directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.close()).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
        this.refresh();
    }

    protected void setInitialFocus() {
        if (this.searchBox != null) {
            this.setInitialFocus((Element)this.searchBox);
        } else {
            super.setInitialFocus();
        }
    }

    private void setSearch(String search) {
        this.filter(search, this.organizer.getEnabledPacks(), this.selectedPackList);
        this.filter(search, this.organizer.getDisabledPacks(), this.availablePackList);
    }

    private void filter(String search, Stream<ResourcePackOrganizer.Pack> packs, @Nullable PackListWidget listWidget) {
        if (listWidget == null) {
            return;
        }
        String string = search.toLowerCase(Locale.ROOT);
        Stream<ResourcePackOrganizer.Pack> stream = packs.filter(pack -> search.isBlank() || pack.getName().toLowerCase(Locale.ROOT).contains(string) || pack.getDisplayName().getString().toLowerCase(Locale.ROOT).contains(string) || pack.getDescription().getString().toLowerCase(Locale.ROOT).contains(string));
        listWidget.set(stream, null);
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.availablePackList != null) {
            this.availablePackList.position(200, this.layout.getContentHeight(), this.width / 2 - 15 - 200, this.layout.getHeaderHeight());
        }
        if (this.selectedPackList != null) {
            this.selectedPackList.position(200, this.layout.getContentHeight(), this.width / 2 + 15, this.layout.getHeaderHeight());
        }
    }

    public void tick() {
        if (this.directoryWatcher != null) {
            try {
                if (this.directoryWatcher.pollForChange()) {
                    this.refreshTimeout = 20L;
                }
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to poll for directory {} changes, stopping", (Object)this.file);
                this.closeDirectoryWatcher();
            }
        }
        if (this.refreshTimeout > 0L && --this.refreshTimeout == 0L) {
            this.refresh();
        }
    }

    private void updatePackLists(// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ResourcePackOrganizer.AbstractPack focused) {
        if (this.selectedPackList != null) {
            this.selectedPackList.set(this.organizer.getEnabledPacks(), focused);
        }
        if (this.availablePackList != null) {
            this.availablePackList.set(this.organizer.getDisabledPacks(), focused);
        }
        if (this.searchBox != null) {
            this.setSearch(this.searchBox.getText());
        }
        if (this.doneButton != null) {
            this.doneButton.active = !this.selectedPackList.children().isEmpty();
        }
    }

    private void refresh() {
        this.organizer.refresh();
        this.updatePackLists(null);
        this.refreshTimeout = 0L;
        this.iconTextures.clear();
    }

    protected static void copyPacks(MinecraftClient client, List<Path> srcPaths, Path destPath) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        srcPaths.forEach(src -> {
            try (Stream<Path> stream = Files.walk(src, new FileVisitOption[0]);){
                stream.forEach(toCopy -> {
                    try {
                        Util.relativeCopy((Path)src.getParent(), (Path)destPath, (Path)toCopy);
                    }
                    catch (IOException iOException) {
                        LOGGER.warn("Failed to copy datapack file  from {} to {}", new Object[]{toCopy, destPath, iOException});
                        mutableBoolean.setTrue();
                    }
                });
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to copy datapack file from {} to {}", src, (Object)destPath);
                mutableBoolean.setTrue();
            }
        });
        if (mutableBoolean.isTrue()) {
            SystemToast.addPackCopyFailure((MinecraftClient)client, (String)destPath.toString());
        }
    }

    public void onFilesDropped(List<Path> paths) {
        String string = PackScreen.streamFileNames(paths).collect(Collectors.joining(", "));
        this.client.setScreen((Screen)new ConfirmScreen(confirmed -> {
            if (confirmed) {
                ArrayList<Path> list2 = new ArrayList<Path>(paths.size());
                HashSet set = new HashSet(paths);
                1 resourcePackOpener = new /* Unavailable Anonymous Inner Class!! */;
                ArrayList list3 = new ArrayList();
                for (Path path : paths) {
                    try {
                        Path path2 = (Path)resourcePackOpener.open(path, list3);
                        if (path2 == null) {
                            LOGGER.warn("Path {} does not seem like pack", (Object)path);
                            continue;
                        }
                        list2.add(path2);
                        set.remove(path2);
                    }
                    catch (IOException iOException) {
                        LOGGER.warn("Failed to check {} for packs", (Object)path, (Object)iOException);
                    }
                }
                if (!list3.isEmpty()) {
                    this.client.setScreen(SymlinkWarningScreen.pack(() -> this.client.setScreen((Screen)this)));
                    return;
                }
                if (!list2.isEmpty()) {
                    PackScreen.copyPacks((MinecraftClient)this.client, list2, (Path)this.file);
                    this.refresh();
                }
                if (!set.isEmpty()) {
                    String string = PackScreen.streamFileNames(set).collect(Collectors.joining(", "));
                    this.client.setScreen((Screen)new NoticeScreen(() -> this.client.setScreen((Screen)this), (Text)Text.translatable((String)"pack.dropRejected.title"), (Text)Text.translatable((String)"pack.dropRejected.message", (Object[])new Object[]{string})));
                    return;
                }
            }
            this.client.setScreen((Screen)this);
        }, (Text)Text.translatable((String)"pack.dropConfirm"), (Text)Text.literal((String)string)));
    }

    private static Stream<String> streamFileNames(Collection<Path> paths) {
        return paths.stream().map(Path::getFileName).map(Path::toString);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private Identifier loadPackIcon(TextureManager textureManager, ResourcePackProfile resourcePackProfile) {
        try (ResourcePack resourcePack = resourcePackProfile.createResourcePack();){
            Identifier identifier;
            block16: {
                InputSupplier inputSupplier = resourcePack.openRoot(new String[]{"pack.png"});
                if (inputSupplier == null) {
                    Identifier identifier2 = UNKNOWN_PACK;
                    return identifier2;
                }
                String string = resourcePackProfile.getId();
                Identifier identifier3 = Identifier.ofVanilla((String)("pack/" + Util.replaceInvalidChars((String)string, Identifier::isPathCharacterValid) + "/" + String.valueOf(Hashing.sha1().hashUnencodedChars((CharSequence)string)) + "/icon"));
                InputStream inputStream = (InputStream)inputSupplier.get();
                try {
                    NativeImage nativeImage = NativeImage.read((InputStream)inputStream);
                    textureManager.registerTexture(identifier3, (AbstractTexture)new NativeImageBackedTexture(() -> ((Identifier)identifier3).toString(), nativeImage));
                    identifier = identifier3;
                    if (inputStream == null) break block16;
                }
                catch (Throwable throwable) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                inputStream.close();
            }
            return identifier;
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load icon from pack {}", (Object)resourcePackProfile.getId(), (Object)exception);
            return UNKNOWN_PACK;
        }
    }

    private Identifier getPackIconTexture(ResourcePackProfile resourcePackProfile) {
        return this.iconTextures.computeIfAbsent(resourcePackProfile.getId(), profileName -> this.loadPackIcon(this.client.getTextureManager(), resourcePackProfile));
    }
}

