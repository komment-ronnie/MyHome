package com.myhome.configuration.properties.mail;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * stores email template settings such as file path, format, encoding, mode, and cache
 * status in a Spring Boot configuration property class.
 * Fields:
 * 	- path (String): in the EmailTemplateProperties class represents the file path
 * for storing email templates.
 * 	- format (String): in the EmailTemplateProperties class is of type String and
 * represents a string value specifying the format of an email template.
 * 	- encoding (String): in the EmailTemplateProperties class represents a string
 * variable specifying the character encoding to be used for email template rendering.
 * 	- mode (String): in the EmailTemplateProperties class represents the template
 * rendering mode, which can be either "HTML" or "TEXT".
 * 	- cache (boolean): in the EmailTemplateProperties class represents whether email
 * templates should be cached or not.
 */
@Data
@Component
@ConfigurationProperties(prefix = "email.template")
public class EmailTemplateProperties {
  private String path;
  private String format;
  private String encoding;
  private String mode;
  private boolean cache;
}
