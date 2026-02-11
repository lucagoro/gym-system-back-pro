package com.mma.gestion.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mma.gestion.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByStudentId(Long studentId);
    
    Optional<Payment> findTopByStudentIdOrderByDueDateDesc(Long studentId);

    boolean existsByStudentId(Long studentId);

    List<Payment> findByStudentIdOrderByPaymentDateDesc(Long studentId);

    // Buscamos por fecha Y por gimnasio al mismo tiempo y oredenamos por fecha de pago descendente
    List<Payment> findByPaymentDateBetweenAndStudentGymIdOrderByPaymentDateDesc(LocalDate start, LocalDate end, Long gymId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.student.gym.id = :gymId AND p.paymentDate BETWEEN :start AND :end")
    BigDecimal sumPaymentsBetween(@Param("gymId") Long gymId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
