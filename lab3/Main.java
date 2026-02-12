package lab3;

import java.util.ArrayList;
import java.util.List;

//Одиночка

class Logger {
    private static Logger instance;
    private List<String> logs = new ArrayList<>();

    private Logger() {} 

    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        String entry = "[LOG]: " + message;
        logs.add(entry);
        System.out.println(entry);
    }
}

// Ланцюжок обов'язків
abstract class OrderHandler {
    protected OrderHandler nextHandler;

    public void setNext(OrderHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract void handle(String order);
}

class QuantityHandler extends OrderHandler {
    @Override
    public void handle(String order) {
        Logger.getInstance().log("Перевірка наявності товару для: " + order);
        if (nextHandler != null) nextHandler.handle(order);
    }
}

class PaymentHandler extends OrderHandler {
    @Override
    public void handle(String order) {
        Logger.getInstance().log("Перевірка оплати для: " + order);
        if (nextHandler != null) nextHandler.handle(order);
    }
}


//Декоратор

interface Notifier {
    void send(String message);
}

class EmailNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("Надсилання Email: " + message);
    }
}

abstract class NotifierDecorator implements Notifier {
    protected Notifier wrapped;

    public NotifierDecorator(Notifier wrapped) {
        this.wrapped = wrapped;
    }

    public void send(String message) {
        wrapped.send(message);
    }
}

class SMSDecorator extends NotifierDecorator {
    public SMSDecorator(Notifier wrapped) {
        super(wrapped);
    }

    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Надсилання SMS: " + message);
    }
}

public class Main {

    public static void main(String[] args) {
        

        Logger logger = Logger.getInstance();
        logger.log("Програма запущена.");

        OrderHandler quantity = new QuantityHandler();
        OrderHandler payment = new PaymentHandler();
        quantity.setNext(payment);

        String myOrder = "Замовлення #4521 (Ноутбук)";
        System.out.println("\nОбробка замовлення");
        quantity.handle(myOrder);

        System.out.println("\nНадсилання сповіщень");
        Notifier notifyChain = new EmailNotifier();
        notifyChain = new SMSDecorator(notifyChain); 
        
        notifyChain.send("Ваше замовлення успішно оброблено!");

        logger.log("Роботу завершено успішно.");
    }
}