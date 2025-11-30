package com.barbearia.Barbearia.service;

import java.util.List;

import com.barbearia.Barbearia.Model.Feedback;

public interface FeedbackService {

        List<Feedback> listarFeedbacks();

        void salvarFeedback(Feedback feedback);
}
