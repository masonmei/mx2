// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm;

class Handler
{
    Label a;
    Label b;
    Label c;
    String d;
    int e;
    Handler f;
    
    static Handler a(Handler f, final Label label, final Label label2) {
        if (f == null) {
            return null;
        }
        f.f = a(f.f, label, label2);
        final int c = f.a.c;
        final int c2 = f.b.c;
        final int c3 = label.c;
        final int n = (label2 == null) ? Integer.MAX_VALUE : label2.c;
        if (c3 < c2 && n > c) {
            if (c3 <= c) {
                if (n >= c2) {
                    f = f.f;
                }
                else {
                    f.a = label2;
                }
            }
            else if (n >= c2) {
                f.b = label;
            }
            else {
                final Handler f2 = new Handler();
                f2.a = label2;
                f2.b = f.b;
                f2.c = f.c;
                f2.d = f.d;
                f2.e = f.e;
                f2.f = f.f;
                f.b = label;
                f.f = f2;
            }
        }
        return f;
    }
}
