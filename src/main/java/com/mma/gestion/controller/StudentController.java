package com.mma.gestion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mma.gestion.StudentStatus;
import com.mma.gestion.dto.RegistrationStatDTO;
import com.mma.gestion.dto.StudentDTO;
import com.mma.gestion.dto.StudentSummaryDTO;
import com.mma.gestion.entity.User;
import com.mma.gestion.service.StudentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Long gymId = user.getGym().getId();
        StudentDTO studentDTO = studentService.getStudentById(id, gymId);
        return ResponseEntity.ok(studentDTO);
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO studentDTO, @AuthenticationPrincipal User user) {
        Long gymId = user.getGym().getId();
        StudentDTO created = studentService.createStudent(studentDTO, gymId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody StudentDTO studentDTO, @AuthenticationPrincipal User user) {
        Long gymId = user.getGym().getId();
        StudentDTO updatedStudent = studentService.updateStudent(id, studentDTO, gymId);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Long gymId = user.getGym().getId();
        studentService.deleteStudent(id, gymId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getStudents(@RequestParam(required = false) StudentStatus status, @AuthenticationPrincipal User user) {
        Long gymId = user.getGym().getId(); // Lo sacamos del objeto que Spring ya cargó

        if (status == null) {
            return ResponseEntity.ok(studentService.getAllStudents(gymId));
        }

        return ResponseEntity.ok(studentService.getStudentsByStatus(status, gymId));
    }

    @GetMapping("/summary")
    public ResponseEntity<StudentSummaryDTO> getStudentsSummary(@AuthenticationPrincipal User user) {
        Long gymId = user.getGym().getId();
        return ResponseEntity.ok(studentService.getStudentsSummary(gymId));
    }

    @GetMapping("/stats/registrations")
    public ResponseEntity<List<RegistrationStatDTO>> getRegistrationStats(@AuthenticationPrincipal User user) {
        // Extraemos el gymId del usuario autenticado
        return ResponseEntity.ok(studentService.getRegistrationStats(user.getGym().getId()));
    }
}   