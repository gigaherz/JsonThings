package gigaherz.jsonthings.item.builder;

public enum AttributeModifierOperation
{
    ADD(0),
    PERCENT_ADD(1),
    PERCENT_INCREASE(2);

    private final int operationCode;

    AttributeModifierOperation(int operationCode)
    {
        this.operationCode = operationCode;
    }

    public int getOperationCode()
    {
        return operationCode;
    }
}
