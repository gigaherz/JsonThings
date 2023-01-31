useClass("dev.gigaherz.jsonthings.things.events.FlexEventContext")
use("chat")

function apply(eventName, args)
{
    var player = args.user;
    var timeLeft = args.timeLeft;

    sendSystemMessage(player, {
     text: "Use Stop! timeLeft=" +timeLeft,
     color: timeLeft <= 0 ? "green" : "blue"
    });

    return success();
}