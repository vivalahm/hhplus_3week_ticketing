package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.adaptor.presentation.dto.request.ChargePointRequest;
import com.hhplus.concertticketing.adaptor.presentation.dto.request.PaymentRequest;
import com.hhplus.concertticketing.adaptor.presentation.dto.request.ReservationRequest;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.PaymentResponse;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.ReservationResponse;
import com.hhplus.concertticketing.business.model.*;
import com.hhplus.concertticketing.business.repository.*;
import com.hhplus.concertticketing.business.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConcurrentTest {

    private static final Logger log = LoggerFactory.getLogger(ConcurrentTest.class);

    @Autowired
    private CustomerUseCase customerUseCase;

    @Autowired
    private ReservationUseCase reservationUseCase;

    @Autowired
    private PaymentUseCase paymentUseCase;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertOptionRepository concertOptionRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Test
    @DisplayName("동시 포인트 충전 테스트")
    void chargePoint_ShouldHandleConcurrentRequests() throws InterruptedException {
        Customer customer = new Customer();
        customer.setName("홍길동");
        customer.setPoint(0.0);
        Customer savedCustomer = customerRepository.saveCustomer(customer);
        ChargePointRequest request = new ChargePointRequest();
        request.setCustomerId(savedCustomer.getId());
        request.setAmount(100.0);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        Runnable task = () -> {
            long startTimeStamp = System.nanoTime();
            try {
                customerUseCase.chargePoint(request);
            } catch (Exception e) {
                log.info("Error: " + e.getMessage());
            } finally {
                long endTimeStamp = System.nanoTime();
                log.info("Execution time: " + (endTimeStamp - startTimeStamp) / 1000000 + "ms");
                latch.countDown();
            }
        };
        for (int i = 0; i < 10; i++) {
            executor.submit(task);
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        executor.shutdown();

        Customer updatedCustomer = customerService.getCustomerById(savedCustomer.getId());
        log.info("최종 사용자 포인트: " + updatedCustomer.getPoint());
        assertEquals(1000.0, updatedCustomer.getPoint());
    }

    @Test
    @DisplayName("동시 포인트 사용 테스트")
    void usePoint_ShouldHandleConcurrentRequests() throws InterruptedException {
        Customer customer2 = new Customer();
        customer2.setName("홍길동");
        customer2.setPoint(10000.0);
        Customer savedCustomer2 = customerRepository.saveCustomer(customer2);


        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        Runnable task = () -> {
            long startTimeStamp = System.nanoTime();
            try {
                customerUseCase.usePoint(savedCustomer2.getId(), 100.0);
            } catch (Exception e) {
                log.info("Error: " + e.getMessage());
            } finally {
                long endTimeStamp = System.nanoTime();
                log.info("Execution time: " + (endTimeStamp - startTimeStamp) / 1000000 + "ms");
                latch.countDown();
            }
        };
        for (int i = 0; i < 10; i++) {
            executor.submit(task);
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        executor.shutdown();

        Customer updatedCustomer = customerService.getCustomerById(savedCustomer2.getId());
        log.info("최종 사용자 포인트: " + updatedCustomer.getPoint());
        assertEquals(9000.0, updatedCustomer.getPoint());
    }


    @Test
    @DisplayName("동시 티켓 예약 테스트")
    void reserveTicket_ShouldHandleConcurrentRequests() throws InterruptedException {
        // 테스트 데이터 생성
        Customer customer = new Customer();
        customer.setName("홍길동");
        customer.setPoint(1000.0);
        Customer savedCustomer = customerRepository.saveCustomer(customer);

        Token token = new Token();
        token.setCustomerId(savedCustomer.getId());
        token.setTokenValue("test-token");
        Token savedToken = tokenRepository.saveToken(token);

        Concert concert = new Concert();
        concert.setTitle("테스트 콘서트");
        concert.setIsFinished(false);
        concert.setIsSoldOut(false);
        Concert savedConcert = concertRepository.saveConcert(concert);

        ConcertOption concertOption = new ConcertOption();
        concertOption.setConcertId(savedConcert.getId());
        concertOption.setPrice(100.0);
        concertOption.setIsAvailable(true);
        concertOption.setConcertDate(LocalDateTime.parse("2024-07-31T23:59:59"));
        ConcertOption savedConcertOption =  concertOptionRepository.saveConcertOption(concertOption);

        Seat seat = new Seat();
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setConcertOptionId(savedConcertOption.getId());
        Seat savedSeat = seatRepository.saveSeat(seat);

        ReservationRequest request = new ReservationRequest();
        request.setTokenValue("test-token");
        request.setConcertOptionId(savedConcertOption.getId());
        request.setSeatId(savedSeat.getId());

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        Runnable task = () -> {
            long startTimeStamp = System.nanoTime();
            try {
                ReservationResponse response = reservationUseCase.reserveTicket(request);
                log.info("예약ID: " + response.getReservationId());
                successCount.incrementAndGet();
            } catch (OptimisticLockingFailureException e) {
                log.info("낙관락 error: " + e.getMessage());
                failureCount.incrementAndGet();
            } catch (Exception e) {
                log.info("Error: " + e.getMessage());
            } finally {
                long endTimeStamp = System.nanoTime();
                log.info("Execution time: " + (endTimeStamp - startTimeStamp) / 1000000 + "ms");
                latch.countDown();
            }
        };

        for (int i = 0; i < 10; i++) {
            executor.submit(task);
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        executor.shutdown();

        log.info("성공한 예약 수: " + successCount.get());
        log.info("실패한 예약 수: " + failureCount.get());

        // 결과 검증: 하나의 예약만 성공해야 한다.
        assertEquals(1, successCount.get());
        assertEquals(9, failureCount.get());
    }

    @Test
    @DisplayName("동시 결제 처리 테스트")
    void processPayment_ShouldHandleConcurrentRequests() throws InterruptedException {
        // 테스트 데이터 생성
        Customer customer = new Customer();
        customer.setName("홍길동");
        customer.setPoint(1000.0);
        Customer savedCustomer = customerRepository.saveCustomer(customer);

        Concert concert = new Concert();
        concert.setTitle("테스트 콘서트");
        concert.setIsFinished(false);
        concert.setIsSoldOut(false);
        Concert savedConcert = concertRepository.saveConcert(concert);

        Token token = new Token();
        token.setConcertId(savedConcert.getId());
        token.setCustomerId(savedCustomer.getId());
        token.setTokenValue("test-token");
        Token savedToken = tokenRepository.saveToken(token);

        ConcertOption concertOption = new ConcertOption();
        concertOption.setConcertId(savedConcert.getId());
        concertOption.setPrice(100.0);
        concertOption.setIsAvailable(true);
        concertOption.setConcertDate(LocalDateTime.parse("2024-07-31T23:59:59"));
        ConcertOption savedConcertOption = concertOptionRepository.saveConcertOption(concertOption);

        Seat seat = new Seat();
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setConcertOptionId(savedConcertOption.getId());
        Seat savedSeat = seatRepository.saveSeat(seat);

        Reservation reservation = new Reservation();
        reservation.setConcertOptionId(savedConcertOption.getId());
        reservation.setCustomerId(savedCustomer.getId());
        reservation.setSeatId(savedSeat.getId());
        reservation.setStatus(ReservationStatus.RESERVING);
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        Reservation savedReservation = reservationRepository.saveReservation(reservation);

        PaymentRequest request = new PaymentRequest();
        request.setReservationId(savedReservation.getId());

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        Runnable task = () -> {
            long startTimeStamp = System.nanoTime();
            try {
                PaymentResponse response = paymentUseCase.processPayment(request);
                log.info("결제 결과: " + response.getStatus());
                successCount.incrementAndGet();
            } catch (OptimisticLockingFailureException e) {
                log.info("낙관적 락 error: " + e.getMessage());
                failureCount.incrementAndGet();
            } catch (Exception e) {
                log.info("Error: " + e.getMessage());
            } finally {
                long endTimeStamp = System.nanoTime();
                log.info("Execution time: " + (endTimeStamp - startTimeStamp) / 1000000 + "ms");
                latch.countDown();
            }
        };

        for (int i = 0; i < 10; i++) {
            executor.submit(task);
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        executor.shutdown();

        log.info("성공한 결제 수: " + successCount.get());
        log.info("실패한 결제 수: " + failureCount.get());

        // 결과 검증: 하나의 결제만 성공해야 한다.
        assertEquals(1, successCount.get());
        assertEquals(9, failureCount.get());
    }

}
