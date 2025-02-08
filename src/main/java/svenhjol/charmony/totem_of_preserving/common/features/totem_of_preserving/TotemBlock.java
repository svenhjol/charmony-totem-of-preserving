package svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class TotemBlock extends BaseEntityBlock {
    private static final MapCodec<TotemBlock> CODEC = simpleCodec(TotemBlock::new);
    private static final VoxelShape SHAPE = Block.box(2, 2, 2, 14, 14, 14);

    public TotemBlock(ResourceKey<Block> key) {
        this(Properties.ofFullCopy(Blocks.GLASS)
            .strength(-1.0f, 3600000.0f)
            .noCollission()
            .noOcclusion()
            .noLootTable()
            .setId(key));
    }

    private TotemBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TotemBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void onExplosionHit(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer) {
        // don't.
    }

    /**
     * Executed when the game wants to delete a totem block.
     */
    @Override
    public void affectNeighborsAfterRemoval(BlockState blockState, ServerLevel level, BlockPos pos, boolean bl) {
        TotemOfPreserving.feature().handlers.serverWantsToRemoveBlock(level, pos);
        super.affectNeighborsAfterRemoval(blockState, level, pos, bl);
    }

    /**
     * Executed when an entity is within the block bounds.
     */
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        TotemOfPreserving.feature().handlers.playerEnteredBlock(level, pos, entity);
        super.entityInside(state, level, pos, entity);
    }
}
