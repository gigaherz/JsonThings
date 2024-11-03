package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.misc.FlexCreativeModeTab;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreativeModeTabBuilder extends BaseBuilder<FlexCreativeModeTab, CreativeModeTabBuilder>
{

    public static CreativeModeTabBuilder begin(ThingParser<FlexCreativeModeTab, CreativeModeTabBuilder> ownerParser, ResourceLocation registryName)
    {
        return new CreativeModeTabBuilder(ownerParser, registryName);
    }

    private StackContext iconItem;
    private final ArrayList<StackContext> items = new ArrayList<>();

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

    @Override
    protected FlexCreativeModeTab buildInternal()
    {
        ResourceLocation registryName = getRegistryName();
        return new FlexCreativeModeTab(registryName.getNamespace() + "." + registryName.getPath().replace("/", "."), iconItem);
    }

    public void addItem(StackContext stackContext)
    {
        this.items.add(stackContext);
    }

    public List<StackContext> getItems()
    {
        return Collections.unmodifiableList(items);
    }

    public CreativeModeTab buildTab(FlexCreativeModeTab tab)
    {
        var icon = tab.icon();
        var name = tab.name();
        return new CreativeModeTab.Builder(CreativeModeTab.Row.TOP,0)
                .icon(() -> icon.toStack(null))
                .title(Component.translatable(name))
                .displayItems((parameters, output) -> {
                    for(var stackContext : this.getItems())
                    {
                        output.accept(stackContext.toStack(null));
                    }
                }).build();
    }
}
