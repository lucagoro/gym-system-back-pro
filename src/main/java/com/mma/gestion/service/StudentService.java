package com.mma.gestion.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mma.gestion.StudentStatus;
import com.mma.gestion.dto.ChartDataDTO;
import com.mma.gestion.dto.RegistrationStatDTO;
import com.mma.gestion.dto.StudentDTO;
import com.mma.gestion.dto.StudentSummaryDTO;
import com.mma.gestion.entity.Gym;
import com.mma.gestion.entity.Payment;
import com.mma.gestion.entity.Student;
import com.mma.gestion.exception.PhoneAlreadyExistsException;
import com.mma.gestion.exception.StudentHasPaymentsException;
import com.mma.gestion.repository.GymRepository;
import com.mma.gestion.repository.PaymentRepository;
import com.mma.gestion.repository.PlanRepository;
import com.mma.gestion.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;
    private final GymRepository gymRepository;
    private final PlanRepository planRepository;

    public StudentDTO getStudentById(Long id, Long gymId) {
        return studentRepository.findByIdAndGymId(id, gymId)
            .map(this::convertToDTO) // Usamos un método helper para no repetir código
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
    }

    public List<StudentDTO> getAllStudents(Long gymId) { 
        return studentRepository.findByGymId(gymId).stream()
                .map(this::convertToDTO)
                .toList(); 
    }

    public StudentDTO createStudent(StudentDTO dto, Long gymId) {
        // Validamos teléfono SOLO dentro de este gimnasio
        if (studentRepository.existsByPhoneAndGymId(dto.getPhone(), gymId)) {
            throw new PhoneAlreadyExistsException("El teléfono ya existe en su gimnasio");
        }

        // Buscamos el gimnasio al que pertenece el usuario que crea el alumno
        Gym gym = gymRepository.findById(gymId)
            .orElseThrow(() -> new RuntimeException("Gimnasio no encontrado"));

        Student student = new Student();
        student.setName(dto.getName());
        student.setSurname(dto.getSurname());
        student.setPhone(dto.getPhone());
        student.setDni(dto.getDni());
        student.setGym(gym); // VINCULAMOS EL ALUMNO AL GIMNASIO

        // Buscamos la referencia del plan que eligió el usuario
        if (dto.getPlanId() != null) {
            student.setPlan(planRepository.getReferenceById(dto.getPlanId()));
        }

        Student savedStudent = studentRepository.save(student);

        return convertToDTO(savedStudent);
    }

   public StudentDTO updateStudent(Long id, StudentDTO dto, Long gymId) {
    // Buscamos asegurando que sea del gym
    Student student = studentRepository.findById(id)
            .filter(s -> s.getGym().getId().equals(gymId))
            .orElseThrow(() -> new RuntimeException("No autorizado para editar este alumno"));

    if (!student.getPhone().equals(dto.getPhone())
            && studentRepository.existsByPhoneAndGymId(dto.getPhone(), gymId)) {
        throw new RuntimeException("El teléfono ya está registrado por otro alumno en este gimnasio");
    }

    student.setName(dto.getName());
    student.setSurname(dto.getSurname());
    student.setPhone(dto.getPhone());
    student.setDni(dto.getDni());

    // Actualizamos el plan si viene uno nuevo en el DTO
    if (dto.getPlanId() != null) {
        student.setPlan(planRepository.getReferenceById(dto.getPlanId()));
    }

    Student updatedStudent = studentRepository.save(student);

    return convertToDTO(updatedStudent);
}

    public void deleteStudent(Long id, Long gymId) {
        Student student = studentRepository.findById(id)
            .filter(s -> s.getGym().getId().equals(gymId))
            .orElseThrow(() -> new RuntimeException("No autorizado para eliminar este alumno"));

        if (paymentRepository.existsByStudentId(id)) {
            throw new StudentHasPaymentsException(id);
        }
        studentRepository.delete(student);
    }

    private StudentStatus calculateStatus(Student student) {
        // Buscamos el último pago directamente en la lista que ya trajo Hibernate
        return student.getPayments().stream()
                .max(Comparator.comparing(Payment::getDueDate)) // Buscamos el vencimiento más lejano
                .map(lastPayment -> {
                    if (lastPayment.getDueDate().isBefore(LocalDate.now())) {
                        return StudentStatus.VENCIDO;
                    }
                    return StudentStatus.AL_DIA;
                })
                .orElse(StudentStatus.SIN_PAGOS); // Si la lista está vacía
    }

    public List<StudentDTO> getStudentsByStatus(StudentStatus status, Long gymId) {
        return studentRepository.findByGymId(gymId).stream() 
                .filter(student -> calculateStatus(student) == status)
                .map(this::convertToDTO)
                .toList();
        }

    public StudentSummaryDTO getStudentsSummary(Long gymId) {
    Map<String, Object> rawData = studentRepository.getRawSummary(gymId);
    List<Map<String, Object>> chartRaw = studentRepository.getMonthlyRegistrations(gymId);

    List<ChartDataDTO> chartData = chartRaw.stream()
        .map(m -> new ChartDataDTO(
            m.get("name") != null ? m.get("name").toString() : "S/D", 
            m.get("total") != null ? ((Number) m.get("total")).longValue() : 0L
        ))
        .collect(Collectors.toList());

    return new StudentSummaryDTO(
        getSafeLong(rawData.get("total")),
        getSafeLong(rawData.get("aldia")),
        getSafeLong(rawData.get("vencidos")),
        getSafeLong(rawData.get("sinpagos")),
        rawData.get("totalmes") != null ? new BigDecimal(rawData.get("totalmes").toString()) : BigDecimal.ZERO,
        chartData
    );
}

    private long getSafeLong(Object value) {
        return (value != null) ? ((Number) value).longValue() : 0L;
    }

    // Obtiene estadísticas de registros mensuales de alumnos
    public List<RegistrationStatDTO> getRegistrationStats(Long gymId) {
        List<Map<String, Object>> rawStats = studentRepository.getRegistrationStats(gymId);
        
        return rawStats.stream()
                .map(row -> new RegistrationStatDTO(
                        (String) row.get("mes"),
                        ((Number) row.get("cantidad")).longValue()
                ))
                .toList();
    }

    // Método helper para convertir de Entity a DTO
    private StudentDTO convertToDTO(Student student) {
        return new StudentDTO(
            student.getId(),
            student.getName(),
            student.getSurname(),
            student.getPhone(),
            student.getDni(),
            calculateStatus(student),
            student.getDueDate(),
            // Si el plan es null, mandamos null o "Sin Plan"
            student.getPlan() != null ? student.getPlan().getName() : "Sin Plan",
            student.getPlan() != null ? student.getPlan().getId() : null
        );
    }   
}
