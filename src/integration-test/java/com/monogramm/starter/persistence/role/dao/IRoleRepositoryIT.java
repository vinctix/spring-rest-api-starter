/*
 * Creation by madmath03 the 2017-09-11.
 */

package com.monogramm.starter.persistence.role.dao;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import com.monogramm.starter.config.data.InitialDataLoader;
import com.monogramm.starter.persistence.AbstractGenericRepositoryIT;
import com.monogramm.starter.persistence.role.entity.Role;
import com.monogramm.starter.persistence.role.exception.RoleNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * {@link IRoleRepository} Integration Test.
 * 
 * @author madmath03
 */
public class IRoleRepositoryIT extends AbstractGenericRepositoryIT<Role, IRoleRepository> {

  private static final String DISPLAYNAME = "Foo";

  @Autowired
  private InitialDataLoader initialDataLoader;

  @Override
  protected Role buildTestEntity() {
    return Role.builder(DISPLAYNAME).build();
  }

  /**
   * Test method for {@link IRoleRepository#findAll()}.
   */
  @Override
  @Test
  public void testFindAll() {
    int expectedSize = 0;
    // ...plus the permissions created at application initialization
    if (initialDataLoader.getRoles() != null) {
      expectedSize += initialDataLoader.getRoles().size();
    }

    final List<Role> actual = getRepository().findAll();

    assertNotNull(actual);
    assertEquals(expectedSize, actual.size());
  }

  /**
   * Test method for
   * {@link IRoleRepository#findAllContainingNameIgnoreCase(java.lang.String)}.
   */
  @Test
  public void testFindAllContainingNameIgnoreCase() {
    final List<Role> models = new ArrayList<>();

    final List<Role> actual = getRepository().findAllContainingNameIgnoreCase(DISPLAYNAME);

    assertThat(actual, is(models));
  }

  /**
   * Test method for {@link IRoleRepository#findByNameIgnoreCase(String)}.
   * 
   * @throws RoleNotFoundException if the role is not found.
   */
  @Test
  public void testFindByNameIgnoreCase() {
    final Role model = this.buildTestEntity();
    model.setName(model.getName().toUpperCase());
    getRepository().add(model);

    final Role actual = getRepository().findByNameIgnoreCase(DISPLAYNAME);

    assertThat(actual, is(model));
  }

  /**
   * Test method for {@link IRoleRepository#findByNameIgnoreCase(java.lang.String)}.
   * 
   * @throws RoleNotFoundException if the role is not found.
   */
  @Test
  public void testFindByNameIgnoreCaseNoResult() {
    assertNull(getRepository().findByNameIgnoreCase(DISPLAYNAME));
  }

  /**
   * Test method for {@link IRoleRepository#findByNameIgnoreCase(java.lang.String)}.
   * 
   * @throws RoleNotFoundException if the role is not found.
   */
  @Test
  public void testFindByNameIgnoreCaseNonUnique() {
    getRepository().add(Role.builder(DISPLAYNAME + "1").build());
    getRepository().add(Role.builder(DISPLAYNAME + "2").build());

    assertNull(getRepository().findByNameIgnoreCase(DISPLAYNAME));
  }

  /**
   * Test method for {@link IRoleRepository#findByNameIgnoreCase(java.lang.String)}.
   * 
   * @throws RoleNotFoundException if the role is not found.
   */
  @Test
  public void testFindByNameIgnoreCaseNotFound() {
    assertNull(getRepository().findByNameIgnoreCase(null));
  }

  /**
   * Test method for {@link IRoleRepository#exists(java.util.UUID, java.lang.String)}.
   */
  @Test
  public void testExistsUUIDString() {
    final boolean expected = true;
    final Role model = this.buildTestEntity();
    final List<Role> models = new ArrayList<>(1);
    models.add(model);
    getRepository().save(models);

    final boolean actual = getRepository().exists(model.getId(), model.getName());

    assertThat(actual, is(expected));
  }

  /**
   * Test method for {@link IRoleRepository#exists(java.util.UUID, java.lang.String)}.
   */
  @Test
  public void testExistsUUIDStringNotFound() {
    final boolean expected = false;

    final boolean actual = getRepository().exists(RANDOM_ID, DISPLAYNAME);

    assertThat(actual, is(expected));
  }

}
