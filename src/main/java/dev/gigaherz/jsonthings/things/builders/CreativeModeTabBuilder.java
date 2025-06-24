package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.misc.FlexCreativeModeTab;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.things.serializers.DelayedVariantProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import dev.gigaherz.jsonthings.things.serializers.ItemVariantProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CreativeModeTabBuilder extends BaseBuilder<FlexCreativeModeTab, CreativeModeTabBuilder>
{
    public static CreativeModeTabBuilder begin(ThingParser<FlexCreativeModeTab, CreativeModeTabBuilder> ownerParser, ResourceLocation registryName)
    {
        return new CreativeModeTabBuilder(ownerParser, registryName);
    }

    private StackContext iconItem;
    private final ArrayList<ItemVariantProvider> items = new ArrayList<>();
    private ResourceLocation[] before;
    private ResourceLocation[] after;
    private String translation_key;
    private boolean rightSide;

    private CreativeModeTabBuilder(ThingParser<FlexCreativeModeTab, CreativeModeTabBuilder> ownerParser, ResourceLocation registryName)
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

    public void setTranslationKey(String key)
    {
        this.translation_key = key;
    }

    public void setRightSide(boolean rightSide)
    {
        this.rightSide = rightSide;
    }

    @Override
    protected FlexCreativeModeTab buildInternal()
    {
        ResourceLocation registryName = getRegistryName();
        return new FlexCreativeModeTab( translation_key != null ? translation_key : registryName.getNamespace() + "." + registryName.getPath().replace("/", "."), iconItem);
    }

    public void addItem(ResourceLocation item)
    {
        this.items.add(new DelayedVariantProvider(item));
    }

    public void addItem(StackContext stackContext)
    {
        this.items.add(stackContext);
    }

    public List<ItemVariantProvider> getVariantProviders()
    {
        return items;
    }
    public CreativeModeTab buildTab(FlexCreativeModeTab tab)
    {
        var icon = tab.icon();
        var name = tab.name();
        var builder = new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0).icon(() -> icon.toStack(null))
                        .title(Component.translatable(name))
                        .displayItems((parameters, output) -> {
                            for (var variantProvider : getVariantProviders())
                            {
                                variantProvider.provideVariants(
                                        ResourceKey.create(Registries.CREATIVE_MODE_TAB, getRegistryName()),
                                        output, parameters, null, true);
                            }
                        });
                if (getBefore() != null)
                    builder = builder.withTabsAfter(getBefore());
                if (getAfter() != null)
                    builder = builder.withTabsBefore(getAfter());
                if (getRightSide())
                    builder = builder.alignedRight();
        return builder.build();
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

    @Override
    public void validate()
    {
    }
}
