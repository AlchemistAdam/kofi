## What is KoFi

KoFi is both a data-interchange format for Java, and also an implementation of
the format. Heavily inspired from INI and JSON.

The word "KoFi" is an acronym of the Danish word "konfigurationsfil", which
translates as "configuration file". This is among one of the use cases I had in
mind when creating the project.

KoFi is not meant to change the world; there are already plenty of formats for
configuration files and data-interchange (such as INI and JSON), but I had fun
making it and have found it very useful in other projects. I hope you will too.

## How to use KoFi

A thorough guide for KoFi will be in the wiki section once I get it set up.

For now, I suggest looking at the
[Document](src/main/java/dk/martinu/kofi/Document.java) class, as this is the
core of KoFi. The [DocumentIO](src/main/java/dk/martinu/kofi/DocumentIO.java)
and [KofiCodec](src/main/java/dk/martinu/kofi/codecs/KofiCodec.java) classes
also have useful information. Almost all source code is documented, and
frequently cross-references relevant code.

## Example

The following is a brief example of what KoFi can do. It shows some formatted
data and how it is interacted with in code:

*sampleData.kofi*
```ini
; this document describes two pets
name = "Van"
type = "cat"
age = 2
[obj]
dogObject = { name: "Susie", type: "dog", age: 5 }
```

*Pet.java*
```java
import java.nio.file.Paths;
import dk.martinu.kofi.*;
class Pet {
    public String name;
    public String type;
    public int age;
    public Pet() {}
    public Pet(String name, String type, int age) {
        this.type = type;
        this.name = name;
        this.age = age;
    }
    public static void main(String[] args) throws Exception {
        Document doc = DocumentIO.readFile(Paths.get("sampleData.kofi"));
        Pet p0 = new Pet(doc.getString("type"), 
                doc.getString("name"), doc.getInt("age"));
        System.out.println(p0.name + " the " + p0.type 
                + " is " + p0.age + " years old.");
        Pet p1 = doc.getObject("obj", "dogObject").construct(Pet.class);
        System.out.println(p1.name + " the " + p1.type
                + " is " + p1.age + " years old.");
    }
}
```

*output*
```text
Van the cat is 2 years old.
Susie the dog is 5 years old.
```