package dev.gigaherz.jsonthings.codegen.api;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.MethodData;

@SuppressWarnings("UnstableApiUsage")
public interface DefineMethod<C, R> extends Implementable<C, R>, Annotatable<DefineMethod<C, R>>
{
    // default: package-private
    DefineMethod<C, R> setPublic();

    DefineMethod<C, R> setPrivate();

    DefineMethod<C, R> setProtected();

    // default: non-final
    DefineMethod<C, R> setFinal();

    DefineArgs0<C, R> setStatic();

    DefineArgs0<C, R> setInstance();

    interface DefineArgs0<C, R> extends Implementable<C, R>
    {
        <P> DefineArgs1<C, R, P> param(TypeToken<P> paramClass);

        default <P> DefineArgs1<C, R, P> param(Class<P> paramClass)
        {
            return param(TypeToken.of(paramClass));
        }
    }

    public interface DefineArgs1<C, R, P0> extends DefineParam<C, R, P0, DefineArgs1<C, R, P0>>
    {
        <P> DefineArgs2<C, R, P0, P> param(TypeToken<P> paramClass);

        default <P> DefineArgs2<C, R, P0, P> param(Class<P> paramClass)
        {
            return param(TypeToken.of(paramClass));
        }
    }

    public interface DefineArgs2<C, R, P0, P1> extends DefineParam<C, R, P1, DefineArgs2<C, R, P0, P1>>
    {
        <P> DefineArgs3<C, R, P0, P1, P> param(TypeToken<P> paramClass);

        default <P> DefineArgs3<C, R, P0, P1, P> param(Class<P> paramClass)
        {
            return param(TypeToken.of(paramClass));
        }
    }

    public interface DefineArgs3<C, R, P0, P1, P2> extends DefineParam<C, R, P2, DefineArgs3<C, R, P0, P1, P2>>
    {
        <P> DefineArgs4<C, R, P0, P1, P2, P> param(TypeToken<P> paramClass);

        default <P> DefineArgs4<C, R, P0, P1, P2, P> param(Class<P> paramClass)
        {
            return param(TypeToken.of(paramClass));
        }
    }

    public interface DefineArgs4<C, R, P0, P1, P2, P3> extends DefineParam<C, R, P3, DefineArgs4<C, R, P0, P1, P2, P3>>
    {
        <P> DefineArgs5<C, R, P0, P1, P2, P3, P> param(TypeToken<P> paramClass);

        default <P> DefineArgs5<C, R, P0, P1, P2, P3, P> param(Class<P> paramClass)
        {
            return param(TypeToken.of(paramClass));
        }
    }

    public interface DefineArgs5<C, R, P0, P1, P2, P3, P4>
            extends DefineParam<C, R, P4, DefineArgs5<C, R, P0, P1, P2, P3, P4>>
    {
        <P> DefineArgs6<C, R, P0, P1, P2, P3, P4, P> param(TypeToken<P> paramClass);

        default <P> DefineArgs6<C, R, P0, P1, P2, P3, P4, P> param(Class<P> paramClass)
        {
            return param(TypeToken.of(paramClass));
        }
    }

    public interface DefineArgs6<C, R, P0, P1, P2, P3, P4, P5>
            extends DefineParam<C, R, P5, DefineArgs6<C, R, P0, P1, P2, P3, P4, P5>>
    {
        <P> DefineArgs7<C, R, P0, P1, P2, P3, P4, P5, P> param(TypeToken<P> paramClass);

        default <P> DefineArgs7<C, R, P0, P1, P2, P3, P4, P5, P> param(Class<P> paramClass)
        {
            return param(TypeToken.of(paramClass));
        }
    }

    public interface DefineArgs7<C, R, P0, P1, P2, P3, P4, P5, P6>
            extends DefineParam<C, R, P6, DefineArgs7<C, R, P0, P1, P2, P3, P4, P5, P6>>
    {
        <P> DefineArgs8<C, R, P0, P1, P2, P3, P4, P5, P6, P> param(TypeToken<P> paramClass);

        default <P> DefineArgs8<C, R, P0, P1, P2, P3, P4, P5, P6, P> param(Class<P> paramClass)
        {
            return param(TypeToken.of(paramClass));
        }
    }

    public interface DefineArgs8<C, R, P0, P1, P2, P3, P4, P5, P6, P7>
            extends DefineParam<C, R, P7, DefineArgs8<C, R, P0, P1, P2, P3, P4, P5, P6, P7>>
    {

    }
}
