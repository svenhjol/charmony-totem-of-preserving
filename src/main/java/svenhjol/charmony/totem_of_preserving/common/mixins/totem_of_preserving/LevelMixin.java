package svenhjol.charmony.totem_of_preserving.common.mixins.totem_of_preserving;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving.TotemOfPreserving;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Shadow
    public abstract ResourceKey<Level> dimension();

    @WrapMethod(
        method = "destroyBlock"
    )
    private boolean hookDestroyBlock(BlockPos pos, boolean bl, Entity entity, int i, Operation<Boolean> original) {
        if (isProtected(pos)) {
            return false;
        }
        return original.call(pos, bl, entity, i);
    }

    @WrapMethod(
        method = "removeBlock"
    )
    private boolean hookRemoveBlock(BlockPos pos, boolean bl, Operation<Boolean> original) {
        if (isProtected(pos)) {
            return false;
        }
        return original.call(pos, bl);
    }

    @WrapMethod(
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z"
    )
    private boolean hookSetBlock(BlockPos pos, BlockState state, int i, int j, Operation<Boolean> original) {
        if (isProtected(pos)) {
            return false;
        }
        return original.call(pos, state, i, j);
    }

    @Unique
    private boolean isProtected(BlockPos pos) {
        var dimension = this.dimension().location();
        var feature = TotemOfPreserving.feature();
        return feature.handlers.protectedPositions.containsKey(dimension)
            && feature.handlers.protectedPositions.get(dimension).contains(pos);
    }
}
