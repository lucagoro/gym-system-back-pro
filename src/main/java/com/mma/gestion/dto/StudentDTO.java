package com.mma.gestion.dto;

import java.time.LocalDate;

import com.mma.gestion.StudentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String name;
    private String surname;
    private String address;
    private Integer age;
    private String phone;
    private String dni;
    private StudentStatus status;
    private LocalDate dueDate;
    private String planName;
    private Long planId;
}
