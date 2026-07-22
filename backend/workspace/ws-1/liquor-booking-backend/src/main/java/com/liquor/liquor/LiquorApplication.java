package com.liquor.liquor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiquorApplication {

	public static void main(String[] args) {
		useWindowsCertificateStore();
		SpringApplication.run(LiquorApplication.class, args);
		System.out.println("chal gya oyy...");
	}

	private static void useWindowsCertificateStore() {
		String osName = System.getProperty("os.name", "").toLowerCase();
		if (osName.contains("win")
				&& System.getProperty("javax.net.ssl.trustStore") == null
				&& System.getProperty("javax.net.ssl.trustStoreType") == null) {
			System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");
		}
	}

}
