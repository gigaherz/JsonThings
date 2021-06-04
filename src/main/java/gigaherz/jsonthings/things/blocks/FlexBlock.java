package gigaherz.jsonthings.things.blocks;

import com.google.common.collect.Maps;
import gigaherz.jsonthings.things.IFlexBlock;
import gigaherz.jsonthings.things.events.BlockEventHandler;
import gigaherz.jsonthings.things.events.FlexEventContext;
import gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

public class FlexBlock extends Block implements IFlexBlock
{
    public FlexBlock(Properties properties, Map<Property<?>, Comparable<?>> propertyDefaultValues)
    {
        super(properties);
        initializeFlex(propertyDefaultValues);
    }

    //region IFlexBlock
    private DynamicShape generalShape;
    private DynamicShape collisionShape;
    private DynamicShape raytraceShape;
    private DynamicShape renderShape;
    private final Map<String, BlockEventHandler> eventHandlers = Maps.newHashMap();

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initializeFlex(Map<Property<?>, Comparable<?>> propertyDefaultValues)
    {
        if (propertyDefaultValues.size() > 0)
        {
            BlockState def = getStateContainer().getBaseState();
            for (Map.Entry<Property<?>, Comparable<?>> entry : propertyDefaultValues.entrySet())
            {
                Property prop = entry.getKey();
                Comparable value = entry.getValue();
                def = def.with(prop, value);
            }

            setDefaultState(def);
        }
    }

    @Override
    public void addEventHandler(String eventName, BlockEventHandler eventHandler)
    {
        eventHandlers.put(eventName, eventHandler);
    }

    @Override
    public BlockEventHandler getEventHandler(String eventName)
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
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        if (this.raytraceShape != null)
            return raytraceShape.getShape(state);
        return super.getRaytraceShape(state, worldIn, pos);
    }

    @Deprecated
    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos)
    {
        if (this.collisionShape != null)
            return collisionShape.getShape(state);
        return super.getCollisionShape(state, reader, pos);
    }

    @Deprecated
    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        if (this.renderShape != null)
            return renderShape.getShape(state);
        return super.getRenderShape(state, worldIn, pos);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        return runEvent("use", FlexEventContext.of(worldIn, pos, state)
                .with(FlexEventContext.USER, player)
                .with(FlexEventContext.HAND, handIn)
                .withRayTrace(hit), () -> super.onBlockActivated(state, worldIn, pos, player, handIn, hit));
    }

    //endregion
}
