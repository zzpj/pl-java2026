package com.zzpj.TrainTripsManager;

import com.zzpj.openapi.api.BookingsApi;
import com.zzpj.openapi.api.StationsApi;
import com.zzpj.openapi.model.Booking;
import com.zzpj.openapi.model.BookingPayment;
import com.zzpj.openapi.model.Problem;
import com.zzpj.openapi.model.Station;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

@RestController
@RequiredArgsConstructor
class TestController {

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${server.port}")
	private String appPort;

	@GetMapping("/hello/{name}")
	public String getServiceName(@PathVariable("name") String name) {
		return "Hello " + name + ", you are using " + applicationName + " on port: " + appPort;
	}
}

@RestController
@RequiredArgsConstructor
class StationController implements StationsApi {

	private final StationService stationService;

	@Override
	public ResponseEntity<List<Station>> getStations() {
		return ResponseEntity.ok(stationService.getStations());
	}
}

@RestController
@RequiredArgsConstructor
class BookingController implements BookingsApi {


	@Override
	public ResponseEntity<Booking> createBooking(Booking booking) {
		if (booking == null || booking.getTripId() == null) {
			throw new IllegalStateException("Booking cannot be null");
		}

		return null;
	}

	@Override
	public ResponseEntity<BookingPayment> createBookingPayment(UUID bookingId, BookingPayment bookingPayment) {
		return null;
	}

	@Override
	public ResponseEntity<Void> deleteBooking(UUID bookingId) {
		return null;
	}

	@Override
	public ResponseEntity<Booking> getBooking(UUID bookingId) {
		return null;
	}

	@Override
	public ResponseEntity<List<Booking>> getBookings() {
		return null;
	}
}

@Service
class StationService {
	public List<Station> getStations() {
		Station plStation = new Station().id(UUIDConstant.LODZ_STATION).name("Łódź Fabryczna").countryCode("PL").timezone("Europe/Warsaw");
		Station deStation = new Station().id(UUIDConstant.BERLIN_STATION).name("Berlin Hauptbahnhof").countryCode("DE").timezone("Europe/Berlin");
		Station frStation = new Station().id(UUIDConstant.PARIS_STATION).name("Paris Gare du Nord").countryCode("FR").timezone("Europe/Paris");
		Station itStation = new Station().id(UUIDConstant.ROME_STATION).name("Roma Termini").countryCode("IT").timezone("Europe/Rome");
		return List.of(plStation, deStation, frStation, itStation);
	}
}

@UtilityClass
class UUIDConstant {
	public static final UUID LODZ_STATION = UUID.fromString("b2cc2fe2-be4b-4733-9e21-9419711d0e04");
	public static final UUID PARIS_STATION = UUID.fromString("083d3f87-a738-4567-9472-2cf0c325c115");
	public static final UUID BERLIN_STATION = UUID.fromString("23c20c5f-d257-46f6-ace3-9074dad470a2");
	public static final UUID ROME_STATION = UUID.fromString("139d47ee-4724-4028-b261-e003fe5fcc40");
}

@RestControllerAdvice
class GlobalExceptionHandler {


	// Obsługa konkretnego błędu
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Object> handleRuntimeError(IllegalStateException ex) {

		Problem problem = new Problem()
				.status(HttpStatus.BAD_REQUEST.value())
				.title("Bad Request")
				.detail(ex.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGeneralError(Exception exception) {

		Problem problem = new Problem()
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.title("An unexpected error occurred")
				.detail(exception.getMessage())
				.status(500);

		return ResponseEntity.status(500).body("An unexpected error occurred: " + exception.getMessage());
	}
}