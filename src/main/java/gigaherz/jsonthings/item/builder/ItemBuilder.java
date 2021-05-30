package gigaherz.jsonthings.item.builder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import gigaherz.jsonthings.item.*;
import gigaherz.jsonthings.microregistries.ThingsByName;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemBuilder
{
    public static final ToolType SWORD_TOOL_TYPE = ToolType.get("sword");

    //@SuppressWarnings("deprecation")
    //private static Field f_tabLabel = ReflectionHelper.findField(ItemGroup.class, ObfuscationReflectionHelper.remapFieldNames(ItemGroup.class.getName(), "field_78034_o"));

    private final List<AttributeModifier> attributeModifiers = Lists.newArrayList();
    private final Multimap<String, String> eventHandlers = ArrayListMultimap.create();

    private Item builtItem = null;

    private ResourceLocation registryName;
    private Integer maxStackSize = null;
    private Integer maxDamage = null;

    private final List<Pair<StackContext, String[]>> creativeMenuStacks = Lists.newArrayList();
    private List<ToolInfo> toolInfos = Lists.newArrayList();
    private Food foodInfo = null;
    private PlantInfo plantInfo = null;
    private ArmorInfo armorInfo = null;

    private DelayedUse delayedUse = null;
    private ContainerInfo containerInfo = null;
    private ModelInfo modelInfo = null;

    private BlockInfo blockInfo = null;

    private ItemBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public static ItemBuilder begin(ResourceLocation registryName)
    {
        return new ItemBuilder(registryName);
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

    public ItemBuilder withAttributeModifier(@Nullable UUID uuid, String name, double amount, int op)
    {
        AttributeModifier.Operation operation = AttributeModifier.Operation.byId(op);
        attributeModifiers.add(uuid != null ?
                new AttributeModifier(uuid, name, amount, operation) :
                new AttributeModifier(name, amount, operation));
        return this;
    }

    public ItemBuilder makeDamageable(int maxDamage)
    {
        if (this.maxDamage != null) throw new RuntimeException("Damageable already set.");
        this.maxDamage = maxDamage;
        return this;
    }

    public ItemBuilder makeBlock(ResourceLocation blockName)
    {
        if (this.blockInfo != null) throw new RuntimeException("Block info already set.");
        if (this.foodInfo != null) throw new RuntimeException("An item cannot be block and food at the same time.");
        if (this.toolInfos.size() > 0) throw new RuntimeException("An item cannot be block and tool at the same time.");
        if (this.armorInfo != null) throw new RuntimeException("An item cannot be block and armor at the same time.");
        this.blockInfo = new BlockInfo(blockName);
        return this;
    }

    public ItemBuilder withTool(ToolType toolType, ResourceLocation tierName)
    {
        if (this.blockInfo != null) throw new RuntimeException("An item can not be tool and block at the same time.");
        if (this.armorInfo != null) throw new RuntimeException("An item cannot be tool and armor at the same time.");
        if (!ThingsByName.ITEM_TIERS.containsKey(tierName)) throw new RuntimeException("No known item tier definition with name '" + tierName + "'");
        this.toolInfos.add(new ToolInfo(toolType, ThingsByName.ITEM_TIERS.getOrDefault(tierName)));
        return this;
    }

    public ItemBuilder makeFood(ResourceLocation foodName)
    {
        if (this.foodInfo != null) throw new RuntimeException("Food info already set.");
        if (this.blockInfo != null) throw new RuntimeException("An item can not be food and block at the same time.");
        if (!ThingsByName.FOODS.containsKey(foodName)) throw new RuntimeException("No known food definition with name '" + foodName + "'");
        this.foodInfo = ThingsByName.FOODS.getOrDefault(foodName);
        return this;
    }

    public ItemBuilder makeFood(Food food)
    {
        if (this.foodInfo != null) throw new RuntimeException("Food info already set.");
        if (this.blockInfo != null) throw new RuntimeException("An item can not be food and block at the same time.");
        this.foodInfo = food;
        return this;
    }

    public ItemBuilder makeArmor(String equipmentSlot, String material)
    {
        if (this.armorInfo != null) throw new RuntimeException("Armor info already set.");
        if (this.blockInfo != null) throw new RuntimeException("An item can not be armor and block at the same time.");
        if (this.toolInfos.size() > 0) throw new RuntimeException("An item cannot be armor and tool at the same time.");
        this.armorInfo = new ArmorInfo(equipmentSlot, material);
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
        Item.Properties properties = new Item.Properties();

        if (maxDamage != null)
        {
            properties = properties.maxDamage(maxDamage);
        }

        if (containerInfo != null)
        {
            properties = properties.containerItem(getItemOrCrash(containerInfo.emptyItem));
        }

        if (foodInfo != null)
        {
            properties = properties.food(foodInfo);
        }

        Item baseItem = null;

        if (toolInfos.size() > 0)
        {
            ToolInfo toolInfo = null; // first tool with a material name
            for(int i=1;i< toolInfos.size();i++)
            {
                ToolInfo other = toolInfos.get(i);
                if (other.material != null && toolInfo == null)
                    toolInfo = other;
                else
                    properties.addToolType(other.toolClass, other.toolTier.getHarvestLevel());
            }
            if (toolInfo != null)
            {
                IItemTier tier = toolInfo.toolTier;
                if (ToolType.AXE == toolInfo.toolClass)
                {
                    baseItem = new FlexAxeItem(tier, toolInfo.toolDamage, toolInfo.toolSpeed, properties);
                }
                else if (ToolType.PICKAXE == toolInfo.toolClass)
                {
                    baseItem = new FlexPickaxeItem(tier, toolInfo.toolDamage, toolInfo.toolSpeed, properties);
                }
                else if (ToolType.SHOVEL == toolInfo.toolClass)
                {
                    baseItem = new FlexSpadeItem(tier, toolInfo.toolDamage, toolInfo.toolSpeed, properties);
                }
                else if (ToolType.HOE == toolInfo.toolClass)
                {
                    baseItem = new FlexHoeItem(tier, toolInfo.toolDamage, toolInfo.toolSpeed, properties);
                }
                else if (SWORD_TOOL_TYPE == toolInfo.toolClass)
                {
                    baseItem = new FlexSwordItem(tier, toolInfo.toolDamage, toolInfo.toolSpeed, properties);
                }
                else
                {
                    //throw new RuntimeException(String.format("Unknown tool class '%s'.", toolInfo.toolClass));
                    // allow unknown classes, but treat them as normal items without a special subclass
                }
            }
        }
        else if (armorInfo != null)
        {
            baseItem = new FlexArmorItem(armorInfo.material, armorInfo.slot, properties);
        }
        else if (plantInfo != null)
        {
            //TODO: ForgeRegistries.BLOCKS.getValue(plantInfo.soil)
            baseItem = new FlexBlockNamedItem(getBlockOrCrash(plantInfo.crops), properties);
        }
        else if (blockInfo != null)
        {
            baseItem = new FlexBlockItem(getBlockOrCrash(blockInfo.block), properties);
        }
        // else other types

        if (baseItem == null)
        {
            baseItem = new FlexItem(properties);
        }

        IFlexItem flexItem = (IFlexItem) baseItem;

        baseItem.setRegistryName(registryName);

        if (delayedUse != null)
        {
            flexItem.setUseAction(delayedUse.useAction);
            flexItem.setUseTime(delayedUse.useTicks);
            flexItem.setUseFinishMode(delayedUse.onComplete);
        }

        for (Pair<StackContext, String[]> tabEntries : creativeMenuStacks)
        {
            StackContext ctx = tabEntries.getFirst();
            String[] tabs = tabEntries.getSecond();

            Set<ItemGroup> tabsIterable = Arrays.stream(tabs).map(this::findCreativeTab).collect(Collectors.toSet());
            flexItem.addCreativeStack(ctx, tabsIterable);
        }
        builtItem = baseItem;
        return baseItem;
    }

    private Item getItemOrCrash(ResourceLocation which)
    {
        if (!ForgeRegistries.ITEMS.containsKey(which))
            throw new RuntimeException(String.format("Attempted to make a block-placing item for '%s' without the associated block", blockInfo.block));
        return ForgeRegistries.ITEMS.getValue(which);
    }

    private Block getBlockOrCrash(ResourceLocation which)
    {
        if (!ForgeRegistries.BLOCKS.containsKey(which))
            throw new RuntimeException(String.format("Attempted to make a block-placing item for '%s' without the associated block", blockInfo.block));
        return ForgeRegistries.BLOCKS.getValue(which);
    }

    @Nullable
    private ItemGroup findCreativeTab(String label)
    {
        for (ItemGroup tab : ItemGroup.GROUPS)
        {
            if (tab.getPath().equals(label))
                return tab;
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

    static class ArmorInfo
    {
        public EquipmentSlotType slot;
        public IArmorMaterial material;

        public ArmorInfo(String equipmentSlot, String material)
        {
            this.slot = EquipmentSlotType.fromString(equipmentSlot);
            this.material = ArmorMaterial.valueOf(material.toUpperCase());
        }
    }

    static class BlockInfo
    {
        public ResourceLocation block;

        public BlockInfo(ResourceLocation blockName)
        {
            this.block = blockName;
        }
    }

    static class ContainerInfo
    {
        public ResourceLocation emptyItem;

        public ContainerInfo(ResourceLocation registryName, String emptyItem)
        {
            if (emptyItem.contains(":"))
                this.emptyItem = new ResourceLocation(emptyItem);
            else
                this.emptyItem = new ResourceLocation(registryName.getNamespace(), emptyItem);
        }
    }

    static class DelayedUse
    {
        public int useTicks;
        public UseAction useAction;
        public CompletionMode onComplete;

        public DelayedUse(int useTicks, String useAction, String completeAction)
        {
            this.useTicks = useTicks;
            this.useAction = UseAction.valueOf(useAction.toUpperCase());
            this.onComplete = CompletionMode.valueOf(completeAction.toUpperCase());
        }
    }

    static class FoodInfo
    {
        public String foodName;

        public FoodInfo(String foodName)
        {
            this.foodName = foodName;
        }
    }

    public static class ModelInfo
    {
        public class ModelMapping
        {
            public final int metadata; // to be removed in 1.13
            public final ResourceLocation fileName;
            public final String variantName;

            public ModelMapping(int metadata, String fileName, String variantName)
            {
                this.metadata = metadata;
                this.fileName = new ResourceLocation(fileName);
                this.variantName = variantName;
            }
        }

        public final List<ModelMapping> mappings = Lists.newArrayList();

        public void addMapping(int metadata, String fileName, String variantName)
        {
            mappings.add(new ModelMapping(metadata, fileName, variantName));
        }
    }

    static class PlantInfo
    {
        public ResourceLocation crops;
        public ResourceLocation soil;
    }

    static class ToolInfo
    {
        public ToolType toolClass;
        public String material;
        public int toolDamage;
        public float toolSpeed;
        public IItemTier toolTier;

        public ToolInfo(ToolType toolType, IItemTier tier)
        {
            this.toolClass = toolType;
            this.toolTier = tier;
        }
    }
}


