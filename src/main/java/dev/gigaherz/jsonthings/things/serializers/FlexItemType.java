package dev.gigaherz.jsonthings.things.serializers;

import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.CompletionMode;
import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.items.*;
import dev.gigaherz.jsonthings.util.Utils;
import joptsimple.internal.Strings;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public class FlexItemType<T extends Item & IFlexItem>
{
    public static final FlexItemType<FlexItem> PLAIN = register("plain", (data) -> (props, builder) -> {
        var useAction = builder.getUseAction();
        var useTime = builder.getUseTime();
        var useFinishMode = builder.getFinishMode();
        var attributeModifiers = builder.getAttributeModifiers();
        var lore = builder.getLore();
        return new FlexItem(props, useAction, useTime, useFinishMode, attributeModifiers, lore != null ? lore : List.of());
    });

    public static final FlexItemType<FlexBlockItem> BLOCK = register("block", data -> {
        final String name = GsonHelper.getAsString(data, "places", null);
        boolean useBlockName = GsonHelper.getAsBoolean(data, "use_block_name", true);
        return (props, builder) -> {
            var useAction = builder.getUseAction();
            var useTime = builder.getUseTime();
            var useFinishMode = builder.getFinishMode();
            var attributeModifiers = builder.getAttributeModifiers();
            var lore = builder.getLore();
            ResourceLocation blockName = name != null ? new ResourceLocation(name) : builder.getRegistryName();
            return new FlexBlockItem(RegistryObject.create(blockName, ForgeRegistries.BLOCKS), useBlockName, props,
                    useAction, useTime, useFinishMode, attributeModifiers, lore != null ? lore : List.of());
        };
    });

    public static final FlexItemType<FlexBowlFoodItem> FOOD_BOWL = register("food_bowl", (data) -> (props, builder) -> {
        var useAction = builder.getUseAction();
        var useTime = builder.getUseTime();
        var useFinishMode = builder.getFinishMode();
        var attributeModifiers = builder.getAttributeModifiers();
        var lore = builder.getLore();
        return new FlexBowlFoodItem(props, useAction, useTime, useFinishMode, attributeModifiers, lore != null ? lore : List.of());
    });

    public static final FlexItemType<FlexBucketItem> BUCKET = register("bucket", data -> {
        String name = GsonHelper.getAsString(data, "fluid", null);
        return (props, builder) ->
        {
            var useAction = builder.getUseAction();
            var useTime = builder.getUseTime();
            var useFinishMode = builder.getFinishMode();
            var attributeModifiers = builder.getAttributeModifiers();
            var lore = builder.getLore();
            ResourceLocation fluidName;
            if (name != null)
            {
                fluidName = new ResourceLocation(name);
            }
            else
            {
                var thisName = builder.getRegistryName();
                var path = thisName.getPath();
                if (path.endsWith("_bucket")) path = path.substring(0,path.length() - "_bucket".length());
                fluidName = new ResourceLocation(thisName.getNamespace(), path);
            }
            return new FlexBucketItem(Lazy.of(() -> Utils.getOrCrash(ForgeRegistries.FLUIDS, fluidName)), props,
                    useAction, useTime, useFinishMode, attributeModifiers, lore != null ? lore : List.of());
        };
    });

    public static final FlexItemType<FlexArmorItem> ARMOR = register("armor", data -> {

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

        return (props, builder) -> {
            var useAction = builder.getUseAction();
            var useTime = builder.getUseTime();
            var useFinishMode = builder.getFinishMode();
            var attributeModifiers = builder.getAttributeModifiers();
            var lore = builder.getLore();
            return new FlexArmorItem(material, slot, props,
                    useAction, useTime, useFinishMode, attributeModifiers, lore != null ? lore : List.of());
        };
    });

    public static final FlexItemType<FlexSwordItem> SWORD = register("sword", makeToolSerializer2(FlexSwordItem::new));
    public static final FlexItemType<FlexShovelItem> SHOVEL = register("shovel", makeToolSerializer(FlexShovelItem::new));
    public static final FlexItemType<FlexAxeItem> AXE = register("axe", makeToolSerializer(FlexAxeItem::new));
    public static final FlexItemType<FlexPickaxeItem> PICKAXE = register("pickaxe", makeToolSerializer2(FlexPickaxeItem::new));
    public static final FlexItemType<FlexHoeItem> HOE = register("hoe", makeToolSerializer2(FlexHoeItem::new));

    public static final FlexItemType<FlexDiggerItem> DIGGER = register("digger", data -> {

        String tier = parseTier(data);

        String tagName = GsonHelper.getAsString(data, "mineable");

        TagKey<Block> tag = Utils.blockTag(tagName);

        float damage = GsonHelper.getAsInt(data, "damage");
        float speed = GsonHelper.getAsFloat(data, "speed");

        return (props, builder) -> {
            var useAction = builder.getUseAction();
            var useTime = builder.getUseTime();
            var useFinishMode = builder.getFinishMode();
            var attributeModifiers = builder.getAttributeModifiers();
            var lore = builder.getLore();
            return new FlexDiggerItem(getTier(tier), damage, speed, tag, props,
                    useAction, useTime, useFinishMode, attributeModifiers, lore != null ? lore : List.of());
        };
    });

    private static <T extends TieredItem & IFlexItem> IItemSerializer<T> makeToolSerializer(DiggerFactory<T> factory)
    {
        return data -> {

            String tier = parseTier(data);

            float damage = GsonHelper.getAsFloat(data, "damage");
            float speed = GsonHelper.getAsFloat(data, "speed");

            return (props, builder) -> {
                var useAction = builder.getUseAction();
                var useTime = builder.getUseTime();
                var useFinishMode = builder.getFinishMode();
                var attributeModifiers = builder.getAttributeModifiers();
                var lore = builder.getLore();
                return factory.create(getTier(tier), damage, speed, props,
                        useAction, useTime, useFinishMode, attributeModifiers, lore != null ? lore : List.of());
            };
        };
    }

    private static <T extends TieredItem & IFlexItem> IItemSerializer<T> makeToolSerializer2(DiggerFactory2<T> factory)
    {
        return data -> {

            String tier = parseTier(data);

            int damage = GsonHelper.getAsInt(data, "damage");
            float speed = GsonHelper.getAsFloat(data, "speed");

            return (props, builder) -> {
                var useAction = builder.getUseAction();
                var useTime = builder.getUseTime();
                var useFinishMode = builder.getFinishMode();
                var attributeModifiers = builder.getAttributeModifiers();
                var lore = builder.getLore();
                return factory.create(getTier(tier), damage, speed, props,
                        useAction, useTime, useFinishMode, attributeModifiers, lore != null ? lore : List.of());
            };
        };
    }

    public static final FlexItemType<FlexTieredItem> TIERED = register("tiered", makeTieredSerializer(FlexTieredItem::new));

    private static <T extends TieredItem & IFlexItem> IItemSerializer<T> makeTieredSerializer(TieredFactory<T> factory)
    {
        return data -> {

            String tier = parseTier(data);

            return (props, builder) -> {
                var useAction = builder.getUseAction();
                var useTime = builder.getUseTime();
                var useFinishMode = builder.getFinishMode();
                var attributeModifiers = builder.getAttributeModifiers();
                var lore = builder.getLore();
                return factory.create(getTier(tier), props,
                        useAction, useTime, useFinishMode, attributeModifiers, lore != null ? lore : List.of());
            };
        };
    }

    @FunctionalInterface
    public interface DiggerFactory<T extends TieredItem & IFlexItem>
    {
        T create(Tier tier, float damage, float speed, Item.Properties properties,
                 UseAnim useAction, int useTime, CompletionMode useFinishMode,
                 Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributeModifiers,
                 List<MutableComponent> lore);
    }

    @FunctionalInterface
    public interface DiggerFactory2<T extends TieredItem & IFlexItem>
    {
        T create(Tier tier, int damage, float speed, Item.Properties properties,
                 UseAnim useAction, int useTime, CompletionMode useFinishMode,
                 Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributeModifiers,
                 List<MutableComponent> lore);
    }

    @FunctionalInterface
    public interface TieredFactory<T extends TieredItem & IFlexItem>
    {
        T create(Tier tier, Item.Properties properties,
                 UseAnim useAction, int useTime, CompletionMode useFinishMode,
                 Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributeModifiers,
                 List<MutableComponent> lore);
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

    public static <T extends Item & IFlexItem> FlexItemType<T> register(String name, IItemSerializer<T> factory)
    {
        return Registry.register(ThingRegistries.ITEM_TYPES, name, new FlexItemType<>(factory));
    }

    private final IItemSerializer<T> factory;

    private FlexItemType(IItemSerializer<T> factory)
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
