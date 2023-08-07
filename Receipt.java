package processor;

import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class Receipt {
	private FileWriter paper;
	private Map<String, Double> items;
	private Map<String, Integer> inventory;
	private ArrayList<StringBuilder> printedResult;
	private Double grandCost;

	public Receipt(String resultName, String condition) throws IOException {
		paper = new FileWriter(resultName, false);
		items = new TreeMap<>();
		inventory = new TreeMap<>();
		grandCost = 0.0;
		printedResult = new ArrayList<>();
	}

	public void add(String itemName, double cost) {
		items.put(itemName, cost);
	}

	public void write(String currFile) throws IOException {
		Scanner orderScanner = new Scanner(new File(currFile));
		orderScanner.next();
		int client = orderScanner.nextInt();
		subRead(client, orderScanner);

	}

	public void subRead(int client, Scanner orderScanner) throws IOException {
		System.out.println("Reading order for client with id: " + client);
		StringBuilder currClient = new StringBuilder();
		currClient.append("----- Order details for client with Id: " + client + " -----\n");
		Map<String, Integer> currPurch = new TreeMap<>();
		while (orderScanner.hasNextLine()) {
			String itemName = orderScanner.next();
			orderScanner.next();
			if (currPurch.containsKey(itemName)) {
				currPurch.put(itemName, currPurch.get(itemName) + 1);
			} else {
				currPurch.put(itemName, 1);
			}
			synchronized (inventory) {
				if (inventory.containsKey(itemName)) {
					inventory.put(itemName, inventory.get(itemName) + 1);
				} else {
					inventory.put(itemName, 1);
				}
			}
		}
		Set<String> set = currPurch.keySet();
		double totalCost = 0.0;
		for (String item : set) {
			currClient.append("Item's name: " + item + ", Cost per item: "
					+ NumberFormat.getCurrencyInstance().format(items.get(item)) + ", Quantity: " + currPurch.get(item)
					+ ", Cost: " + NumberFormat.getCurrencyInstance().format(currPurch.get(item) * items.get(item))
					+ "\n");
			totalCost += (currPurch.get(item) * items.get(item));
		}
		synchronized (grandCost) {
			grandCost += totalCost;
		}
		currClient.append("Order Total: " + NumberFormat.getCurrencyInstance().format(totalCost) + "\n");
		synchronized (printedResult) {
			printedResult.add(currClient);
		}
	}

	public void subWrite() throws IOException {
		printedResult.sort(null);
		for (StringBuilder client : printedResult) {
			paper.write("" + client);
		}
		paper.write("***** Summary of all orders *****\n");
		Set<String> invNames = inventory.keySet();
		for (String item : invNames) {
			paper.write("Summary - Item's name: " + item + ", Cost per item: "
					+ NumberFormat.getCurrencyInstance().format(items.get(item)) + ", Number sold: "
					+ inventory.get(item) + ", Item's Total: "
					+ NumberFormat.getCurrencyInstance().format(inventory.get(item) * items.get(item)) + "\n");
		}
		paper.write("Summary Grand Total: " + NumberFormat.getCurrencyInstance().format(grandCost) + "\n");
		paper.close();
	}

}
