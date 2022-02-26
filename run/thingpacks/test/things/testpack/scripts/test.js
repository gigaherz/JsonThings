function apply(eventName, args)
{
    var mc = Packages.net.minecraft.Minecraft.getInstance();

    LOGGER.info("Test");

    return FlexEventResult.pass(args.stack);
}