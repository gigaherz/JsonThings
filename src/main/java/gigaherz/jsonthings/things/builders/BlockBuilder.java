package gigaherz.jsonthings.things.builders;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import gigaherz.jsonthings.things.IFlexBlock;
import gigaherz.jsonthings.things.blocks.FlexBlock;
import gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlockBuilder
{
    private final Multimap<String, String> eventHandlers = ArrayListMultimap.create();

    private Block builtBlock = null;

    private final List<Property<?>> properties = Lists.newArrayList();
    private final Map<String, Property<?>> propertiesByName = Maps.newHashMap();
    private final Map<Property, Comparable> propertyDefaultValues = Maps.newHashMap();
    private Material blockMaterial = Material.ROCK;
    private MaterialColor blockMaterialColor = null;
    private ResourceLocation registryName;
    private ItemBuilder itemBuilder;
    private DynamicShape generalShape;
    private DynamicShape collisionShape;
    private DynamicShape raytraceShape;
    private DynamicShape renderShape;

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
        flexBlock.setGeneralShape(generalShape);
        flexBlock.setCollisionShape(collisionShape);
        flexBlock.setRaytraceShape(raytraceShape);
        flexBlock.setRenderShape(renderShape);

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

    public BlockBuilder withProperty(Property<?> property)
    {
        String propertyName = property.getName();
        if (propertiesByName.containsKey(propertyName))
            throw new IllegalStateException("A property with name " + propertyName + " has already been declared");
        this.properties.add(property);
        this.propertiesByName.put(propertyName, property);
        return this;
    }

    public BlockBuilder withDefaultState(String name, String value)
    {
        Property prop = propertiesByName.get(name);
        if (prop == null)
            throw new IllegalStateException("No property declared with name " + name);
        Optional<Comparable> propValue = prop.parseValue(value);
        Comparable val = propValue.orElseThrow(() -> new IllegalStateException("Value " + value + " for property " + name + " not found in the allowed values."));
        this.propertyDefaultValues.put(prop, val);
        return this;
    }

    public Map<String, Property<?>> getPropertiesByName()
    {
        return Collections.unmodifiableMap(propertiesByName);
    }

    public BlockBuilder withGeneralShape(DynamicShape shape)
    {
        this.generalShape = shape;
        return this;
    }

    public BlockBuilder withCollisionShape(DynamicShape shape)
    {
        this.collisionShape = shape;
        return this;
    }

    public BlockBuilder withRaytraceShape(DynamicShape shape)
    {
        this.raytraceShape = shape;
        return this;
    }

    public BlockBuilder withRenderShape(DynamicShape shape)
    {
        this.renderShape = shape;
        return this;
    }
}
