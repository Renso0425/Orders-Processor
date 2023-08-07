package processor;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

public class OrdersProcessor {
	public static void main(String args[]) throws IOException {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter item's data file name: ");
		String dataFileName = input.next();
		System.out.println("Enter 'y' for multiple threads, any other character otherwise: ");
		String multiThread = input.next();
		System.out.println("Enter number of orders to process: ");
		int numOfOrders = input.nextInt();
		System.out.println("Enter order's base filename: ");
		String baseFileName = input.next();
		System.out.println("Enter result's filename: ");
		String resultFileName = input.next();
		input.close();
		long startTime = System.currentTimeMillis();
		Receipt receipt = new Receipt(resultFileName, multiThread);
		Scanner fileScanner = new Scanner(new File(dataFileName));
		while (fileScanner.hasNextLine()) {
			String itemName = fileScanner.next();
			double cost = fileScanner.nextDouble();
			receipt.add(itemName, cost);
		}
		if (multiThread.equals("y")) {
			ArrayList<Thread> allThreads = new ArrayList<>();
			for (int orderNum = 1; orderNum <= numOfOrders; orderNum++) {
				String currFile = baseFileName + orderNum + ".txt";
				allThreads.add(new Thread(new Shopping(receipt, currFile)));
			}
			for (Thread currThread : allThreads) {
				currThread.start();
			}
			for (Thread currThread : allThreads) {
				try {
					currThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} else {
			for (int i = 1; i <= numOfOrders; i++) {
				String currFile = baseFileName + i + ".txt";
				Shopping shop = new Shopping(receipt, currFile);
				shop.run();
			}
		}
		receipt.subWrite();

		long endTime = System.currentTimeMillis();
		System.out.println("Processing time (msec): " + (endTime - startTime));
		System.out.println("Results can be found in the file: " + resultFileName);
	}
}