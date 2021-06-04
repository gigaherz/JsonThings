package gigaherz.jsonthings.things.blocks;

import gigaherz.jsonthings.things.IFlexBlock;
import gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.state.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import java.util.Map;

public class FlexSlabBlock extends SlabBlock implements IFlexBlock
{
    public FlexSlabBlock(Properties properties, Map<Property<?>, Comparable<?>> propertyDefaultValues)
    {
        super(properties);
        initializeFlex(propertyDefaultValues);
    }

    //region IFlexBlock
    private DynamicShape generalShape;
    private DynamicShape collisionShape;
    private DynamicShape raytraceShape;
    private DynamicShape renderShape;

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
    public void setGeneralShape(DynamicShape shape)
    {
        this.generalShape = shape;
    }

    @Override
    public void setCollisionShape(DynamicShape shape)
    {
        this.collisionShape = shape;
    }

    @Override
    public void setRaytraceShape(DynamicShape shape)
    {
        this.raytraceShape = shape;
    }

    @Override
    public void setRenderShape(DynamicShape shape)
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
    //endregion
}
