package com.leka.teashop.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.context.annotation.ScopedProxyMode.INTERFACES;

@Configuration
public class TeaShopConfiguration implements WebMvcConfigurer {

    @Bean
    @Override
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("/WEB-INF/images/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations("/WEB-INF/views/js/");

        registry.addResourceHandler("/css/**")
                .addResourceLocations("/WEB-INF/views/css/");
    }
    /**
     * The Method adds view to default link "/".
     * Link "/..." is also resolved, because registering view controller
     * prevents error 404 which appears when after user login the redirection to
     * welcome page occurs by path "../teashop/...?continue"
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("welcome-page");
        registry.addViewController("/...").setViewName("welcome-page");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/language/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean(name = "filters")
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = INTERFACES)
    public Map<String, Boolean> getNewFilters() {
        return new HashMap<>();
    }

}
