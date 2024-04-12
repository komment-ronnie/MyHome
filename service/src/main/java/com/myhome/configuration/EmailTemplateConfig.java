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
 * is a configuration class for email templates in a Spring Boot application. It
 * provides methods for configuring a ResourceBundleMessageSource for email messages
 * and a Spring Template Engine with Thymeleaf-specific settings and an email message
 * source. The class also defines an ITemplateResolver instance with customized
 * configuration settings for Thymeleaf template resolution.
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
   * creates a `ResourceBundleMessageSource` instance for email localization, setting
   * the basename, default locale, default encoding, and cache seconds according to the
   * `localizationProperties`.
   * 
   * @returns a ResourceBundleMessageSource instance configured for email localization.
   * 
   * 	- `ResourceBundleMessageSource`: This is the class that is being returned, which
   * implements the `MessageSource` interface and provides access to message keys in a
   * resource bundle.
   * 	- `setBasename()`: This method sets the basename of the resource bundle file,
   * which is the name of the file without the file extension. In this case, it is set
   * to `localizationProperties.getPath()`.
   * 	- `setDefaultLocale()`: This method sets the default locale for the message source,
   * which determines the language and regional settings that are used when looking up
   * messages in the resource bundle. It is set to `Locale.ENGLISH` in this case.
   * 	- `setDefaultEncoding()`: This method sets the default encoding for the message
   * source, which determines the character set used when reading or writing the resource
   * bundle file. It is set to `localizationProperties.getEncoding()` in this case.
   * 	- `setCacheSeconds()`: This method sets the number of seconds that the message
   * source will cache messages in memory before checking the resource bundle file
   * again. It is set to `localizationProperties.getCacheSeconds()` in this case.
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
   * creates a Spring Template Engine instance, sets its template resolver and message
   * source, and returns the engine.
   * 
   * @param emailMessageSource message source for email-related templates, providing a
   * way to localize and manage email-related messages within the Thymeleaf template engine.
   * 
   * 	- `ResourceBundleMessageSource`: This is the message source interface that provides
   * the messages for the template engine. It can be used to retrieve messages in
   * different languages and cultures.
   * 
   * @returns a Spring Template Engine instance configured with Thymeleaf template
   * resolver and email message source.
   * 
   * 	- `SpringTemplateEngine`: This is the base class that provides the functionality
   * for rendering Thymeleaf templates using the Spring framework.
   * 	- `templateResolver()`: This is an instance of `ThymeleafTemplateResolver`, which
   * is responsible for resolving the Thymeleaf templates to be rendered.
   * 	- `emailMessageSource()`: This is an instance of `ResourceBundleMessageSource`,
   * which provides the email messages that are used in the template rendering process.
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
   * creates a `ITemplateResolver` instance for Thymeleaf templates, setting prefix and
   * suffix based on property values, mode, encoding, and cacheability.
   * 
   * @returns a `ITemplateResolver` instance configured to resolve Thymeleaf templates
   * based on their location and properties.
   * 
   * 	- `ClassLoaderTemplateResolver`: This is the class that implements the
   * `TemplateResolver` interface and provides functionality for resolving Thymeleaf templates.
   * 	- `prefix`: The prefix of the template path, which is set to the full path of the
   * template if it ends with the file separator character (`file.separator`), or the
   * concatenation of the template path and file separator otherwise.
   * 	- `suffix`: The suffix of the template path, which is set to the format of the template.
   * 	- `templateMode`: The mode of the template, which can be either `HTML`, `XML`,
   * or `TEXT`.
   * 	- `characterEncoding`: The character encoding of the template, which can be set
   * to a specific encoding or `UTF-8` by default.
   * 	- `cacheable`: A boolean indicating whether the template is cacheable or not.
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
