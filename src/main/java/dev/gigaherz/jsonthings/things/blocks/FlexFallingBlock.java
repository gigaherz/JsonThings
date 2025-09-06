package dev.gigaherz.jsonthings.things.blocks;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gigaherz.jsonthings.things.IFlexBlock;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.FlexEventType;
import dev.gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

public class FlexFallingBlock extends FallingBlock implements IFlexBlock
{
    public static final MapCodec<FlexFallingBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ColorRGBA.CODEC.fieldOf("falling_dust_color").forGetter(p_304722_ -> p_304722_.dustColor),
                    propertiesCodec()
            ).apply(instance, (color, properties) -> new FlexFallingBlock(color, properties, Collections.emptyMap()))
    );

    public FlexFallingBlock(ColorRGBA dustColor, Properties properties, Map<Property<?>, Comparable<?>> propertyDefaultValues)
    {
        super(properties);
        initializeFlex(propertyDefaultValues);
        this.dustColor = dustColor;
    }

    @Override
    protected MapCodec<? extends FlexFallingBlock> codec()
    {
        return CODEC;
    }

    //region FallingDust implementation details
    private final ColorRGBA dustColor;

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos)
    {
        return dustColor.rgba(); // TODO: make event-aware
    }
    //endregion

    //region IFlexBlock
    @SuppressWarnings("rawtypes")
    private final Map<FlexEventType, FlexEventHandler> eventHandlers = Maps.newHashMap();
    private DynamicShape generalShape;
    private DynamicShape collisionShape;
    private DynamicShape raytraceShape;
    private DynamicShape renderShape;

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initializeFlex(Map<Property<?>, Comparable<?>> propertyDefaultValues)
    {
        if (!propertyDefaultValues.isEmpty())
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
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        if (this.generalShape != null)
            return generalShape.getShape(state);
        return super.getShape(state, worldIn, pos, context);
    }

    @Deprecated
    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos)
    {
        if (this.raytraceShape != null)
            return raytraceShape.getShape(state);
        return super.getInteractionShape(state, worldIn, pos);
    }

    @Deprecated
    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos)
    {
        if (this.collisionShape != null)
            return collisionShape.getShape(state);
        return super.getBlockSupportShape(state, reader, pos);
    }

    @Deprecated
    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos)
    {
        if (this.renderShape != null)
            return renderShape.getShape(state);
        return super.getOcclusionShape(state, worldIn, pos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        return runEvent(FlexEventType.USE_BLOCK_WITHOUT_ITEM, FlexEventContext.of(level, pos, state)
                .with(FlexEventContext.USER, player)
                .withRayTrace(hitResult), () -> super.useWithoutItem(state, level, pos, player, hitResult));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        return runEvent(FlexEventType.USE_BLOCK_WITH_ITEM, FlexEventContext.of(level, pos, state)
                .with(FlexEventContext.USER, player)
                .withRayTrace(hitResult), () -> super.useItemOn(stack, state, level, pos, player, hand, hitResult));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return runEvent(FlexEventType.GET_STATE_FOR_PLACEMENT, FlexEventContext.of(context.getLevel(), context.getClickedPos(), this.defaultBlockState())
                .with(FlexEventContext.USER, context.getPlayer()).with(FlexEventContext.USE_CONTEXT, context).with(FlexEventContext.STATE_DEFINITION, this.stateDefinition)
                , () -> super.getStateForPlacement(context));
    }
    //endregion
}
