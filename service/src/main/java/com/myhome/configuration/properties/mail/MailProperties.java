package com.myhome.configuration.properties.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * defines properties related to sending emails using Spring Boot, including host,
 * username, password, port, protocol, debug, and dev mode.
 * Fields:
 * 	- host (String): in the MailProperties class represents the mail server hostname.
 * 	- username (String): in the MailProperties class represents a user name for sending
 * emails.
 * 	- password (String): in MailProperties represents a string value used to authenticate
 * mail server connections.
 * 	- port (int): in the MailProperties class represents an integer value indicating
 * the mail server's port number for communication.
 * 	- protocol (String): in MailProperties represents the mail protocol used for
 * sending and receiving emails, which could be SMTP, IMAP, POP3 or any other.
 * 	- debug (boolean): in MailProperties is a boolean flag indicating whether the
 * mail server's debugging capabilities are enabled.
 * 	- devMode (boolean): in MailProperties represents a boolean flag indicating whether
 * the mail server settings are for development use or not.
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

