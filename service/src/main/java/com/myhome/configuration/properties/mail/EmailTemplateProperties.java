package com.myhome.configuration.properties.mail;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * is used to store email template settings such as path, format, encoding, mode, and
 * cache status in Spring Boot application.
 * Fields:
 * 	- path (String): in EmailTemplateProperties represents a string value specifying
 * the file path for storing email templates.
 * 	- format (String): in the EmailTemplateProperties class is a String variable
 * representing the format of an email template.
 * 	- encoding (String): in the EmailTemplateProperties class specifies the character
 * encoding to be used for email template rendering.
 * 	- mode (String): in the EmailTemplateProperties class represents the template
 * rendering mode, which can be either "HTML" or "TEXT".
 * 	- cache (boolean): in the EmailTemplateProperties class is of type boolean and
 * represents whether or not to cache email templates.
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
