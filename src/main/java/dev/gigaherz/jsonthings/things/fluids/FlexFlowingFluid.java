package dev.gigaherz.jsonthings.things.fluids;

import com.google.common.collect.Maps;
import dev.gigaherz.jsonthings.things.IFlexFluid;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.FlexEventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.fluids.FluidAttributes;

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

    public FlexFlowingFluid(List<Property<?>> properties, Map<Property<?>, Comparable<?>> propertyDefaultValues,
                            int slopeDistance, int dropOff, boolean canConvertToSource, int tickDelay, float explosionResistance,
                            Supplier<Block> block)
    {
        this.slopeDistance = slopeDistance;
        this.dropOff = dropOff;
        this.canConvertToSource = canConvertToSource;
        this.tickDelay = tickDelay;
        this.explosionResistance = explosionResistance;
        this.block = block;

        initializeFlex(propertyDefaultValues);

        flowing = new Flowing(this, propertyDefaultValues) {
            @Override
            protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder1)
            {
                super.createFluidStateDefinition(builder1);
                properties.forEach(builder1::add);
            }
        };
    }

    //region IFlexFluid
    private final Map<String, FlexEventHandler> eventHandlers = Maps.newHashMap();

    private Supplier<Item> bucketItem = () -> Items.AIR;
    private NonNullLazy<FluidAttributes> attributesBuilder;

    @SuppressWarnings({"rawtypes", "unchecked"})
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
    public void addEventHandler(String eventName, FlexEventHandler eventHandler)
    {
        eventHandlers.put(eventName, eventHandler);
    }

    @Override
    public FlexEventHandler getEventHandler(String eventName)
    {
        return eventHandlers.get(eventName);
    }

    @Override
    public void setBucketItem(Supplier<Item> bucketItem)
    {
        this.bucketItem = bucketItem;
    }

    @Override
    public void setAttributesBuilder(NonNullLazy<FluidAttributes> attrsBuilder)
    {
        attributesBuilder = attrsBuilder;
    }

    //endregion

    //region Fluid
    @Override
    protected FluidAttributes createAttributes()
    {
        return attributesBuilder.get();
    }

    @Override
    public Item getBucket()
    {
        return bucketItem.get();
    }

    @Override
    public boolean isSame(Fluid pFluid) {
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

    public BlockState createLegacyBlock(FluidState pState) {
        return block.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(pState));
    }

    @Override
    public int getAmount(FluidState state) {
        return 8;
    }

    @Override
    public boolean isSource(FluidState state) {
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

    @Override
    protected boolean canConvertToSource()
    {
        return canConvertToSource;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor pLevel, BlockPos pPos, BlockState pState)
    {
        InteractionResult result = InteractionResult.PASS;
        if (pLevel instanceof Level level)
        {
            result = runEvent("before_destroy", FlexEventContext.of(level, pPos, pState), FlexEventResult::pass).result();
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
        return Optional.ofNullable(getAttributes().getFillSound());
    }

    //endregion

    public static class Flowing extends FlowingFluid
    {
        private final FlexFlowingFluid parent;

        public Flowing(FlexFlowingFluid parent, Map<Property<?>, Comparable<?>> propertyDefaultValues)
        {
            this.parent = parent;
            initializeFlex(propertyDefaultValues);
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isSame(Fluid pFluid) {
            return pFluid == getSource() || pFluid == getFlowing();
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state) {
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
        protected FluidAttributes createAttributes()
        {
            return parent.createAttributes();
        }

        @Override
        public Item getBucket()
        {
            return parent.getBucket();
        }

        @Override
        protected boolean canBeReplacedWith(FluidState pFluidState, BlockGetter pBlockReader, BlockPos pPos, Fluid pFluid, Direction pDirection)
        {
            return pDirection == Direction.DOWN && !isSame(pFluid);
        }

        @Override
        public int getTickDelay(LevelReader p_76120_)
        {
            return parent.getTickDelay(p_76120_);
        }

        @Override
        protected float getExplosionResistance()
        {
            return parent.getExplosionResistance();
        }

        @Override
        protected BlockState createLegacyBlock(FluidState pState)
        {
            return parent.createLegacyBlock(pState);
        }

        @Override
        public Fluid getFlowing()
        {
            return parent.getFlowing();
        }

        @Override
        public Fluid getSource()
        {
            return parent.getSource();
        }

        @Override
        protected boolean canConvertToSource()
        {
            return parent.canConvertToSource();
        }

        @Override
        protected void beforeDestroyingBlock(LevelAccessor pLevel, BlockPos pPos, BlockState pState)
        {
            parent.beforeDestroyingBlock(pLevel, pPos, pState);
        }

        @Override
        protected int getSlopeFindDistance(LevelReader pLevel)
        {
            return parent.getSlopeFindDistance(pLevel);
        }

        @Override
        protected int getDropOff(LevelReader pLevel)
        {
            return parent.getDropOff(pLevel);
        }
    }
}
