/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.resource.DefaultClientResourcePackProvider
 *  net.minecraft.registry.VersionedIdentifier
 *  net.minecraft.resource.DefaultResourcePack
 *  net.minecraft.resource.DefaultResourcePackBuilder
 *  net.minecraft.resource.ResourcePack
 *  net.minecraft.resource.ResourcePackInfo
 *  net.minecraft.resource.ResourcePackPosition
 *  net.minecraft.resource.ResourcePackProfile
 *  net.minecraft.resource.ResourcePackProfile$InsertionPosition
 *  net.minecraft.resource.ResourcePackProfile$PackFactory
 *  net.minecraft.resource.ResourcePackSource
 *  net.minecraft.resource.ResourceType
 *  net.minecraft.resource.VanillaResourcePackProvider
 *  net.minecraft.resource.metadata.PackResourceMetadata
 *  net.minecraft.resource.metadata.ResourceMetadataMap
 *  net.minecraft.resource.metadata.ResourceMetadataSerializer
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.path.SymlinkFinder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.resource;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.DefaultResourcePackBuilder;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackPosition;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaResourcePackProvider;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataMap;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.path.SymlinkFinder;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DefaultClientResourcePackProvider
extends VanillaResourcePackProvider {
    private static final PackResourceMetadata METADATA = new PackResourceMetadata((Text)Text.translatable((String)"resourcePack.vanilla.description"), SharedConstants.getGameVersion().packVersion(ResourceType.CLIENT_RESOURCES).majorRange());
    private static final ResourceMetadataMap METADATA_MAP = ResourceMetadataMap.of((ResourceMetadataSerializer)PackResourceMetadata.CLIENT_RESOURCES_SERIALIZER, (Object)METADATA);
    public static final String HIGH_CONTRAST_ID = "high_contrast";
    private static final Map<String, Text> PROFILE_NAME_TEXTS = Map.of("programmer_art", Text.translatable((String)"resourcePack.programmer_art.name"), "high_contrast", Text.translatable((String)"resourcePack.high_contrast.name"));
    private static final ResourcePackInfo INFO = new ResourcePackInfo("vanilla", (Text)Text.translatable((String)"resourcePack.vanilla.name"), ResourcePackSource.BUILTIN, Optional.of(VANILLA_ID));
    private static final ResourcePackPosition REQUIRED_POSITION = new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.BOTTOM, false);
    private static final ResourcePackPosition OPTIONAL_POSITION = new ResourcePackPosition(false, ResourcePackProfile.InsertionPosition.TOP, false);
    private static final Identifier ID = Identifier.ofVanilla((String)"resourcepacks");
    private final @Nullable Path resourcePacksPath;

    public DefaultClientResourcePackProvider(Path assetsPath, SymlinkFinder symlinkFinder) {
        super(ResourceType.CLIENT_RESOURCES, DefaultClientResourcePackProvider.createDefaultPack((Path)assetsPath), ID, symlinkFinder);
        this.resourcePacksPath = this.getResourcePacksPath(assetsPath);
    }

    private static ResourcePackInfo createInfo(String id, Text title) {
        return new ResourcePackInfo(id, title, ResourcePackSource.BUILTIN, Optional.of(VersionedIdentifier.createVanilla((String)id)));
    }

    private @Nullable Path getResourcePacksPath(Path path) {
        Path path2;
        if (SharedConstants.isDevelopment && path.getFileSystem() == FileSystems.getDefault() && Files.isDirectory(path2 = path.getParent().resolve("resourcepacks"), new LinkOption[0])) {
            return path2;
        }
        return null;
    }

    private static DefaultResourcePack createDefaultPack(Path assetsPath) {
        DefaultResourcePackBuilder defaultResourcePackBuilder = new DefaultResourcePackBuilder().withMetadataMap(METADATA_MAP).withNamespaces(new String[]{"minecraft", "realms"});
        return defaultResourcePackBuilder.runCallback().withDefaultPaths().withPath(ResourceType.CLIENT_RESOURCES, assetsPath).build(INFO);
    }

    protected Text getDisplayName(String id) {
        Text text = (Text)PROFILE_NAME_TEXTS.get(id);
        return text != null ? text : Text.literal((String)id);
    }

    protected @Nullable ResourcePackProfile createDefault(ResourcePack pack) {
        return ResourcePackProfile.create((ResourcePackInfo)INFO, (ResourcePackProfile.PackFactory)DefaultClientResourcePackProvider.createPackFactory((ResourcePack)pack), (ResourceType)ResourceType.CLIENT_RESOURCES, (ResourcePackPosition)REQUIRED_POSITION);
    }

    protected @Nullable ResourcePackProfile create(String fileName, ResourcePackProfile.PackFactory packFactory, Text displayName) {
        return ResourcePackProfile.create((ResourcePackInfo)DefaultClientResourcePackProvider.createInfo((String)fileName, (Text)displayName), (ResourcePackProfile.PackFactory)packFactory, (ResourceType)ResourceType.CLIENT_RESOURCES, (ResourcePackPosition)OPTIONAL_POSITION);
    }

    protected void forEachProfile(BiConsumer<String, Function<String, ResourcePackProfile>> consumer) {
        super.forEachProfile(consumer);
        if (this.resourcePacksPath != null) {
            this.forEachProfile(this.resourcePacksPath, consumer);
        }
    }
}

