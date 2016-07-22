package lck07_J_CSR;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class BankAccount {
	private double balanceAmount; // Total amount in bank account 
	private final Lock lock = new ReentrantLock();
	private final Random number = new Random(123L);

	BankAccount(double balance) { 
		this.balanceAmount = balance;
	}
	// Deposits amount from this object instance
	// to BankAccount instance argument ba

	private void depositAmount(BankAccount ba, double amount)
			throws InterruptedException {
		while (true) {
			if (this.lock.tryLock()) {
				try {
					if (ba.lock.tryLock()) {
							try {
								if (amount > balanceAmount) {
									throw new IllegalArgumentException( "Transfer cannot be completed");
								}
								ba.balanceAmount += amount; this.balanceAmount -= amount; break;
							} finally { ba.lock.unlock();
							} 
							}
				} finally { this.lock.unlock();
				} 
				}
			int n = number.nextInt(1000);
			int TIME = 1000 + n; // 1 second + random delay to prevent livelock 
			Thread.sleep(TIME);
		}
		}
	public static void initiateTransfer(final BankAccount first, final BankAccount second, final double amount) {
		Thread transfer = new Thread(new Runnable() { 
			public void run() {
				try {
					first.depositAmount(second, amount);
				} catch (InterruptedException e) { 
					Thread.currentThread().interrupt(); // Reset interrupted status
				}
			} });
		transfer.start();
	}
	
	public static void main(String[] args) { 
		BankAccount a = new BankAccount(5000); 
		BankAccount b = new BankAccount(6000); 
		initiateTransfer(a, b, 1000); // starts thread 1 
		initiateTransfer(b, a, 1000); // starts thread 2
	}
}