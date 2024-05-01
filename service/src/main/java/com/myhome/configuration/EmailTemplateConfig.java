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
 * defines a bean for email template configuration, including Thymeleaf template
 * resolver and an email message source for rendering emails. The SpringTemplateEngine
 * class is also defined, which sets up a new instance of the engine with customized
 * settings for Thymeleaf template resolution using the `thymeleafTemplateResolver()`
 * method.
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
   * creates a `ResourceBundleMessageSource` instance to handle email localization
   * messages. It sets the basename, default locale, encoding, and cache seconds based
   * on configuration properties.
   * 
   * @returns a `ResourceBundleMessageSource` instance configured to retrieve email
   * messages from a localization file path.
   * 
   * 	- `ResourceBundleMessageSource`: This is the class that is being returned, which
   * provides a way to retrieve message keys and their associated messages in a specific
   * locale.
   * 	- `setBasename()`: This method sets the basename of the resource bundle file.
   * 	- `setDefaultLocale()`: This method sets the default locale for the message source.
   * 	- `setDefaultEncoding()`: This method sets the default encoding for the message
   * source.
   * 	- `setCacheSeconds()`: This method sets the cache seconds for the message source.
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
   * creates a Spring Template Engine instance and configures it with Thymeleaf-specific
   * settings, including a Template Resolver and a Message Source for email messages.
   * 
   * @param emailMessageSource message source for email-related messages in the Spring
   * Template Engine.
   * 
   * 	- `ResourceBundleMessageSource`: This is an interface that represents a message
   * source for Thymeleaf templates. It provides a way to retrieve messages from a
   * resource bundle.
   * 	- `emailMessageSource`: This is the specific implementation of the
   * `ResourceBundleMessageSource` interface, providing messages related to emails.
   * 	- `thymeleafTemplateResolver()`: This is a method that returns a `ThymeleafTemplateResolver`,
   * which is responsible for resolving Thymeleaf templates at runtime.
   * 	- `SpringTemplateEngine`: This is the base class for Spring's template engines,
   * providing common functionality for handling templates and template resolution.
   * 
   * @returns a Spring Template Engine instance configured to use Thymeleaf as the
   * template engine and email message source.
   * 
   * 	- `SpringTemplateEngine`: This is an instance of the `SpringTemplateEngine` class,
   * which provides a Java-based template engine for rendering Thymeleaf templates.
   * 	- `templateResolver()`: This is an instance of the `ThymeleafTemplateResolver`
   * class, which resolves Thymeleaf templates to Java classes.
   * 	- `emailMessageSource`: This is an instance of the `ResourceBundleMessageSource`
   * class, which provides a way to retrieve messages from a resource bundle.
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
   * creates a `ITemplateResolver` instance to resolve Thymeleaf templates based on
   * properties provided by the user.
   * 
   * @returns a `ITemplateResolver` instance that provides Thymeleaf template resolution
   * capabilities.
   * 
   * 	- `ClassLoaderTemplateResolver`: This is the class that implements the
   * `ITemplateResolver` interface and provides the functionality for resolving Thymeleaf
   * templates.
   * 	- `setPrefix`: The value of this property is the path to the template file, which
   * is appended with the file separator character if necessary.
   * 	- `setSuffix`: The value of this property is the suffix of the template file,
   * which determines the format of the template.
   * 	- `setTemplateMode`: The value of this property is the mode in which the template
   * should be processed.
   * 	- `setCharacterEncoding`: The value of this property is the encoding of the
   * template, which determines how the template should be interpreted.
   * 	- `setCacheable`: The value of this property indicates whether the template should
   * be cached or not.
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
