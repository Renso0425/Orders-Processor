package processor;

import java.io.*;
import java.util.*;

public class Shopping implements Runnable {
	private Receipt receipt;
	private String orderFile;

	public Shopping(Receipt receipt, String currFile) {
		this.receipt = receipt;
		this.orderFile = currFile;
	}

	@Override
	public void run() {
		try {
			receipt.write(orderFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
