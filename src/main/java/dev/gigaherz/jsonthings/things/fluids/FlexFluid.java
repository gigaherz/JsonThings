package dev.gigaherz.jsonthings.things.fluids;

import com.google.common.collect.Maps;
import dev.gigaherz.jsonthings.things.IFlexFluid;
import dev.gigaherz.jsonthings.things.builders.FluidBuilder;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.FlexEventType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FlexFluid extends Fluid implements IFlexFluid
{
    public FlexFluid(FluidBuilder builder)
    {
        this.fluidType = builder.getAttributesType();
        this.properties = builder.getProperties();

        super();

        initializeFlex(builder.getPropertyDefaultValues());
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
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder)
    {
        super.createFluidStateDefinition(builder);
        properties.forEach(builder::add);
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
    protected boolean canBeReplacedWith(FluidState pFluidState, BlockGetter pBlockReader, BlockPos pPos, Fluid pFluid, Direction pDirection)
    {
        return true;
    }

    @Override
    public Vec3 getFlow(BlockGetter pBlockReader, BlockPos pPos, FluidState pFluidState)
    {
        return Vec3.ZERO;
    }

    @Override
    public int getTickDelay(LevelReader level)
    {
        return 0;
    }

    @Override
    protected float getExplosionResistance()
    {
        return 0;
    }

    @Override
    public float getHeight(FluidState pState, BlockGetter level, BlockPos pos)
    {
        return 0;
    }

    @Override
    public float getOwnHeight(FluidState pState)
    {
        return 0;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState pState)
    {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState pState)
    {
        return true;
    }

    @Override
    public int getAmount(FluidState pState)
    {
        return 0;
    }

    @Override
    public VoxelShape getShape(FluidState pState, BlockGetter level, BlockPos pos)
    {
        return Shapes.empty();
    }
    //endregion
}
