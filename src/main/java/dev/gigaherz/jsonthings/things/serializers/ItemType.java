package dev.gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.items.*;
import dev.gigaherz.jsonthings.util.Utils;
import joptsimple.internal.Strings;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.ToolType;

@SuppressWarnings("ClassCanBeRecord")
public class ItemType<T extends Item & IFlexItem>
{
    public static final ItemType<FlexItem> PLAIN = register("plain", (data) -> new ItemFactory<>((props, builder) -> new FlexItem(props)));

    public static final ItemType<FlexBlockItem> BLOCK = register("block", data -> {
        String name = JSONUtils.getAsString(data, "places", null);
        ResourceLocation blockName = name != null ? new ResourceLocation(name) : null;
        boolean useBlockName = JSONUtils.getAsBoolean(data, "use_block_name", true);
        return new ItemFactory<>((props, builder) -> new FlexBlockItem(Utils.getBlockOrCrash(blockName != null ? blockName : builder.getRegistryName()), useBlockName, props));
    });

    public static final ItemType<FlexArmorItem> ARMOR = register("armor", data -> {

        String slotName;
        if (data.has("equipment_slot"))
        {
            String str = data.get("equipment_slot").getAsString();
            if (!Strings.isNullOrEmpty(str))
            {
                slotName = str;
            }
            else
            {
                throw new RuntimeException("Armor equipment slot must be a non-empty string.");
            }
        }
        else
        {
            throw new RuntimeException("Armor info must have a non-empty 'equipment_slot' string.");
        }

        String materialName;
        if (data.has("material"))
        {
            String str = data.get("material").getAsString();
            if (!Strings.isNullOrEmpty(str))
            {
                materialName = str;
            }
            else
            {
                throw new RuntimeException("Armor material must be a non-empty string.");
            }
        }
        else
        {
            throw new RuntimeException("Armor info must have a non-empty 'material' string.");
        }

        final EquipmentSlotType slot = EquipmentSlotType.byName(slotName);
        final ArmorMaterial material = ArmorMaterial.valueOf(materialName.toUpperCase());

        return new ItemFactory<>((props, builder) -> new FlexArmorItem(material, slot, props));
    });

    public static final ItemType<FlexSwordItem> SWORD = registerTool2("sword", FlexSwordItem::new, ToolType.get("sword"));
    public static final ItemType<FlexShovelItem> SHOVEL = registerTool("shovel", FlexShovelItem::new, ToolType.SHOVEL);
    public static final ItemType<FlexAxeItem> AXE = registerTool("axe", FlexAxeItem::new, ToolType.AXE);
    public static final ItemType<FlexPickaxeItem> PICKAXE = registerTool2("pickaxe", FlexPickaxeItem::new, ToolType.PICKAXE);
    public static final ItemType<FlexHoeItem> HOE = registerTool2("hoe", FlexHoeItem::new, ToolType.HOE);

    public static final ItemType<FlexDiggerItem> DIGGER = register("digger", data -> {

        String tier = parseTier(data);

        float damage = JSONUtils.getAsInt(data, "damage");
        float speed = JSONUtils.getAsFloat(data, "speed");

        return new ItemFactory<>((props, builder) -> new FlexDiggerItem(getTier(tier), damage, speed, props));
    });

    private static <T extends ToolItem & IFlexItem> ItemType<T> registerTool(String name, DiggerFactory<T> factory, ToolType defaultToolType)
    {
        return register(name, data -> {

            String tierName = parseTier(data);

            float damage = JSONUtils.getAsFloat(data, "damage");
            float speed = JSONUtils.getAsFloat(data, "speed");

            IItemTier tier = getTier(tierName);
            Pair<ToolType, Integer> defaultTool = Pair.of(defaultToolType, tier.getLevel());

            return new ItemFactory<>((props, builder) -> factory.create(tier, damage, speed, props), defaultTool);
        });
    }

    private static <T extends TieredItem & IFlexItem> ItemType<T> registerTool2(String name, DiggerFactory2<T> factory, ToolType defaultToolType)
    {
        return register(name, data -> {

            String tierName = parseTier(data);

            int damage = JSONUtils.getAsInt(data, "damage");
            float speed = JSONUtils.getAsFloat(data, "speed");

            IItemTier tier = getTier(tierName);
            Pair<ToolType, Integer> defaultTool = Pair.of(defaultToolType, tier.getLevel());

            return new ItemFactory<>((props, builder) -> factory.create(tier, damage, speed, props), defaultTool);
        });
    }

    public static final ItemType<FlexTieredItem> TIERED = registerTiered("tiered", FlexTieredItem::new);

    private static <T extends TieredItem & IFlexItem> ItemType<T> registerTiered(String name, TieredFactory<T> factory)
    {
        return register(name, data -> {

            String tier = parseTier(data);

            return new ItemFactory<>((props, builder) -> factory.create(getTier(tier), props));
        });
    }

    @FunctionalInterface
    public interface DiggerFactory<T extends TieredItem & IFlexItem>
    {
        T create(IItemTier tier, float damage, float speed, Item.Properties properties);
    }

    @FunctionalInterface
    public interface DiggerFactory2<T extends TieredItem & IFlexItem>
    {
        T create(IItemTier tier, int damage, float speed, Item.Properties properties);
    }

    @FunctionalInterface
    public interface TieredFactory<T extends TieredItem & IFlexItem>
    {
        T create(IItemTier tier, Item.Properties properties);
    }

    private static IItemTier getTier(String tierName)
    {
        return Utils.getOrCrash(ThingRegistries.ITEM_TIERS, new ResourceLocation(tierName));
    }

    private static String parseTier(JsonObject data)
    {
        String tierName;
        if (data.has("tier"))
        {
            String str = data.get("tier").getAsString();
            if (!Strings.isNullOrEmpty(str))
            {
                tierName = str;
            }
            else
            {
                throw new RuntimeException("Tool tier must be a non-empty string.");
            }
        }
        else
        {
            throw new RuntimeException("Tool info must have a non-empty 'tier' string.");
        }

        return tierName;
    }

    public static void init()
    {
        /* do nothing */
    }

    public static <T extends Item & IFlexItem> ItemType<T> register(String name, IItemSerializer<T> factory)
    {
        return Registry.register(ThingRegistries.ITEM_TYPES, name, new ItemType<>(factory));
    }

    private final IItemSerializer<T> factory;

    private ItemType(IItemSerializer<T> factory)
    {
        this.factory = factory;
    }

    public ItemFactory<T> getFactory(JsonObject data)
    {
        return factory.createFactory(data);
    }

    public String toString()
    {
        return "ItemType{" + ThingRegistries.ITEM_TYPES.getKey(this) + "}";
    }
}
