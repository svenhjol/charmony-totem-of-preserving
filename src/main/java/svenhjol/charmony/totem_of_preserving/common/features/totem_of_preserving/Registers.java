package svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import svenhjol.charmony.api.events.AnvilEvents;
import svenhjol.charmony.api.events.PlayerKilledDropCallback;
import svenhjol.charmony.api.tweaks.TotemInventoryCheckProvider;
import svenhjol.charmony.api.tweaks.TotemPreservingProvider;
import svenhjol.charmony.core.Api;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class Registers extends Setup<TotemOfPreserving> {
    public final Supplier<DataComponentType<TotemData>> data;
    public final Supplier<SoundEvent> releaseSound;
    public final Supplier<SoundEvent> storeSound;

    public final Supplier<TotemItem> item;
    public final Supplier<TotemBlock> block;
    public final Supplier<BlockEntityType<TotemBlockEntity>> blockEntity;

    public final List<TotemPreservingProvider> preservingProviders = new ArrayList<>();
    public final List<TotemInventoryCheckProvider> inventoryCheckProviders = new ArrayList<>();

    public Registers(TotemOfPreserving feature) {
        super(feature);
        var registry = CommonRegistry.forFeature(feature);

        data = registry.dataComponent("totem_of_preserving",
            () -> builder -> builder
                .persistent(TotemData.CODEC)
                .networkSynchronized(TotemData.STREAM_CODEC));

        registry.dataComponentTooltipProvider(data);

        block = registry.block("totem_of_preserving_holder", TotemBlock::new);
        item = registry.item("totem_of_preserving", TotemItem::new);

        blockEntity = registry.blockEntity("totem_block", () -> TotemBlockEntity::new, List.of(block));

        releaseSound = registry.sound("release_totem_items");
        storeSound = registry.sound("store_totem_items");
    }

    @Override
    public Runnable boot() {
        return () -> {
            Api.consume(TotemPreservingProvider.class, preservingProviders::add);
            Api.consume(TotemInventoryCheckProvider.class, inventoryCheckProviders::add);

            PlayerKilledDropCallback.EVENT.register(feature().handlers::playerInventoryDrop);
            AnvilEvents.UPDATE.handle(feature().handlers::anvilUpdate);
        };
    }
}
