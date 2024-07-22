.source sieve.java
.class super sieve
.super java/lang/Object


.method <init> ()V
.limit stack 1
.limit locals 1
.line 3
l0:    aload_0
l1:    invokespecial java/lang/Object/<init> ()V
l4:    return

.end method

.method public static main ([Ljava/lang/String;)V
.limit stack 3
.limit locals 8
.line 7
l0:    ldc 49000
l2:    newarray boolean
l4:    astore_1
.line 8
l5:    iconst_0
l6:    istore 7
.line 9
l8:    ldc "How many iterations? "
l10:    invokestatic library/IO/write (Ljava/lang/String;)V
l13:    invokestatic library/IO/readInt ()I
l16:    istore 6
.line 10
l18:    ldc "Supply largest number to be tested "
l20:    invokestatic library/IO/write (Ljava/lang/String;)V
l23:    invokestatic library/IO/readInt ()I
l26:    istore_3
.line 11
l27:    iload_3
l28:    ldc 49000
l30:    if_icmple l39
.line 12
l33:    ldc "n too large, sorry"
l35:    invokestatic library/IO/write (Ljava/lang/String;)V
.line 13
l38:    return
.line 15
l39:    iconst_1
l40:    istore 5
.line 16
l42:    iload 5
l44:    iload 6
l46:    if_icmpgt l147
.line 17
l49:    iconst_0
l50:    istore 7
.line 18
l52:    iconst_2
l53:    istore_2
.line 19
l54:    iload_2
l55:    iload_3
l56:    if_icmpgt l72
.line 20
l59:    aload_1
l60:    iload_2
l61:    iconst_2
l62:    isub
l63:    iconst_1
l64:    bastore
.line 21
l65:    iload_2
l66:    iconst_1
l67:    iadd
l68:    istore_2
l69:    goto l54
.line 23
l72:    iconst_2
l73:    istore_2
.line 24
l74:    iload_2
l75:    iload_3
l76:    if_icmpgt l138
.line 25
l79:    aload_1
l80:    iload_2
l81:    iconst_2
l82:    isub
l83:    baload
l84:    ifeq l131
.line 26
l87:    iload 7
l89:    iconst_1
l90:    iadd
l91:    istore 7
.line 27
l93:    iload_2
l94:    istore 4
.line 28
l96:    aload_1
l97:    iload 4
l99:    iconst_2
l100:    isub
l101:    iconst_0
l102:    bastore
.line 29
l103:    iload 4
l105:    iload_2
l106:    iadd
l107:    istore 4
.line 30
l109:    iload 4
l111:    iload_3
l112:    if_icmpgt l131
.line 31
l115:    aload_1
l116:    iload 4
l118:    iconst_2
l119:    isub
l120:    iconst_0
l121:    bastore
.line 32
l122:    iload 4
l124:    iload_2
l125:    iadd
l126:    istore 4
l128:    goto l109
.line 35
l131:    iload_2
l132:    iconst_1
l133:    iadd
l134:    istore_2
l135:    goto l74
.line 37
l138:    iload 5
l140:    iconst_1
l141:    iadd
l142:    istore 5
l144:    goto l42
.line 39
l147:    iload 7
l149:    invokestatic library/IO/write (I)V
l152:    ldc " primes"
l154:    invokestatic library/IO/write (Ljava/lang/String;)V
.line 40
l157:    return

.end method

