package com.barbearia.Barbearia.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.barbearia.Barbearia.Model.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT f FROM Feedback f JOIN FETCH f.cliente")
    List<Feedback> listarFeedbacks();

}
