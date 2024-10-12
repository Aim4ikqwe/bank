import java.util.Random;

class Account {
    private int balance = 0;

    public synchronized void deposit(int amount) {
        balance += amount;
        System.out.println("Пополнен баланс на сумму: " + amount + ". Текущий баланс: " + balance + ".");
        notifyAll(); // Уведомляем ожидателей
    }

    public synchronized void withdraw(int amount) throws InterruptedException {
        while (balance < amount) {
            System.out.println("Ожидание пополнения. Текущий баланс: " + balance + ". Необходимо: " + amount + ".");
            wait(); // Ждем пополнения
        }
        balance -= amount;
        System.out.println("Снято: " + amount + ". Остаток на счете: " + balance + ".");
    }

    public synchronized int getBalance() {
        return balance;
    }
}

class DepositThread extends Thread {
    private final Account account;
    private final Random random = new Random();

    public DepositThread(Account account) {
        this.account = account;
    }

    @Override
    public void run() {
        while (true) {
            int amount = random.nextInt(100) + 1; // Случайная сумма от 1 до 100
            account.deposit(amount);
            try {
                Thread.sleep(random.nextInt(1000) + 500); // Задержка от 0.5 до 1.5 секунд
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Account account = new Account();

        // Запускаем поток для пополнения счета
        DepositThread depositThread = new DepositThread(account);
        depositThread.start();

        // Снимаем деньги в основном потоке
        Random random = new Random();
        while (true) { // Пробуем снять деньги 5 раз
            int amountToWithdraw = random.nextInt(100) + 50; // Случайная сумма от 50 до 150
            try {
                account.withdraw(amountToWithdraw);
                Thread.sleep(random.nextInt(1000) + 1000); // Задержка перед следующим снятием
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Конечный баланс: " + account.getBalance() + ".");
    }
}
