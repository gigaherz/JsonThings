package gigaherz.jsonthings.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FlexBlock extends Block implements IFlexBlock
{
    public FlexBlock(Block.Properties properties, List<Property<?>> stateProperties, Map<String, String> propertyDefaultValues)
    {
        super(properties);

        init(stateProperties, propertyDefaultValues);
    }

    // IFlexBlock implementation

    private void init(List<Property<?>> stateProperties, Map<String, String> propertyDefaultValues)
    {
        if (stateProperties.size() > 0)
        {
            StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
            stateProperties.forEach(builder::add);
            this.stateContainer = builder.createStateContainer(Block::getDefaultState, BlockState::new);

            BlockState def = getStateContainer().getBaseState();
            for(Map.Entry<String, String> entry : propertyDefaultValues.entrySet())
            {
                String key = entry.getKey();
                String valueName = entry.getValue();
                //noinspection rawtypes
                Property prop = stateContainer.getProperty(key);
                if (prop == null)
                    throw new IllegalStateException("Unknown property " + key + " setting default state");
                Optional<?> value = prop.parseValue(valueName);
                if (value.isPresent())
                {
                   //noinspection unchecked,rawtypes
                   def = def.with(prop, (Comparable)value.get());
                }
            }

            setDefaultState(def);
        }
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
}
