package uk.co.boots.columbus.cmdb.cli;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CmdbCliApplication {
	private static final Logger log = LoggerFactory.getLogger(CmdbCliApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CmdbCliApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {

		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate, ApplicationArguments cliargs) throws Exception {
		return args -> {
			String envResource = "/api/environments/config/yaml/";
			String user;
			String pass;
			String server;
			String port;
			String fileName;
			
			List<String> envs = cliargs.getOptionValues("env");
			if (envs != null && envs.size() > 0)
				envResource = envResource + cliargs.getOptionValues("env").get(0);
			else
				envResource = envResource + "all";
			
			try{
				user = cliargs.getOptionValues("user").get(0);
				pass = cliargs.getOptionValues("pass").get(0);
				server = cliargs.getOptionValues("server").get(0);
				port = cliargs.getOptionValues("port").get(0);
				fileName = cliargs.getOptionValues("file").get(0);
			}
			catch (Exception e) {
				throw new ExceptionWithExitCode (e.getMessage(), 2);
			}
			
			try {
				HttpEntity<LoginDetails> request = new HttpEntity<>(new LoginDetails(user, pass));
				TokenResponse token = restTemplate.postForObject("http://" + server + ":" + port + "/api/login",
						request, TokenResponse.class);
				HttpHeaders headers = new HttpHeaders();
				headers.set("Authorization", token.getToken());
				headers.setAccept(Arrays.asList(new MediaType("text", "plain", Charset.forName("utf-8"))));
				HttpEntity<String> hdrParams = new HttpEntity<String>("parameters", headers);
				ResponseEntity<String> yr = restTemplate.exchange(
						"http://" + server + ":" + port + envResource, HttpMethod.POST,
						hdrParams, String.class);
				File file = new File(fileName);
				FileUtils.writeStringToFile(file, yr.getBody(), Charset.forName("utf-8"), false);
			} catch (RestClientException e) {
				log.info("Error!");
				log.info(e.getMessage());
				throw new ExceptionWithExitCode(e.getMessage(), 1);
			}
		};
	}
}
