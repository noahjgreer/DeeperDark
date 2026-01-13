/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.LecternBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.LecternBlockEntity
 *  net.minecraft.command.permission.LeveledPermissionPredicate
 *  net.minecraft.command.permission.PermissionPredicate
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.WritableBookContentComponent
 *  net.minecraft.component.type.WrittenBookContentComponent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.screen.LecternScreenHandler
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.PropertyDelegate
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.server.command.CommandOutput
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Clearable
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class LecternBlockEntity
extends BlockEntity
implements Clearable,
NamedScreenHandlerFactory {
    public static final int field_31348 = 0;
    public static final int field_31349 = 1;
    public static final int field_31350 = 0;
    public static final int field_31351 = 1;
    private final Inventory inventory = new /* Unavailable Anonymous Inner Class!! */;
    private final PropertyDelegate propertyDelegate = new /* Unavailable Anonymous Inner Class!! */;
    ItemStack book = ItemStack.EMPTY;
    int currentPage;
    private int pageCount;

    public LecternBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.LECTERN, pos, state);
    }

    public ItemStack getBook() {
        return this.book;
    }

    public boolean hasBook() {
        return this.book.contains(DataComponentTypes.WRITABLE_BOOK_CONTENT) || this.book.contains(DataComponentTypes.WRITTEN_BOOK_CONTENT);
    }

    public void setBook(ItemStack book) {
        this.setBook(book, null);
    }

    void onBookRemoved() {
        this.currentPage = 0;
        this.pageCount = 0;
        LecternBlock.setHasBook(null, (World)this.getWorld(), (BlockPos)this.getPos(), (BlockState)this.getCachedState(), (boolean)false);
    }

    public void setBook(ItemStack book, @Nullable PlayerEntity player) {
        this.book = this.resolveBook(book, player);
        this.currentPage = 0;
        this.pageCount = LecternBlockEntity.getPageCount((ItemStack)this.book);
        this.markDirty();
    }

    void setCurrentPage(int currentPage) {
        int i = MathHelper.clamp((int)currentPage, (int)0, (int)(this.pageCount - 1));
        if (i != this.currentPage) {
            this.currentPage = i;
            this.markDirty();
            LecternBlock.setPowered((World)this.getWorld(), (BlockPos)this.getPos(), (BlockState)this.getCachedState());
        }
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getComparatorOutput() {
        float f = this.pageCount > 1 ? (float)this.getCurrentPage() / ((float)this.pageCount - 1.0f) : 1.0f;
        return MathHelper.floor((float)(f * 14.0f)) + (this.hasBook() ? 1 : 0);
    }

    private ItemStack resolveBook(ItemStack book, @Nullable PlayerEntity player) {
        World world = this.world;
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            WrittenBookContentComponent.resolveInStack((ItemStack)book, (ServerCommandSource)this.getCommandSource(player, serverWorld), (PlayerEntity)player);
        }
        return book;
    }

    private ServerCommandSource getCommandSource(@Nullable PlayerEntity player, ServerWorld world) {
        MutableText text;
        String string;
        if (player == null) {
            string = "Lectern";
            text = Text.literal((String)"Lectern");
        } else {
            string = player.getStringifiedName();
            text = player.getDisplayName();
        }
        Vec3d vec3d = Vec3d.ofCenter((Vec3i)this.pos);
        return new ServerCommandSource(CommandOutput.DUMMY, vec3d, Vec2f.ZERO, world, (PermissionPredicate)LeveledPermissionPredicate.GAMEMASTERS, string, (Text)text, world.getServer(), (Entity)player);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.book = view.read("Book", ItemStack.CODEC).map(itemStack -> this.resolveBook(itemStack, null)).orElse(ItemStack.EMPTY);
        this.pageCount = LecternBlockEntity.getPageCount((ItemStack)this.book);
        this.currentPage = MathHelper.clamp((int)view.getInt("Page", 0), (int)0, (int)(this.pageCount - 1));
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        if (!this.getBook().isEmpty()) {
            view.put("Book", ItemStack.CODEC, (Object)this.getBook());
            view.putInt("Page", this.currentPage);
        }
    }

    public void clear() {
        this.setBook(ItemStack.EMPTY);
    }

    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        if (((Boolean)oldState.get((Property)LecternBlock.HAS_BOOK)).booleanValue() && this.world != null) {
            Direction direction = (Direction)oldState.get((Property)LecternBlock.FACING);
            ItemStack itemStack = this.getBook().copy();
            float f = 0.25f * (float)direction.getOffsetX();
            float g = 0.25f * (float)direction.getOffsetZ();
            ItemEntity itemEntity = new ItemEntity(this.world, (double)pos.getX() + 0.5 + (double)f, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5 + (double)g, itemStack);
            itemEntity.setToDefaultPickupDelay();
            this.world.spawnEntity((Entity)itemEntity);
        }
    }

    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new LecternScreenHandler(i, this.inventory, this.propertyDelegate);
    }

    public Text getDisplayName() {
        return Text.translatable((String)"container.lectern");
    }

    private static int getPageCount(ItemStack stack) {
        WrittenBookContentComponent writtenBookContentComponent = (WrittenBookContentComponent)stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
        if (writtenBookContentComponent != null) {
            return writtenBookContentComponent.pages().size();
        }
        WritableBookContentComponent writableBookContentComponent = (WritableBookContentComponent)stack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
        if (writableBookContentComponent != null) {
            return writableBookContentComponent.pages().size();
        }
        return 0;
    }
}

