package com.assignment.spring;

import com.assignment.spring.api.WeatherResponse;
import com.assignment.spring.controller.WeatherController;
import com.assignment.spring.controller.WeatherDTO;
import com.assignment.spring.persistence.WeatherEntity;
import com.assignment.spring.persistence.WeatherRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

	@LocalServerPort
	int randomServerPort;

	@Autowired
	RestTemplate restTemplate = new RestTemplate();


	@Autowired
	private WeatherRepository weatherRepository;

	@Value("${test.host}")
	String host;


	@Value("${service.url}")
	String url;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testGetWeatherSuccess() throws  URISyntaxException {

		final String baseUrl = host + randomServerPort + url;

		URI uri = new URI(baseUrl+"?city=Amsterdam");

		ResponseEntity<WeatherDTO> result =
				restTemplate.getForEntity(uri, WeatherDTO.class);
		//Verify request succeed
		Assert.assertEquals(200, result.getStatusCodeValue());
		Assert.assertEquals(true, result.getBody() instanceof WeatherDTO);
	}

	@Test
	public void testGetWeatherDbInsert() throws  URISyntaxException {

		final String baseUrl = host + randomServerPort + url;

		URI uri = new URI(baseUrl+"?city=Naples");

		ResponseEntity<WeatherDTO> result =
				restTemplate.getForEntity(uri, WeatherDTO.class);
		//Verify request succeed
		Assert.assertEquals(200, result.getStatusCodeValue());
		Assert.assertEquals(true, weatherRepository.findById(result.getBody().getEntityId()).isPresent());

	}

	@Test
	public void testGetWeatherBadReuest() throws  URISyntaxException {

		final String baseUrl = host + randomServerPort + url;

		//no city set to force bad request
		URI uri = new URI(baseUrl+"?city");

		try{

			restTemplate.getForEntity(uri, WeatherDTO.class);

		}catch(HttpClientErrorException ex){
			//Verify bad request from the message stored in body
			Assert.assertEquals(400, ex.getRawStatusCode());

		}
	}


}
