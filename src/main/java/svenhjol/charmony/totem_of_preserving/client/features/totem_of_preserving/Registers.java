package svenhjol.charmony.totem_of_preserving.client.features.totem_of_preserving;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.client.ClientRegistry;

public final class Registers extends Setup<TotemOfPreserving> {
    public Registers(TotemOfPreserving feature) {
        super(feature);
        var registry = ClientRegistry.forFeature(feature);
        var common = feature.common.get();

        registry.blockEntityRenderer(
            common.registers.blockEntity.get(),
            TotemRenderer::new);

        registry.blockRenderType(
            common.registers.block.get(),
            ChunkSectionLayer.CUTOUT);
    }

    @Override
    public Runnable boot() {
        return () -> {
            ClientRegistry.forFeature(feature()).itemTab(
                feature().common.get().registers.item.get(),
                CreativeModeTabs.COMBAT,
                Items.TOTEM_OF_UNDYING
            );
        };
    }
}
