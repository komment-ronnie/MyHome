package com.myhome.configuration.properties.mail;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * represents a configuration class for email localization properties, including path,
 * encoding, and cache seconds.
 * Fields:
 * 	- path (String): in the EmailTemplateLocalizationProperties class represents a
 * string value specifying the location of an email template file.
 * 	- encoding (String): represents a string value specifying the character encoding
 * used for email templates.
 * 	- cacheSeconds (int): in the EmailTemplateLocalizationProperties class represents
 * the number of seconds an email template's location data is cached before it is refreshed.
 */
@Data
@Component
@ConfigurationProperties(prefix = "email.location")
public class EmailTemplateLocalizationProperties {
  private String path;
  private String encoding;
  private int cacheSeconds;
}
