package gigaherz.jsonthings.mixin;

import gigaherz.jsonthings.things.parsers.ThingResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin
{
    @Redirect(method = "makeServerStem(Lnet/minecraft/util/registry/DynamicRegistries$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/world/storage/SaveFormat$LevelSave;)Lnet/minecraft/client/Minecraft$PackManager;",
            at = @At(value = "NEW", target = "([Lnet/minecraft/resources/IPackFinder;)Lnet/minecraft/resources/ResourcePackList;")
    )
    public ResourcePackList redirectPackListCreation(IPackFinder... finders)
    {
        ResourcePackList list = new ResourcePackList(finders);
        list.addPackFinder(ThingResourceManager.INSTANCE.getWrappedPackFinder());
        return list;
    }
}
