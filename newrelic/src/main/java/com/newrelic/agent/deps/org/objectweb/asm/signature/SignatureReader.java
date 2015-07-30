// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.signature;

public class SignatureReader
{
    private final String a;
    
    public SignatureReader(final String a) {
        this.a = a;
    }
    
    public void accept(final SignatureVisitor signatureVisitor) {
        final String a = this.a;
        final int length = a.length();
        int n;
        if (a.charAt(0) == '<') {
            n = 2;
            char char1;
            do {
                final int index = a.indexOf(58, n);
                signatureVisitor.visitFormalTypeParameter(a.substring(n - 1, index));
                n = index + 1;
                final char char2 = a.charAt(n);
                if (char2 == 'L' || char2 == '[' || char2 == 'T') {
                    n = a(a, n, signatureVisitor.visitClassBound());
                }
                while ((char1 = a.charAt(n++)) == ':') {
                    n = a(a, n, signatureVisitor.visitInterfaceBound());
                }
            } while (char1 != '>');
        }
        else {
            n = 0;
        }
        if (a.charAt(n) == '(') {
            ++n;
            while (a.charAt(n) != ')') {
                n = a(a, n, signatureVisitor.visitParameterType());
            }
            for (int i = a(a, n + 1, signatureVisitor.visitReturnType()); i < length; i = a(a, i + 1, signatureVisitor.visitExceptionType())) {}
        }
        else {
            for (int j = a(a, n, signatureVisitor.visitSuperclass()); j < length; j = a(a, j, signatureVisitor.visitInterface())) {}
        }
    }
    
    public void acceptType(final SignatureVisitor signatureVisitor) {
        a(this.a, 0, signatureVisitor);
    }
    
    private static int a(final String s, int n, final SignatureVisitor signatureVisitor) {
        final char char1;
        switch (char1 = s.charAt(n++)) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'V':
            case 'Z': {
                signatureVisitor.visitBaseType(char1);
                return n;
            }
            case '[': {
                return a(s, n, signatureVisitor.visitArrayType());
            }
            case 'T': {
                final int index = s.indexOf(59, n);
                signatureVisitor.visitTypeVariable(s.substring(n, index));
                return index + 1;
            }
            default: {
                int n2 = n;
                int n3 = 0;
                int n4 = 0;
            Block_3:
                while (true) {
                    final char char2;
                    switch (char2 = s.charAt(n++)) {
                        case '.':
                        case ';': {
                            if (n3 == 0) {
                                final String substring = s.substring(n2, n - 1);
                                if (n4 != 0) {
                                    signatureVisitor.visitInnerClassType(substring);
                                }
                                else {
                                    signatureVisitor.visitClassType(substring);
                                }
                            }
                            if (char2 == ';') {
                                break Block_3;
                            }
                            n2 = n;
                            n3 = 0;
                            n4 = 1;
                            continue;
                        }
                        case '<': {
                            final String substring2 = s.substring(n2, n - 1);
                            if (n4 != 0) {
                                signatureVisitor.visitInnerClassType(substring2);
                            }
                            else {
                                signatureVisitor.visitClassType(substring2);
                            }
                            n3 = 1;
                        Label_0368:
                            while (true) {
                                final char char3;
                                switch (char3 = s.charAt(n)) {
                                    case '>': {
                                        break Label_0368;
                                    }
                                    case '*': {
                                        ++n;
                                        signatureVisitor.visitTypeArgument();
                                        continue;
                                    }
                                    case '+':
                                    case '-': {
                                        n = a(s, n + 1, signatureVisitor.visitTypeArgument(char3));
                                        continue;
                                    }
                                    default: {
                                        n = a(s, n, signatureVisitor.visitTypeArgument('='));
                                        continue;
                                    }
                                }
                            }
                            continue;
                        }
                    }
                }
                signatureVisitor.visitEnd();
                return n;
            }
        }
    }
}
