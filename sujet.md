# Static analysis

## Instructions

Some of the exercises in this practical session use PMD, JavaParser or require a full project as input.

To obtain and use PMD, consult the instructions given in https://pmd.github.io/pmd/pmd_userdocs_installation.html

The folder [javaparser-starter](code/javaparser-starter) contains the code of an application that uses JavaParser to print all public classes and public methods from a given project. You can use this example as a starting point for all exercises using JavaParser.

We recommend you use the following projects as input for the exercises:

- [Apache Commons Collections](https://github.com/apache/commons-collections)
- [Apache Commons CLI](https://github.com/apache/commons-cli)
- [Apache Commons Math](https://github.com/apache/commons-math)
- [Apache Commons Lang](https://github.com/apache/commons-lang)

Feel free to use any other project you want.

## Exercises

1. [TCC *vs* LCC](exercises/tcc-vs-lcc.md)

2. [Using PMD](exercises/using-pmd.md)

3. [Extending PMD](exercises/extending-pmd.md)

4. [No getter!](exercises/no-getter.md)

5. [Cyclomatic Complexity with JavaParser](exercises/jp-cc.md) 

6. [Class cohesion with JavaParser](exercises/jp-tcc.md) (bonus)


## Exercice 2

**cmd:**  pmd check -f html -R category/java/design.xml/AvoidDeeplyNestedIfStmts -d .\commons-collections\src\ -r report.html 

**AvoidDeeplyNestedIfStmts**
Évitez de créer des instructions if-then profondément imbriquées, car elles sont plus difficiles à lire et sujettes aux erreurs à maintenir.

**Résultat:**
![texte alternatif](/images/reportAvoidDeeplyNestedIfStmts.png)

**MapUtils Line 230**

```java
public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Boolean) {
                    return (Boolean) answer;
                }
                if (answer instanceof String) {
                    return Boolean.valueOf((String) answer);
                }
                if (answer instanceof Number) {
                    final Number n = (Number) answer;
                    return n.intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }
        return null;
    }
```
**Solution proposée:**

```java
public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key) {
    if (map == null) {
        return null;
    }

    final Object answer = map.get(key);
    if (answer == null) {
        return null;
    }

    if (answer instanceof Boolean) {
        return (Boolean) answer;
    }

    if (answer instanceof String) {
        return Boolean.valueOf((String) answer);
    }

    if (answer instanceof Number) {
        final Number n = (Number) answer;
        return n.intValue() != 0;
    }

    return null;
}
```


 **cmd:**  pmd check -f html -R category/java/design.xml/AvoidCatchingGenericException -d .\commons-collections\src\ -r report.html

 **AvoidCatchingGenericExeption:**
 Évitez d’attraper des exceptions génériques telles que NullPointerException, RuntimeException, Exception dans le bloc try-catch. 

**Résultat:**
![texte alternatif](/images/reportAvoidCatchingGenericExeption.png)

**TreeBidiMap Line 1118**

```java

private boolean doEquals(final Object obj, final DataElement dataElement) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        final Map<?, ?> other = (Map<?, ?>) obj;
        if (other.size() != size()) {
            return false;
        }

        if (nodeCount > 0) {
            try {
                for (final MapIterator<?, ?> it = getMapIterator(dataElement); it.hasNext(); ) {
                    final Object key = it.next();
                    final Object value = it.getValue();
                    if (!value.equals(other.get(key))) {
                        return false;
                    }
                }
            } catch (final ClassCastException | NullPointerException ex) {
                return false;
            }
        }
        return true;
    }
 
```
Ceci est un faux positif car dans les 2 exeptions le retour est le même donc c'est exactement la meme chose que de faire : 

```java
try {
    for (final MapIterator<?, ?> it = getMapIterator(dataElement); it.hasNext(); ) {
        final Object key = it.next();
        final Object value = it.getValue();
        if (!value.equals(other.get(key))) {
            return false;
        }
    }
}catch (final ClassCastException ex) {
    return false;
} catch (final NullPointerException ex) {
    return false;
}
```

## Exercice 3

```xml
<rule name="threeDeeplyNestedIf"
      language="apex"
      message="Three or more deeply nested if statement"
      class="net.sourceforge.pmd.lang.rule.xpath.XPathRule">
   <description>
      Détection de l&apos;utilisation de trois ifinstructions imbriquées
   </description>
   <priority>3</priority>
   <properties>
      <property name="xpath">
         <value>
<![CDATA[
//IfStatement[count(ancestor::IfStatement) >= 2]

]]>
         </value>
      </property>
   </properties>
</rule>
```

**Programme test**

```java
public class ThreeDeeplyNestedIftest{

	public String testThreeDeeplyNestedIf1(int value){
		if(value > 0 && value < 10){
			if(value > 3 && value < 7){
				if(value ==5)
					return "5";
			}
			return "entre 1 et 3 ou 7 et 9";
		}
		return "<0 ou >=10";
	}

	 public String testThreeDeeplyNestedIf2(int value){
		if(value > 0 && value < 10){
			if(value > 3 && value < 7){
				return "entre 4 et 6";
			}
			return "entre 1 et 3 ou 7 et 9";
		}
		return "<0 ou >=10";
	}

	 public String testThreeDeeplyNestedIf3(int value){
		if(value > 0 && value < 10){
			return "entre 1 et 9";
		}
		return "<0 ou >=10";
	}

	public String testThreeDeeplyNestedIf4(int value){
		if(value > 0 && value < 10){
			for(int i = 0; i<10; i++){
				if(i + value >3 && value - i < 7){
					for(j=0; j<10; j++){
						if(value + i + j > 7)
							return("ok");
					}
					return("ok");
				}
			}
			return "ok";
		}
		return "<0 ou >=10";
	}

}
```

**Matched nodes:**

if(value==5)...

if(value + i +j > 7)

## Exercice 4


## Exercice 5 


## Exercice 6





