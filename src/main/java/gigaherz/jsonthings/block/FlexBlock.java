package gigaherz.jsonthings.block;

import gigaherz.jsonthings.block.builder.DynamicShape;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import java.util.List;
import java.util.Map;

public class FlexBlock extends Block implements IFlexBlock
{

    @SuppressWarnings("rawtypes")
    public FlexBlock(Properties properties, List<Property<?>> stateProperties, Map<Property, Comparable> propertyDefaultValues)
    {
        super(properties);

        initializeFlex(stateProperties, propertyDefaultValues);
    }

    // IFlexBlock implementation
    private DynamicShape generalShape;
    private DynamicShape collisionShape;
    private DynamicShape raytraceShape;
    private DynamicShape renderShape;

    @SuppressWarnings({"rawtypes","unchecked"})
    private void initializeFlex(List<Property<?>> stateProperties, Map<Property, Comparable> propertyDefaultValues)
    {
        if (stateProperties.size() > 0)
        {
            StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
            stateProperties.forEach(builder::add);
            this.stateContainer = builder.createStateContainer(Block::getDefaultState, BlockState::new);

            BlockState def = getStateContainer().getBaseState();
            for(Map.Entry<Property, Comparable> entry : propertyDefaultValues.entrySet())
            {
                Property prop = entry.getKey();
                Comparable value = entry.getValue();
                def = def.with(prop, value);
            }

            setDefaultState(def);
        }
    }

    public void setGeneralShape(DynamicShape shape)
    {
        this.generalShape = shape;
    }
    public void setCollisionShape(DynamicShape shape)
    {
        this.collisionShape = shape;
    }
    public void setRaytraceShape(DynamicShape shape)
    {
        this.raytraceShape = shape;
    }
    public void setRenderShape(DynamicShape shape)
    {
        this.renderShape = shape;
    }

    // Block implementation

    @Override
    public StateContainer<Block, BlockState> getStateContainer()
    {
        return stateContainer;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        // Handled in init()
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if (this.generalShape != null)
            return generalShape.getShape(state);
        return super.getShape(state, worldIn, pos, context);
    }

    @Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        if (this.raytraceShape != null)
            return raytraceShape.getShape(state);
        return super.getRaytraceShape(state, worldIn, pos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos)
    {
        if (this.collisionShape != null)
            return collisionShape.getShape(state);
        return super.getCollisionShape(state, reader, pos);
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        if (this.collisionShape != null)
            return collisionShape.getShape(state);
        return super.getRenderShape(state, worldIn, pos);
    }
}
