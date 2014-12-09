package org.appfuse.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.appfuse.Constants;
import org.appfuse.model.Address;
import org.appfuse.model.Role;
import org.appfuse.model.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectRetrievalFailureException;

public class UserDaoTest extends BaseDaoTestCase {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserDao dao;
    @Autowired
    private RoleDao rdao;

    @Test(expected=ObjectRetrievalFailureException.class)
    public void testGetUserInvalid() throws Exception {
        dao.get(1000L);
    }

    @Test
    public void testGetUser() throws Exception {
        User user = dao.get(-1L);

        assertNotNull(user);
        assertEquals(1, user.getRoles().size());
        assertTrue(user.isEnabled());
    }

    @Test
    public void testGetUserPassword() throws Exception {
        User user = dao.get(-1L);
        String password = dao.getUserPassword(user.getId());
        assertNotNull(password);
        log.debug("password: " + password);
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = dao.get(-1L);

        Address address = user.getAddress();
        address.setAddress("new address");

        dao.save(user);

        user = dao.get(-1L);
        assertEquals(user.getAddress(), address);
        assertEquals("new address", user.getAddress().getAddress());

        //verify that violation occurs when adding new user with same username
        user.setId(null);

        // should throw DataIntegrityViolationException
        dao.save(user);
    }

    @Test
    public void testAddUserRole() throws Exception {
        User user = dao.get(-1L);
        assertEquals(1, user.getRoles().size());

        Role role = rdao.getRoleByName(Constants.ADMIN_ROLE);
        user.addRole(role);
        dao.save(user);

        assertEquals(2, user.getRoles().size());

        //add the same role twice - should result in no additional role
        user.addRole(role);
        dao.save(user);

        assertEquals("more than 2 roles", 2, user.getRoles().size());

        user.getRoles().remove(role);
        dao.save(user);

        assertEquals(1, user.getRoles().size());
    }

    @Test(expected=ObjectRetrievalFailureException.class)
    public void testAddAndRemoveUser() throws Exception {
        User user = new User("testuser");
        user.setPassword("testpass");
        user.setFirstName("Test");
        user.setLastName("Last");
        Address address = new Address();
        address.setCity("Denver");
        address.setProvince("CO");
        address.setCountry("USA");
        address.setPostalCode("80210");
        user.setAddress(address);
        user.setEmail("testuser@appfuse.org");
        user.setWebsite("http://raibledesigns.com");

        Role role = rdao.getRoleByName(Constants.USER_ROLE);
        assertNotNull(role.getId());
        user.addRole(role);

        user = dao.save(user);

        assertNotNull(user.getId());
        assertEquals("testpass", user.getPassword());

        dao.remove(user.getId());

        // should throw EntityNotFoundException
        dao.get(user.getId());
    }

    @Test
    public void testUserExists() throws Exception {
        boolean b = dao.exists(-1L);
        assertTrue(b);
    }

    @Test
    public void testUserNotExists() throws Exception {
        boolean b = dao.exists(111L);
        assertFalse(b);
    }


    @Test
    public void testSearch() throws Exception {
        // reindexAll all the data
        dao.reindex();

        List<User> found = dao.search("*");
        assertEquals(3, found.size());

        found = dao.search("Tomcat");
        assertEquals(1, found.size());
    }

    @Test
    public void testUserPosition() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	Method getPosition = User.class.getMethod("getPosition");

    	User user1 = dao.get(-1L);
    	User user2 = dao.get(-2L);
    	User user3 = dao.get(-3L);

    	assertEquals("Administrator", getPosition.invoke(user1));
    	assertEquals("Manager", getPosition.invoke(user2));
    	assertEquals("DevOps", getPosition.invoke(user3));
    }

    @Test
    public void testUpdateUserPosition() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	Method getPosition = User.class.getMethod("getPosition");
    	Method setPosition = User.class.getMethod("setPosition", String.class);
    	User user1 = dao.get(-1L);

    	setPosition.invoke(user1, "test position");

    	dao.save(user1);
    	assertEquals("test position", getPosition.invoke(user1));
    }
}
