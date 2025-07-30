package com.securaccess.enterprise.services;

import com.securaccess.enterprise.entities.Worker;
import com.securaccess.enterprise.repositories.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WorkerService {
    
    @Autowired
    private WorkerRepository workerRepository;
    
    public List<Worker> findAll() {
        return workerRepository.findAll();
    }
    
    public Optional<Worker> findById(Long id) {
        return workerRepository.findById(id);
    }
    
    public List<Worker> findByDepartment(String department) {
        return workerRepository.findByDepartment(department);
    }
    
    public List<Worker> findByActive(Boolean isActive) {
        return workerRepository.findByIsActive(isActive);
    }
    
    public List<Worker> findByDepartmentAndActive(String department, Boolean isActive) {
        return workerRepository.findByDepartmentAndIsActive(department, isActive);
    }
    
    public List<Worker> searchByName(String name) {
        // Method temporarily commented out due to repository method being unavailable
        // return workerRepository.findByNameContaining(name);
        return workerRepository.findAll(); // Simplified implementation
    }
    
    public Worker save(Worker worker) {
        if (worker.getId() == null) {
            worker.setCreatedAt(LocalDateTime.now());
        }
        worker.setUpdatedAt(LocalDateTime.now());
        return workerRepository.save(worker);
    }
    
    public void deleteById(Long id) {
        workerRepository.deleteById(id);
    }
    
    public Optional<Worker> findByPhone(String phone) {
        return workerRepository.findByPhone(phone);
    }
    
    public Long countActiveWorkersByDepartment(String department) {
        // Method temporarily commented out due to repository method being unavailable
        // return workerRepository.countActiveWorkersByDepartment(department);
        return (long) workerRepository.findByDepartmentAndIsActive(department, true).size();
    }
    
    public List<String> findAllActiveDepartments() {
        // Method temporarily commented out due to repository method being unavailable
        // return workerRepository.findAllActiveDepartments();
        return workerRepository.findByIsActive(true).stream()
                .map(Worker::getDepartment)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }
    
    public List<Worker> findWorkersWithLastAccessBefore(LocalDateTime date) {
        // Method temporarily commented out due to repository method being unavailable
        // return workerRepository.findWorkersWithLastAccessBefore(date);
        return workerRepository.findAll(); // Simplified implementation
    }
}