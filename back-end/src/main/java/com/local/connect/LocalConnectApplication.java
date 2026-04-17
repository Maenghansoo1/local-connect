package com.local.connect;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocalConnectApplication {

	public static void main(String[] args) {
		// .env 파일을 시스템 프로퍼티로 로드 (없으면 무시)
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));

		SpringApplication.run(LocalConnectApplication.class, args);
	}

}
