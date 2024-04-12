package com.myhome.configuration.properties.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * is a configuration class that defines properties related to sending emails using
 * Spring Boot, including the mail host, username, password, port, protocol, debug,
 * and dev mode.
 * Fields:
 * 	- host (String): in MailProperties represents the mail server hostname.
 * 	- username (String): in the MailProperties class is of type String and represents
 * a user name for sending emails.
 * 	- password (String): in MailProperties represents a string value used to authenticate
 * mail server connections.
 * 	- port (int): in MailProperties represents an integer value indicating the mail
 * server's port number for communication.
 * 	- protocol (String): in MailProperties represents a mail protocol (e.g., SMTP,
 * IMAP, POP3) used for sending and receiving emails.
 * 	- debug (boolean): in the MailProperties class is a boolean flag indicating whether
 * the mail server's debugging capabilities are enabled.
 * 	- devMode (boolean): in the MailProperties class is a boolean flag indicating
 * whether the mail server settings are for development use or not.
 */
@Data
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {
  private String host;
  private String username;
  private String password;
  private int port;
  private String protocol;
  private boolean debug;
  private boolean devMode;
}

