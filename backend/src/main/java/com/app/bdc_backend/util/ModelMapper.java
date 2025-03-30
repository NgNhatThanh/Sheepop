package com.app.bdc_backend.util;

import org.modelmapper.convention.MatchingStrategies;

public class ModelMapper {

    private static org.modelmapper.ModelMapper mapper;

    public static org.modelmapper.ModelMapper getInstance(){
        if(mapper == null){
            mapper = new org.modelmapper.ModelMapper();
            mapper.getConfiguration()
                    .setMatchingStrategy(MatchingStrategies.STANDARD);
        }
        return mapper;
    }

}