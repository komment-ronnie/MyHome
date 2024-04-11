package com.myhome.configuration;

import com.myhome.configuration.properties.mail.EmailTemplateLocalizationProperties;
import com.myhome.configuration.properties.mail.EmailTemplateProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Locale;

/**
 * is a configuration class that sets up email template handling for an application.
 * It provides a template engine and message source for Thymeleaf templates, allowing
 * for dynamic email content generation. The class also defines the path to the email
 * templates and their format, as well as caching and encoding settings.
 */
@Configuration
@RequiredArgsConstructor
public class EmailTemplateConfig {

  private final EmailTemplateProperties templateProperties;
  private final EmailTemplateLocalizationProperties localizationProperties;

  /**
   * creates a `ResourceBundleMessageSource` instance with configuration options set
   * from the `localizationProperties` object, and returns it.
   * 
   * @returns a `ResourceBundleMessageSource` instance configured to handle email localization.
   * 
   * 	- `ResourceBundleMessageSource`: This is the class that represents the message
   * source for email localization.
   * 	- `setBasename(localizationProperties.getPath())`: This sets the base name of the
   * resource bundle file.
   * 	- `setDefaultLocale(Locale.ENGLISH)`: Sets the default locale for the message source.
   * 	- `setDefaultEncoding(localizationProperties.getEncoding())`: Sets the default
   * encoding for the message source.
   * 	- `setCacheSeconds(localizationProperties.getCacheSeconds())`: Sets the cache
   * seconds for the message source.
   */
  @Bean
  public ResourceBundleMessageSource emailMessageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename(localizationProperties.getPath());
    messageSource.setDefaultLocale(Locale.ENGLISH);
    messageSource.setDefaultEncoding(localizationProperties.getEncoding());
    messageSource.setCacheSeconds(localizationProperties.getCacheSeconds());
    return messageSource;
  }

  /**
   * creates a Spring Template Engine instance and configures it with a Thymeleaf
   * Template Resolver and an Email Message Source for rendering emails.
   * 
   * @param emailMessageSource message source for email-related messages used by the SpringTemplateEngine.
   * 
   * 	- `ResourceBundleMessageSource`: This interface represents a message source that
   * retrieves message keys from a resource bundle. It provides access to messages in
   * various formats, including HTML, XML, and YAML.
   * 
   * @returns a Spring Template Engine instance with Thymeleaf-specific settings and a
   * message source for email messages.
   * 
   * 	- The SpringTemplateEngine is initialized with a new instance.
   * 	- A template resolver is set to `thymeleafTemplateResolver()`.
   * 	- A message source for emails is set to `emailMessageSource`.
   */
  @Bean
  public SpringTemplateEngine thymeleafTemplateEngine(ResourceBundleMessageSource emailMessageSource) {
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(thymeleafTemplateResolver());
    templateEngine.setTemplateEngineMessageSource(emailMessageSource);
    return templateEngine;
  }

  /**
   * creates a `ITemplateResolver` instance that sets the prefix, suffix, mode, encoding
   * and caching properties for Thymeleaf template resolution.
   * 
   * @returns a `ITemplateResolver` instance with customized configuration settings for
   * Thymeleaf template resolution.
   * 
   * 	- `templateProperties`: This is an instance of `ITemplateProperties`, which
   * contains information about the Thymeleaf template, such as its path, format, mode,
   * and encoding.
   * 	- `prefix`: This is a string that represents the prefix of the template path. If
   * the template path ends with the file separator, this property is set to the template
   * path itself. Otherwise, it is set to the template path concatenated with the file
   * separator.
   * 	- `suffix`: This is a string that represents the suffix of the template path. It
   * contains the format of the template.
   * 	- `templateMode`: This is an integer that represents the mode of the template.
   * It can be one of the following values: `ITemplateMode.NONE`, `ITemplateMode.HTML`,
   * or `ITemplateMode.XML`.
   * 	- `characterEncoding`: This is a string that represents the character encoding
   * of the template. It can be any of the supported encodings, such as `UTF-8`,
   * `ISO-8859-1`, etc.
   * 	- `cacheable`: This is a boolean value that indicates whether the template should
   * be cached or not. If set to `true`, the template will be cached; otherwise, it
   * will not be cached.
   */
  private ITemplateResolver thymeleafTemplateResolver() {
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

    String templatePath = templateProperties.getPath();
    String fileSeparator = System.getProperty("file.separator");
    templateResolver.setPrefix(templatePath.endsWith(fileSeparator) ? templatePath : templatePath + fileSeparator);

    templateResolver.setSuffix(templateProperties.getFormat());
    templateResolver.setTemplateMode(templateProperties.getMode());
    templateResolver.setCharacterEncoding(templateProperties.getEncoding());
    templateResolver.setCacheable(templateProperties.isCache());
    return templateResolver;
  }

}
