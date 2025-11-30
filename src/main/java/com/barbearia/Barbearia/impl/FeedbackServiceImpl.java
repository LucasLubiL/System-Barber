package com.barbearia.Barbearia.impl;

import com.barbearia.Barbearia.Model.Feedback;
import com.barbearia.Barbearia.Repository.FeedbackRepository;
import com.barbearia.Barbearia.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public List<Feedback> listarFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Override
    public void salvarFeedback(Feedback feedback) {
        feedbackRepository.save(feedback);
    }
}