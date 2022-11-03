# Patching the java.base Module
Example project to patch the Java Double implementation in order to optimize string parsing with the FastDoubleParser

### Challenges
* Packaging the compiled patch module into a .jar to then supply to `--patch-module` at runtime

## How to compile and use
### Compiling the patched java.base module
```bash
javac --patch-module java.base=src -d target/java.base -cp src/  src/java/lang/Double.java
```
### Compile example program
```bash
javac -d "example.program/target" example.program/src/example/sql/Main.java
```
### Run example program with the patch
```bash
java --patch-module java.base=target/java.base -cp postgresql-x.x.x.jar:example.program/target/ example.sql.Main 
```
## Patches made to Double.java
```java
public static boolean USE_FAST_PARSER = true;

public static void goFast(boolean b)
{
    USE_FAST_PARSER = b;
}

public static double parseDouble(String s) throws NumberFormatException {
    if (USE_FAST_PARSER)
        return FastDoubleParser.parseDouble(s);
    return FloatingDecimal.parseDouble(s);
}
```

### Adjust FastParser to not recursively call Double.parseDouble()
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

@Override
long valueOfHexLiteral(
        CharSequence str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent,
        boolean isSignificandTruncated, int exponentOfTruncatedSignificand)
{
    double d=FastDoubleMath.tryHexFloatToDoubleTruncated(isNegative,significand,exponent,isSignificandTruncated,
    exponentOfTruncatedSignificand);
    return Double.doubleToRawLongBits(Double.isNaN(d)?FloatingDecimal.parseDouble(str.subSequence(startIndex,endIndex).toString()):d);
}
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
