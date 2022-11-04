# Patching the java.base Module
Example project to patch the Java Double implementation in order to optimize string parsing with the FastDoubleParser

## How to compile and use
### Compiling the patched java.base module
```bash
javac --patch-module java.base=src -d target/java.base -cp src/  src/java/lang/Double.java
```
### Build patch module .jar
```bash
jar cvf  java.base.jar -C target/java.base .
```
### Compile example program
```bash
javac -d "example.program/target" example.program/src/example/sql/Main.java
```
### Run example program with the patch
```bash
java --patch-module java.base=target/java.base -cp postgresql-x.x.x.jar:example.program/target/ example.sql.Main
```
**or**
```
java --patch-module java.base=java.base.jar -cp postgresql-x.x.x.jar:example.program/target/ example.sql.Main
```
## Patches made to Double.java
```java
public static double parseDouble(String s) throws NumberFormatException {
    if (FastDoubleParser.isFast())
    {
        //System.out.println("Going fast");
        return FastDoubleParser.parseDouble(s);
    }
    return FloatingDecimal.parseDouble(s);
}
```

### Toggle with ENV variable JAVA_FAST_DOUBLE_PARSER
```java
class FastDoubleParser {
    static private final String ENABLE_ENV = System.getenv("JAVA_FAST_DOUBLE_PARSER");
    public static boolean isFast()
    {
        return ENABLE_ENV != null;
    }
}
```

### Adjust FastParser to not recursively call Double.parseDouble()
One example of this...
```java
@Override
long valueOfFloatLiteral(CharSequence str, int startIndex, int endIndex, boolean isNegative,
    long significand, int exponent, boolean isSignificandTruncated,
    int exponentOfTruncatedSignificand)
{
    double d = FastDoubleMath.tryDecFloatToDoubleTruncated(isNegative, significand, exponent, isSignificandTruncated,
    exponentOfTruncatedSignificand);
    return Double.doubleToRawLongBits(Double.isNaN(d) ? FloatingDecimal.parseDouble(str.subSequence(startIndex, endIndex).toString()) : d);
}
```

## Attempting to patch with a .jar 
```bash
❯ jar cvf java.base.jar -C target/java.base .
```
```bash
❯ java --patch-module java.base=java.base.jar -verbose:class --list-modules | grep Double
[0.011s][info][class,load] java.lang.Double source: java.base.jar
```
```bash
❯ java --patch-module java.base=target/java.base -verbose:class --list-modules | grep Double
[0.011s][info][class,load] java.lang.Double source: target/java.base
```

## Project Tree
```
├── READEME.md
├── example.program
│   ├── example.program.iml
│   ├── src
│   │   ├── example
│   │   │   └── sql
│   │   │       └── Main.java
│   │   └── module-info.java
│   └── target
│       └── example
│           └── sql
│               └── Main.class
├── java.base.iml
├── src
│   ├── ch
│   │   └── randelshofer
│   │       └── fastdoubleparser
│   │           ├── AbstractFloatValueParser.java
│   │           ├── AbstractFloatingPointBitsFromByteArray.java
│   │           ├── AbstractFloatingPointBitsFromCharArray.java
│   │           ├── AbstractFloatingPointBitsFromCharSequence.java
│   │           ├── DoubleBitsFromByteArray.java
│   │           ├── DoubleBitsFromCharArray.java
│   │           ├── DoubleBitsFromCharSequence.java
│   │           ├── FastDoubleMath.java
│   │           ├── FastDoubleParser.java
│   │           ├── FastDoubleSwar.java
│   │           ├── FastFloatMath.java
│   │           ├── FastFloatParser.java
│   │           ├── FloatBitsFromByteArray.java
│   │           ├── FloatBitsFromCharArray.java
│   │           ├── FloatBitsFromCharSequence.java
│   │           └── package-info.java
│   ├── java
│   │   └── lang
│   │       └── Double.java
│   └── module-info.java
└── target
    └── java.base
        ├── ch
        │   └── randelshofer
        │       └── fastdoubleparser
        │           ├── AbstractFloatValueParser.class
        │           ├── AbstractFloatingPointBitsFromByteArray.class
        │           ├── AbstractFloatingPointBitsFromCharArray.class
        │           ├── AbstractFloatingPointBitsFromCharSequence.class
        │           ├── DoubleBitsFromByteArray.class
        │           ├── DoubleBitsFromCharArray.class
        │           ├── DoubleBitsFromCharSequence.class
        │           ├── FastDoubleMath$UInt128.class
        │           ├── FastDoubleMath.class
        │           ├── FastDoubleParser.class
        │           └── FastDoubleSwar.class
        ├── java
        │   └── lang
        │       └── Double.class
        └── module-info.class
```
