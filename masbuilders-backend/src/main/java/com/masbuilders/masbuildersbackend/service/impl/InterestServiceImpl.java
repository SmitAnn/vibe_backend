package com.masbuilders.masbuildersbackend.service.impl;


import com.masbuilders.masbuildersbackend.entity.Interest;
import com.masbuilders.masbuildersbackend.repository.InterestRepository;
import com.masbuilders.masbuildersbackend.service.InterestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterestServiceImpl implements InterestService {

    private final InterestRepository interestRepository;

    public InterestServiceImpl(InterestRepository interestRepository) {
        this.interestRepository = interestRepository;
    }

    @Override
    public Interest addInterest(Interest interest) {
        return interestRepository.save(interest);
    }

    @Override
    public List<Interest> getSellerInterests(String sellerId) {
        return interestRepository.findBySellerId(sellerId);
    }

    @Override
    public List<Interest> getBuyerInterests(String buyerId) {
        return interestRepository.findByBuyerId(buyerId);
    }
}

