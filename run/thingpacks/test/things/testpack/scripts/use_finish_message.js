useClass("dev.gigaherz.jsonthings.things.events.FlexEventContext")
use("chat")

function apply(eventName, args)
{
    var player = args.user;
    var stack = args.stack;

    sendSystemMessage(player, "Use Finished!");

    return success(stack);
}