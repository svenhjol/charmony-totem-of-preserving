package svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import svenhjol.charmony.api.tweaks.TotemInventoryCheckProvider;
import svenhjol.charmony.api.tweaks.TotemPreservingProvider;
import svenhjol.charmony.api.tweaks.TotemType;
import svenhjol.charmony.core.Api;
import svenhjol.charmony.core.base.Setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Providers extends Setup<TotemOfPreserving> implements
    TotemPreservingProvider,
    TotemInventoryCheckProvider
{
    public Providers(TotemOfPreserving feature) {
        super(feature);
        Api.registerProvider(this);
    }

    @Override
    public List<ItemStack> getInventoryItemsForTotem(Player player) {
        var inventory = player.getInventory();

        List<ItemStack> out = new ArrayList<>(inventory.items);

        for (EquipmentSlot slot : EquipmentSlotGroup.ARMOR) {
            out.add(player.getItemBySlot(slot));
        }

        out.add(player.getItemBySlot(EquipmentSlot.OFFHAND));

        return out;
    }

    @Override
    public void deleteInventoryItems(Player player) {
        player.getInventory().items.clear();
        player.equipment.clear();
    }

    @Override
    public Optional<ItemStack> findTotemFromInventory(Player player, TotemType totemType) {
        if (totemType == TotemType.Preserving) {
            var totem = feature().registers.item.get();

            var mainHand = player.getMainHandItem();
            if (mainHand.is(totem)) {
                return Optional.of(mainHand);
            }

            var offHand = player.getOffhandItem();
            if (offHand.is(totem)) {
                return Optional.of(offHand);
            }

            for (var item : player.getInventory().items) {
                if (item.is(totem)) {
                    return Optional.of(item);
                }
            }
        }

        return Optional.empty();
    }
}
