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
        subscription.setTotalAmountPaid(subtypeCost(subscriptionEntryDto.getSubscriptionType())
                +findi(subscriptionEntryDto.getSubscriptionType())*(subscriptionEntryDto.getNoOfScreensRequired()));
        subscription.setStartSubscriptionDate(new Date());

        subscription.setUser(user);
        user.setSubscription(subscription);
        userRepository.save(user);
        int amount=subtypeCost(subscriptionEntryDto.getSubscriptionType())
                +findi(subscriptionEntryDto.getSubscriptionType())*(subscriptionEntryDto.getNoOfScreensRequired());

        return amount;
    }

    private int findi(SubscriptionType subscriptionType) {
        if(subscriptionType.equals(SubscriptionType.ELITE))return 350;
        else if(subscriptionType.equals(SubscriptionType.PRO))return 250;
        else{
            return 200;
        }
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        User user=userRepository.findById(userId).get();
        int amount=0;
        Subscription subscription=user.getSubscription();
        if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        else if(subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(1000+(350*subscription.getNoOfScreensSubscribed()));
            amount=(1000+(350*subscription.getNoOfScreensSubscribed()))-(800+(250*subscription.getNoOfScreensSubscribed()));
        }
        else{
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(800+(250*subscription.getNoOfScreensSubscribed()));
            amount=(800+(250*subscription.getNoOfScreensSubscribed()))-(500+(200*subscription.getNoOfScreensSubscribed()));
        }
        subscription.setUser(user);
        user.setSubscription(subscription);

        userRepository.save(user);

        //update the subscription in the repository
        return amount;
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
