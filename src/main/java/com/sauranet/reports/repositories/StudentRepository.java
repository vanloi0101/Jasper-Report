package com.sauranet.reports.repositories;

import com.sauranet.reports.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}
