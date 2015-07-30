// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.util;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.newrelic.agent.deps.org.objectweb.asm.tree.analysis.Interpreter;
import com.newrelic.agent.deps.org.objectweb.asm.tree.analysis.Analyzer;
import com.newrelic.agent.deps.org.objectweb.asm.tree.analysis.BasicVerifier;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.tree.MethodNode;

class CheckMethodAdapter$1 extends MethodNode
{
    final /* synthetic */ MethodVisitor val$cmv;
    
    CheckMethodAdapter$1(final int n, final int n2, final String s, final String s2, final String s3, final String[] array, final MethodVisitor val$cmv) {
        this.val$cmv = val$cmv;
        super(n, n2, s, s2, s3, array);
    }
    
    public void visitEnd() {
        final Analyzer analyzer = new Analyzer((Interpreter)new BasicVerifier());
        try {
            analyzer.analyze("dummy", (MethodNode)this);
        }
        catch (Exception ex) {
            if (ex instanceof IndexOutOfBoundsException && this.maxLocals == 0 && this.maxStack == 0) {
                throw new RuntimeException("Data flow checking option requires valid, non zero maxLocals and maxStack values.");
            }
            ex.printStackTrace();
            final StringWriter stringWriter = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(stringWriter, true);
            CheckClassAdapter.printAnalyzerResult(this, analyzer, printWriter);
            printWriter.close();
            throw new RuntimeException(ex.getMessage() + ' ' + stringWriter.toString());
        }
        this.accept(this.val$cmv);
    }
}
