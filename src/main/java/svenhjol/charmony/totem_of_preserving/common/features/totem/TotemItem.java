package svenhjol.charmony.totem_of_preserving.common.features.totem;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import svenhjol.charmony.totem_of_preserving.client.features.totem.Tooltip;

import java.util.List;
import java.util.Optional;

public class TotemItem extends Item {
    public TotemItem(ResourceKey<Item> key) {
        super(new Properties()
            .stacksTo(1)
            .durability(Totem.feature().durability())
            .repairable(Items.ECHO_SHARD)
            .rarity(Rarity.UNCOMMON)
            .setId(key));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(DataComponents.ENCHANTABLE);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var player = context.getPlayer();
        var hand = context.getHand();

        if (player != null) {
            return Totem.feature().handlers.useTotemInHand(level, player, hand);
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack totem, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        var data = TotemData.get(totem);

        if (!data.message().isEmpty()) {
            tooltip.add(Component.literal(data.message()));
        }

        if (!data.items().isEmpty()) {
            var size = data.items().size();
            var str = size == 1 ? "totem_of_preserving.item" : "totem_of_preserving.items";
            tooltip.add(Component.literal(I18n.get(str, size)));
        }

        super.appendHoverText(totem, context, tooltip, flag);
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
