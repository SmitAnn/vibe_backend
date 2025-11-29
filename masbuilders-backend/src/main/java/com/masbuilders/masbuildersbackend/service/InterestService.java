package com.masbuilders.masbuildersbackend.service;



import com.masbuilders.masbuildersbackend.entity.Interest;

import java.util.List;

public interface InterestService {

    Interest addInterest(Interest interest);

    List<Interest> getSellerInterests(String sellerId);

    List<Interest> getBuyerInterests(String buyerId);
}
