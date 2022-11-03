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
