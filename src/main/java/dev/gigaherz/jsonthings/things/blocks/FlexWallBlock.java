package dev.gigaherz.jsonthings.things.blocks;

import com.google.common.collect.Maps;
import dev.gigaherz.jsonthings.things.IFlexBlock;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.Property;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;

public class FlexWallBlock extends WallBlock implements IFlexBlock
{
    public FlexWallBlock(Properties properties, Map<Property<?>, Comparable<?>> propertyDefaultValues)
    {
        super(properties);
        initializeFlex(propertyDefaultValues);
    }

    //region IFlexBlock
    private DynamicShape generalShape;
    private DynamicShape collisionShape;
    private DynamicShape raytraceShape;
    private DynamicShape renderShape;
    private final Map<String, FlexEventHandler<ActionResultType>> eventHandlers = Maps.newHashMap();

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initializeFlex(Map<Property<?>, Comparable<?>> propertyDefaultValues)
    {
        if (propertyDefaultValues.size() > 0)
        {
            BlockState def = getStateDefinition().any();
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
    public void addEventHandler(String eventName, FlexEventHandler<ActionResultType> eventHandler)
    {
        eventHandlers.put(eventName, eventHandler);
    }

    @Override
    public FlexEventHandler<ActionResultType> getEventHandler(String eventName)
    {
        return eventHandlers.get(eventName);
    }

    @Override
    public void setGeneralShape(@Nullable DynamicShape shape)
    {
        this.generalShape = shape;
    }

    @Override
    public void setCollisionShape(@Nullable DynamicShape shape)
    {
        this.collisionShape = shape;
    }

    @Override
    public void setRaytraceShape(@Nullable DynamicShape shape)
    {
        this.raytraceShape = shape;
    }

    @Override
    public void setRenderShape(@Nullable DynamicShape shape)
    {
        this.renderShape = shape;
    }
    //endregion

    //region Block
    @Deprecated
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if (this.generalShape != null)
            return generalShape.getShape(state);
        return super.getShape(state, worldIn, pos, context);
    }

    @Deprecated
    @Override
    public VoxelShape getInteractionShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        if (this.raytraceShape != null)
            return raytraceShape.getShape(state);
        return super.getInteractionShape(state, worldIn, pos);
    }

    @Deprecated
    @Override
    public VoxelShape getBlockSupportShape(BlockState state, IBlockReader reader, BlockPos pos)
    {
        if (this.collisionShape != null)
            return collisionShape.getShape(state);
        return super.getBlockSupportShape(state, reader, pos);
    }

    @Deprecated
    @Override
    public VoxelShape getOcclusionShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        if (this.renderShape != null)
            return renderShape.getShape(state);
        return super.getOcclusionShape(state, worldIn, pos);
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        return runEvent("use", FlexEventContext.of(worldIn, pos, state)
                .withHand(player, handIn)
                .withRayTrace(hit), () -> super.use(state, worldIn, pos, player, handIn, hit));
    }

    //endregion
}
