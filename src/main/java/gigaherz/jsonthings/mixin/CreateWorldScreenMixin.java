package gigaherz.jsonthings.mixin;

import gigaherz.jsonthings.parser.ThingResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin
{
    @Redirect(method="func_243423_B()Lcom/mojang/datafixers/util/Pair;",
            at = @At(value="NEW", target="([Lnet/minecraft/resources/IPackFinder;)Lnet/minecraft/resources/ResourcePackList;")
    )
    public ResourcePackList redirectPackListCreation(IPackFinder... finders) {
        ResourcePackList list = new ResourcePackList(finders);
        list.addPackFinder(ThingResourceManager.INSTANCE.getFolderPackFinder());
        return list;
    }
}
