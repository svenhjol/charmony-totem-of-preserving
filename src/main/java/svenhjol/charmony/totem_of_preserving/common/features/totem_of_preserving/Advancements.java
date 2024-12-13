package svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving;

import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helper.AdvancementHelper;

public final class Advancements extends Setup<TotemOfPreserving> {
    public Advancements(TotemOfPreserving feature) {
        super(feature);
    }

    public void usedTotem(Player player) {
        AdvancementHelper.trigger("used_totem_of_preserving", player);
    }
}
