package com.mma.gestion.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mma.gestion.entity.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findByGymId(Long gymId); // Siempre filtrar por gimnasio

    List<Expense> findByGymIdAndExpenseDateBetweenOrderByExpenseDateDesc(Long gymId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.gym.id = :gymId AND e.expenseDate BETWEEN :start AND :end")
    BigDecimal sumExpensesBetween(@Param("gymId") Long gymId, @Param("start") LocalDate start, @Param("end") LocalDate end);

}
