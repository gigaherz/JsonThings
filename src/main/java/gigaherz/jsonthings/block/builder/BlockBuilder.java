package gigaherz.jsonthings.block.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gigaherz.jsonthings.block.BlockFlex;
import gigaherz.jsonthings.block.IFlexBlock;
import gigaherz.jsonthings.item.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class BlockBuilder
{
    private static Field f_tabLabel = ReflectionHelper.findField(CreativeTabs.class, ObfuscationReflectionHelper.remapFieldNames(CreativeTabs.class.getName(), "field_78034_o"));

    private final List<Pair<StackContext, String[]>> creativeMenuStacks = Lists.newArrayList();
    private final List<AttributeModifier> attributeModifiers = Lists.newArrayList();
    private final Map<String, String> eventHandlers = Maps.newHashMap();

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
