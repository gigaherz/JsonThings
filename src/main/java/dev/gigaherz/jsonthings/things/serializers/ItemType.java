package dev.gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.items.*;
import dev.gigaherz.jsonthings.util.Utils;
import joptsimple.internal.Strings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public class ItemType<T extends Item & IFlexItem>
{
    public static final ItemType<FlexItem> PLAIN = register("plain", (data) -> (props, builder) -> new FlexItem(props));

    public static final ItemType<FlexBlockItem> BLOCK = register("block", data -> {
        String name = GsonHelper.getAsString(data, "places", null);
        ResourceLocation blockName = name != null ? new ResourceLocation(name) : null;
        boolean useBlockName = GsonHelper.getAsBoolean(data, "use_block_name", true);
        return (props, builder) -> new FlexBlockItem(Utils.getBlockOrCrash(blockName != null ? blockName : builder.getRegistryName()), useBlockName, props);
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

        final EquipmentSlot slot = EquipmentSlot.byName(slotName);
        final ArmorMaterials material = ArmorMaterials.valueOf(materialName.toUpperCase());

        return (props, builder) -> new FlexArmorItem(material, slot, props);
    });

    public static final ItemType<FlexSwordItem> SWORD = register("sword", makeToolSerializer2(FlexSwordItem::new));
    public static final ItemType<FlexShovelItem> SHOVEL = register("shovel", makeToolSerializer(FlexShovelItem::new));
    public static final ItemType<FlexAxeItem> AXE = register("axe", makeToolSerializer(FlexAxeItem::new));
    public static final ItemType<FlexPickaxeItem> PICKAXE = register("pickaxe", makeToolSerializer2(FlexPickaxeItem::new));
    public static final ItemType<FlexHoeItem> HOE = register("hoe", makeToolSerializer2(FlexHoeItem::new));

    public static final ItemType<FlexDiggerItem> DIGGER = register("digger", data -> {

        String tier = parseTier(data);

        String tagName = GsonHelper.getAsString(data, "mineable");

        TagKey<Block> tag = Utils.blockTag(tagName);

        float damage = GsonHelper.getAsInt(data, "damage");
        float speed = GsonHelper.getAsFloat(data, "speed");

        return (props, builder) -> new FlexDiggerItem(getTier(tier), damage, speed, tag, props);
    });

    private static <T extends TieredItem & IFlexItem> IItemSerializer<T> makeToolSerializer(DiggerFactory<T> factory)
    {
        return data -> {

            String tier = parseTier(data);

            float damage = GsonHelper.getAsFloat(data, "damage");
            float speed = GsonHelper.getAsFloat(data, "speed");

            return (props, builder) -> factory.create(getTier(tier), damage, speed, props);
        };
    }

    private static <T extends TieredItem & IFlexItem> IItemSerializer<T> makeToolSerializer2(DiggerFactory2<T> factory)
    {
        return data -> {

            String tier = parseTier(data);

            int damage = GsonHelper.getAsInt(data, "damage");
            float speed = GsonHelper.getAsFloat(data, "speed");

            return (props, builder) -> factory.create(getTier(tier), damage, speed, props);
        };
    }

    public static final ItemType<FlexTieredItem> TIERED = register("tiered", makeTieredSerializer(FlexTieredItem::new));

    private static <T extends TieredItem & IFlexItem> IItemSerializer<T> makeTieredSerializer(TieredFactory<T> factory)
    {
        return data -> {

            String tier = parseTier(data);

            return (props, builder) -> factory.create(getTier(tier), props);
        };
    }

    @FunctionalInterface
    public interface DiggerFactory<T extends TieredItem & IFlexItem>
    {
        T create(Tier tier, float damage, float speed, Item.Properties properties);
    }

    @FunctionalInterface
    public interface DiggerFactory2<T extends TieredItem & IFlexItem>
    {
        T create(Tier tier, int damage, float speed, Item.Properties properties);
    }

    @FunctionalInterface
    public interface TieredFactory<T extends TieredItem & IFlexItem>
    {
        T create(Tier tier, Item.Properties properties);
    }

    private static Tier getTier(String tierName)
    {
        return Objects.requireNonNull(TierSortingRegistry.byName(new ResourceLocation(tierName)), "The specified tier has not been found in the tier sorting registry");
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

    public IItemFactory<T> getFactory(JsonObject data)
    {
        return factory.createFactory(data);
    }

    public String toString()
    {
        return "ItemType{" + ThingRegistries.ITEM_TYPES.getKey(this) + "}";
    }
}
