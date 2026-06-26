package ma.ensa.minisoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import ma.ensa.minisoc.logs.service.TcpServer;
import ma.ensa.minisoc.logs.service.TrafficSimulator;

@SpringBootApplication
public class MinisocApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinisocApplication.class, args);

	}
}
