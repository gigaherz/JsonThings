package dev.gigaherz.jsonthings.mixin;

import dev.gigaherz.jsonthings.things.parsers.ThingResourceManager;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.server.Main.class)
public class ServerMainMixin
{
    @Redirect(method = "main([Ljava/lang/String;)V",
            at = @At(value = "NEW", target = "([Lnet/minecraft/resources/IPackFinder;)Lnet/minecraft/resources/ResourcePackList;")
    )
    public ResourcePackList redirectPackListCreation(IPackFinder... finders)
    {
        ResourcePackList list = new ResourcePackList(finders);
        list.addPackFinder(ThingResourceManager.instance().getWrappedPackFinder());
        return list;
    }
}
