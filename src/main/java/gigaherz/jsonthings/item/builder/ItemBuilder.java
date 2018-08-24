package gigaherz.jsonthings.item.builder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import gigaherz.jsonthings.item.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemBuilder
{
    private static Field f_tabLabel = ReflectionHelper.findField(CreativeTabs.class, ObfuscationReflectionHelper.remapFieldNames(CreativeTabs.class.getName(),"field_78034_o"));

    private final List<Pair<StackContext,String[]>> creativeMenuStacks = Lists.newArrayList();
    private final List<AttributeModifier> attributeModifiers = Lists.newArrayList();
    private final Map<String, String> eventHandlers = Maps.newHashMap();

    private Item builtItem = null;

    private ResourceLocation registryName;
    private String translationKey;
    private Integer maxStackSize = null;
    private Integer maxDamage = null;
    private ToolInfo toolInfo = null;
    private DelayedUse delayedUse = null;
    private ContainerInfo containerInfo = null;
    private FoodInfo foodInfo = null;
    private PlantInfo plantInfo = null;
    private ModelInfo modelInfo = null;

    private ItemBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public static ItemBuilder begin(ResourceLocation registryName)
    {
        return new ItemBuilder(registryName);
    }

    public ItemBuilder withTranslationKey(String translationKey)
    {
        if (this.translationKey != null) throw new RuntimeException("Translation key already set.");
        this.translationKey = translationKey;
        return this;
    }

    public ItemBuilder withMaxStackSize(int maxStackSize)
    {
        if (this.maxStackSize != null) throw new RuntimeException("Max stack size already set.");
        this.maxStackSize = maxStackSize;
        return this;
    }

    public ItemBuilder withCreativeMenuStack(StackContext stackContext, String[] tabs)
    {
        creativeMenuStacks.add(Pair.of(stackContext, tabs));
        return this;
    }

    public ItemBuilder makeDamageable(int maxDamage)
    {
        if (this.maxDamage != null) throw new RuntimeException("Damageable already set.");
        this.maxDamage = maxDamage;
        return this;
    }

    public ItemBuilder makeTool(String toolType, String material)
    {
        if (this.toolInfo != null) throw new RuntimeException("Tool info already set.");
        this.toolInfo = new ToolInfo(toolType, material);
        return this;
    }

    public ItemBuilder makeDelayedUse(int useTicks, String useType, String completeAction)
    {
        if (this.delayedUse != null) throw new RuntimeException("Delayed use already set.");
        this.delayedUse = new DelayedUse(useTicks, useType, completeAction);
        return this;
    }

    public ItemBuilder makeContainer(String emptyItem)
    {
        if (this.containerInfo != null) throw new RuntimeException("Delayed use already set.");
        this.containerInfo = new ContainerInfo(registryName, emptyItem);
        return this;
    }

    public Item build()
    {
        Item baseItem;

        if (toolInfo != null)
        {
            Item.ToolMaterial material = Item.ToolMaterial.valueOf(toolInfo.material.toUpperCase());
            switch (toolInfo.toolClass)
            {
                case "axe":
                    baseItem = new ItemFlexAxe(material, toolInfo.toolDamage, toolInfo.toolSpeed);
                    break;
                case "pick":
                case "pickaxe":
                    baseItem = new ItemFlexPickaxe(material);
                    break;
                case "shovel":
                case "spade":
                    baseItem = new ItemFlexSpade(material);
                    break;
                case "hoe":
                    baseItem = new ItemFlexHoe(material);
                    break;
                case "sword":
                    baseItem = new ItemFlexSword(material);
                    break;
                default:
                    throw new RuntimeException(String.format("Unknown tool class '%s'.", toolInfo.toolClass));
            }
        }
        // else if food and other types
        else if (foodInfo != null && plantInfo != null)
        {
            baseItem = new ItemFlexPlantFood(foodInfo.healAmount, foodInfo.saturation,
                    ForgeRegistries.BLOCKS.getValue(plantInfo.crops),
                    ForgeRegistries.BLOCKS.getValue(plantInfo.soil));
        }
        else if (foodInfo != null)
        {
            baseItem = new ItemFlexFood(foodInfo.healAmount, foodInfo.saturation, foodInfo.isWolfFood);
        }
        else if(plantInfo != null)
        {
            baseItem = new ItemFlexPlant(
                    ForgeRegistries.BLOCKS.getValue(plantInfo.crops),
                    ForgeRegistries.BLOCKS.getValue(plantInfo.soil));
        }
        else
        {
            baseItem = new ItemFlex();
        }

        IFlexItem flexItem = (IFlexItem)baseItem;

        baseItem.setRegistryName(registryName);

        if (translationKey != null)
        {
            baseItem.setTranslationKey(translationKey);
        }

        if (maxDamage != null)
        {
            baseItem.setMaxDamage(maxDamage);
        }

        if (delayedUse != null)
        {
            flexItem.setUseAction(delayedUse.useAction);
            flexItem.setUseTime(delayedUse.useTicks);
            flexItem.setUseFinishMode(delayedUse.onComplete);
        }

        if (containerInfo != null)
        {
            baseItem.setContainerItem(ForgeRegistries.ITEMS.getValue(containerInfo.emptyItem));
        }

        for(Pair<StackContext, String[]> tabEntries : creativeMenuStacks)
        {
            StackContext ctx = tabEntries.getLeft();
            String[] tabs = tabEntries.getRight();

            Set<CreativeTabs> tabsIterable = Arrays.stream(tabs).map(this::findCreativeTab).collect(Collectors.toSet());
            flexItem.addCreativeStack(ctx, tabsIterable);
        }

        builtItem = baseItem;
        return baseItem;
    }

    @Nullable
    private CreativeTabs findCreativeTab(String label)
    {
        try
        {
            for(CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY)
            {
                    if (f_tabLabel.get(tab).equals(label))
                        return tab;
            }
        }
        catch (IllegalAccessException e)
        {
            // left blank intentionally
        }
        return null;
    }

    @Nullable
    public Item getBuiltItem()
    {
        return builtItem;
    }

    @Nullable
    public ModelInfo getModelInfo()
    {
        return modelInfo;
    }
}
