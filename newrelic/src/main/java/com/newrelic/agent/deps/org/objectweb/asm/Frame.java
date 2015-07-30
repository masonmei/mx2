// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm;

final class Frame
{
    static final int[] a;
    Label b;
    int[] c;
    int[] d;
    private int[] e;
    private int[] f;
    private int g;
    private int h;
    private int[] i;
    
    private int a(final int n) {
        if (this.e == null || n >= this.e.length) {
            return 0x2000000 | n;
        }
        int n2 = this.e[n];
        if (n2 == 0) {
            final int[] e = this.e;
            final int n3 = 0x2000000 | n;
            e[n] = n3;
            n2 = n3;
        }
        return n2;
    }
    
    private void a(final int n, final int n2) {
        if (this.e == null) {
            this.e = new int[10];
        }
        final int length = this.e.length;
        if (n >= length) {
            final int[] e = new int[Math.max(n + 1, 2 * length)];
            System.arraycopy(this.e, 0, e, 0, length);
            this.e = e;
        }
        this.e[n] = n2;
    }
    
    private void b(final int n) {
        if (this.f == null) {
            this.f = new int[10];
        }
        final int length = this.f.length;
        if (this.g >= length) {
            final int[] f = new int[Math.max(this.g + 1, 2 * length)];
            System.arraycopy(this.f, 0, f, 0, length);
            this.f = f;
        }
        this.f[this.g++] = n;
        final int g = this.b.f + this.g;
        if (g > this.b.g) {
            this.b.g = g;
        }
    }
    
    private void a(final ClassWriter classWriter, final String s) {
        final int b = b(classWriter, s);
        if (b != 0) {
            this.b(b);
            if (b == 16777220 || b == 16777219) {
                this.b(16777216);
            }
        }
    }
    
    private static int b(final ClassWriter classWriter, final String s) {
        final int n = (s.charAt(0) == '(') ? (s.indexOf(41) + 1) : 0;
        switch (s.charAt(n)) {
            case 'V': {
                return 0;
            }
            case 'B':
            case 'C':
            case 'I':
            case 'S':
            case 'Z': {
                return 16777217;
            }
            case 'F': {
                return 16777218;
            }
            case 'J': {
                return 16777220;
            }
            case 'D': {
                return 16777219;
            }
            case 'L': {
                return 0x1700000 | classWriter.c(s.substring(n + 1, s.length() - 1));
            }
            default: {
                int n2;
                for (n2 = n + 1; s.charAt(n2) == '['; ++n2) {}
                int n3 = 0;
                switch (s.charAt(n2)) {
                    case 'Z': {
                        n3 = 16777225;
                        break;
                    }
                    case 'C': {
                        n3 = 16777227;
                        break;
                    }
                    case 'B': {
                        n3 = 16777226;
                        break;
                    }
                    case 'S': {
                        n3 = 16777228;
                        break;
                    }
                    case 'I': {
                        n3 = 16777217;
                        break;
                    }
                    case 'F': {
                        n3 = 16777218;
                        break;
                    }
                    case 'J': {
                        n3 = 16777220;
                        break;
                    }
                    case 'D': {
                        n3 = 16777219;
                        break;
                    }
                    default: {
                        n3 = (0x1700000 | classWriter.c(s.substring(n2 + 1, s.length() - 1)));
                        break;
                    }
                }
                return n2 - n << 28 | n3;
            }
        }
    }
    
    private int a() {
        if (this.g > 0) {
            final int[] f = this.f;
            final int g = this.g - 1;
            this.g = g;
            return f[g];
        }
        final int n = 50331648;
        final Label b = this.b;
        return n | -(--b.f);
    }
    
    private void c(final int n) {
        if (this.g >= n) {
            this.g -= n;
        }
        else {
            final Label b = this.b;
            b.f -= n - this.g;
            this.g = 0;
        }
    }
    
    private void a(final String s) {
        final char char1 = s.charAt(0);
        if (char1 == '(') {
            this.c((Type.getArgumentsAndReturnSizes(s) >> 2) - 1);
        }
        else if (char1 == 'J' || char1 == 'D') {
            this.c(2);
        }
        else {
            this.c(1);
        }
    }
    
    private void d(final int n) {
        if (this.i == null) {
            this.i = new int[2];
        }
        final int length = this.i.length;
        if (this.h >= length) {
            final int[] i = new int[Math.max(this.h + 1, 2 * length)];
            System.arraycopy(this.i, 0, i, 0, length);
            this.i = i;
        }
        this.i[this.h++] = n;
    }
    
    private int a(final ClassWriter classWriter, final int n) {
        int n2;
        if (n == 16777222) {
            n2 = (0x1700000 | classWriter.c(classWriter.I));
        }
        else {
            if ((n & 0xFFF00000) != 0x1800000) {
                return n;
            }
            n2 = (0x1700000 | classWriter.c(classWriter.H[n & 0xFFFFF].g));
        }
        for (int i = 0; i < this.h; ++i) {
            int n3 = this.i[i];
            final int n4 = n3 & 0xF0000000;
            final int n5 = n3 & 0xF000000;
            if (n5 == 33554432) {
                n3 = n4 + this.c[n3 & 0x7FFFFF];
            }
            else if (n5 == 50331648) {
                n3 = n4 + this.d[this.d.length - (n3 & 0x7FFFFF)];
            }
            if (n == n3) {
                return n2;
            }
        }
        return n;
    }
    
    void a(final ClassWriter classWriter, final int n, final Type[] array, final int n2) {
        this.c = new int[n2];
        this.d = new int[0];
        int i = 0;
        if ((n & 0x8) == 0x0) {
            if ((n & 0x80000) == 0x0) {
                this.c[i++] = (0x1700000 | classWriter.c(classWriter.I));
            }
            else {
                this.c[i++] = 16777222;
            }
        }
        for (int j = 0; j < array.length; ++j) {
            final int b = b(classWriter, array[j].getDescriptor());
            this.c[i++] = b;
            if (b == 16777220 || b == 16777219) {
                this.c[i++] = 16777216;
            }
        }
        while (i < n2) {
            this.c[i++] = 16777216;
        }
    }
    
    void a(final int n, final int n2, final ClassWriter classWriter, final Item item) {
        Label_2251: {
            switch (n) {
                case 0:
                case 116:
                case 117:
                case 118:
                case 119:
                case 145:
                case 146:
                case 147:
                case 167:
                case 177: {
                    break;
                }
                case 1: {
                    this.b(16777221);
                    break;
                }
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 16:
                case 17:
                case 21: {
                    this.b(16777217);
                    break;
                }
                case 9:
                case 10:
                case 22: {
                    this.b(16777220);
                    this.b(16777216);
                    break;
                }
                case 11:
                case 12:
                case 13:
                case 23: {
                    this.b(16777218);
                    break;
                }
                case 14:
                case 15:
                case 24: {
                    this.b(16777219);
                    this.b(16777216);
                    break;
                }
                case 18: {
                    switch (item.b) {
                        case 3: {
                            this.b(16777217);
                            break Label_2251;
                        }
                        case 5: {
                            this.b(16777220);
                            this.b(16777216);
                            break Label_2251;
                        }
                        case 4: {
                            this.b(16777218);
                            break Label_2251;
                        }
                        case 6: {
                            this.b(16777219);
                            this.b(16777216);
                            break Label_2251;
                        }
                        case 7: {
                            this.b(0x1700000 | classWriter.c("java/lang/Class"));
                            break Label_2251;
                        }
                        case 8: {
                            this.b(0x1700000 | classWriter.c("java/lang/String"));
                            break Label_2251;
                        }
                        case 16: {
                            this.b(0x1700000 | classWriter.c("java/lang/invoke/MethodType"));
                            break Label_2251;
                        }
                        default: {
                            this.b(0x1700000 | classWriter.c("java/lang/invoke/MethodHandle"));
                            break Label_2251;
                        }
                    }
                    break;
                }
                case 25: {
                    this.b(this.a(n2));
                    break;
                }
                case 46:
                case 51:
                case 52:
                case 53: {
                    this.c(2);
                    this.b(16777217);
                    break;
                }
                case 47:
                case 143: {
                    this.c(2);
                    this.b(16777220);
                    this.b(16777216);
                    break;
                }
                case 48: {
                    this.c(2);
                    this.b(16777218);
                    break;
                }
                case 49:
                case 138: {
                    this.c(2);
                    this.b(16777219);
                    this.b(16777216);
                    break;
                }
                case 50: {
                    this.c(1);
                    this.b(-268435456 + this.a());
                    break;
                }
                case 54:
                case 56:
                case 58: {
                    this.a(n2, this.a());
                    if (n2 <= 0) {
                        break;
                    }
                    final int a = this.a(n2 - 1);
                    if (a == 16777220 || a == 16777219) {
                        this.a(n2 - 1, 16777216);
                        break;
                    }
                    if ((a & 0xF000000) != 0x1000000) {
                        this.a(n2 - 1, a | 0x800000);
                        break;
                    }
                    break;
                }
                case 55:
                case 57: {
                    this.c(1);
                    this.a(n2, this.a());
                    this.a(n2 + 1, 16777216);
                    if (n2 <= 0) {
                        break;
                    }
                    final int a2 = this.a(n2 - 1);
                    if (a2 == 16777220 || a2 == 16777219) {
                        this.a(n2 - 1, 16777216);
                        break;
                    }
                    if ((a2 & 0xF000000) != 0x1000000) {
                        this.a(n2 - 1, a2 | 0x800000);
                        break;
                    }
                    break;
                }
                case 79:
                case 81:
                case 83:
                case 84:
                case 85:
                case 86: {
                    this.c(3);
                    break;
                }
                case 80:
                case 82: {
                    this.c(4);
                    break;
                }
                case 87:
                case 153:
                case 154:
                case 155:
                case 156:
                case 157:
                case 158:
                case 170:
                case 171:
                case 172:
                case 174:
                case 176:
                case 191:
                case 194:
                case 195:
                case 198:
                case 199: {
                    this.c(1);
                    break;
                }
                case 88:
                case 159:
                case 160:
                case 161:
                case 162:
                case 163:
                case 164:
                case 165:
                case 166:
                case 173:
                case 175: {
                    this.c(2);
                    break;
                }
                case 89: {
                    final int a3 = this.a();
                    this.b(a3);
                    this.b(a3);
                    break;
                }
                case 90: {
                    final int a4 = this.a();
                    final int a5 = this.a();
                    this.b(a4);
                    this.b(a5);
                    this.b(a4);
                    break;
                }
                case 91: {
                    final int a6 = this.a();
                    final int a7 = this.a();
                    final int a8 = this.a();
                    this.b(a6);
                    this.b(a8);
                    this.b(a7);
                    this.b(a6);
                    break;
                }
                case 92: {
                    final int a9 = this.a();
                    final int a10 = this.a();
                    this.b(a10);
                    this.b(a9);
                    this.b(a10);
                    this.b(a9);
                    break;
                }
                case 93: {
                    final int a11 = this.a();
                    final int a12 = this.a();
                    final int a13 = this.a();
                    this.b(a12);
                    this.b(a11);
                    this.b(a13);
                    this.b(a12);
                    this.b(a11);
                    break;
                }
                case 94: {
                    final int a14 = this.a();
                    final int a15 = this.a();
                    final int a16 = this.a();
                    final int a17 = this.a();
                    this.b(a15);
                    this.b(a14);
                    this.b(a17);
                    this.b(a16);
                    this.b(a15);
                    this.b(a14);
                    break;
                }
                case 95: {
                    final int a18 = this.a();
                    final int a19 = this.a();
                    this.b(a18);
                    this.b(a19);
                    break;
                }
                case 96:
                case 100:
                case 104:
                case 108:
                case 112:
                case 120:
                case 122:
                case 124:
                case 126:
                case 128:
                case 130:
                case 136:
                case 142:
                case 149:
                case 150: {
                    this.c(2);
                    this.b(16777217);
                    break;
                }
                case 97:
                case 101:
                case 105:
                case 109:
                case 113:
                case 127:
                case 129:
                case 131: {
                    this.c(4);
                    this.b(16777220);
                    this.b(16777216);
                    break;
                }
                case 98:
                case 102:
                case 106:
                case 110:
                case 114:
                case 137:
                case 144: {
                    this.c(2);
                    this.b(16777218);
                    break;
                }
                case 99:
                case 103:
                case 107:
                case 111:
                case 115: {
                    this.c(4);
                    this.b(16777219);
                    this.b(16777216);
                    break;
                }
                case 121:
                case 123:
                case 125: {
                    this.c(3);
                    this.b(16777220);
                    this.b(16777216);
                    break;
                }
                case 132: {
                    this.a(n2, 16777217);
                    break;
                }
                case 133:
                case 140: {
                    this.c(1);
                    this.b(16777220);
                    this.b(16777216);
                    break;
                }
                case 134: {
                    this.c(1);
                    this.b(16777218);
                    break;
                }
                case 135:
                case 141: {
                    this.c(1);
                    this.b(16777219);
                    this.b(16777216);
                    break;
                }
                case 139:
                case 190:
                case 193: {
                    this.c(1);
                    this.b(16777217);
                    break;
                }
                case 148:
                case 151:
                case 152: {
                    this.c(4);
                    this.b(16777217);
                    break;
                }
                case 168:
                case 169: {
                    throw new RuntimeException("JSR/RET are not supported with computeFrames option");
                }
                case 178: {
                    this.a(classWriter, item.i);
                    break;
                }
                case 179: {
                    this.a(item.i);
                    break;
                }
                case 180: {
                    this.c(1);
                    this.a(classWriter, item.i);
                    break;
                }
                case 181: {
                    this.a(item.i);
                    this.a();
                    break;
                }
                case 182:
                case 183:
                case 184:
                case 185: {
                    this.a(item.i);
                    if (n != 184) {
                        final int a20 = this.a();
                        if (n == 183 && item.h.charAt(0) == '<') {
                            this.d(a20);
                        }
                    }
                    this.a(classWriter, item.i);
                    break;
                }
                case 186: {
                    this.a(item.h);
                    this.a(classWriter, item.h);
                    break;
                }
                case 187: {
                    this.b(0x1800000 | classWriter.a(item.g, n2));
                    break;
                }
                case 188: {
                    this.a();
                    switch (n2) {
                        case 4: {
                            this.b(285212681);
                            break Label_2251;
                        }
                        case 5: {
                            this.b(285212683);
                            break Label_2251;
                        }
                        case 8: {
                            this.b(285212682);
                            break Label_2251;
                        }
                        case 9: {
                            this.b(285212684);
                            break Label_2251;
                        }
                        case 10: {
                            this.b(285212673);
                            break Label_2251;
                        }
                        case 6: {
                            this.b(285212674);
                            break Label_2251;
                        }
                        case 7: {
                            this.b(285212675);
                            break Label_2251;
                        }
                        default: {
                            this.b(285212676);
                            break Label_2251;
                        }
                    }
                    break;
                }
                case 189: {
                    final String g = item.g;
                    this.a();
                    if (g.charAt(0) == '[') {
                        this.a(classWriter, '[' + g);
                        break;
                    }
                    this.b(0x11700000 | classWriter.c(g));
                    break;
                }
                case 192: {
                    final String g2 = item.g;
                    this.a();
                    if (g2.charAt(0) == '[') {
                        this.a(classWriter, g2);
                        break;
                    }
                    this.b(0x1700000 | classWriter.c(g2));
                    break;
                }
                default: {
                    this.c(n2);
                    this.a(classWriter, item.g);
                    break;
                }
            }
        }
    }
    
    boolean a(final ClassWriter classWriter, final Frame frame, final int n) {
        boolean b = false;
        final int length = this.c.length;
        final int length2 = this.d.length;
        if (frame.c == null) {
            frame.c = new int[length];
            b = true;
        }
        for (int i = 0; i < length; ++i) {
            int a;
            if (this.e != null && i < this.e.length) {
                final int n2 = this.e[i];
                if (n2 == 0) {
                    a = this.c[i];
                }
                else {
                    final int n3 = n2 & 0xF0000000;
                    final int n4 = n2 & 0xF000000;
                    if (n4 == 16777216) {
                        a = n2;
                    }
                    else {
                        if (n4 == 33554432) {
                            a = n3 + this.c[n2 & 0x7FFFFF];
                        }
                        else {
                            a = n3 + this.d[length2 - (n2 & 0x7FFFFF)];
                        }
                        if ((n2 & 0x800000) != 0x0 && (a == 16777220 || a == 16777219)) {
                            a = 16777216;
                        }
                    }
                }
            }
            else {
                a = this.c[i];
            }
            if (this.i != null) {
                a = this.a(classWriter, a);
            }
            b |= a(classWriter, a, frame.c, i);
        }
        if (n > 0) {
            for (int j = 0; j < length; ++j) {
                b |= a(classWriter, this.c[j], frame.c, j);
            }
            if (frame.d == null) {
                frame.d = new int[1];
                b = true;
            }
            return b | a(classWriter, n, frame.d, 0);
        }
        final int n5 = this.d.length + this.b.f;
        if (frame.d == null) {
            frame.d = new int[n5 + this.g];
            b = true;
        }
        for (int k = 0; k < n5; ++k) {
            int a2 = this.d[k];
            if (this.i != null) {
                a2 = this.a(classWriter, a2);
            }
            b |= a(classWriter, a2, frame.d, k);
        }
        for (int l = 0; l < this.g; ++l) {
            final int n6 = this.f[l];
            final int n7 = n6 & 0xF0000000;
            final int n8 = n6 & 0xF000000;
            int a3;
            if (n8 == 16777216) {
                a3 = n6;
            }
            else {
                if (n8 == 33554432) {
                    a3 = n7 + this.c[n6 & 0x7FFFFF];
                }
                else {
                    a3 = n7 + this.d[length2 - (n6 & 0x7FFFFF)];
                }
                if ((n6 & 0x800000) != 0x0 && (a3 == 16777220 || a3 == 16777219)) {
                    a3 = 16777216;
                }
            }
            if (this.i != null) {
                a3 = this.a(classWriter, a3);
            }
            b |= a(classWriter, a3, frame.d, n5 + l);
        }
        return b;
    }
    
    private static boolean a(final ClassWriter classWriter, int n, final int[] array, final int n2) {
        final int n3 = array[n2];
        if (n3 == n) {
            return false;
        }
        if ((n & 0xFFFFFFF) == 0x1000005) {
            if (n3 == 16777221) {
                return false;
            }
            n = 16777221;
        }
        if (n3 == 0) {
            array[n2] = n;
            return true;
        }
        int n4;
        if ((n3 & 0xFF00000) == 0x1700000 || (n3 & 0xF0000000) != 0x0) {
            if (n == 16777221) {
                return false;
            }
            if ((n & 0xFFF00000) == (n3 & 0xFFF00000)) {
                if ((n3 & 0xFF00000) == 0x1700000) {
                    n4 = ((n & 0xF0000000) | 0x1700000 | classWriter.a(n & 0xFFFFF, n3 & 0xFFFFF));
                }
                else {
                    n4 = (-268435456 + (n3 & 0xF0000000) | 0x1700000 | classWriter.c("java/lang/Object"));
                }
            }
            else if ((n & 0xFF00000) == 0x1700000 || (n & 0xF0000000) != 0x0) {
                n4 = (Math.min((((n & 0xF0000000) == 0x0 || (n & 0xFF00000) == 0x1700000) ? 0 : -268435456) + (n & 0xF0000000), (((n3 & 0xF0000000) == 0x0 || (n3 & 0xFF00000) == 0x1700000) ? 0 : -268435456) + (n3 & 0xF0000000)) | 0x1700000 | classWriter.c("java/lang/Object"));
            }
            else {
                n4 = 16777216;
            }
        }
        else if (n3 == 16777221) {
            n4 = (((n & 0xFF00000) == 0x1700000 || (n & 0xF0000000) != 0x0) ? n : 16777216);
        }
        else {
            n4 = 16777216;
        }
        if (n3 != n4) {
            array[n2] = n4;
            return true;
        }
        return false;
    }
    
    static {
        _clinit_();
        final int[] a2 = new int[202];
        final String s = "EFFFFFFFFGGFFFGGFFFEEFGFGFEEEEEEEEEEEEEEEEEEEEDEDEDDDDDCDCDEEEEEEEEEEEEEEEEEEEEBABABBBBDCFFFGGGEDCDCDCDCDCDCDCDCDCDCEEEEDDDDDDDCDCDCEFEFDDEEFFDEDEEEBDDBBDDDDDDCCCCCCCCEFEDDDCDCDEEEEEEEEEEFEEEEEEDDEEDDEE";
        for (int i = 0; i < a2.length; ++i) {
            a2[i] = s.charAt(i) - 'E';
        }
        a = a2;
    }
    
    static /* synthetic */ void _clinit_() {
    }
}
