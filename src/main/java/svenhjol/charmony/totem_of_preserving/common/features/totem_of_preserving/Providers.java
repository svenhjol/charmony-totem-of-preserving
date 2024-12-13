package svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import svenhjol.charmony.api.TotemInventoryCheckProvider;
import svenhjol.charmony.api.TotemPreservingProvider;
import svenhjol.charmony.api.TotemType;
import svenhjol.charmony.core.Api;
import svenhjol.charmony.core.base.Setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Providers extends Setup<TotemOfPreserving> implements
    TotemPreservingProvider,
    TotemInventoryCheckProvider
{
    public final List<TotemPreservingProvider> preservingProviders = new ArrayList<>();
    public final List<TotemInventoryCheckProvider> inventoryCheckProviders = new ArrayList<>();

    public Providers(TotemOfPreserving feature) {
        super(feature);
    }

    @Override
    public List<ItemStack> getInventoryItemsForTotem(Player player) {
        List<ItemStack> out = new ArrayList<>();
        var inventory = player.getInventory();

        out.addAll(inventory.items);
        out.addAll(inventory.armor);
        out.addAll(inventory.offhand);

        return out;
    }

    @Override
    public void deleteInventoryItems(Player player) {
        var inventory = player.getInventory();

        inventory.items.clear();
        inventory.armor.clear();
        inventory.offhand.clear();
    }

    @Override
    public Optional<ItemStack> findTotemFromInventory(Player player, TotemType totemType) {
        if (totemType == TotemType.PRESERVING) {
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

    @Override
    public Runnable boot() {
        return () -> {
            Api.registerProvider(this);
            Api.consume(TotemPreservingProvider.class, preservingProviders::add);
            Api.consume(TotemInventoryCheckProvider.class, inventoryCheckProviders::add);
        };
    }
}
