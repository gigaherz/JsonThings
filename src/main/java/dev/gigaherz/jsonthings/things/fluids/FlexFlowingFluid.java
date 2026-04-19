package dev.gigaherz.jsonthings.things.fluids;

import com.google.common.collect.Maps;
import dev.gigaherz.jsonthings.things.IFlexFluid;
import dev.gigaherz.jsonthings.things.builders.FluidBuilder;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.FlexEventType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class FlexFlowingFluid extends FlowingFluid implements IFlexFluid
{
    private final Fluid flowing;
    private final int slopeDistance;
    private final int dropOff;
    private final boolean canConvertToSource;
    private final int tickDelay;
    private final float explosionResistance;
    private final Supplier<Block> block;

    public FlexFlowingFluid(FluidBuilder builder, int slopeDistance, int dropOff, boolean canConvertToSource,
                            int tickDelay, float explosionResistance, Supplier<Block> block)
    {
        this.fluidType = builder.getAttributesType();
        this.properties = builder.getProperties();

        super();

        this.slopeDistance = slopeDistance;
        this.dropOff = dropOff;
        this.canConvertToSource = canConvertToSource;
        this.tickDelay = tickDelay;
        this.explosionResistance = explosionResistance;
        this.block = block;

        var propertyDefaultValues = builder.getPropertyDefaultValues();

        initializeFlex(propertyDefaultValues);

        this.flowing = new Flowing(propertyDefaultValues, this.properties);
    }

    //region IFlexFluid
    @SuppressWarnings("rawtypes")
    private final Map<FlexEventType, FlexEventHandler> eventHandlers = Maps.newHashMap();
    private final Supplier<FluidType> fluidType;
    private final List<Property<?>> properties;
    private Supplier<Item> bucketItem = () -> Items.AIR;

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initializeFlex(Map<Property<?>, Comparable<?>> propertyDefaultValues)
    {
        if (!propertyDefaultValues.isEmpty())
        {
            FluidState def = getStateDefinition().any();
            for (Map.Entry<Property<?>, Comparable<?>> entry : propertyDefaultValues.entrySet())
            {
                Property prop = entry.getKey();
                Comparable value = entry.getValue();
                def = def.setValue(prop, value);
            }

            registerDefaultState(def);
        }
    }

    @Override
    public <T> void addEventHandler(FlexEventType<T> event, FlexEventHandler<T> eventHandler)
    {
        eventHandlers.put(event, eventHandler);
    }

    @Override
    public <T> FlexEventHandler<T> getEventHandler(FlexEventType<T> event)
    {
        //noinspection unchecked
        return eventHandlers.get(event);
    }

    @Override
    public void setBucketItem(Supplier<Item> bucketItem)
    {
        this.bucketItem = bucketItem;
    }

    //endregion

    //region Fluid
    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder1)
    {
        super.createFluidStateDefinition(builder1);
        properties.forEach(builder1::add);
    }

    @Override
    public FluidType getFluidType()
    {
        return this.fluidType.get();
    }

    @Override
    public Item getBucket()
    {
        return bucketItem != null ? bucketItem.get() : Items.AIR;
    }

    @Override
    public boolean isSame(Fluid pFluid)
    {
        return pFluid == getSource() || pFluid == getFlowing();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState pFluidState, BlockGetter pBlockReader, BlockPos pPos, Fluid pFluid, Direction pDirection)
    {
        return pDirection == Direction.DOWN && !isSame(pFluid);
    }

    @Override
    public int getTickDelay(LevelReader level)
    {
        return tickDelay;
    }

    @Override
    protected float getExplosionResistance()
    {
        return explosionResistance;
    }

    public BlockState createLegacyBlock(FluidState pState)
    {
        var b = block.get();
        if (b == Blocks.AIR)
            return b.defaultBlockState();
        return b.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(pState));
    }

    @Override
    public int getAmount(FluidState state)
    {
        return 8;
    }

    @Override
    public boolean isSource(FluidState state)
    {
        return true;
    }
    //endregion

    //region FlowingFluid
    @Override
    public Fluid getFlowing()
    {
        return flowing;
    }

    @Override
    public Fluid getSource()
    {
        return this;
    }

    @Deprecated
    @Override
    protected boolean canConvertToSource(ServerLevel level)
    {
        return canConvertToSource;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor pLevel, BlockPos pPos, BlockState pState)
    {
        InteractionResult result = InteractionResult.PASS;
        if (pLevel instanceof Level level)
        {
            result = runEvent(FlexEventType.BEFORE_DESTROY, FlexEventContext.of(level, pPos, pState), () -> InteractionResult.PASS);
        }
        if (result == InteractionResult.PASS)
        {
            BlockEntity blockentity = pState.hasBlockEntity() ? pLevel.getBlockEntity(pPos) : null;
            Block.dropResources(pState, pLevel, pPos, blockentity);
        }
    }

    @Override
    protected int getSlopeFindDistance(LevelReader pLevel)
    {
        return slopeDistance;
    }

    @Override
    protected int getDropOff(LevelReader pLevel)
    {
        return dropOff;
    }

    @Nonnull
    @Override
    public Optional<SoundEvent> getPickupSound()
    {
        return Optional.ofNullable(getFluidType().getSound(SoundActions.BUCKET_FILL));
    }

    //endregion

    public class Flowing extends FlowingFluid
    {
        private final List<Property<?>> properties;

        public Flowing(Map<Property<?>, Comparable<?>> propertyDefaultValues, List<Property<?>> properties)
        {
            this.properties = properties;

            super();

            initializeFlex(propertyDefaultValues);
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder)
        {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
            properties.forEach(builder::add);
        }

        @Override
        public boolean isSame(Fluid pFluid)
        {
            return pFluid == getSource() || pFluid == getFlowing();
        }

        public int getAmount(FluidState state)
        {
            return state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state)
        {
            return false;
        }

        private void initializeFlex(Map<Property<?>, Comparable<?>> propertyDefaultValues)
        {
            if (propertyDefaultValues.size() > 0)
            {
                FluidState def = getStateDefinition().any();
                for (Map.Entry<Property<?>, Comparable<?>> entry : propertyDefaultValues.entrySet())
                {
                    Property prop = entry.getKey();
                    Comparable value = entry.getValue();
                    def = def.setValue(prop, value);
                }

                registerDefaultState(def);
            }
        }

        @Override
        public FluidType getFluidType()
        {
            return FlexFlowingFluid.this.getFluidType();
        }

        @Override
        public Item getBucket()
        {
            return FlexFlowingFluid.this.getBucket();
        }

        @Override
        protected boolean canBeReplacedWith(FluidState pFluidState, BlockGetter pBlockReader, BlockPos pPos, Fluid pFluid, Direction pDirection)
        {
            return pDirection == Direction.DOWN && !isSame(pFluid);
        }

        @Override
        public int getTickDelay(LevelReader p_76120_)
        {
            return FlexFlowingFluid.this.getTickDelay(p_76120_);
        }

        @Override
        protected float getExplosionResistance()
        {
            return FlexFlowingFluid.this.getExplosionResistance();
        }

        @Override
        protected BlockState createLegacyBlock(FluidState pState)
        {
            return FlexFlowingFluid.this.createLegacyBlock(pState);
        }

        @Override
        public Fluid getFlowing()
        {
            return FlexFlowingFluid.this.getFlowing();
        }

        @Override
        public Fluid getSource()
        {
            return FlexFlowingFluid.this.getSource();
        }

        @Deprecated
        @Override
        protected boolean canConvertToSource(ServerLevel level)
        {
            return FlexFlowingFluid.this.canConvertToSource(level);
        }

        @Override
        protected void beforeDestroyingBlock(LevelAccessor pLevel, BlockPos pPos, BlockState pState)
        {
            FlexFlowingFluid.this.beforeDestroyingBlock(pLevel, pPos, pState);
        }

        @Override
        protected int getSlopeFindDistance(LevelReader pLevel)
        {
            return FlexFlowingFluid.this.getSlopeFindDistance(pLevel);
        }

        @Override
        protected int getDropOff(LevelReader pLevel)
        {
            return FlexFlowingFluid.this.getDropOff(pLevel);
        }
    }
}
