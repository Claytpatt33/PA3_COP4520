import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;

public class MinotaurGiftsSimulation {
    public static void main(String[] args) throws InterruptedException {
        final int NUMBER_OF_PRESENTS = 500000;
        GiftLinkedList giftList = new GiftLinkedList();
        Thread[] servants = new Thread[4];
        AtomicInteger presentsAddedCounter = new AtomicInteger(0);
        AtomicInteger thankYouNotesWrittenCounter = new AtomicInteger(0);

        for (int i = 0; i < servants.length; i++) {
            servants[i] = new Thread(() -> {
                while (presentsAddedCounter.get() < NUMBER_OF_PRESENTS || !giftList.isEmpty()) {
                    double action = Math.random();
                    if (action < 0.4 && presentsAddedCounter.get() < NUMBER_OF_PRESENTS) {
                        int presentTag = presentsAddedCounter.incrementAndGet();
                        giftList.addGift(new Gift(presentTag));
                    } else if (action >= 0.4 && action < 0.8 && !giftList.isEmpty()) {
                        if (giftList.removeGift()) {
                            thankYouNotesWrittenCounter.incrementAndGet();
                        }
                    } else {
                        int presentTag = (int) (Math.random() * NUMBER_OF_PRESENTS) + 1;
                        giftList.containsGift(presentTag);
                    }
                }
            });
            servants[i].start();
        }

        for (Thread servant : servants) {
            servant.join();
        }

        System.out.println("All presents were processed properly:");
        System.out.println("Total presents that were added " + presentsAddedCounter.get());
        System.out.println("Total 'Thank You' notes that were written: " + thankYouNotesWrittenCounter.get());
        
        if (presentsAddedCounter.get() == thankYouNotesWrittenCounter.get() && presentsAddedCounter.get() == NUMBER_OF_PRESENTS) {
            System.out.println("Success! Every present was matched with a 'Thank You' note.");
        } else {
            System.out.println("Mismatch detected! Some presents were not matched with 'Thank You' notes.");
        }
    }
}

class Gift {
    int tag;
    Gift next;
    private final Lock lock = new ReentrantLock();

    Gift(int tag) {
        this.tag = tag;
        this.next = null;
    }

    void lock() {
        lock.lock();
    }

    void unlock() {
        lock.unlock();
    }
}

class GiftLinkedList {
    private final Gift head;

    GiftLinkedList() {
        head = new Gift(0);
    }

    public void addGift(Gift newGift) {
        Gift prev = head;
        prev.lock();
        try {
            Gift current = prev.next;
            while (current != null && newGift.tag > current.tag) {
                current.lock();
                prev.unlock();
                prev = current;
                current = current.next;
            }
            newGift.next = current;
            prev.next = newGift;
        } finally {
            prev.unlock();
        }
    }

    public boolean removeGift() {
        Gift prev = head;
        prev.lock();
        try {
            if (prev.next != null) {
                Gift toRemove = prev.next;
                toRemove.lock();
                prev.next = toRemove.next;
                toRemove.unlock();
                return true;
            }
        } finally {
            prev.unlock();
        }
        return false;
    }

    public boolean containsGift(int tag) {
        Gift current = head.next;
        while (current != null) {
            current.lock();
            try {
                if (current.tag == tag) {
                    return true;
                }
            } finally {
                current.unlock();
            }
            current = current.next;
        }
        return false;
    }

    public boolean isEmpty() {
        return head.next == null;
    }
}
