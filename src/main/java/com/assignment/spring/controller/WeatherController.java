package com.assignment.spring.controller;

import com.assignment.spring.api.WeatherResponse;
import com.assignment.spring.configuration.ApiConfig;
import com.assignment.spring.persistence.WeatherEntity;
import com.assignment.spring.persistence.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

@RestController
public class WeatherController {

    @Autowired
    private ApiConfig config;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeatherRepository weatherRepository;

    @RequestMapping("/weather")
    @ResponseBody
    public ResponseEntity<WeatherDTO> weather(HttpServletRequest request) {
        try
        {
            UriComponents uriComponents = UriComponentsBuilder.newInstance()
                    .scheme(config.getProtocol()).host(config.getHost()).path(config.getEndpoint()).query(config.getQuery()).
                            buildAndExpand(request.getParameter(config.getParameter()), config.getKey());

            ResponseEntity<WeatherResponse> response = restTemplate.getForEntity(uriComponents.toUriString(), WeatherResponse.class);
            //if the call dosen't throw HttpClientErrorException it is a 200 so we go ahead in storing data and return the body
            return new ResponseEntity<>(mapDTOfromEntity(mapResponseAndSave(response.getBody()), response.getStatusCode()), HttpStatus.OK);

        }catch(HttpClientErrorException ex){
            return new ResponseEntity<>(new WeatherDTO(ex.getMessage()), null, ex.getStatusCode());
        }catch(RuntimeException rex){
            throw rex;
        }

    }

    private WeatherEntity mapResponseAndSave(WeatherResponse response){
        WeatherEntity entity = new WeatherEntity();
        entity.setCity(response.getName());
        entity.setCountry(response.getSys().getCountry());
        entity.setTemperature(response.getMain().getTemp());

        return weatherRepository.save(entity);
    }

    private WeatherDTO mapDTOfromEntity(WeatherEntity entity, HttpStatus status){
        WeatherDTO weatherDTO = new WeatherDTO();
        weatherDTO.setEntityId(entity.getId());
        weatherDTO.setCity(entity.getCity());
        weatherDTO.setCountry(entity.getCountry());
        weatherDTO.setTemperature(entity.getTemperature());
        weatherDTO.setStatusMessage(status.getReasonPhrase());
        return weatherDTO;
    }




}
