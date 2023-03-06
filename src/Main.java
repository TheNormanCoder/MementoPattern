public class Main {
    public static void main(String[] args) {
        Originator originator = new Originator();
        Caretaker caretaker = new Caretaker();

        originator.setState("State1");
        caretaker.add(originator.save());

        originator.setState("State2");
        caretaker.add(originator.save());

        originator.restore(caretaker.get(0));
        System.out.println(originator.getState());

        originator.restore(caretaker.get(1));
        System.out.println(originator.getState());
    }
}
