package svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TotemBlockEntity extends BlockEntity {
    private static final String COUNT_TAG = "count";
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
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        items.clear();

        var count = valueInput.getIntOr(COUNT_TAG, 0);

        message = valueInput.getString(MESSAGE_TAG).orElse("");
        owner = valueInput.read(OWNER_TAG, UUIDUtil.CODEC).orElse(null);
        damage = valueInput.getInt(DAMAGE_TAG).orElse(0);

        NonNullList<ItemStack> finalItems = NonNullList.withSize(count, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(valueInput, finalItems);
        finalItems.forEach(items::add);
    }

    // NonNullLists don't support addAll()
    @SuppressWarnings("UseBulkOperation")
    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        NonNullList<ItemStack> finalItems = NonNullList.create();
        items.forEach(finalItems::add);

        ContainerHelper.saveAllItems(valueOutput, finalItems, true);

        valueOutput.putInt(COUNT_TAG, items.size());
        valueOutput.putString(MESSAGE_TAG, message);
        valueOutput.store(OWNER_TAG, UUIDUtil.CODEC, owner);
        valueOutput.putInt(DAMAGE_TAG, damage);
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
