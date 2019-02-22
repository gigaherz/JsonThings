package gigaherz.jsonthings.block.builder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import gigaherz.jsonthings.block.BlockFlex;
import gigaherz.jsonthings.block.IFlexBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public class BlockBuilder
{
    private final Multimap<String, String> eventHandlers = ArrayListMultimap.create();

    private Block builtBlock = null;

    private ResourceLocation registryName;
    private String translationKey;

    private BlockBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public static BlockBuilder begin(ResourceLocation registryName)
    {
        return new BlockBuilder(registryName);
    }

    public BlockBuilder withTranslationKey(String translationKey)
    {
        if (this.translationKey != null) throw new RuntimeException("Translation key already set.");
        this.translationKey = translationKey;
        return this;
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    public Block build()
    {
        Block baseBlock = new BlockFlex(Material.ROCK, Material.ROCK.getMaterialMapColor());

        IFlexBlock flexBlock = (IFlexBlock) baseBlock;

        baseBlock.setRegistryName(registryName);

        if (translationKey != null)
        {
            baseBlock.setTranslationKey(translationKey);
        }
        else
        {
            baseBlock.setTranslationKey(registryName.getNamespace() + "." + registryName.getPath());
        }

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
