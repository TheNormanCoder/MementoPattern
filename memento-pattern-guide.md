# Guida al Memento Pattern

## Cos'è il Memento Pattern?

Il Memento Pattern è un pattern comportamentale che permette di salvare e ripristinare lo stato precedente di un oggetto senza rivelare i dettagli della sua implementazione. È particolarmente utile quando si ha bisogno di implementare funzionalità come "annulla" (undo) o ripristinare uno stato salvato.

## Struttura del Pattern

Il pattern è composto da tre componenti principali:

1. **Originator**: L'oggetto il cui stato deve essere salvato e ripristinato
   - Ha metodi per impostare e ottenere il suo stato
   - Può creare un oggetto Memento contenente il suo stato attuale
   - Può ripristinare il suo stato da un oggetto Memento

2. **Memento**: Un oggetto che memorizza lo stato interno dell'Originator
   - È immutabile (lo stato è spesso dichiarato come final)
   - Ha metodi per restituire lo stato salvato
   - Protegge lo stato salvato da accessi esterni

3. **Caretaker**: Mantiene una collezione di oggetti Memento
   - Non modifica o esamina il contenuto dei Memento
   - Può aggiungere nuovi Memento alla lista
   - Può restituire un Memento specifico dalla lista

## Implementazione di Base

```java
// Originator
public class Originator {
    private String state;

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public Memento save() {
        return new Memento(state);
    }

    public void restore(Memento memento) {
        state = memento.getState();
    }
}

// Memento
public class Memento {
    private final String state;

    public Memento(String stateToSave) {
        state = stateToSave;
    }

    public String getState() {
        return state;
    }
}

// Caretaker
public class Caretaker {
    private final List<Memento> mementos = new ArrayList<>();

    public void add(Memento memento) {
        mementos.add(memento);
    }

    public Memento get(int index) {
        return mementos.get(index);
    }
}
```

## Esempio di Utilizzo

```java
public class Main {
    public static void main(String[] args) {
        Originator originator = new Originator();
        Caretaker caretaker = new Caretaker();

        originator.setState("State1");
        caretaker.add(originator.save());

        originator.setState("State2");
        caretaker.add(originator.save());

        originator.restore(caretaker.get(0));
        System.out.println(originator.getState()); // Output: State1

        originator.restore(caretaker.get(1));
        System.out.println(originator.getState()); // Output: State2
    }
}
```

## Relazione con Event Sourcing

Il Pattern Memento può essere considerato alla base del concetto di Event Sourcing, anche se quest'ultimo è una tecnica più complessa e articolata:

1. **Concetto fondamentale**: Entrambi si basano sull'idea di conservare la storia di cambiamenti di stato di un sistema, anziché solo lo stato attuale.

2. **Ricostruzione dello stato**: In Event Sourcing, lo stato attuale viene ricostruito applicando sequenzialmente tutti gli eventi, mentre nel Pattern Memento possiamo ripristinare direttamente uno stato salvato.

3. **Differenze principali**:
   - **Granularità**: Il Memento tipicamente salva "snapshot" completi di stato, mentre l'Event Sourcing registra singoli eventi/cambiamenti
   - **Persistenza**: Event Sourcing solitamente implica la persistenza permanente degli eventi in un database, mentre il Memento è spesso utilizzato per memorizzare stati temporanei in memoria
   - **Scalabilità**: Event Sourcing è progettato per sistemi distribuiti su larga scala, il Memento per oggetti singoli

4. **Evoluzione**: Si può vedere l'Event Sourcing come un'evoluzione del Memento Pattern che aggiunge:
   - Log di eventi immutabili come fonte primaria di verità
   - Modelli di proiezione (views/read models)
   - Gestione di eventi come elementi di prima classe nel sistema
   - Capacità di "replay" della storia completa

## Casi di Applicazione Reali

### 1. Editor di Testo con Funzionalità Undo/Redo

```java
// La classe Originator - l'editor di testo
class TextEditor {
    private String text;
    
    public void write(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    // Crea un memento con lo stato corrente
    public TextEditorMemento save() {
        return new TextEditorMemento(text);
    }
    
    // Ripristina lo stato da un memento
    public void restore(TextEditorMemento memento) {
        text = memento.getSavedText();
    }
}

// La classe Memento - memorizza lo stato dell'editor
class TextEditorMemento {
    private final String text;
    
    public TextEditorMemento(String textToSave) {
        this.text = textToSave;
    }
    
    // Questo metodo è accessibile solo all'Originator
    public String getSavedText() {
        return text;
    }
}

// La classe Caretaker - gestisce la cronologia
class TextEditorHistory {
    private final Stack<TextEditorMemento> mementos = new Stack<>();
    
    public void push(TextEditorMemento memento) {
        mementos.push(memento);
    }
    
    public TextEditorMemento pop() {
        return mementos.pop();
    }
    
    public boolean isEmpty() {
        return mementos.isEmpty();
    }
}
```

### 2. Videogioco con Sistema di Checkpoint

```java
// La classe Originator - rappresenta lo stato del gioco
class GameState {
    private int level;
    private int score;
    private List<String> inventory;
    private String playerPosition;
    
    // Metodi per modificare lo stato...
    
    // Crea un checkpoint (memento) con lo stato corrente del gioco
    public GameMemento createCheckpoint() {
        return new GameMemento(level, score, new ArrayList<>(inventory), playerPosition);
    }
    
    // Ripristina il gioco da un checkpoint (memento)
    public void restoreCheckpoint(GameMemento memento) {
        this.level = memento.getLevel();
        this.score = memento.getScore();
        this.inventory = new ArrayList<>(memento.getInventory());
        this.playerPosition = memento.getPlayerPosition();
    }
}

// La classe Memento - memorizza lo stato del gioco
class GameMemento {
    private final int level;
    private final int score;
    private final List<String> inventory;
    private final String playerPosition;
    private final Date creationDate;
    
    // Costruttore e metodi getter...
}

// La classe Caretaker - gestisce i checkpoint
class CheckpointManager {
    private final Map<String, GameMemento> checkpoints = new HashMap<>();
    
    // Metodi per salvare e recuperare i checkpoint...
}
```

### 3. Applicazione di Disegno con Storia delle Modifiche

Fornisce un'implementazione di un canvas di disegno con forme e colori, con funzionalità di undo/redo avanzate che consentono di navigare nella cronologia completa delle modifiche.

### 4. Form Wizard con Navigazione tra Passaggi

Implementa un form multi-step con la possibilità di salvare lo stato in punti specifici del processo e navigare avanti/indietro tra i vari passaggi.

## Altri Casi d'Uso Comuni

1. **Software grafico e CAD**:
   - Salvataggio degli stati di un disegno durante la modifica
   - Ripristino a versioni precedenti di un progetto

2. **Database e transazioni**:
   - Snapshot del database prima di operazioni rischiose
   - Rollback di transazioni

3. **Applicazioni finanziarie**:
   - Registrazione dello stato di un conto in determinati momenti
   - Verifica di transazioni passate

4. **Simulazioni**:
   - Salvataggio di stati intermedi di una simulazione
   - Analisi di scenari alternativi partendo da un punto specifico

5. **Configurazioni di sistema**:
   - Backup di configurazioni funzionanti
   - Gestione di profili diversi

6. **IDE e ambienti di sviluppo**:
   - Salvataggio dello stato del workspace
   - Versioni intermedie di refactoring complessi

## Vantaggi del Pattern Memento

- Incapsula lo stato dell'oggetto senza violare l'incapsulamento
- Semplifica l'Originator poiché non deve tenere traccia dei suoi stati precedenti
- Fornisce un meccanismo semplice per ripristinare lo stato di un oggetto
- Separa le responsabilità tra i vari componenti (principio di singola responsabilità)
- Facilita l'implementazione di funzionalità di undo/redo e ripristino da checkpoint

## Conclusione

Il Pattern Memento è un pattern potente e flessibile per gestire lo stato degli oggetti nel tempo. La sua implementazione richiede una chiara separazione delle responsabilità tra l'Originator, il Memento e il Caretaker. È particolarmente utile in scenari dove è necessario mantenere una cronologia degli stati e poter tornare indietro a stati precedenti.

Quando applicato correttamente, il pattern Memento consente di aggiungere funzionalità di ripristino, annullamento e cronologia a un'applicazione in modo elegante e mantenendo l'incapsulamento degli oggetti.
