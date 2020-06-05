package Roulette;

public class RouletteInstance {

    private static Roulette current = null;
    private static Roulette previous = null;

    public static Roulette getCurrent() {
        if (current == null) {
            current = new Roulette();
        }
        return current;
    }

    public static Roulette getPrevious() {
        return previous;
    }

    public static void setCurrent(Roulette roulette) {
        RouletteInstance.current = roulette;
    }

    public static void setPrevious(Roulette roulette) {
        RouletteInstance.previous = roulette;
    }

    public void update() {
        if (System.currentTimeMillis() > getCurrent().getRollTime()) {
            current.setResult(current.roll());
            current.updateScore(current.getResult());

            RouletteInstance.previous = RouletteInstance.current;
            RouletteInstance.current = new Roulette();
            System.out.println(RouletteInstance.getCurrent().getSessionId());
            System.out.println(RouletteInstance.getPrevious().getResult());
            System.out.println(RouletteInstance.getPrevious().getResultInt());
            //@TODO
            //ws broadcast new session and results
        }
    }
}
