package gigaherz.jsonthings.mixin;

import gigaherz.jsonthings.things.parsers.ThingResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin
{
    @Redirect(method = "makeServerStem(Lnet/minecraft/core/RegistryAccess$RegistryHolder;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;)Lnet/minecraft/client/Minecraft$ServerStem;",
            at = @At(value = "NEW", target = "(Lnet/minecraft/server/packs/PackType;[Lnet/minecraft/server/packs/repository/RepositorySource;)Lnet/minecraft/server/packs/repository/PackRepository;")
    )
    public PackRepository redirectPackListCreation(PackType type, RepositorySource... finders)
    {
        PackRepository list = new PackRepository(type, finders);
        list.addPackFinder(ThingResourceManager.INSTANCE.getWrappedPackFinder());
        return list;
    }
}
