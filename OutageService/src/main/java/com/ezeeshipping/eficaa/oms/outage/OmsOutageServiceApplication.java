package com.ezeeshipping.eficaa.oms.outage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;



@SpringBootApplication
@EnableScheduling
public class OmsOutageServiceApplication extends SpringBootServletInitializer {
	 private static final String dateFormat = "yyyy-MM-dd";
	    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	public static void main(String[] args) {
		SpringApplication.run(OmsOutageServiceApplication.class, args);
	}
	 @Bean
	    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
	        return builder -> {
	            builder.simpleDateFormat(dateTimeFormat);
	            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
	            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
	        };
	    }
	/* @Bean
	    public Docket api() { 
	        return new Docket(DocumentationType.SWAGGER_2)  
	          .select()                                  
	          .apis(RequestHandlerSelectors.any())              
	          .paths(PathSelectors.any())                          
	          .build();                                           
	    }
	
	@Bean
	   public Docket productApi() {
	      return new Docket(DocumentationType.SWAGGER_2).select()
	         .apis(RequestHandlerSelectors.basePackage("com.ezeeshipping.eficaa.oms.outage")).build();
	   }*/
	/*@Bean
	public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
	        ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier,
	        EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties,
	        WebEndpointProperties webEndpointProperties, Environment environment) {
	    List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
	    Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
	    allEndpoints.addAll(webEndpoints);
	    allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
	    allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
	    String basePath = webEndpointProperties.getBasePath();
	    EndpointMapping endpointMapping = new EndpointMapping(basePath);
	    boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment,
	            basePath);
	    return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
	            corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath),
	            shouldRegisterLinksMapping, null);
	}

	private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment,
	        String basePath) {
	    return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath)
	        || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
	}*/
	/* @Bean("asyncExecution")
	 public TaskExecutor getAysncExecutor() {
		 ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 1000, keepAliveTime, unit, workQueue)
	 }*/

}
	   
	
	

