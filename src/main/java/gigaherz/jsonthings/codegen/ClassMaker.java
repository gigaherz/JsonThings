package gigaherz.jsonthings.codegen;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import gigaherz.jsonthings.codegen.api.*;
import gigaherz.jsonthings.codegen.codetree.ClassInfo;
import gigaherz.jsonthings.codegen.codetree.CodeBlock;
import gigaherz.jsonthings.codegen.codetree.MethodInfo;
import gigaherz.jsonthings.codegen.codetree.ValueExpression;
import gigaherz.jsonthings.codegen.type.TypeTokenProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class ClassMaker
{
    public BasicClass begin()
    {
        return new BasicClassImpl();
    }

    public class BasicClassImpl extends ClassImpl<Object, Object> implements BasicClass
    {

        public BasicClassImpl()
        {
            super(TypeToken.of(Object.class));
        }

        @Override
        public <T> DefineClass<? extends T> extending(TypeToken<T> baseClass)
        {
            Class<? super T> rawType = baseClass.getRawType();
            if (baseClass.isArray())
                throw new IllegalStateException("The provided class " + baseClass + " is an array!");
            if (baseClass.isPrimitive())
                throw new IllegalStateException("The provided class " + baseClass + " is a primitive type!");
            if (rawType.isInterface())
                throw new IllegalStateException("The provided class " + baseClass + " is an interface!");
            if (rawType.isEnum())
                throw new IllegalStateException("The provided class " + baseClass + " is an enum!");
            return new ClassImpl<>(baseClass, this);
        }

        @Override
        public BasicClass setPublic()
        {
            modifiers |= Modifier.PUBLIC;
            return this;
        }

        @Override
        public BasicClass setPrivate()
        {
            modifiers |= Modifier.PRIVATE;
            return this;
        }

        @Override
        public BasicClass setProtected()
        {
            modifiers |= Modifier.PROTECTED;
            return this;
        }

        @Override
        public BasicClass setFinal()
        {
            modifiers |= Modifier.FINAL;
            return this;
        }

        @Override
        public BasicClass setStatic()
        {
            modifiers |= Modifier.STATIC;
            return this;
        }

        @Override
        public BasicClass setAbstract()
        {
            modifiers |= Modifier.ABSTRACT;
            return this;
        }

        @Override
        public <A extends Annotation> BasicClass annotate(A a)
        {
            annotations.add(a);
            return this;
        }
    }

    private class ClassImpl<C, T extends C> implements DefineClass<T>
    {
        protected final List<Annotation> annotations = Lists.newArrayList();
        protected final Map<String, FieldImpl<?>> fields = Maps.newHashMap();
        protected final Multimap<String, MethodImpl<?>> methods = ArrayListMultimap.create();
        protected final TypeTokenProxy<T> thisClass = new TypeTokenProxy<>();
        protected final TypeToken<C> superClass;
        protected final List<TypeToken<?>> superInterfaces = Lists.newArrayList();
        protected int modifiers;

        public ClassImpl(TypeToken<C> baseClass)
        {
            this.superClass = baseClass;
        }

        public ClassImpl(TypeToken<C> baseClass, BasicClassImpl copyFrom)
        {
            this(baseClass);

            copyFrom.fields.forEach((k, v) -> fields.put(k, new FieldImpl(v)));
            copyFrom.methods.forEach((k, v) -> methods.put(k, new MethodImpl(v)));
            annotations.addAll(copyFrom.annotations);
        }

        @Override
        public DefineClass<T> implementing(TypeToken<?> interfaceClass)
        {
            if (!interfaceClass.getRawType().isInterface())
                throw new IllegalStateException("The provided class " + interfaceClass + " is not an interface!");
            return this;
        }

        @Override
        public <F> DefineField<T, F> field(String name, TypeToken<F> fieldType)
        {
            FieldImpl<F> field = new FieldImpl<>(fieldType);
            fields.put(name, field);
            return field;
        }

        @Override
        public <R> DefineMethod<T, R> method(String name, TypeToken<R> returnType)
        {
            return null;
        }

        @Override
        public DefineMethod<T, Void> constructor()
        {
            return null;
        }

        @Override
        public ClassInfo<T> make()
        {
            return null;
        }

        private class FieldImpl<F> implements DefineField<T, F>
        {
            protected final List<Annotation> annotations = Lists.newArrayList();
            protected final TypeToken<F> fieldType;
            protected int modifiers;
            protected ValueExpression<F> init;

            private FieldImpl(TypeToken<F> fieldType)
            {
                this.fieldType = fieldType;
            }

            public FieldImpl(FieldImpl<F> copyFrom)
            {
                this(copyFrom.fieldType);

                this.modifiers = copyFrom.modifiers;
                this.init = copyFrom.init;
                annotations.addAll(copyFrom.annotations);
            }

            @Override
            public <A extends Annotation> DefineField<T, F> annotate(A a)
            {
                annotations.add(a);
                return this;
            }

            @Override
            public DefineField<T, F> setPublic()
            {
                modifiers |= Modifier.PUBLIC;
                return this;
            }

            @Override
            public DefineField<T, F> setPrivate()
            {
                modifiers |= Modifier.PRIVATE;
                return this;
            }

            @Override
            public DefineField<T, F> setProtected()
            {
                modifiers |= Modifier.PROTECTED;
                return this;
            }

            @Override
            public DefineField<T, F> setStatic()
            {
                modifiers |= Modifier.STATIC;
                return this;
            }

            @Override
            public DefineField<T, F> setFinal()
            {
                modifiers |= Modifier.FINAL;
                return this;
            }

            @Override
            public DefineField<T, F> initializer(ValueExpression<F> expr)
            {
                init = expr;
                return this;
            }

            @Override
            public DefineClass<T> finish()
            {
                return this;
            }
        }

        private class MethodImpl<R> implements DefineMethod<T, R>
        {
            protected final List<Annotation> annotations = Lists.newArrayList();
            protected final List<ParamDefinition<?>> params = Lists.newArrayList();
            protected final TypeToken<R> returnType;
            protected int modifiers;
            protected Function<MethodInfo, CodeBlock> impl;


            public MethodImpl(TypeToken<R> returnType)
            {
                this.returnType = returnType;
            }

            public MethodImpl(MethodImpl<R> copyFrom)
            {
                this(copyFrom.returnType);
                this.modifiers = copyFrom.modifiers;
                this.impl = copyFrom.impl;
                annotations.addAll(copyFrom.annotations);
            }

            @Override
            public <A extends Annotation> DefineMethod<T, R> annotate(A a)
            {
                annotations.add(a);
                return this;
            }

            @Override
            public DefineMethod<T, R> setPublic()
            {
                modifiers |= Modifier.PUBLIC;
                return this;
            }

            @Override
            public DefineMethod<T, R> setPrivate()
            {
                modifiers |= Modifier.PRIVATE;
                return this;
            }

            @Override
            public DefineMethod<T, R> setProtected()
            {
                modifiers |= Modifier.PROTECTED;
                return this;
            }

            @Override
            public DefineMethod<T, R> setFinal()
            {
                modifiers |= Modifier.FINAL;
                return this;
            }

            @Override
            public DefineArgs0<T, R> setStatic()
            {
                modifiers |= Modifier.STATIC;
                return new DefineArgsImpl0();
            }

            @Override
            public DefineArgs1<T, R, T> setInstance()
            {
                modifiers &= ~Modifier.STATIC;
                return new DefineArgsImpl1<T>(addParam(thisClass));
            }

            @Override
            public DefineClass<T> finish()
            {
                return this;
            }

            @Override
            public DefineClass<T> makeAbstract()
            {
                this.modifiers |= Modifier.ABSTRACT;
                return finish();
            }

            protected <P> ParamDefinition<P> addParam(TypeToken<P> paramType)
            {
                ParamDefinition<P> def = new ParamDefinition<>(paramType);
                params.add(def);
                return def;
            }

            @Override
            public DefineClass<T> implementation(Function<MethodInfo, CodeBlock> code)
            {
                this.impl = code;
                return this;
            }

            private class Impl implements Implementable<T, MethodInfo>
            {
                @Override
                public DefineClass<T> finish()
                {
                    return this;
                }

                @Override
                public DefineClass<T> makeAbstract()
                {
                    modifiers |= Modifier.ABSTRACT;
                    return this;
                }

                @Override
                public DefineClass<T> implementation(Function<MethodInfo, CodeBlock> code)
                {
                    return MethodImpl.this.implementation(code);
                }
            }

            private class ImplWithParam<P, Z extends DefineParam<T, P, Z>> extends Impl implements DefineParam<T, P, Z>
            {
                protected final ParamDefinition<P> param;

                private ImplWithParam(ParamDefinition<P> param)
                {
                    this.param = param;
                }

                @Override
                public <A extends Annotation> Z annotate(A a)
                {
                    param.annotations.add(a);
                    return (Z) this;
                }

                @Override
                public Z withName(String name)
                {
                    param.name = name;
                    return (Z) this;
                }
            }

            private class DefineArgsImpl0 extends Impl implements DefineArgs0<T, R>
            {
                @Override
                public <P> DefineArgs1<T, R, P> param(TypeToken<P> paramClass)
                {
                    return new DefineArgsImpl1<>(addParam(paramClass));
                }
            }

            private class DefineArgsImpl1<P0> extends ImplWithParam<P0, DefineArgs1<T, R, P0>>
                    implements DefineArgs1<T, R, P0>
            {
                private DefineArgsImpl1(ParamDefinition<P0> param)
                {
                    super(param);
                }

                @Override
                public <P> DefineArgs2<T, R, P0, P> param(TypeToken<P> paramClass)
                {
                    return new DefineArgsImpl2<>(addParam(paramClass));
                }
            }

            private class DefineArgsImpl2<P0, P1> extends ImplWithParam<P1, DefineArgs2<T, R, P0, P1>>
                    implements DefineArgs2<T, R, P0, P1>
            {

                private DefineArgsImpl2(ParamDefinition<P1> param)
                {
                    super(param);
                }

                @Override
                public <P> DefineArgs3<T, R, P0, P1, P> param(TypeToken<P> paramClass)
                {
                    return new DefineArgsImpl3<>(addParam(paramClass));
                }
            }

            private class DefineArgsImpl3<P0, P1, P2> extends ImplWithParam<P2, DefineArgs3<T, R, P0, P1, P2>>
                    implements DefineArgs3<T, R, P0, P1, P2>
            {

                private DefineArgsImpl3(ParamDefinition<P2> param)
                {
                    super(param);
                }

                @Override
                public <P> DefineArgs4<T, R, P0, P1, P2, P> param(TypeToken<P> paramClass)
                {
                    return new DefineArgsImpl4<>(addParam(paramClass));
                }
            }

            private class DefineArgsImpl4<P0, P1, P2, P3> extends ImplWithParam<P3, DefineArgs4<T, R, P0, P1, P2, P3>>
                    implements DefineArgs4<T, R, P0, P1, P2, P3>
            {

                private DefineArgsImpl4(ParamDefinition<P3> param)
                {
                    super(param);
                }

                @Override
                public <P> DefineArgs5<T, R, P0, P1, P2, P3, P> param(TypeToken<P> paramClass)
                {
                    return new DefineArgsImpl5<>(addParam(paramClass));
                }
            }

            private class DefineArgsImpl5<P0, P1, P2, P3, P4>
                    extends ImplWithParam<P4, DefineArgs5<T, R, P0, P1, P2, P3, P4>>
                    implements DefineArgs5<T, R, P0, P1, P2, P3, P4>
            {

                private DefineArgsImpl5(ParamDefinition<P4> param)
                {
                    super(param);
                }

                @Override
                public <P> DefineArgs6<T, R, P0, P1, P2, P3, P4, P> param(TypeToken<P> paramClass)
                {
                    return new DefineArgsImpl6<>(addParam(paramClass));
                }
            }

            private class DefineArgsImpl6<P0, P1, P2, P3, P4, P5>
                    extends ImplWithParam<P5, DefineArgs6<T, R, P0, P1, P2, P3, P4, P5>>
                    implements DefineArgs6<T, R, P0, P1, P2, P3, P4, P5>
            {

                private DefineArgsImpl6(ParamDefinition<P5> param)
                {
                    super(param);
                }

                @Override
                public <P> DefineArgs7<T, R, P0, P1, P2, P3, P4, P5, P> param(TypeToken<P> paramClass)
                {
                    return new DefineArgsImpl7<>(addParam(paramClass));
                }
            }

            private class DefineArgsImpl7<P0, P1, P2, P3, P4, P5, P6>
                    extends ImplWithParam<P6, DefineArgs7<T, R, P0, P1, P2, P3, P4, P5, P6>>
                    implements DefineArgs7<T, R, P0, P1, P2, P3, P4, P5, P6>
            {

                private DefineArgsImpl7(ParamDefinition<P6> param)
                {
                    super(param);
                }

                @Override
                public <P> DefineArgs8<T, R, P0, P1, P2, P3, P4, P5, P6, P> param(TypeToken<P> paramClass)
                {
                    return new DefineArgsImpl8<>(addParam(paramClass));
                }
            }

            private class DefineArgsImpl8<P0, P1, P2, P3, P4, P5, P6, P7>
                    extends ImplWithParam<P7, DefineArgs8<T, R, P0, P1, P2, P3, P4, P5, P6, P7>>
                    implements DefineArgs8<T, R, P0, P1, P2, P3, P4, P5, P6, P7>
            {
                private DefineArgsImpl8(ParamDefinition<P7> param)
                {
                    super(param);
                }
            }

            public class ParamDefinition<P>
            {
                protected final List<Annotation> annotations = Lists.newArrayList();
                protected final TypeToken<P> paramType;
                protected String name;

                public ParamDefinition(TypeToken<P> paramType)
                {
                    this.paramType = paramType;
                }
            }
        }
    }
}
