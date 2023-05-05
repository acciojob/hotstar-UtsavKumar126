package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception {

        //Add a webSeries to the database and update the ratings of the productionHouse
        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        if (webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName()) != null) {
            throw new Exception("Series is already present");
        }
        //use function written in Repository Layer for the same
        else {//Dont forget to save the production and webseries Repo
            WebSeries webSeries1 = new WebSeries();
            webSeries1.setSeriesName(webSeriesEntryDto.getSeriesName());
            webSeries1.setAgeLimit(webSeriesEntryDto.getAgeLimit());
            webSeries1.setRating(webSeriesEntryDto.getRating());
            webSeries1.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
            webSeries1.setProductionHouse(productionHouse);
            productionHouse.getWebSeriesList().add(webSeries1);

            double curRating = productionHouse.getRatings();
            int noOfwebSerires = productionHouse.getWebSeriesList().size();

            double newRating = ((curRating * noOfwebSerires) + webSeriesEntryDto.getRating()) / noOfwebSerires + 1;

            productionHouse.setRatings(newRating);

            productionHouseRepository.save(productionHouse);

            return webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName()).getId();
        }
    }

}
