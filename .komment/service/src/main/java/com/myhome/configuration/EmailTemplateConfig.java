{"name":"EmailTemplateConfig.java","path":"service/src/main/java/com/myhome/configuration/EmailTemplateConfig.java","content":{"structured":{"description":"An Email Template Config class that sets up email template handling for an application using Thymeleaf templates. It provides a template engine and message source for dynamic email content generation, and configures caching, encoding, and other settings. The class also defines the path to the email templates and their format.","items":[{"id":"5c1c011a-13c2-a196-a747-246444dc7013","ancestors":[],"type":"function","description":"is a configuration class for email templates in a Spring Boot application. It provides methods for configuring a ResourceBundleMessageSource for email messages and a Spring Template Engine with Thymeleaf-specific settings and an email message source. The class also defines an ITemplateResolver instance with customized configuration settings for Thymeleaf template resolution.","name":"EmailTemplateConfig","code":"@Configuration\n@RequiredArgsConstructor\npublic class EmailTemplateConfig {\n\n  private final EmailTemplateProperties templateProperties;\n  private final EmailTemplateLocalizationProperties localizationProperties;\n\n  /**\n   * creates a `ResourceBundleMessageSource` instance with configuration options set\n   * from the `localizationProperties` object, and returns it.\n   * \n   * @returns a `ResourceBundleMessageSource` instance configured to handle email localization.\n   * \n   * \t- `ResourceBundleMessageSource`: This is the class that represents the message\n   * source for email localization.\n   * \t- `setBasename(localizationProperties.getPath())`: This sets the base name of the\n   * resource bundle file.\n   * \t- `setDefaultLocale(Locale.ENGLISH)`: Sets the default locale for the message source.\n   * \t- `setDefaultEncoding(localizationProperties.getEncoding())`: Sets the default\n   * encoding for the message source.\n   * \t- `setCacheSeconds(localizationProperties.getCacheSeconds())`: Sets the cache\n   * seconds for the message source.\n   */\n  @Bean\n  public ResourceBundleMessageSource emailMessageSource() {\n    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();\n    messageSource.setBasename(localizationProperties.getPath());\n    messageSource.setDefaultLocale(Locale.ENGLISH);\n    messageSource.setDefaultEncoding(localizationProperties.getEncoding());\n    messageSource.setCacheSeconds(localizationProperties.getCacheSeconds());\n    return messageSource;\n  }\n\n  /**\n   * creates a Spring Template Engine instance and configures it with a Thymeleaf\n   * Template Resolver and an Email Message Source for rendering emails.\n   * \n   * @param emailMessageSource message source for email-related messages used by the SpringTemplateEngine.\n   * \n   * \t- `ResourceBundleMessageSource`: This interface represents a message source that\n   * retrieves message keys from a resource bundle. It provides access to messages in\n   * various formats, including HTML, XML, and YAML.\n   * \n   * @returns a Spring Template Engine instance with Thymeleaf-specific settings and a\n   * message source for email messages.\n   * \n   * \t- The SpringTemplateEngine is initialized with a new instance.\n   * \t- A template resolver is set to `thymeleafTemplateResolver()`.\n   * \t- A message source for emails is set to `emailMessageSource`.\n   */\n  @Bean\n  public SpringTemplateEngine thymeleafTemplateEngine(ResourceBundleMessageSource emailMessageSource) {\n    SpringTemplateEngine templateEngine = new SpringTemplateEngine();\n    templateEngine.setTemplateResolver(thymeleafTemplateResolver());\n    templateEngine.setTemplateEngineMessageSource(emailMessageSource);\n    return templateEngine;\n  }\n\n  /**\n   * creates a `ITemplateResolver` instance that sets the prefix, suffix, mode, encoding\n   * and caching properties for Thymeleaf template resolution.\n   * \n   * @returns a `ITemplateResolver` instance with customized configuration settings for\n   * Thymeleaf template resolution.\n   * \n   * \t- `templateProperties`: This is an instance of `ITemplateProperties`, which\n   * contains information about the Thymeleaf template, such as its path, format, mode,\n   * and encoding.\n   * \t- `prefix`: This is a string that represents the prefix of the template path. If\n   * the template path ends with the file separator, this property is set to the template\n   * path itself. Otherwise, it is set to the template path concatenated with the file\n   * separator.\n   * \t- `suffix`: This is a string that represents the suffix of the template path. It\n   * contains the format of the template.\n   * \t- `templateMode`: This is an integer that represents the mode of the template.\n   * It can be one of the following values: `ITemplateMode.NONE`, `ITemplateMode.HTML`,\n   * or `ITemplateMode.XML`.\n   * \t- `characterEncoding`: This is a string that represents the character encoding\n   * of the template. It can be any of the supported encodings, such as `UTF-8`,\n   * `ISO-8859-1`, etc.\n   * \t- `cacheable`: This is a boolean value that indicates whether the template should\n   * be cached or not. If set to `true`, the template will be cached; otherwise, it\n   * will not be cached.\n   */\n  private ITemplateResolver thymeleafTemplateResolver() {\n    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();\n\n    String templatePath = templateProperties.getPath();\n    String fileSeparator = System.getProperty(\"file.separator\");\n    templateResolver.setPrefix(templatePath.endsWith(fileSeparator) ? templatePath : templatePath + fileSeparator);\n\n    templateResolver.setSuffix(templateProperties.getFormat());\n    templateResolver.setTemplateMode(templateProperties.getMode());\n    templateResolver.setCharacterEncoding(templateProperties.getEncoding());\n    templateResolver.setCacheable(templateProperties.isCache());\n    return templateResolver;\n  }\n\n}","location":{"start":21,"insert":21,"offset":" ","indent":0,"comment":{"start":14,"end":20}},"item_type":"class","length":99},{"id":"313a72f7-b234-3cb5-b54f-6d05c4ed3a9e","ancestors":["5c1c011a-13c2-a196-a747-246444dc7013"],"type":"function","description":"creates a `ResourceBundleMessageSource` instance for email localization, setting the basename, default locale, default encoding, and cache seconds according to the `localizationProperties`.","params":[],"returns":{"type_name":"instance","description":"a ResourceBundleMessageSource instance configured for email localization.\n\n* `ResourceBundleMessageSource`: This is the class that is being returned, which implements the `MessageSource` interface and provides access to message keys in a resource bundle.\n* `setBasename()`: This method sets the basename of the resource bundle file, which is the name of the file without the file extension. In this case, it is set to `localizationProperties.getPath()`.\n* `setDefaultLocale()`: This method sets the default locale for the message source, which determines the language and regional settings that are used when looking up messages in the resource bundle. It is set to `Locale.ENGLISH` in this case.\n* `setDefaultEncoding()`: This method sets the default encoding for the message source, which determines the character set used when reading or writing the resource bundle file. It is set to `localizationProperties.getEncoding()` in this case.\n* `setCacheSeconds()`: This method sets the number of seconds that the message source will cache messages in memory before checking the resource bundle file again. It is set to `localizationProperties.getCacheSeconds()` in this case.","complex_type":true},"usage":{"language":"java","code":"@Bean\n  public ResourceBundleMessageSource emailMessageSource() {\n    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();\n    messageSource.setBasename(localizationProperties.getPath());\n    messageSource.setDefaultLocale(Locale.ENGLISH);\n    messageSource.setDefaultEncoding(localizationProperties.getEncoding());\n    messageSource.setCacheSeconds(localizationProperties.getCacheSeconds());\n    return messageSource;\n  }\n","description":""},"name":"emailMessageSource","code":"@Bean\n  public ResourceBundleMessageSource emailMessageSource() {\n    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();\n    messageSource.setBasename(localizationProperties.getPath());\n    messageSource.setDefaultLocale(Locale.ENGLISH);\n    messageSource.setDefaultEncoding(localizationProperties.getEncoding());\n    messageSource.setCacheSeconds(localizationProperties.getCacheSeconds());\n    return messageSource;\n  }","location":{"start":44,"insert":44,"offset":" ","indent":2,"comment":{"start":27,"end":43}},"item_type":"method","length":9},{"id":"223a5749-a0bf-d7aa-ef44-8515ed1d9a46","ancestors":["5c1c011a-13c2-a196-a747-246444dc7013"],"type":"function","description":"creates a Spring Template Engine instance, sets its template resolver and message source, and returns the engine.","params":[{"name":"emailMessageSource","type_name":"ResourceBundleMessageSource","description":"message source for email-related templates, providing a way to localize and manage email-related messages within the Thymeleaf template engine.\n\n* `ResourceBundleMessageSource`: This is the message source interface that provides the messages for the template engine. It can be used to retrieve messages in different languages and cultures.","complex_type":true}],"returns":{"type_name":"SpringTemplateEngine","description":"a Spring Template Engine instance configured with Thymeleaf template resolver and email message source.\n\n* `SpringTemplateEngine`: This is the base class that provides the functionality for rendering Thymeleaf templates using the Spring framework.\n* `templateResolver()`: This is an instance of `ThymeleafTemplateResolver`, which is responsible for resolving the Thymeleaf templates to be rendered.\n* `emailMessageSource()`: This is an instance of `ResourceBundleMessageSource`, which provides the email messages that are used in the template rendering process.","complex_type":true},"usage":{"language":"java","code":"@Bean\n  public SpringTemplateEngine thymeleafTemplateEngine(ResourceBundleMessageSource emailMessageSource) {\n    SpringTemplateEngine templateEngine = new SpringTemplateEngine();\n    templateEngine.setTemplateResolver(thymeleafTemplateResolver());\n    templateEngine.setTemplateEngineMessageSource(emailMessageSource);\n    return templateEngine;\n  }\n","description":"\nThe code creates a bean for the emailMessageSource object, which is used to create an instance of the SpringTemplateEngine class. The thymeleafTemplateResolver() method is also called, which creates an instance of the ClassLoaderTemplateResolver class and sets various properties. Finally, the template engine's message source is set to the created instance of ResourceBundleMessageSource using setTemplateEngineMessageSource(), resulting in a fully configured SpringTemplateEngine object that can be used for email-related templates."},"name":"thymeleafTemplateEngine","code":"@Bean\n  public SpringTemplateEngine thymeleafTemplateEngine(ResourceBundleMessageSource emailMessageSource) {\n    SpringTemplateEngine templateEngine = new SpringTemplateEngine();\n    templateEngine.setTemplateResolver(thymeleafTemplateResolver());\n    templateEngine.setTemplateEngineMessageSource(emailMessageSource);\n    return templateEngine;\n  }","location":{"start":71,"insert":71,"offset":" ","indent":2,"comment":{"start":53,"end":70}},"item_type":"method","length":7},{"id":"d09a32fc-8f6a-76a5-9a48-7b217f4a389f","ancestors":["5c1c011a-13c2-a196-a747-246444dc7013"],"type":"function","description":"creates a `ITemplateResolver` instance for Thymeleaf templates, setting prefix and suffix based on property values, mode, encoding, and cacheability.","params":[],"returns":{"type_name":"ITemplateResolver","description":"a `ITemplateResolver` instance configured to resolve Thymeleaf templates based on their location and properties.\n\n* `ClassLoaderTemplateResolver`: This is the class that implements the `TemplateResolver` interface and provides functionality for resolving Thymeleaf templates.\n* `prefix`: The prefix of the template path, which is set to the full path of the template if it ends with the file separator character (`file.separator`), or the concatenation of the template path and file separator otherwise.\n* `suffix`: The suffix of the template path, which is set to the format of the template.\n* `templateMode`: The mode of the template, which can be either `HTML`, `XML`, or `TEXT`.\n* `characterEncoding`: The character encoding of the template, which can be set to a specific encoding or `UTF-8` by default.\n* `cacheable`: A boolean indicating whether the template is cacheable or not.","complex_type":true},"usage":{"language":"java","code":"// ClassName is the class name for the template resolver\nITemplateResolver templateResolver = new ClassLoaderTemplateResolver();\n\n// Set the prefix and suffix for the template resolver\ntemplateResolver.setPrefix(templateProperties.getPath());\ntemplateResolver.setSuffix(templateProperties.getFormat());\n\n// Set the template mode for the template resolver\ntemplateResolver.setTemplateMode(templateProperties.getMode());\n\n// Set the character encoding for the template resolver\ntemplateResolver.setCharacterEncoding(templateProperties.getEncoding());\n\n// Set whether the template is cacheable or not\ntemplateResolver.setCacheable(templateProperties.isCache());\n","description":""},"name":"thymeleafTemplateResolver","code":"private ITemplateResolver thymeleafTemplateResolver() {\n    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();\n\n    String templatePath = templateProperties.getPath();\n    String fileSeparator = System.getProperty(\"file.separator\");\n    templateResolver.setPrefix(templatePath.endsWith(fileSeparator) ? templatePath : templatePath + fileSeparator);\n\n    templateResolver.setSuffix(templateProperties.getFormat());\n    templateResolver.setTemplateMode(templateProperties.getMode());\n    templateResolver.setCharacterEncoding(templateProperties.getEncoding());\n    templateResolver.setCacheable(templateProperties.isCache());\n    return templateResolver;\n  }","location":{"start":105,"insert":105,"offset":" ","indent":2,"comment":{"start":78,"end":104}},"item_type":"method","length":13}]}}}