package svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TotemBlockEntity extends BlockEntity {
    private static final String OWNER_TAG = "owner";
    private static final String MESSAGE_TAG = "message";
    private static final String DAMAGE_TAG = "damage";

    private List<ItemStack> items = new ArrayList<>();
    private String message;
    private UUID owner;
    private float rotateTicks = 0f;
    private int damage = 0;

    public TotemBlockEntity(BlockPos pos, BlockState state) {
        super(TotemOfPreserving.feature().registers.blockEntity.get(), pos, state);
    }

    public float getRotateTicks() {
        return rotateTicks;
    }

    public void setRotateTicks(float rotateTicks) {
        this.rotateTicks = rotateTicks;
    }

    // NonNullLists don't support addAll()
    @SuppressWarnings("UseBulkOperation")
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        items.clear();

        // This is dumb but I can't think of another way to get the initial size for the nonnulllist.
        var listTag = tag.getList(ContainerHelper.TAG_ITEMS, 10);
        var size = listTag.size();

        NonNullList<ItemStack> finalItems = NonNullList.withSize(size, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, finalItems, provider);
        finalItems.forEach(items::add);

        message = tag.getString(MESSAGE_TAG);
        owner = tag.read(OWNER_TAG, UUIDUtil.CODEC).orElse(null);
        damage = tag.getInt(DAMAGE_TAG);
    }

    // NonNullLists don't support addAll()
    @SuppressWarnings("UseBulkOperation")
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        NonNullList<ItemStack> finalItems = NonNullList.create();
        items.forEach(finalItems::add);

        ContainerHelper.saveAllItems(tag, finalItems, true, provider);
        tag.putString(MESSAGE_TAG, message);
        tag.store(OWNER_TAG, UUIDUtil.CODEC, owner);
        tag.putInt(DAMAGE_TAG, damage);
    }

    public void setDirty() {
        var blockState = this.getBlockState();
        if (level != null) {
            level.gameEvent(GameEvent.BLOCK_CHANGE, this.worldPosition, GameEvent.Context.of(blockState));
        }
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public String getMessage() {
        return message;
    }

    public UUID getOwner() {
        return owner;
    }

    public int getDamage() {
        return damage;
    }
}
