package svenhjol.charmony.totem_of_preserving.client.features.totem;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.client.ClientRegistry;

public final class Registers extends Setup<Totem> {
    public Registers(Totem feature) {
        super(feature);
        var registry = ClientRegistry.forFeature(feature);

        registry.blockEntityRenderer(
            feature().common.get().registers.blockEntity,
            () -> TotemRenderer::new);

        registry.blockRenderType(
            feature().common.get().registers.block,
            RenderType::cutout);
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
