package com.mma.gestion.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mma.gestion.PaymentMethod;
import com.mma.gestion.dto.PaymentDTO;
import com.mma.gestion.entity.Payment;
import com.mma.gestion.entity.Plan;
import com.mma.gestion.entity.Student;
import com.mma.gestion.exception.PaymentNotFoundException;
import com.mma.gestion.exception.StudentNotFoundException;
import com.mma.gestion.repository.PaymentRepository;
import com.mma.gestion.repository.PlanRepository;
import com.mma.gestion.repository.StudentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final PlanRepository planRepository;

    // 1. Registro de Pago AUTOMATIZADO (El más importante)
    @Transactional
    public PaymentDTO createPaymentForStudent(Long studentId, Long gymId, Long planId, PaymentMethod method, LocalDate paymentDate) {
        // Buscamos alumno filtrando por Gym para seguridad
        Student student = studentRepository.findByIdAndGymId(studentId, gymId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Buscamos el plan que eligió el usuario en el Modal
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        // Actualizamos el plan del alumno (por si se cambió)
        student.setPlan(plan);

        Payment payment = new Payment();    
        payment.setStudent(student);
        payment.setAmount(plan.getPrice()); // Viene del Plan
        payment.setPaymentDate(paymentDate); // Viene del Modal
        payment.setMethod(method); // Viene del Modal
        
        // Lógica de Vencimiento Inteligente:
        // Si ya tiene un vencimiento futuro, sumamos desde ahí. Si no, desde hoy.
        LocalDate baseDate = (student.getDueDate() != null && student.getDueDate().isAfter(LocalDate.now())) 
                             ? student.getDueDate() 
                             : paymentDate;
        
        LocalDate newDueDate = baseDate.plusDays(plan.getDurationDays());
        payment.setDueDate(newDueDate);
        
        // Seteamos el periodo (ej: "2026-01")
        payment.setPeriod(paymentDate.format(DateTimeFormatter.ofPattern("yyyy-MM")));

        // IMPORTANTE: Actualizamos el vencimiento en el Alumno
        student.setDueDate(newDueDate);
        studentRepository.save(student);

        return convertToDTO(paymentRepository.save(payment));
    }

    // 2. Obtener pagos de un alumno
    public List<PaymentDTO> getPaymentsByStudentId(Long studentId, Long gymId) {
        // Validamos que el alumno exista y sea de ese gym
        if (!studentRepository.existsByIdAndGymId(studentId, gymId)) {
            throw new StudentNotFoundException(studentId);
        }

        return paymentRepository.findByStudentIdOrderByPaymentDateDesc(studentId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // 3. Obtener pago por ID
    public PaymentDTO getPaymentById(Long paymentId, Long gymId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        
        // Validamos pertenencia al gym
        if (!payment.getStudent().getGym().getId().equals(gymId)) {
            throw new RuntimeException("No autorizado");
        }
        
        return convertToDTO(payment);
    }

    public List<PaymentDTO> getAllPayments(Long gymId) {
    // Buscamos pagos donde el estudiante pertenezca al gimnasio del ADMIN
    return paymentRepository.findAll().stream()
            .filter(p -> p.getStudent().getGym().getId().equals(gymId))
            .map(this::convertToDTO)
            .toList();
    }

    public List<PaymentDTO> getPaymentsByDate(LocalDate start, LocalDate end, Long gymId) {
    // El repo ya nos trae solo lo que corresponde al gimnasio y al rango de fechas
    return paymentRepository.findByPaymentDateBetweenAndStudentGymIdOrderByPaymentDateDesc(start, end, gymId)
            .stream()
            .map(this::convertToDTO)
            .toList();
    }

    // 4. Update (Solo para corregir montos o periodos, el vencimiento es delicado)
    @Transactional
    public PaymentDTO updatePayment(Long paymentId, PaymentDTO dto, Long gymId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (!payment.getStudent().getGym().getId().equals(gymId)) {
            throw new RuntimeException("No autorizado");
        }

        payment.setAmount(dto.getAmount());
        payment.setPeriod(dto.getPeriod());

        return convertToDTO(paymentRepository.save(payment));
    }

    @Transactional
    public void deletePayment(Long paymentId, Long gymId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        
        if (!payment.getStudent().getGym().getId().equals(gymId)) {
            throw new RuntimeException("No autorizado");
        }
        
        paymentRepository.delete(payment);
    }

    // Método helper para no repetir código
    private PaymentDTO convertToDTO(Payment p) {
        return new PaymentDTO(
                p.getId(),
                p.getAmount(),
                p.getPaymentDate(),
                p.getDueDate(),
                p.getPeriod(),
                p.getStudent().getId(),
                p.getStudent().getName(), // Agregamos el nombre del alumno al DTO
                p.getStudent().getPlan().getName()  
        );
    }
    
}
