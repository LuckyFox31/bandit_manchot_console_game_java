import java.lang.Thread;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private static Scanner scanner = new Scanner(System.in);
    private static Random random = new Random();
    private static String[] loader = {"◐", "◓", "◑", "◒"};
    private static String[] symbolsArray = {"\u001b[34;1m♣\u001b[33;1m", "\u001b[35;1m♠\u001b[33;1m", "\u001b[31;1m♥\u001b[33;1m", "\u001b[33;1m♦\u001b[33;1m"};
    private static int currentBalance = 0;

    public static void main(String[] args) throws InterruptedException {
        showRules();
        askStartGame();
        startGame();
    }

    public static void showRules(){
        System.out.println(
                "\u001b[33;1m⚁═══════════════════════════════════⚄\n" +
                "║           \u001b[35;1m-- Règles --\u001b[33;1m            ║\n" +
                "║                                   ║\n" +
                "║ \u001b[30m• Misez une une somme.\u001b[33;1m            ║\n" +
                "║ \u001b[30m• 0 symbole identique = \u001b[31;1mx0\u001b[33;1m        ║\n" +
                "║ \u001b[30m• 2 symboles identiques = \u001b[35mx0.5\u001b[33;1m    ║\n" +
                "║ \u001b[30m• 3 symboles identiques = \u001b[32;1mx2\u001b[33;1m      ║\n" +
                "║                                   ║\n" +
                "║    \u001b[30mPour lancer, tapez \u001b[36;1m'start'\u001b[33;1m     ║\n" +
                "║                                   ║\n" +
                "⚀═══════════════════════════════════⚅\u001b[0m\n"
        );
    }

    public static void askStartGame() throws InterruptedException {
        String userInput = scanner.nextLine();

        System.out.println(userInput);
        System.out.println(userInput.equals("start"));

        if(userInput.equals("start")){
            System.out.println("\u001b[2J");
            showLoader(3, "Lancement du jeu... ");
            System.out.println("\u001b[2J");
        } else {
            exitGame();
        }
    }

    public static void startGame() throws InterruptedException {
        boolean restart = false;
        setCurrentBalance(1000);
        displayHud();

        do{
            updateBetAndPlaySentence(0, false);

            int bet = getBet();
            String[] sortedSymbols = getRandomSymbols();
            setCurrentBalance(getCurrentBalance() - bet);

            updateBalance(getCurrentBalance());
            updateBetAndPlaySentence(bet, false);

            startAnimation(sortedSymbols);
            int earning = getEarning(sortedSymbols, bet);

            if(earning == 0){
                earning = bet * -1;
            } else {
                setCurrentBalance(getCurrentBalance() + earning);
            }
            updateSymbolsAndEarning(sortedSymbols, earning);

            updateBalance(getCurrentBalance());

            if(getCurrentBalance() > 0){
                updateBetAndPlaySentence(bet, true);
                restart = askRestart();
            } else {
                updateBetAndPlaySentence(bet, false);
                restart = false;
            }
        }while(restart);

        Thread.sleep(2000);

        exitGame();
    }

    public static void showLoader(int cycle, String sentence) throws InterruptedException {
        for (int x = 0; x < cycle; x++) {
            for (int y = 0; y < loader.length; y++) {
                System.out.print(sentence + loader[y] + "\r");
                Thread.sleep(200);
            }
        }
    }

    public static String repeat(int count, String with) {
        return new String(new char[count]).replace("\0", with);
    }

    public static void displayHud(){
        System.out.println(
                "\u001b[33;1m⚁════════════════════⚃════════════════════⚄\n" +
                "║                    ║ \u001b[30mSolde : \u001b[36;1m1000\u001b[33;1m       ║\n" +
                "║       \u001b[34;1m♣  \u001b[35;1m♠  \u001b[31;1m♥\u001b[33;1m      ║                    ║\n" +
                "║                    ║                    ║\n" +
                "║                    ║                    ║\n" +
                "⚀════════════════════⚂════════════════════⚅\n"
        );
    }

    public static void updateBalance(int balance){
        System.out.print(
                "\u001b[6A║                    ║ Solde : \u001b[36;1m" +
                balance +
                "\u001b[33;1m" +
                repeat((11 - String.valueOf(balance).length()), " ") +
                "║\n\n\n\n\n\n"
        );
    }

    public static void updateSymbolsAndEarning(String[] symbols, int earning){
        String earningWord;
        if(earning > 0){
            earningWord = "Gain";
        } else {
            earningWord = "Perte";
        }

        System.out.print(
                "\u001b[5A║       " + symbols[0] + "  " + symbols[1] + "  " + symbols[2] + "      ║ "
                + earningWord +
                " : \u001b[36;1m" +
                earning +
                "\u001b[33;1m   " +
                "\n\n\n\n\n"
        );
    }

    public static void updateBetAndPlaySentence(int bet, boolean displayPlaySentence){
        if(displayPlaySentence){
            System.out.print(
                    "\u001b[3A║                    ║ \u001b[30mJouer ? (\u001b[32;1mY\u001b[30m / \u001b[31;1mn\u001b[30m)\u001b[33;1m    ║\n\n\n"
            );
        } else {
            System.out.print(
                    "\u001b[3A║ \u001b[30mMise : " +
                    "\u001b[35;1m" +
                    bet +
                    "\u001b[33;1m" +
                    repeat((12 - String.valueOf(bet).length()), " ") +
                    "║                    ║\n\n\n"
            );
        }
    }

    public static int getBet(){
        boolean restart = false;
        int result = 0;

        do {
            System.out.println("Renseigner votre mise (max: " + currentBalance + ") : ");
            String userInput = scanner.nextLine();

            System.out.print("\u001b[2A\u001b[0J");

            if(!isNumeric(userInput) || Integer.parseInt(userInput) > currentBalance ){
                restart = true;
            } else {
                restart = false;
                result = Integer.parseInt(userInput);
            }
        } while (restart);

        return result;
    }

    public static String[] getRandomSymbols(){
        String[] symbols = {
                symbolsArray[random.nextInt(symbolsArray.length)],
                symbolsArray[random.nextInt(symbolsArray.length)],
                symbolsArray[random.nextInt(symbolsArray.length)]
        };

        return symbols;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void startAnimation(String[] symbols) throws InterruptedException {
        for (int i = 0; i <= 15; i++) {
            if(i <= 5){
                updateSymbolsAndEarning(
                        new String[]{
                                symbolsArray[random.nextInt(symbolsArray.length)],
                                symbolsArray[random.nextInt(symbolsArray.length)],
                                symbolsArray[random.nextInt(symbolsArray.length)]
                        },
                        0
                );
            } else if (i <= 10) {
                updateSymbolsAndEarning(
                        new String[]{
                                symbols[0],
                                symbolsArray[random.nextInt(symbolsArray.length)],
                                symbolsArray[random.nextInt(symbolsArray.length)]
                        },
                        0
                );
            } else {
                updateSymbolsAndEarning(
                        new String[]{
                                symbols[0],
                                symbols[1],
                                symbolsArray[random.nextInt(symbolsArray.length)]
                        },
                        0
                );
            }

            Thread.sleep(300);
        }
    }

    public static int getEarning(String[] symbols, int bet){
        int result = 0;

        if(symbols[0] == symbols[1] && symbols[1] == symbols[2] && symbols[0] == symbols[2]){
            // x2
            result = bet * 2;
        } else if(symbols[0] == symbols[1] || symbols[1] == symbols[2] || symbols[0] == symbols[2]){
            // x0.5
            result = bet / 2;
        }

        return result;
    }

    public static boolean askRestart(){
        System.out.println("Souhaitez-vous rejouer ? (Y/n)");
        String userInput = scanner.nextLine();

        boolean result = false;

        System.out.print("\u001b[2A\u001b[0J");

        if(userInput.equals("Y") || userInput.equals("y") || userInput.equals("yes")){
            result = true;
        }

        return result;
    }

    public static void exitGame() throws InterruptedException {
        System.out.println("\u001b[2J");
        showLoader(3, "Fermeture du programme... ");
        System.out.println("\u001b[2J");

        System.exit(0);
    }

    public static void setCurrentBalance(int currentBalance) {
        Game.currentBalance = currentBalance;
    }

    public static int getCurrentBalance() {
        return currentBalance;
    }
}
