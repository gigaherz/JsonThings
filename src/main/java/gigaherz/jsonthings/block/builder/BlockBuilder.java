package gigaherz.jsonthings.block.builder;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import gigaherz.jsonthings.block.FlexBlock;
import gigaherz.jsonthings.block.IFlexBlock;
import gigaherz.jsonthings.item.builder.ItemBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class BlockBuilder
{
    private final Multimap<String, String> eventHandlers = ArrayListMultimap.create();

    private Block builtBlock = null;

    private final List<Property<?>> properties = Lists.newArrayList();
    private final Map<String, String> propertyDefaultValues = Maps.newHashMap();
    private Material blockMaterial = Material.ROCK;
    private MaterialColor blockMaterialColor = null;
    private ResourceLocation registryName;
    private ItemBuilder itemBuilder;

    private BlockBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public static BlockBuilder begin(ResourceLocation registryName)
    {
        return new BlockBuilder(registryName);
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    public Block build()
    {
        Block.Properties props = blockMaterialColor != null ?
                Block.Properties.create(blockMaterial, blockMaterialColor) :
                Block.Properties.create(blockMaterial);

        Block baseBlock = new FlexBlock(props, properties, propertyDefaultValues);

        IFlexBlock flexBlock = (IFlexBlock) baseBlock;

        baseBlock.setRegistryName(registryName);

        // TODO

        builtBlock = baseBlock;
        return baseBlock;
    }

    @Nullable
    public Block getBuiltBlock()
    {
        return builtBlock;
    }

    public BlockBuilder withItem(ItemBuilder itemBuilder)
    {
        this.itemBuilder = itemBuilder;
        return this;
    }

    public BlockBuilder withProperty(Property<?> deserialize)
    {
        this.properties.add(deserialize);
        return this;
    }

    public BlockBuilder withDefaultState(String name, String value)
    {
        this.propertyDefaultValues.put(name, value);
        return this;
    }
}
