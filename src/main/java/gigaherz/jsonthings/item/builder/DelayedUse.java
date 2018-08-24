package gigaherz.jsonthings.item.builder;

import net.minecraft.item.EnumAction;

public class DelayedUse
{
    public int useTicks;
    public EnumAction useAction;
    public CompletionMode onComplete;

    public DelayedUse(int useTicks, String useAction, String completeAction)
    {
        this.useTicks = useTicks;
        this.useAction = EnumAction.valueOf(useAction.toUpperCase());
        this.onComplete = CompletionMode.valueOf(completeAction.toUpperCase());
    }

    public enum CompletionMode
    {
        USE_ITEM,
        CONTINUE
    }
}
