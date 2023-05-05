package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user=userRepository.findById(subscriptionEntryDto.getUserId()).get();

        Subscription subscription=new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setTotalAmountPaid(subtypeCost(subscriptionEntryDto.getSubscriptionType()));
        subscription.setStartSubscriptionDate(new Date());

        subscription.setUser(user);
        user.setSubscription(subscription);
        userRepository.save(user);

        return subtypeCost(subscriptionEntryDto.getSubscriptionType());
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        User user=userRepository.findById(userId).get();
        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        else if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.PRO)){
            user.getSubscription().setSubscriptionType(SubscriptionType.ELITE);
            user.getSubscription().setTotalAmountPaid(subtypeCost(SubscriptionType.ELITE));
            user.getSubscription().setNoOfScreensSubscribed(1000);

            userRepository.save(user);
            return 200;
        }
        else{
            user.getSubscription().setSubscriptionType(SubscriptionType.PRO);
            user.getSubscription().setTotalAmountPaid(subtypeCost(SubscriptionType.PRO));
            user.getSubscription().setNoOfScreensSubscribed(800);

            userRepository.save(user);
            return 300;
        }
        //update the subscription in the repository
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription>subscriptionList=subscriptionRepository.findAll();
        int amount=0;
        for(Subscription subscription:subscriptionList){
            amount+=subscription.getTotalAmountPaid();
        }
        return amount;
    }

    public int subtypeCost(SubscriptionType subscriptionType){
        Map<SubscriptionType,Integer> rateMap=new HashMap<>();
        rateMap.put(SubscriptionType.BASIC,500);
        rateMap.put(SubscriptionType.PRO,800);
        rateMap.put(SubscriptionType.ELITE,1000);

        return rateMap.get(subscriptionType);
    }

}
