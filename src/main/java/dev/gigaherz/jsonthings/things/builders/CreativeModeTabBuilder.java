package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.misc.FlexCreativeModeTab;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.things.serializers.ItemVariantProvider;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CreativeModeTabBuilder extends BaseBuilder<FlexCreativeModeTab, CreativeModeTabBuilder>
{
    public static CreativeModeTabBuilder begin(ThingParser<CreativeModeTabBuilder> ownerParser, ResourceLocation registryName)
    {
        return new CreativeModeTabBuilder(ownerParser, registryName);
    }

    private StackContext iconItem;
    private final ArrayList<ItemVariantProvider> items = new ArrayList<>();
    private ResourceLocation[] before;
    private ResourceLocation[] after;
    private String translation_key;
    private boolean rightSide;

    private CreativeModeTabBuilder(ThingParser<CreativeModeTabBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Creative Mode Tab";
    }

    public void setIcon(StackContext stackContext)
    {
        this.iconItem = stackContext;
    }

    public void setBefore(ResourceLocation... before)
    {
        this.before = before;
    }

    public void setAfter(ResourceLocation... after)
    {
        this.after = after;
    }

    @Override
    protected FlexCreativeModeTab buildInternal()
    {
        ResourceLocation registryName = getRegistryName();
        return new FlexCreativeModeTab( translation_key != null ? translation_key : registryName.getNamespace() + "." + registryName.getPath().replace("/", "."), iconItem);
    }

    public void addItem(ResourceLocation item)
    {
        this.items.add(new ItemVariantProvider()
        {
            final ResourceLocation itemName = item;
            private ItemVariantProvider actualVariant;

            final
            @Override
            public void provideVariants(ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.Output output, CreativeModeTab.ItemDisplayParameters parameters, @Nullable ItemBuilder context, boolean explicit)
            {
                if (actualVariant == null)
                {
                    var builder = JsonThings.itemParser.getBuildersMap().get(itemName);
                    if (builder != null)
                    {
                        actualVariant = builder;
                    }
                    else
                    {
                        var item = Utils.getItemOrCrash(itemName);
                        if (item instanceof ItemVariantProvider provider)
                        {
                            actualVariant = provider;
                        }
                        else
                        {
                            actualVariant = (tabKey1, output1, parameters1, context1, explicit1) -> {
                                output1.accept(item.getDefaultInstance());
                            };
                        }
                    }
                }

                actualVariant.provideVariants(tabKey, output, parameters, context, explicit);
            }
        });
    }

    public void addItem(StackContext stackContext)
    {
        this.items.add(stackContext);
    }

    public List<StackContext> getItems()
    {
        return items.stream().filter(t -> t instanceof StackContext).map(t -> (StackContext)t).toList();
    }

    public List<ItemVariantProvider> getVariantProviders()
    {
        return items;
    }

    public ResourceLocation @Nullable[] getBefore()
    {
        return before;
    }

    public ResourceLocation @Nullable[] getAfter()
    {
        return after;
    }

    public boolean getRightSide()
    {
        return rightSide;
    }

    public void setTranslationKey(String key)
    {
        this.translation_key = key;
    }

    public void setRightSide(boolean rightSide)
    {
        this.rightSide = rightSide;
    }
}
