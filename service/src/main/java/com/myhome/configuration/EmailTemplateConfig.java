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
/**
 * :
 * The EmailTemplateConfig class is a custom configuration class for Spring's email
 * template engine. It provides several properties and methods for configuring the
 * email message source, including the basename, default locale, default encoding,
 * and cache seconds. Additionally, it provides a method to create a Spring Template
 * Engine instance with Thymeleaf-specific settings and an email message source.
 * 
 * The thymeleafTemplateEngine Method:
 * The thymeleafTemplateEngine method creates a Spring Template Engine instance
 * configured to use Thymeleaf as the template engine and email message source. It
 * sets the template resolver, template engine message source, and other configuration
 * properties using the `thymeleafTemplateProperties` instance.
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
  /**
   * creates a `ResourceBundleMessageSource` instance that retrieves email messages
   * from a specified location based on the application's locale and encoding.
   * 
   * @returns a `ResourceBundleMessageSource` object configured to handle email-related
   * messages.
   * 
   * 	- `basename`: The path to the resource bundle file.
   * 	- `defaultLocale`: The default locale for the message source.
   * 	- `defaultEncoding`: The default encoding for the message source.
   * 	- `cacheSeconds`: The number of seconds the message source will cache messages.
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
  /**
   * creates a Spring Template Engine instance and sets its template resolver and message
   * source to enable Thymeleaf template engine functionality.
   * 
   * @param emailMessageSource message source for email-related messages, which is used
   * by the `SpringTemplateEngine` to retrieve templates for email rendering.
   * 
   * 	- `ResourceBundleMessageSource`: This is an interface that provides message source
   * functionality for Thymeleaf. It allows for the retrieval of messages from a resource
   * bundle.
   * 	- `emailMessageSource`: This is an implementation of the `ResourceBundleMessageSource`
   * interface, providing email-specific message sources.
   * 
   * Therefore, the input `emailMessageSource` has properties such as the location of
   * the resource bundle and any additional parameters required for message source functionality.
   * 
   * @returns a Spring Template Engine instance configured with Thymeleaf templates and
   * an email message source.
   * 
   * 	- `SpringTemplateEngine`: The template engine is an instance of the Spring Template
   * Engine class, which provides the functionality for rendering Thymeleaf templates.
   * 	- `templateResolver`: The `templateResolver` attribute is a reference to a
   * `ThymeleafTemplateResolver` object, which manages the resolution of Thymeleaf
   * templates based on their names and locations.
   * 	- `emailMessageSource`: The `emailMessageSource` attribute is a reference to an
   * implementation of the `MessageSource` interface, which provides the message sources
   * for email-related messages.
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
  /**
   * creates a `ITemplateResolver` instance that resolves Thymeleaf templates based on
   * the properties provided. It sets the prefix, suffix, template mode, character
   * encoding, and caching behavior of the resolver.
   * 
   * @returns a `ITemplateResolver` instance with configured template properties.
   * 
   * 	- `ClassLoaderTemplateResolver`: This is the class that implements the
   * `ITemplateResolver` interface and provides the functionality for resolving Thymeleaf
   * templates.
   * 	- `setPrefix()`: This method sets the prefix of the template path. If the template
   * path ends with the file separator, the prefix will be the same as the template
   * path. Otherwise, it will be the template path concatenated with the file separator.
   * 	- `setSuffix()`: This method sets the suffix of the template path. It specifies
   * the format of the template.
   * 	- `setTemplateMode()`: This method sets the mode of the template. It can be one
   * of the following values: `HTML`, `XML`, or `TEXT`.
   * 	- `setCharacterEncoding()`: This method sets the character encoding of the template.
   * It determines how the template will be interpreted and rendered.
   * 	- `setCacheable()`: This method specifies whether the template is cacheable or
   * not. If it is set to `true`, the template will be cached, otherwise it will not
   * be cached.
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
