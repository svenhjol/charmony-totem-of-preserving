package svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import svenhjol.charmony.totem_of_preserving.client.features.totem_of_preserving.Tooltip;

import java.util.Optional;

public class TotemItem extends Item {
    public TotemItem(ResourceKey<Item> key) {
        super(new Properties()
            .stacksTo(1)
            .durability(TotemOfPreserving.feature().durability())
            .repairable(Items.ECHO_SHARD)
            .rarity(Rarity.UNCOMMON)
            .setId(key));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        var data = TotemData.get(stack);
        return data.glint();
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return TotemOfPreserving.feature().handlers.useTotemInHand(level, player, hand);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack totem) {
        var data = TotemData.get(totem);
        if (data.items().isEmpty()) {
            return Optional.empty();
        }
        NonNullList<ItemStack> items = NonNullList.create();
        items.addAll(data.items());
        return Optional.of(new Tooltip(items));
    }

    public static void setGlint(ItemStack totem, boolean flag) {
        var mutable = TotemData.mutable(totem);
        mutable.setGlint(flag);
        TotemData.set(totem, mutable);
    }
}
