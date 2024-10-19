package papyrus.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import papyrus.dao.objavaRepository;
import papyrus.dao.uporabnikRepository;
import papyrus.dto.LoginRequest;
import papyrus.models.Objava;
import papyrus.models.Uporabnik;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class uporabnikControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private uporabnikRepository uporabnikRepository;

    @Autowired
    private objavaRepository objavaRepository;

    @BeforeEach
    void setUp() {
        uporabnikRepository.deleteAll();
        objavaRepository.deleteAll();
    }

    @Test
    void testDodajUporabnika() {
        Uporabnik newUser = new Uporabnik();
        newUser.setName("Janez");
        newUser.setEmail("janez@example.com");
        newUser.setPassword("password123");

        ResponseEntity<Uporabnik> response = restTemplate.postForEntity("/uporabnik", newUser, Uporabnik.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Janez", response.getBody().getName());
    }

    @Test
    void testVrniUporabnike() {
        Uporabnik user1 = new Uporabnik();
        user1.setName("Janez");
        user1.setEmail("janez@example.com");
        user1.setPassword("password123");
        uporabnikRepository.save(user1);

        Uporabnik user2 = new Uporabnik();
        user2.setName("Marko");
        user2.setEmail("marko@example.com");
        user2.setPassword("password456");
        uporabnikRepository.save(user2);

        ResponseEntity<List> response = restTemplate.getForEntity("/uporabnik", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testVrniUporabnikeIme() {
        Uporabnik user = new Uporabnik();
        user.setName("Janez");
        user.setEmail("janez@example.com");
        user.setPassword("password123");
        uporabnikRepository.save(user);

        // Use ParameterizedTypeReference to specify the expected response type
        ResponseEntity<List<Uporabnik>> response = restTemplate.exchange(
                "/uporabnik/ime/Janez",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Uporabnik>>() {}
        );

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Janez", response.getBody().get(0).getName());
    }

    @Test
    void testDeleteUporabnik() {
        Uporabnik user = new Uporabnik();
        user.setName("Janez");
        user.setEmail("janez@example.com");
        user.setPassword("password123");
        uporabnikRepository.save(user);

        restTemplate.delete("/uporabnik/" + user.getId());

        ResponseEntity<Uporabnik> response = restTemplate.getForEntity("/uporabnik/ime" + user.getName(), Uporabnik.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testLoginUser() {
        Uporabnik user = new Uporabnik();
        user.setName("Janez");
        user.setEmail("janez@example.com");
        user.setPassword("password123");
        uporabnikRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("janez@example.com", "password123");

        ResponseEntity<Uporabnik> response = restTemplate.postForEntity("/uporabnik/login", loginRequest, Uporabnik.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Janez", response.getBody().getName());
    }
}
