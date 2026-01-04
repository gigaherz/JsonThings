package dev.gigaherz.jsonthings.client;

/*
public class DeveloperCommands
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext)
    {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("jsonthings")
                        .then(Commands.literal("item")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .then(Commands.literal("tool")
                                                .executes((ctx) -> makeTool(StringArgumentType.getString(ctx, "name")))
                                        )
                                        .then(Commands.literal("copy")
                                                .then(Commands.literal("held")
                                                        .executes((ctx) -> makeCopy(StringArgumentType.getString(ctx, "name")))
                                                )
                                        )
                                        .executes((ctx) -> makeItem(StringArgumentType.getString(ctx, "name")))
                                )
                        )

        );
    }

    private static int makeCopy(String name)
    {
        Minecraft mc = Minecraft.getInstance();
        Path folder = FMLPaths.GAMEDIR.get().resolve("jsonthings/items");
        if (!folder.toFile().mkdirs())
        {
        }
        Path file = folder.resolve("atlas.png");
        return showSuccessMessage(mc, folder, file, "Item");
    }

    private static int makeTool(String name)
    {
        return 0;
    }

    private static int makeItem(String name)
    {
        return 0;
    }

    private static int dumpHeldItem(InteractionHand hand)
    {
        Minecraft mc = Minecraft.getInstance();
        ItemStack held = Objects.requireNonNull(mc.player).getItemInHand(hand);
        if (held.getCount() <= 0)
        {
            mc.gui.getChat().addMessage(Component.literal("You must be holding an item in your " + hand + " to use this command."));
            return 0;
        }

        return dumpItemModel(held);
    }

    private static int showSuccessMessage(Minecraft mc, Path outFolder, Path outFile, String what)
    {
        MutableComponent pathComponent = Component.literal(outFile.toFile().getAbsolutePath());
        pathComponent = pathComponent.withStyle(style -> style
                .withUnderlined(true)
                .withColor(ChatFormatting.GREEN)
                .withHoverEvent(new HoverEvent.ShowText(Component.literal("Click to open folder")))
                .withClickEvent(new ClickEvent.OpenFile(outFolder.toFile().getAbsolutePath())));
        mc.gui.getChat().addMessage(Component.literal(what + " dumped to ").append(pathComponent));
        return 1;
    }

}
*/