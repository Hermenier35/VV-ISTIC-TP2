# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

A refresher on TCC and LCC is available in the [course notes](https://oscarlvp.github.io/vandv-classes/#cohesion-graph).

## Answer

### Quand TCC et LCC produisent-ils la même valeur ? 
Les mesures de cohésion de classe serrée (Tight Class Cohesion, TCC) et de cohésiopn de classe lâche (Loose Class Cohesion, LCC) produissent la même valeur lorsque toutes les méthodes de la classe sont directement ou indirectement connectées par l'utilisation d'au moins une variable d'instance commune.
Autrement dit, le graphe de cohésion de la classe doit etre entièrement connecté,  ce qui signifie que tous les noeuds (méthodes) appartiennent au même composant fortement connexe. 

Il existe d'autres cas : 

Cas 2 : Graphe sans connexions
Si aucune méthode ne partage de variables d'instance avec une autre méthode, le graphe est totalement déconnecté. Dans ce cas :
TCC = 0, car aucune paire de méthodes n'est directement connectée.
LCC = 0, car il n'y a aucune connexion indirecte non plus. Ainsi, TCC = LCC = 0.

Cas 3 : Graphe à un seul nœud
Si la classe ne contient qu’une seule méthode, il n’y a aucune paire de méthodes à considérer. Dans ce cas, TCC et LCC sont égaux et définis comme 1 par convention, car une méthode unique est considérée comme complètement cohésive par rapport à elle-même.

Cas 4 : Paires de méthodes parfaitement connectées
Si toutes les paires de méthodes connectées directement (comptées pour TCC) forment un composant connexe sans aucune connexion supplémentaire, TCC et LCC donneront la même valeur. Cela peut se produire dans une classe dont les méthodes sont organisées en groupes totalement indépendants mais où chaque groupe est complètement connecté.



### Exemple d'une classe où TCC = LCC :
```java
public classe Rectangle {
    private int width;
    private int height;

    public Rectangle (int width, int height){
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int calculArea(){
        return width * height;
    }
    public int calculPerimeter(){
        return 2 * (width + height)
    }
}
```


Dans cet exemple, les méthodes sont getWidth, getHeight, calculArea, calculPerimeter. Toutes ces méthodes utilisent au moins une variable d'instance partagée (width ou height).
Donc le graphe de cohésion est entiérement connecté:
    - getWidth et calculArea partagent width.
    - getHeight et calculArea partagent height.
    - getWidth et calculPerimeter partagent width.
    - getHeight et calculPerimeter partagent height.

C'est pourquoi TCC = LCC = 1 .

### LCC pourrait-elle être inférieure à TCC ? 

Non, LCC ne peut pas être inférieure à TCC.
Le TCC mesure le ratio de paires de méthodes directement connéctées par rapport à toutes les paires possibles, et le LCC inclut les connexions directes et indirects.
Ce qui signifie que chaque paire directement connectée (TCC) est aussi comptée comme une paire indirectement connectée (LCC),
LCC >= TCC pour toutes les classes.

