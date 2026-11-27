package com.gym.repository;
import com.gym.model.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GymRepo extends JpaRepository<Gym,Long>{}
