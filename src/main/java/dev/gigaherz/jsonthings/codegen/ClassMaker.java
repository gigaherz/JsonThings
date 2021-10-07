package dev.gigaherz.jsonthings.codegen;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.*;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ClassInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.FieldInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ParamInfo;
import dev.gigaherz.jsonthings.codegen.codetree.ClassData;
import dev.gigaherz.jsonthings.codegen.codetree.CodeBlock;
import dev.gigaherz.jsonthings.codegen.codetree.ValueExpression;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class ClassMaker
{
    public static boolean generateMethodParameterTable = false;

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
        public <T> ClassDef<? extends T> extending(TypeToken<T> baseClass)
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

    public class ClassImpl<C, T extends C> implements ClassDef<T>
    {
        protected final List<Annotation> annotations = Lists.newArrayList();
        protected final List<FieldImpl<?>> fields = Lists.newArrayList();
        protected final List<MethodImpl<?>> constructors = Lists.newArrayList();
        protected final List<MethodImpl<?>> methods = Lists.newArrayList();
        protected final TypeToken<C> superClass;
        protected final List<TypeToken<?>> superInterfaces = Lists.newArrayList();
        protected int modifiers;

        private static int nextClassId = 1;
        private final int classId = (nextClassId++);// + classId
        protected String name = /*this.getClass().getPackageName() + "." + */ "C" + classId;
        protected String fullName = this.getClass().getPackageName() + "." + name;

        public ClassImpl(TypeToken<C> baseClass)
        {
            this.superClass = baseClass;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public ClassImpl(TypeToken<C> baseClass, BasicClassImpl copyFrom)
        {
            this(baseClass);

            copyFrom.fields.forEach((v) -> fields.add(new FieldImpl(v)));
            copyFrom.methods.forEach((v) -> methods.add(new MethodImpl(v)));
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
            FieldImpl<F> field = new FieldImpl<>(name, fieldType);
            fields.add(field);
            return field;
        }

        @Override
        public <R> DefineMethod<T, R> method(String name, TypeToken<R> returnType)
        {
            var m = new MethodImpl<>(name, returnType);
            methods.add(m);
            return m;
        }

        @Override
        public DefineMethod<T, Void> constructor()
        {
            var m = new MethodImpl<>("<init>", TypeToken.of(void.class));
            constructors.add(m);
            return m;
        }

        public byte[] makeClass()
        {
            var cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

            cw.visit(Opcodes.V16, modifiers | Opcodes.ACC_SUPER, getInternalName(), getSignature(),
                    TypeProxy.of(superClass).getInternalName(),
                    superInterfaces.stream().map(iface -> TypeProxy.of(iface).getInternalName()).toArray(String[]::new));

                /*for(var ann : annotations)
                {
                    cw.visitAnnotation(ann.annotationType())
                }*/

            for(var fi : fields)
            {
                var fname = fi.name;

                var fv = cw.visitField(fi.modifiers, fname, TypeProxy.getTypeDescriptor(fi.fieldType), TypeProxy.getTypeSignature(fi.fieldType), null);

                    /*for(var ann : fi.annotations)
                    {
                        fv.visitAnnotation(ann.annotationType())
                    }*/

                //fv.visitTypeAnnotation()

                //fv.visitAttribute();

                fv.visitEnd();
            }


            for(var mi : constructors)
            {
                var mv = cw.visitMethod(mi.modifiers, mi.name, mi.getDescriptor(), mi.getSignature(), mi.getExceptions());

                    /*for(var ann : mi.annotations)
                    {
                        mv.visitAnnotation(ann.annotationType())
                    }*/

                if (generateMethodParameterTable)
                {
                    for (var param : mi.params)
                    {
                        if (param.name == null)
                            continue;
                        mv.visitParameter(param.name, param.modifiers);
                        // mv.visitParameterAnnotation()
                    }
                }

                if ((mi.modifiers & Opcodes.ACC_ABSTRACT) == 0)
                {
                    CodeBlock<?> code = CodeBlock.begin(mi);

                    //noinspection unchecked,rawtypes
                    mi.impl.accept((CodeBlock)code);

                    var insns = code.instructions();

                    Label startLabel = code.startLabel;

                    mv.visitCode();

                    if ((mi.modifiers & Opcodes.ACC_STATIC) == 0)
                    {
                        // super

                        if (code.instructions().size() > 0 && code.instructions().get(0) instanceof CodeBlock.SuperCall sc)
                        {
                            insns.remove(0);

                            sc.compile(mv);
                        }
                        else
                        {
                            // default constructor

                            mv.visitLabel(startLabel = new Label());
                            mv.visitVarInsn(Opcodes.ALOAD,0);
                            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, TypeProxy.of(superClass).getInternalName(), "<init>", "()V", false);
                        }
                    }

                    // field initializers

                    for (var fi : fields)
                    {
                        var fname = fi.name;

                        if (fi.init != null)
                        {
                            mv.visitLabel(new Label());

                            mv.visitVarInsn(Opcodes.ALOAD,0); // this

                            fi.init.compile(mv);

                            mv.visitFieldInsn(Opcodes.PUTFIELD, mi.owner().thisType().getInternalName(), fname, TypeProxy.getTypeDescriptor(fi.fieldType));
                        }
                    }

                    for(var ins : insns)
                    {
                        ins.compile(mv);
                    }

                    var endLabel = new Label();
                    mv.visitLabel(endLabel);

                    // locals
                    for(var local : code.locals)
                    {
                        var n = local.name == null ? "this" : local.name;
                        mv.visitLocalVariable(n, local.variableType.getDescriptor(), local.variableType.getSignature(), startLabel, endLabel, local.index);
                        // mv.visitLocalVariableAnnotation
                    }
                }
                mv.visitEnd();
            }

            for(var mi : methods)
            {
                var mname = mi.name;

                var mv = cw.visitMethod(mi.modifiers, mname, mi.getDescriptor(), mi.getSignature(), mi.getExceptions());

                    /*for(var ann : mi.annotations)
                    {
                        mv.visitAnnotation(ann.annotationType())
                    }*/

                if (generateMethodParameterTable)
                {
                    for (var param : mi.params)
                    {
                        if (param.name == null)
                            continue;
                        mv.visitParameter(param.name, param.modifiers);

                        // mv.visitParameterAnnotation()
                    }
                }

                if ((mi.modifiers & Opcodes.ACC_ABSTRACT) == 0)
                {
                    CodeBlock<?> code = CodeBlock.begin(mi);

                    //noinspection unchecked,rawtypes
                    mi.impl.accept((CodeBlock)code);

                    var insns = code.instructions();

                    Label startLabel = code.startLabel;

                    mv.visitCode();

                    for(var ins : insns)
                    {
                        ins.compile(mv);
                    }

                    var endLabel = new Label();
                    mv.visitLabel(endLabel);

                    // locals
                    for(var local : code.locals)
                    {
                        var n = local.name == null ? "this" : local.name;
                        mv.visitLocalVariable(n, local.variableType.getDescriptor(), local.variableType.getSignature(), startLabel, endLabel, local.index);
                        // mv.visitLocalVariableAnnotation
                    }
                }
                mv.visitEnd();
            }

            return cw.toByteArray();
        }

        @Override
        public ClassData<T> make()
        {
            return null;
        }

        @Override
        public TypeToken<T> actualType()
        {
            throw new IllegalStateException("Cannot resolve a type definition to its actual type!");
        }

        @Override
        public String getSimpleName()
        {
            return name;
        }

        @Override
        public String getInternalName()
        {
            return fullName.replace(".","/");
        }

        @Override
        public String getCanonicalName()
        {
            return fullName;
        }

        @Override
        public String getDescriptor()
        {
            return "L" + getInternalName() + ";";
        }

        @Override
        public boolean isPrimitive()
        {
            return false;
        }

        @Override
        public boolean isArray()
        {
            return false;
        }

        @Nullable
        @Override
        public Class<?> getRawType()
        {
            return null;
        }

        @Override
        public TypeToken<? super C> superClass()
        {
            return superClass;
        }

        @Override
        public TypeProxy<T> thisType()
        {
            return this;
        }

        @Override
        public List<? extends MethodInfo<?>> constructors()
        {
            return this.constructors;
        }

        @Override
        public List<? extends MethodInfo<?>> methods()
        {
            return this.methods;
        }

        @Override
        public List<? extends FieldInfo<?>> fields()
        {
            return this.fields;
        }

        @Override
        public ClassInfo<? super C> superClassInfo()
        {
            return ClassData.getSuperClassInfo(superClass);
        }

        @Override
        public Optional<FieldInfo<?>> findField(String fieldName)
        {
            return fields.stream().filter(f -> fieldName.equals(f.name)).findFirst().map(f -> f);
        }

        @Override
        public ClassDef<T> finish()
        {
            return this;
        }

        private class FieldImpl<F> implements DefineField<T, F>, FieldInfo<F>
        {
            protected final List<Annotation> annotations = Lists.newArrayList();
            protected final TypeToken<F> fieldType;
            protected int modifiers;
            protected ValueExpression<F> init;
            protected String name;

            private FieldImpl(String name, TypeToken<F> fieldType)
            {
                this.name = name;
                this.fieldType = fieldType;
            }

            public FieldImpl(FieldImpl<F> copyFrom)
            {
                this(copyFrom.name, copyFrom.fieldType);

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
            public ClassDef<T> finish()
            {
                return ClassImpl.this;
            }

            @Override
            public String name()
            {
                return this.name;
            }

            @Override
            public int modifiers()
            {
                return this.modifiers;
            }

            @Override
            public TypeToken<F> type()
            {
                return this.fieldType;
            }
        }

        private class MethodImpl<R> implements DefineMethod<T, R>, MethodInfo<R>
        {
            protected final List<Annotation> annotations = Lists.newArrayList();
            protected final List<ParamDefinition<?>> params = Lists.newArrayList();
            protected final List<TypeToken<? extends Throwable>> exceptions = Lists.newArrayList();
            protected final String name;
            protected final TypeToken<R> returnType;
            protected int modifiers;
            protected Consumer<CodeBlock<R>> impl;


            public MethodImpl(String name, TypeToken<R> returnType)
            {
                this.name = name;
                this.returnType = returnType;
            }

            public MethodImpl(MethodImpl<R> copyFrom)
            {
                this(copyFrom.name, copyFrom.returnType);
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
                return new DefineArgsImpl1<T>(addParam(ClassImpl.this));
            }

            @Override
            public ClassDef<T> finish()
            {
                return ClassImpl.this;
            }

            @Override
            public DefineClass<T> makeAbstract()
            {
                this.modifiers |= Modifier.ABSTRACT;
                return finish();
            }

            protected <P> ParamDefinition<P> addParam(TypeProxy<P> paramType)
            {
                ParamDefinition<P> def = new ParamDefinition<>(paramType);
                params.add(def);
                return def;
            }

            @Override
            public DefineClass<T> implementation(Consumer<CodeBlock<R>> code)
            {
                this.impl = code;
                return this;
            }

            @Nullable
            public String[] getExceptions()
            {
                if (exceptions.size() > 0)
                    return exceptions.stream().map(ex -> TypeProxy.of(ex).getInternalName()).toArray(String[]::new);

                return null;
            }

            public String getDescriptor()
            {
                var sb = new StringBuilder();

                sb.append("(");

                for(var param : params)
                {
                    if (param.name == null)
                        continue;
                    sb.append(param.paramType().getDescriptor());
                }

                sb.append(")");

                sb.append(TypeProxy.getTypeDescriptor(returnType));

                return sb.toString();
            }

            @Nullable
            public String getSignature()
            {
                return null;
            }

            @Override
            public List<? extends ParamInfo<?>> params()
            {
                return params;
            }

            @Override
            public TypeToken<R> returnType()
            {
                return returnType;
            }

            @Override
            public ClassInfo<?> owner()
            {
                return ClassImpl.this;
            }

            @Override
            public String name()
            {
                return null;
            }

            @Override
            public int modifiers()
            {
                return 0;
            }

            private class Impl implements Implementable<T, R>
            {
                @Override
                public ClassDef<T> finish()
                {
                    return ClassImpl.this;
                }

                @Override
                public DefineClass<T> makeAbstract()
                {
                    modifiers |= Modifier.ABSTRACT;
                    return finish();
                }

                @Override
                public DefineClass<T> implementation(Consumer<CodeBlock<R>> code)
                {
                    return MethodImpl.this.implementation(code);
                }
            }

            private class ImplWithParam<P, Z extends DefineParam<T, R, P, Z>> extends Impl implements DefineParam<T, R, P, Z>
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
                    return new DefineArgsImpl1<>(addParam(TypeProxy.of(paramClass)));
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
                    return new DefineArgsImpl2<>(addParam(TypeProxy.of(paramClass)));
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
                    return new DefineArgsImpl3<>(addParam(TypeProxy.of(paramClass)));
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
                    return new DefineArgsImpl4<>(addParam(TypeProxy.of(paramClass)));
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
                    return new DefineArgsImpl5<>(addParam(TypeProxy.of(paramClass)));
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
                    return new DefineArgsImpl6<>(addParam(TypeProxy.of(paramClass)));
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
                    return new DefineArgsImpl7<>(addParam(TypeProxy.of(paramClass)));
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
                    return new DefineArgsImpl8<>(addParam(TypeProxy.of(paramClass)));
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

            public class ParamDefinition<P> implements ParamInfo<P>
            {
                protected final List<Annotation> annotations = Lists.newArrayList();
                protected final TypeProxy<P> paramType;
                @Nullable
                protected String name;
                protected int modifiers;

                public ParamDefinition(TypeProxy<P> paramType)
                {
                    this.paramType = paramType;
                }

                @Override
                public TypeProxy<P> paramType()
                {
                    return this.paramType;
                }

                @Nullable
                @Override
                public String name()
                {
                    return this.name;
                }
            }
        }
    }

}
