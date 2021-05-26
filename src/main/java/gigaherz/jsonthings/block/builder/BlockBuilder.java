package gigaherz.jsonthings.block.builder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import gigaherz.jsonthings.block.FlexBlock;
import gigaherz.jsonthings.block.IFlexBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class BlockBuilder
{
    private final Multimap<String, String> eventHandlers = ArrayListMultimap.create();

    private Block builtBlock = null;

    private Material blockMaterial = Material.ROCK;
    private MaterialColor blockMaterialColor = null;
    private ResourceLocation registryName;

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

        Block baseBlock = new FlexBlock(props);

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
}
