package com.zzpj.TrainTripsOrganizerService;

import com.zzpj.openapi.ApiClient;
import com.zzpj.openapi.api.BookingsApi;
import com.zzpj.openapi.api.StationsApi;
import com.zzpj.openapi.model.Booking;
import com.zzpj.openapi.model.Problem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


//	@Bean
//	public StationsApi stationsApi(@Value("${TRAIN_TRIP_MANAGER_SERVICE}") String baseUrl) {
//		ApiClient apiClient = new ApiClient();
//		apiClient.setBasePath(baseUrl);
//		return new StationsApi(apiClient);
//	}
//
//	@Bean
//	public BookingsApi bookingsApi(@Value("${TRAIN_TRIP_MANAGER_SERVICE}") String baseUrl) {
//		ApiClient apiClient = new ApiClient();
//		apiClient.setBasePath(baseUrl);
//		return new BookingsApi(apiClient);
//	}
//	@Bean
//	public CommandLineRunner commandLineRunner(StationsApi stationsApi, BookingsApi bookingsApi) {
//		return args -> {
//			stationsApi.getStations().forEach(System.out::println);
//
//			System.out.println("------");
//
//			try {
//				Booking booking = bookingsApi.createBooking(new Booking());
//			} catch (RestClientResponseException e) {
//				Problem responseBodyAs = e.getResponseBodyAs(Problem.class);
//				System.out.println(responseBodyAs);
//			}
//
//		};
//	}

//	@Bean
//	public ServiceInstanceListSupplier serviceInstanceListSupplier() {
//		return new ServiceInstanceListSupplier() {
//
//			private final String serviceId = "train-trips-manager-service";
//
//			// Lista znanych instancji (statyczna)
//			private final List<ServiceInstance> instances = List.of(
//					new DefaultServiceInstance("1", serviceId, "localhost", 8021, false),
//					new DefaultServiceInstance("2", serviceId, "localhost", 8022, false),
//					new DefaultServiceInstance("3", serviceId, "localhost", 8023, false)
//			);
//
//			@Override
//			public String getServiceId() {
//				return serviceId;
//			}
//
//			@Override
//			public Flux<List<ServiceInstance>> get() {
//
//				// Filtrowanie tylko żywych instancji
//				List<ServiceInstance> alive = instances.stream()
//						.filter(this::isAlive)
//						.toList();
//
//				System.out.println("LB → dostępne instancje: " + alive.stream().map(ServiceInstance::getPort).toList());
//
//				return Flux.just(alive);
//			}
//
//			// Health-check TCP
//			private boolean isAlive(ServiceInstance instance) {
//				try (Socket socket = new Socket()) {
//					socket.connect(new InetSocketAddress(instance.getHost(), instance.getPort()), 150);
//					return true;
//				} catch (Exception e) {
//					System.out.println("LB → instancja DOWN: port " + instance.getPort());
//					return false;
//				}
//			}
//
//
//		};
//	}
//
//	@Bean
//	@LoadBalanced
//	public RestTemplate restTemplate() {
//		return new RestTemplate();
//	}
//
//	@Bean
//	public CommandLineRunner commandLineRunner(RestTemplate restTemplate) {
//		return args -> {
//
//			String applicationName = "FromOrganizationService";
//			int i = 0;
//			while (true) {
//				try {
//					ResponseEntity<String> forEntity = restTemplate.getForEntity("http://train-trips-manager-service/hello/" + applicationName, String.class);
//					System.out.println("LB → Request id: " + i + " Response: " + forEntity.getBody());
//					Thread.sleep(1000);
//					i++;
//				} catch (Exception e) {
//					System.out.println("ERROR: " + e.getMessage());
//				}
//			}
//
//		};
//	}

	@Value("${config.server.demo}")
	private String message;

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {


			System.out.println("Config Server message: " + message);

		};
	}

}
