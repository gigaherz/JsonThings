package dev.gigaherz.jsonthings.things;

public enum UseFinishMode
{
    USE_ITEM(false),
    CONTINUE(true);

    private final boolean useOnRelease;

    UseFinishMode(boolean useOnRelease)
    {
        this.useOnRelease = useOnRelease;
    }

    public boolean isUseOnRelease()
    {
        return useOnRelease;
    }
}
