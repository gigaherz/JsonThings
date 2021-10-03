package dev.gigaherz.jsonthings.mixin;

import dev.gigaherz.jsonthings.things.parsers.ThingResourceManager;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin
{
    @Redirect(method = "getDataPackSelectionSettings()Lcom/mojang/datafixers/util/Pair;",
            at = @At(value = "NEW", target = "(Lnet/minecraft/server/packs/PackType;[Lnet/minecraft/server/packs/repository/RepositorySource;)Lnet/minecraft/server/packs/repository/PackRepository;")
    )
    public PackRepository redirectPackListCreation(PackType type, RepositorySource... finders)
    {
        PackRepository list = new PackRepository(type, finders);
        list.addPackFinder(ThingResourceManager.instance().getWrappedPackFinder());
        return list;
    }
}
