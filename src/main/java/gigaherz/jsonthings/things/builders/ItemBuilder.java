package gigaherz.jsonthings.things.builders;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import gigaherz.jsonthings.things.IFlexItem;
import gigaherz.jsonthings.things.items.*;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.parsers.ThingResourceManager;
import gigaherz.jsonthings.things.shapes.DynamicShape;
import gigaherz.jsonthings.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemBuilder
{
    public static final ToolType SWORD_TOOL_TYPE = ToolType.get("sword");

    private final List<AttributeModifier> attributeModifiers = Lists.newArrayList();
    private final Multimap<String, String> eventHandlers = ArrayListMultimap.create();

    private IFlexItem builtItem = null;

    private final ResourceLocation registryName;

    private ResourceLocation parentBuilder;
    private ItemBuilder parentBuilderObj;
    private RegistryObject<Item> parentItem;

    private Integer maxStackSize = null;
    private Integer maxDamage = null;

    private final List<Pair<StackContext, String[]>> creativeMenuStacks = Lists.newArrayList();
    private final List<ToolInfo> toolInfos = Lists.newArrayList();
    private Food foodInfo = null;
    private PlantInfo plantInfo = null;
    private ArmorInfo armorInfo = null;

    private DelayedUse delayedUse = null;
    private ContainerInfo containerInfo = null;

    private BlockInfo blockInfo = null;
    private String colorHandler = null;

    private ItemBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public static ItemBuilder begin(ResourceLocation registryName)
    {
        return new ItemBuilder(registryName);
    }

    public ItemBuilder withParentItem(ResourceLocation parentName)
    {
        if (this.parentBuilder != null)
            throw new IllegalStateException("Cannot set parent block and parent builder at the same time");
        if (this.parentItem != null)
            throw new IllegalStateException("Parent item already set");
        this.parentItem = RegistryObject.of(parentName, ForgeRegistries.ITEMS);
        return this;
    }

    public ItemBuilder withParentBuilder(ResourceLocation parentName)
    {
        if (this.parentBuilder != null)
            throw new IllegalStateException("Parent builder already set");
        if (this.parentItem != null)
            throw new IllegalStateException("Cannot set parent item and parent builder at the same time");
        this.parentItem = RegistryObject.of(parentName, ForgeRegistries.ITEMS);
        this.parentBuilder = parentName;
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

    public ItemBuilder withAttributeModifier(@Nullable UUID uuid, String name, double amount, int op)
    {
        AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(op);
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
        if (!ThingRegistries.ITEM_TIERS.containsKey(tierName))
            throw new RuntimeException("No known item tier definition with name '" + tierName + "'");
        IItemTier tier = ThingRegistries.ITEM_TIERS.get(tierName);
        if (tier == null)
            throw new IllegalStateException("Property with name " + tierName + " not found in ThingRegistries.ITEM_TIERS");
        this.toolInfos.add(new ToolInfo(toolType, tier));
        return this;
    }

    public ItemBuilder makeFood(ResourceLocation foodName)
    {
        if (this.foodInfo != null) throw new RuntimeException("Food info already set.");
        if (this.blockInfo != null) throw new RuntimeException("An item can not be food and block at the same time.");
        if (!ThingRegistries.FOODS.containsKey(foodName))
            throw new RuntimeException("No known food definition with name '" + foodName + "'");
        Food foodInfo = ThingRegistries.FOODS.get(foodName);
        if (foodInfo == null)
            throw new IllegalStateException("Property with name " + foodName + " not found in ThingRegistries.FOODS");
        this.foodInfo = foodInfo;
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

    public ItemBuilder withColorHandler(String colorHandler)
    {
        this.colorHandler = colorHandler;
        return this;
    }

    public IFlexItem build()
    {
        Item.Properties properties = new Item.Properties();

        if (maxDamage != null)
        {
            properties = properties.durability(maxDamage);
        }

        if (containerInfo != null)
        {
            properties = properties.craftRemainder(Utils.getItemOrCrash(containerInfo.emptyItem));
        }

        if (foodInfo != null)
        {
            properties = properties.food(foodInfo);
        }


        IFlexItem flexItem = null;
        if (toolInfos.size() > 0)
        {
            ToolInfo toolInfo = null; // first tool with a material name
            for (int i = 1; i < toolInfos.size(); i++)
            {
                ToolInfo other = toolInfos.get(i);
                if (other.material != null && toolInfo == null)
                    toolInfo = other;
                else
                    properties.addToolType(other.toolClass, other.toolTier.getLevel());
            }
            if (toolInfo != null)
            {
                IItemTier tier = toolInfo.toolTier;
                if (ToolType.AXE == toolInfo.toolClass)
                {
                    flexItem = new FlexAxeItem(tier, toolInfo.toolDamage, toolInfo.toolSpeed, properties);
                }
                else if (ToolType.PICKAXE == toolInfo.toolClass)
                {
                    flexItem = new FlexPickaxeItem(tier, toolInfo.toolDamage, toolInfo.toolSpeed, properties);
                }
                else if (ToolType.SHOVEL == toolInfo.toolClass)
                {
                    flexItem = new FlexSpadeItem(tier, toolInfo.toolDamage, toolInfo.toolSpeed, properties);
                }
                else if (ToolType.HOE == toolInfo.toolClass)
                {
                    flexItem = new FlexHoeItem(tier, toolInfo.toolDamage, toolInfo.toolSpeed, properties);
                }
                else if (SWORD_TOOL_TYPE == toolInfo.toolClass)
                {
                    flexItem = new FlexSwordItem(tier, toolInfo.toolDamage, toolInfo.toolSpeed, properties);
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
            flexItem = new FlexArmorItem(armorInfo.material, armorInfo.slot, properties);
        }
        else if (plantInfo != null)
        {
            //TODO: ForgeRegistries.BLOCKS.getValue(plantInfo.soil)
            flexItem = new FlexBlockNamedItem(Utils.getBlockOrCrash(plantInfo.crops), properties);
        }
        else if (blockInfo != null)
        {
            flexItem = new FlexBlockItem(Utils.getBlockOrCrash(blockInfo.block), properties);
        }
        // else other types

        if (flexItem == null)
        {
            flexItem = new FlexItem(properties);
        }

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

        builtItem = flexItem;
        return flexItem;
    }

    @Nullable
    private ItemGroup findCreativeTab(String label)
    {
        for (ItemGroup tab : ItemGroup.TABS)
        {
            if (tab.getRecipeFolderName().equals(label))
                return tab;
        }
        return null;
    }

    public IFlexItem getBuiltItem()
    {
        if (builtItem == null)
            return build();
        return builtItem;
    }

    public ItemBuilder getParentBuilder()
    {
        if (parentBuilder == null)
            throw new IllegalStateException("Parent builder not set");
        if (parentBuilderObj == null)
        {
            parentBuilderObj = ThingResourceManager.INSTANCE.itemParser.getBuildersMap().get(parentBuilder);
        }
        if (parentBuilderObj == null)
            throw new IllegalStateException("Parent builder not found");
        return parentBuilderObj;
    }

    @Nullable
    private <T> T getValueWithParent(@Nullable T thisValue, Function<ItemBuilder, T> parentGetter)
    {
        if (thisValue != null) return thisValue;
        if (parentBuilder != null)
        {
            ItemBuilder parent = getParentBuilder();
            return parentGetter.apply(parent);
        }
        return null;
    }

    @Nullable
    public String getColorHandler()
    {
        return getValueWithParent(colorHandler, ItemBuilder::getColorHandler);
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    static class ArmorInfo
    {
        public EquipmentSlotType slot;
        public IArmorMaterial material;

        public ArmorInfo(String equipmentSlot, String material)
        {
            this.slot = EquipmentSlotType.byName(equipmentSlot);
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


