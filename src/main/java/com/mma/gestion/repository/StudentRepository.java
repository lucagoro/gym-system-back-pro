package com.mma.gestion.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mma.gestion.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    boolean existsByPhone(String phone);

    List<Student> findAllByOrderBySurnameAsc();

    @EntityGraph(attributePaths = {"payments"}) // Esto trae los pagos en la misma consulta
    List<Student> findByGymId(Long gymId);

    boolean existsByIdAndGymId(Long id, Long gymId);
    Optional<Student> findByIdAndGymId(Long id, Long gymId);

    boolean existsByPhoneAndGymId(String phone, Long gymId);

    // Buscar por nombre dentro de un mismo gimnasio
    List<Student> findByNameContainingIgnoreCaseAndGymId(String name, Long gymId);

    @Query(value = """
        SELECT 
            (SELECT COUNT(*) FROM student WHERE gym_id = :gymId) as total,
            
            COUNT(CASE WHEN last_p.due_date >= CURRENT_DATE THEN 1 END) as alDia,
            COUNT(CASE WHEN last_p.due_date < CURRENT_DATE THEN 1 END) as vencidos,
            COUNT(CASE WHEN last_p.id IS NULL THEN 1 END) as sinPagos,
            
            (SELECT COALESCE(SUM(amount), 0) 
            FROM payment 
            WHERE student_id IN (SELECT id FROM student WHERE gym_id = :gymId)
            AND payment_date >= DATE_TRUNC('month', CURRENT_DATE)
            AND payment_date < DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month'
            ) as totalMes
        FROM student s
        LEFT JOIN (
            SELECT DISTINCT ON (student_id) id, due_date, student_id
            FROM payment
            ORDER BY student_id, due_date DESC
        ) last_p ON s.id = last_p.student_id
        WHERE s.gym_id = :gymId
        """, nativeQuery = true)
    Map<String, Object> getRawSummary(@Param("gymId") Long gymId);

    @Query(value = "SELECT " +
       "TO_CHAR(registration_date, 'Mon') as name, " +
       "COUNT(*) as total " +
       "FROM student " +
       "WHERE gym_id = :gymId " +
       "GROUP BY TO_CHAR(registration_date, 'Mon'), EXTRACT(MONTH FROM registration_date) " +
       "ORDER BY EXTRACT(MONTH FROM registration_date) ASC " +
       "LIMIT 6", nativeQuery = true)
    List<Map<String, Object>> getMonthlyRegistrations(Long gymId);
    
    @Query(value = """
    SELECT 
        to_char(registration_date, 'YYYY-MM') as mes, 
        count(*) as cantidad 
    FROM student 
    WHERE gym_id = :gymId 
    GROUP BY mes 
    ORDER BY mes DESC 
    LIMIT 12
    """, nativeQuery = true)
    List<Map<String, Object>> getRegistrationStats(@Param("gymId") Long gymId);

}
