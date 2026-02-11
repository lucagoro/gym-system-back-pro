package com.mma.gestion.dto;

import java.time.LocalDate;

import com.mma.gestion.StudentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String name;
    private String surname;
    private String phone;
    private String dni;
    private StudentStatus status;
    private LocalDate dueDate;
    private String planName;
    private Long planId;
}
