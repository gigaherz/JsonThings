package dev.gigaherz.jsonthings.things.fluids;

import com.google.common.collect.Maps;
import dev.gigaherz.jsonthings.things.IFlexFluid;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.fluids.FluidAttributes;

import java.util.Map;
import java.util.function.Supplier;

public class FlexFluid extends Fluid implements IFlexFluid
{
    public FlexFluid(Map<Property<?>, Comparable<?>> propertyDefaultValues)
    {
        initializeFlex(propertyDefaultValues);
    }

    //region IFlexFluid
    private final Map<String, FlexEventHandler> eventHandlers = Maps.newHashMap();

    private Supplier<Item> bucketItem;
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
