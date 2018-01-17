/*
 * Creation by madmath03 the 2017-12-30.
 */

package com.monogramm.starter.config.data;

import com.monogramm.starter.persistence.AbstractGenericEntity;
import com.monogramm.starter.persistence.EntityNotFoundException;
import com.monogramm.starter.persistence.GenericService;
import com.monogramm.starter.persistence.parameter.entity.Parameter;
import com.monogramm.starter.persistence.parameter.exception.ParameterNotFoundException;
import com.monogramm.starter.persistence.parameter.service.IParameterService;
import com.monogramm.starter.persistence.permission.entity.Permission;
import com.monogramm.starter.persistence.permission.exception.PermissionNotFoundException;
import com.monogramm.starter.persistence.permission.service.IPermissionService;
import com.monogramm.starter.persistence.role.entity.Role;
import com.monogramm.starter.persistence.role.exception.RoleNotFoundException;
import com.monogramm.starter.persistence.role.service.IRoleService;
import com.monogramm.starter.persistence.type.entity.Type;
import com.monogramm.starter.persistence.type.exception.TypeNotFoundException;
import com.monogramm.starter.persistence.type.service.ITypeService;
import com.monogramm.starter.persistence.user.entity.User;
import com.monogramm.starter.persistence.user.exception.UserNotFoundException;
import com.monogramm.starter.persistence.user.service.IUserService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

/**
 * An abstract initial Data Loader.
 * 
 * @author madmath03
 */
public abstract class AbstractDataLoader implements ApplicationListener<ContextRefreshedEvent> {

  /**
   * Logger for {@link AbstractDataLoader}.
   */
  private static final Logger LOG = LogManager.getLogger(AbstractDataLoader.class);


  private static final Object[] EMPTY_PARAMS = new Object[] {};

  private final Locale locale = Locale.getDefault();


  private boolean alreadySetup = false;


  private final Environment env;
  private final MessageSource messageSource;


  private final IParameterService parameterService;
  private final ITypeService typeService;
  private final IPermissionService permissionService;
  private final IRoleService roleService;
  private final IUserService userService;



  private final Collection<Parameter> parameters = new HashSet<>();
  private final Collection<Type> types = new HashSet<>();
  private final Collection<Permission> permissions = new HashSet<>();
  private final Collection<Role> roles = new HashSet<>();
  private final Collection<User> users = new HashSet<>();



  /**
   * Create a {@link AbstractDataLoader}.
   * 
   * @param env application environment properties.
   * @param messageSource the messages i8n source.
   * @param userService the user service.
   * @param roleService the role service.
   * @param permissionService the permission service.
   * @param typeService the type service.
   * @param parameterService the parameters service.
   */
  @Autowired
  public AbstractDataLoader(Environment env, MessageSource messageSource, IUserService userService,
      IRoleService roleService, IPermissionService permissionService, ITypeService typeService,
      IParameterService parameterService) {
    super();
    this.env = env;
    this.messageSource = messageSource;

    this.userService = userService;
    this.roleService = roleService;
    this.permissionService = permissionService;
    this.typeService = typeService;
    this.parameterService = parameterService;
  }

  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    if (alreadySetup) {
      if (LOG.isInfoEnabled()) {
        final String msg = messageSource
            .getMessage("config.data.initialization.already_initialized", EMPTY_PARAMS, locale);
        LOG.info(msg);
      }
      return;
    } else if (LOG.isInfoEnabled()) {
      final String msg =
          messageSource.getMessage("config.data.initialization.start", EMPTY_PARAMS, locale);
      LOG.info(msg);
    }

    this.alreadySetup = this.initData();

    if (LOG.isInfoEnabled()) {
      final String msg;
      if (this.alreadySetup) {
        msg = messageSource.getMessage("config.data.initialization.end", EMPTY_PARAMS, locale);
      } else {
        msg = messageSource.getMessage("config.data.initialization.failure", EMPTY_PARAMS, locale);
      }
      LOG.info(msg);
    }

  }

  /**
   * Initialize the data of the application.
   * 
   * @return {@code true} if the application data have been initialized.
   */
  protected boolean initData() {
    // Setup the initial data
    boolean initDone = this.initDefaultData();

    // Setup the demo data
    if (initDone && "true".equalsIgnoreCase(this.env.getProperty("monogramm.data.demo"))) {
      initDone &= this.initDemoData();
    }

    return initDone;
  }

  /**
   * Initialize the data of the application.
   * 
   * @return {@code true} if the application data have been initialized.
   */
  protected abstract boolean initDefaultData();

  /**
   * Initialize the demonstration data of the application.
   * 
   * @return {@code true} if the application data have been initialized.
   */
  protected abstract boolean initDemoData();

  /**
   * Get the {@link #alreadySetup}.
   * 
   * @return the {@link #alreadySetup}.
   */
  public final boolean isAlreadySetup() {
    return alreadySetup;
  }



  /**
   * Get the {@link #locale}.
   * 
   * @return the {@link #locale}.
   */
  protected Locale getLocale() {
    return locale;
  }



  /**
   * Get the {@link #env}.
   * 
   * @return the {@link #env}.
   */
  protected Environment getEnv() {
    return env;
  }

  /**
   * Get the {@link #messageSource}.
   * 
   * @return the {@link #messageSource}.
   */
  protected MessageSource getMessageSource() {
    return messageSource;
  }



  /**
   * Get the {@link #parameterService}.
   * 
   * @return the {@link #parameterService}.
   */
  public IParameterService getParameterService() {
    return parameterService;
  }

  /**
   * Get the {@link #typeService}.
   * 
   * @return the {@link #typeService}.
   */
  protected ITypeService getTypeService() {
    return typeService;
  }

  /**
   * Get the {@link #permissionService}.
   * 
   * @return the {@link #permissionService}.
   */
  protected IPermissionService getPermissionService() {
    return permissionService;
  }

  /**
   * Get the {@link #roleService}.
   * 
   * @return the {@link #roleService}.
   */
  protected IRoleService getRoleService() {
    return roleService;
  }

  /**
   * Get the {@link #userService}.
   * 
   * @return the {@link #userService}.
   */
  protected IUserService getUserService() {
    return userService;
  }



  /**
   * Get the {@link #parameters}.
   * 
   * @return the {@link #parameters}.
   */
  public Collection<Parameter> getParameters() {
    return Collections.unmodifiableCollection(parameters);
  }

  /**
   * Get the {@link #types}.
   * 
   * @return the {@link #types}.
   */
  public Collection<Type> getTypes() {
    return Collections.unmodifiableCollection(types);
  }

  /**
   * Get the {@link #permissions}.
   * 
   * @return the {@link #permissions}.
   */
  public Collection<Permission> getPermissions() {
    return Collections.unmodifiableCollection(permissions);
  }

  /**
   * Get the {@link #roles}.
   * 
   * @return the {@link #roles}.
   */
  public Collection<Role> getRoles() {
    return Collections.unmodifiableCollection(roles);
  }

  /**
   * Get the {@link #users}.
   * 
   * @return the {@link #users}.
   */
  public Collection<User> getUsers() {
    return Collections.unmodifiableCollection(users);
  }



  /**
   * Create the parameter with given details.
   * 
   * @param name the name.
   * @param value the value.
   * 
   * @return the parameter created.
   */
  protected Parameter createParameter(final String name, final Object value) {
    final Object[] logParam = new String[] {name};

    Parameter parameter;
    try {
      parameter = parameterService.findByName(name);
    } catch (ParameterNotFoundException e) {
      if (LOG.isDebugEnabled()) {
        final String msg = messageSource
            .getMessage("config.data.initialization.parameter_not_exists", logParam, locale);
        LOG.debug(msg);
      }
      parameter = null;
    }

    if (parameter == null) {
      parameter = Parameter.builder(name, value).build();

      parameterService.add(parameter);

      if (LOG.isDebugEnabled()) {
        final String msg = messageSource.getMessage("config.data.initialization.parameter_created",
            logParam, locale);
        LOG.debug(msg);
      }

    } else if (LOG.isDebugEnabled()) {
      final String msg = messageSource
          .getMessage("config.data.initialization.parameter_already_exists", logParam, locale);
      LOG.debug(msg);
    }

    // Keep track of the initial data in memory
    this.parameters.add(parameter);

    return parameter;
  }

  /**
   * Create the type with given details.
   * 
   * @param name the name.
   * 
   * @return the type created.
   */
  protected Type createType(final String name) {
    final Object[] logParam = new String[] {name};

    Type type;
    try {
      type = typeService.findByName(name);
    } catch (TypeNotFoundException e) {
      if (LOG.isDebugEnabled()) {
        final String msg = messageSource.getMessage("config.data.initialization.type_not_exists",
            logParam, locale);
        LOG.debug(msg);
      }
      type = null;
    }

    if (type == null) {
      type = Type.builder(name).build();

      typeService.add(type);

      if (LOG.isDebugEnabled()) {
        final String msg =
            messageSource.getMessage("config.data.initialization.type_created", logParam, locale);
        LOG.debug(msg);
      }

    } else if (LOG.isDebugEnabled()) {
      final String msg = messageSource.getMessage("config.data.initialization.type_already_exists",
          logParam, locale);
      LOG.debug(msg);
    }

    // Keep track of the initial data in memory
    this.types.add(type);

    return type;
  }

  /**
   * Create the role with given details.
   * 
   * @param name the name.
   * 
   * @return the role created.
   */
  protected Role createRole(final String name) {
    return this.createRole(name, Collections.emptyList());
  }

  /**
   * Create the role with given details.
   * 
   * @param name the name.
   * @param users a users collection to add to the role.
   * 
   * @return the role created.
   */
  protected Role createRole(final String name, final Collection<User> users) {
    final Object[] logParam = new String[] {name};

    Role role;
    try {
      role = roleService.findByName(name);
    } catch (RoleNotFoundException e) {
      if (LOG.isDebugEnabled()) {
        final String msg = messageSource.getMessage("config.data.initialization.role_not_exists",
            logParam, locale);
        LOG.debug(msg);
      }
      role = null;
    }

    if (role == null) {
      role = Role.builder(name).build();
      role.addUsers(users);

      roleService.add(role);

      if (LOG.isDebugEnabled()) {
        final String msg =
            messageSource.getMessage("config.data.initialization.role_created", logParam, locale);
        LOG.debug(msg);
      }

    } else if (LOG.isDebugEnabled()) {
      final String msg = messageSource.getMessage("config.data.initialization.role_already_exists",
          logParam, locale);
      LOG.debug(msg);
    }

    // Keep track of the initial data in memory
    this.roles.add(role);

    return role;
  }

  /**
   * Create the permission with given details.
   * 
   * @param name the name.
   * @param roles the roles to associate to the new {@link Permission}.
   * 
   * @return the permission created.
   */
  protected Permission createPermission(final String name, final Collection<Role> roles) {
    final Object[] logParam = new String[] {name};

    Permission permission;
    try {
      permission = permissionService.findByName(name);
    } catch (PermissionNotFoundException e) {
      if (LOG.isDebugEnabled()) {
        final String msg = messageSource
            .getMessage("config.data.initialization.permission_not_exists", logParam, locale);
        LOG.debug(msg);
      }
      permission = null;
    }

    if (permission == null) {
      permission = Permission.builder(name).build();

      permission.addRoles(roles);

      permissionService.add(permission);

      if (LOG.isDebugEnabled()) {
        final String msg = messageSource.getMessage("config.data.initialization.permission_created",
            logParam, locale);
        LOG.debug(msg);
      }

    } else if (LOG.isDebugEnabled()) {
      final String msg = messageSource
          .getMessage("config.data.initialization.permission_already_exists", logParam, locale);
      LOG.debug(msg);
    }

    // Keep track of the initial data in memory
    this.permissions.add(permission);

    return permission;
  }

  /**
   * Create the user with given details.
   * 
   * @param username the username.
   * @param email the email.
   * @param password the password.
   * @param userRole the user's role.
   * 
   * @return the user created.
   */
  protected User createUser(final String username, final String email, final char[] password,
      final Role userRole) {
    final Object[] logParam = new String[] {username, email};

    User user;
    try {
      user = userService.findByUsernameOrEmail(username, email);
    } catch (UserNotFoundException e) {
      if (LOG.isDebugEnabled()) {
        final String msg = messageSource.getMessage("config.data.initialization.user_not_exists",
            logParam, locale);
        LOG.debug(msg);
      }
      user = null;
    }

    if (user == null) {
      LOG.warn(" ");
      LOG.warn("User name: " + username);
      LOG.warn("User email: " + email);
      LOG.warn("User password: " + Arrays.toString(password));
      LOG.warn(" ");
      user = User.builder(username, email).password(password).role(userRole).build();

      userService.add(user);

      if (LOG.isDebugEnabled()) {
        final String msg =
            messageSource.getMessage("config.data.initialization.user_created", logParam, locale);
        LOG.debug(msg);
      }

      this.enableUser(user, logParam);

      this.verifyUser(user, logParam);

    } else if (LOG.isDebugEnabled()) {
      final String msg = messageSource.getMessage("config.data.initialization.user_already_exists",
          logParam, locale);
      LOG.debug(msg);
    }

    // Keep track of the initial data in memory
    this.users.add(user);

    return user;
  }

  protected void enableUser(final User user, final Object[] logParam) {
    try {
      userService.setEnabled(user.getId(), true);

      if (LOG.isDebugEnabled()) {
        final String msg =
            messageSource.getMessage("config.data.initialization.user_activated", logParam, locale);
        LOG.debug(msg);
      }

    } catch (UserNotFoundException e) {
      if (LOG.isErrorEnabled()) {
        final String msg = messageSource
            .getMessage("config.data.initialization.user_activation_error", logParam, locale);
        LOG.error(msg);
      }
    }
  }

  protected void verifyUser(final User user, final Object[] logParam) {
    try {
      userService.setVerified(user.getId(), true);

      if (LOG.isDebugEnabled()) {
        final String msg =
            messageSource.getMessage("config.data.initialization.user_verified", logParam, locale);
        LOG.debug(msg);
      }

    } catch (UserNotFoundException e) {
      if (LOG.isErrorEnabled()) {
        final String msg = messageSource
            .getMessage("config.data.initialization.user_verification_error", logParam, locale);
        LOG.error(msg);
      }
    }
  }

  /**
   * Update an entity's owner.
   * 
   * @param <T> entity class type.
   * 
   * @param entity entity to update.
   * @param owner the new owner of the entity/
   * @param entityService the entity service to use for update
   * 
   * @return {@code true} if the entity has been updated, {@code false} otherwise.
   */
  protected <T extends AbstractGenericEntity> boolean updateOwner(final T entity, final User owner,
      final GenericService<T, ?> entityService) {
    final Object[] logParam = new Object[] {entity};

    boolean updated = false;

    if (entity.getOwner() == null) {
      try {
        entity.setOwner(owner);
        final T updatedEntity = entityService.update(entity);

        updated = updatedEntity != null;
      } catch (EntityNotFoundException e) {
        if (LOG.isErrorEnabled()) {
          final String msg = messageSource
              .getMessage("config.data.initialization.entity_ownership_error", logParam, locale);
          LOG.error(msg);
        }
      }
    }

    return updated;
  }

  /**
   * Update a collection of entities' owner.
   * 
   * @param <T> entity class type.
   * 
   * @param entities entities to update.
   * @param owner the new owner of the entity/
   * @param entityService the entity service to use for update
   * 
   * @return {@code true} if the entity has been updated, {@code false} otherwise.
   */
  protected <T extends AbstractGenericEntity> boolean updateOwner(final Collection<T> entities,
      final User owner, final GenericService<T, ?> entityService) {
    boolean updated = false;

    for (final T entity : entities) {
      updated |= this.updateOwner(entity, owner, entityService);
    }

    return updated;
  }

}
