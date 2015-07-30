// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.util;

import com.newrelic.agent.deps.org.objectweb.asm.signature.SignatureVisitor;

public final class TraceSignatureVisitor extends SignatureVisitor
{
    private final StringBuffer declaration;
    private boolean isInterface;
    private boolean seenFormalParameter;
    private boolean seenInterfaceBound;
    private boolean seenParameter;
    private boolean seenInterface;
    private StringBuffer returnType;
    private StringBuffer exceptions;
    private int argumentStack;
    private int arrayStack;
    private String separator;
    
    public TraceSignatureVisitor(final int n) {
        super(327680);
        this.separator = "";
        this.isInterface = ((n & 0x200) != 0x0);
        this.declaration = new StringBuffer();
    }
    
    private TraceSignatureVisitor(final StringBuffer declaration) {
        super(327680);
        this.separator = "";
        this.declaration = declaration;
    }
    
    public void visitFormalTypeParameter(final String s) {
        this.declaration.append(this.seenFormalParameter ? ", " : "<").append(s);
        this.seenFormalParameter = true;
        this.seenInterfaceBound = false;
    }
    
    public SignatureVisitor visitClassBound() {
        this.separator = " extends ";
        this.startType();
        return this;
    }
    
    public SignatureVisitor visitInterfaceBound() {
        this.separator = (this.seenInterfaceBound ? ", " : " extends ");
        this.seenInterfaceBound = true;
        this.startType();
        return this;
    }
    
    public SignatureVisitor visitSuperclass() {
        this.endFormals();
        this.separator = " extends ";
        this.startType();
        return this;
    }
    
    public SignatureVisitor visitInterface() {
        this.separator = (this.seenInterface ? ", " : (this.isInterface ? " extends " : " implements "));
        this.seenInterface = true;
        this.startType();
        return this;
    }
    
    public SignatureVisitor visitParameterType() {
        this.endFormals();
        if (this.seenParameter) {
            this.declaration.append(", ");
        }
        else {
            this.seenParameter = true;
            this.declaration.append('(');
        }
        this.startType();
        return this;
    }
    
    public SignatureVisitor visitReturnType() {
        this.endFormals();
        if (this.seenParameter) {
            this.seenParameter = false;
        }
        else {
            this.declaration.append('(');
        }
        this.declaration.append(')');
        this.returnType = new StringBuffer();
        return new TraceSignatureVisitor(this.returnType);
    }
    
    public SignatureVisitor visitExceptionType() {
        if (this.exceptions == null) {
            this.exceptions = new StringBuffer();
        }
        else {
            this.exceptions.append(", ");
        }
        return new TraceSignatureVisitor(this.exceptions);
    }
    
    public void visitBaseType(final char c) {
        switch (c) {
            case 'V': {
                this.declaration.append("void");
                break;
            }
            case 'B': {
                this.declaration.append("byte");
                break;
            }
            case 'J': {
                this.declaration.append("long");
                break;
            }
            case 'Z': {
                this.declaration.append("boolean");
                break;
            }
            case 'I': {
                this.declaration.append("int");
                break;
            }
            case 'S': {
                this.declaration.append("short");
                break;
            }
            case 'C': {
                this.declaration.append("char");
                break;
            }
            case 'F': {
                this.declaration.append("float");
                break;
            }
            default: {
                this.declaration.append("double");
                break;
            }
        }
        this.endType();
    }
    
    public void visitTypeVariable(final String s) {
        this.declaration.append(s);
        this.endType();
    }
    
    public SignatureVisitor visitArrayType() {
        this.startType();
        this.arrayStack |= 0x1;
        return this;
    }
    
    public void visitClassType(final String s) {
        if ("java/lang/Object".equals(s)) {
            if (this.argumentStack % 2 != 0 || this.seenParameter) {
                this.declaration.append(this.separator).append(s.replace('/', '.'));
            }
        }
        else {
            this.declaration.append(this.separator).append(s.replace('/', '.'));
        }
        this.separator = "";
        this.argumentStack *= 2;
    }
    
    public void visitInnerClassType(final String s) {
        if (this.argumentStack % 2 != 0) {
            this.declaration.append('>');
        }
        this.argumentStack /= 2;
        this.declaration.append('.');
        this.declaration.append(this.separator).append(s.replace('/', '.'));
        this.separator = "";
        this.argumentStack *= 2;
    }
    
    public void visitTypeArgument() {
        if (this.argumentStack % 2 == 0) {
            ++this.argumentStack;
            this.declaration.append('<');
        }
        else {
            this.declaration.append(", ");
        }
        this.declaration.append('?');
    }
    
    public SignatureVisitor visitTypeArgument(final char c) {
        if (this.argumentStack % 2 == 0) {
            ++this.argumentStack;
            this.declaration.append('<');
        }
        else {
            this.declaration.append(", ");
        }
        if (c == '+') {
            this.declaration.append("? extends ");
        }
        else if (c == '-') {
            this.declaration.append("? super ");
        }
        this.startType();
        return this;
    }
    
    public void visitEnd() {
        if (this.argumentStack % 2 != 0) {
            this.declaration.append('>');
        }
        this.argumentStack /= 2;
        this.endType();
    }
    
    public String getDeclaration() {
        return this.declaration.toString();
    }
    
    public String getReturnType() {
        return (this.returnType == null) ? null : this.returnType.toString();
    }
    
    public String getExceptions() {
        return (this.exceptions == null) ? null : this.exceptions.toString();
    }
    
    private void endFormals() {
        if (this.seenFormalParameter) {
            this.declaration.append('>');
            this.seenFormalParameter = false;
        }
    }
    
    private void startType() {
        this.arrayStack *= 2;
    }
    
    private void endType() {
        if (this.arrayStack % 2 == 0) {
            this.arrayStack /= 2;
        }
        else {
            while (this.arrayStack % 2 != 0) {
                this.arrayStack /= 2;
                this.declaration.append("[]");
            }
        }
    }
}
